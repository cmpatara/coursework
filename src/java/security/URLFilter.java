/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pojo.User;

/**
 * This is URL filter that checks every url-request entered by the user in
 * browser (address bar). This filter just checks whether the user is logged in
 * or not to prevent unauthorized access to the specific pages (see urlPatters).
 */
@WebFilter(urlPatterns = {"/faces/admin.xhtml", "/faces/addAssignment.xhtml",
    "/faces/editUser.xhtml", "/faces/user.xhtml"})
public class URLFilter implements Filter {

    /**
     * Initializes filter (not used).
     * @param filterConfig the configuration of filter
     * @throws ServletException when something went wrong
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // It's not required to be implemented.
    }

    /**
     * Method to filter requested URL.
     * First, it checks whether there is a logged in user
     * in current session and if so, checks user role, otherwise
     * redirects to the login page.
     * 
     * @param req the servlet request
     * @param res the servlet response
     * @param chain A FilterChain is an object provided by the servlet 
     * container to the developer giving a view into the invocation chain 
     * of a filtered request for a resource. Filters use the FilterChain 
     * to invoke the next filter in the chain, or if the calling filter is 
     * the last filter in the chain, to invoke the resource at the end of the chain.
     * @throws IOException when something went wrong
     * @throws ServletException when something went wrong 
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false); // Get current session.

        // Check whether current session exists and there is an attribute 'user'.
        boolean loggedIn = (session != null) ? session.getAttribute("user") != null : false;

        // This is login URL to where this filter redirects unautorized users.
        String loginURL = request.getContextPath() + "/faces/login.xhtml";

        if (!loggedIn) { // If user is not authorized.
            response.sendRedirect(loginURL); // Redirect to login page.
        } else {

            if (session != null) {

                // Retrieve current logged in user from current session.
                User user = (User) session.getAttribute("user");

                // Retrieve requested url.
                String curURL = request.getRequestURL().toString();

                // Handle case when simple user tries to access admin pages.
                if (!user.getEmail().equals("admin") && (curURL.contains("admin")
                        || curURL.contains("User"))) {
                    response.sendRedirect(loginURL); // Redirect to login page.    

                    // Handle case when admin user tries to access simple user page.
                } else if (user.getEmail().equals("admin") && (curURL.contains("user")
                        || curURL.contains("Assignment"))) {
                    response.sendRedirect(loginURL); // Redirect to login page.                  
                } else { // If everything is ok.
                    chain.doFilter(request, response); // Allow the user to view requested page.
                }               
            }
        }
    }

    /**
     * Called when filter is destroyed.
     */
    @Override
    public void destroy() {
        // It's not required to be implemented.
    }
}
