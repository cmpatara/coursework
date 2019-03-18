/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import dao.DB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import pojo.User;

/**
 * The Class AdminJSFManagedBean. This class is intended to manage all user
 * actions with role 'admin'. This managed bean is called from admin.xhtml,
 * addUser.xhtml and editUser.xhtml.
 */
@ManagedBean(name = "adminBean")
@SessionScoped
public class AdminJSFManagedBean {

    /**
     * Stores the edited user.
     */
    private User editedUser;

    /**
     * Creates a new instance of AdminJSFManagedBean.
     */
    public AdminJSFManagedBean() {
        editedUser = new User(); // Instantiate new user that will be
        // alive during this session (session scoped).
    }

    /**
     * Retrieves all users from SQLite database.
     *
     * @return the list of {@link User} objects.
     */
    public List<User> users() {
        List<User> res = new ArrayList<>();
        try {
            res.addAll(DB.users());
        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }
        return res;
    }

    /**
     * Finds user with the given email in SQLite database and stores it into
     * {@link AdminJSFManagedBean#editedUser}.
     *
     * @param email the given email (or unique id of user)
     * @return the editUser.xhtml page if successful, otherwise error.xhtml page
     */
    public String edit(String email) {

        try {
            editedUser = DB.findUser(email);

        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }

        return "editUser?faces-redirect=true";
    }

    /**
     * Overloading method. Edits the user (see
     * {@link AdminJSFManagedBean#editedUser}).
     *
     * @return the admin.xhtml page if success, otherwise error.xhtml
     */
    public String edit() {

        try {
            User user = new User();
            user.setEmail(editedUser.getEmail());
            user.setPhone(editedUser.getPhone());
            user.setPswrd(editedUser.getPswrd());
            user.setFname(editedUser.getFname());
            user.setLname(editedUser.getLname());
            DB.editUser(user);
        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }
        return "admin?faces-redirect=true";
    }

    /**
     * Deletes user with the given email.
     *
     * @param email the given email
     * @return admin.xhtml page if success, otherwise error.xhtml
     */
    public String delete(String email) {
        try {

            DB.deleteUser(email);
        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }
        return "admin";
    }

    /**
     * Creates new User and adds it to the database using given parameters.
     *
     * @param email given email
     * @param pswrd given password (not hashed)
     * @param fname given first name
     * @param lname given last name
     * @param phone given phone number (can be null)
     * @return login.xhtml page if success, otherwise the same page
     * addUser.xhtml with error message
     */
    public String add(String email, String pswrd, String fname, String lname, String phone) {
        User user = new User();
        user.setEmail(email);
        user.setPhone(phone);
        user.setPswrd(pswrd);
        user.setFname(fname);
        user.setLname(lname);
        try {
            DB.addUser(user);
        } catch (SQLException exc) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("error", true);
            return "addUser";
        }
        return "login";
    }

    /**
     * Gets the edited user.
     *
     * @return the edited user
     */
    public User getEditedUser() {
        return editedUser;
    }

    /**
     * Sets the edited user.
     *
     * @param editedUser the new edited user
     */
    public void setEditedUser(User editedUser) {
        this.editedUser = editedUser;
    }

}
