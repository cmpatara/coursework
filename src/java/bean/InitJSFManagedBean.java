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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * The Class InitJSFManagedBean. This bean is used to initialize application
 * only once (when the user runs application for the first time in browser).
 */
@ManagedBean(name = "initBean")
@ViewScoped
public class InitJSFManagedBean {

    /**
     * Creates a new instance of InitJSFManagedBean.
     */
    public InitJSFManagedBean() {
    }

    /**
     * Initializes SQLite database (creates tables if not exist and creates
     * session with empty user attribute.
     */
    public void init() {
        try {
            DB.init();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext()
                    .getSession(true); // Here we create new session.
            session.setAttribute("user", null);
        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }
    }
}
