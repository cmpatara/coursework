/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import dao.DB;
import java.io.IOException;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import pojo.User;

/**
 * The Class LoginJSFManagedBean. This managed bean is responsible for the
 * login/logout users. It's called from login.xhtml and header.xhtml pages.
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginJSFManagedBean {

    /**
     * Creates a new instance of LoginJSFManagedBean.
     */
    public LoginJSFManagedBean() {
    }

    /**
     * This method retrieves current session object and checks whether there is
     * an attribute 'user' and if so, returns it, otherwise returns null (last
     * case means there is no logged in user in current session).
     *
     * @return User object is logged in, otherwise null
     */
    public User getLoggedIn() {
        User res = null;
        HttpSession session = (HttpSession) FacesContext
                .getCurrentInstance().getExternalContext()
                .getSession(false); // Get current session.
        if (session != null) { // Here session can be null initially (when the
            // user only opens application).

            Object obj = session.getAttribute("user");
            if (obj != null) {
                res = (User) obj;
            }
        }
        return res;
    }

    /**
     * Logs out the current user. To do that, just set 'user' attribute to null.
     * Note, here we don't invalidate session.
     *
     * @return index.xhtml page
     */
    public String logout() {
        HttpSession session = (HttpSession) FacesContext
                .getCurrentInstance().getExternalContext()
                .getSession(false); // Get current session.
        session.setAttribute("user", null);
        return "index?faces-redirect=true";
    }

    /**
     * Redirects to login.xhtml page.
     *
     * @return login.xhtml page
     */
    public String login() {
        return "login";
    }

    /**
     * Performs login action. First, it tries to find user with given email and
     * password in database and if found, returns admin.xhtml or user.xhtml
     * (depending on the user role), otherwise returns login (or error.xhtml if
     * there is a problem with SQLite database).
     *
     * @param email the given email
     * @param pswrd the given password
     * @return admin/user.xhtml or login if invalid credentials, or error.xhtml
     * if something went wrong with SQLite query (or database connection)
     */
    public String login(String email, String pswrd) {

        try {
            User user = DB.findUser(email, pswrd);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext()
                    .getSession(false); // Here we use existing session.
            session.setAttribute("user", user);

            if (user == null) { // If user not found with the given credentials.
                return "login";
            } else { // If user has been found.
                if (user.getEmail().equals("admin")) { // If logged in user is admin user.
                    return "admin";
                } else { // If logged in user is simple user.
                    return "user?faces-redirect=true";
                }
            }

        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }

        return "index";
    }

}
