/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import dao.DB;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import pojo.Assignment;
import pojo.User;

/**
 * The Class UserJSFManagedBean. This bean is intended to manage simple user
 * actions. It's invoked from user.xhtml or addAssignment.xhtml.
 */
@ManagedBean(name = "userBean")
@SessionScoped
public class UserJSFManagedBean {

    /**
     * The Constant UPLOAD_FOLDER. Stores path to default storage of uploaded files.
     */
    public final static Path UPLOAD_FOLDER = Paths.get("/tomcat/uploads");

    /*
    This variable stores details of currently adding/deleting assignment
    (here we need only details and uploading file, since datetime
    is current value and defined automatically as well as id number).
     */
    private Assignment assignment;

    /**
     * Creates a new instance of UserJSFManagedBean.
     */
    public UserJSFManagedBean() {
        assignment = new Assignment();
    }

    /**
     * Downloads stored document from SQLite database. At the end of response
     * (responseComplete), download dialog window opens in the browser.
     *
     * @param a the Assignment object from which document is downloaded
     */
    public void download(Assignment a) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.setResponseHeader("Content-Type", Files.probeContentType(a.getFile().toPath()));
            externalContext.setResponseHeader("Content-Length", a.getDocument().length + "");
            externalContext.setResponseHeader("Content-Disposition",
                    "attachment;filename=\"" + a.getFile().getName() + "\"");
            externalContext.getResponseOutputStream().write(a.getDocument());
            facesContext.responseComplete();
        } catch (IOException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }
    }

    /**
     * Retrieves from database all Assignment objects.
     *
     * @return the list of all Assignments that belong to the currently logged
     * in user
     */
    public List<Assignment> assignments() {
        List<Assignment> res = new ArrayList<>();
        HttpSession session = (HttpSession) FacesContext
                .getCurrentInstance().getExternalContext()
                .getSession(false); // Get current session.
        if (session != null) {

            Object obj = session.getAttribute("user");
            if (obj != null) {
                User user = (User) obj;
                try {
                    res.addAll(DB.assignments(user.getEmail()));

                    // Here we need to create tmp folder on server to store
                    // all documents in order to allow the user uploading these files.
                    if (!Files.exists(UPLOAD_FOLDER)) {
                        Files.createDirectories(UPLOAD_FOLDER);
                    }

                    for (Assignment assign : res) {

                        String filename = FilenameUtils.getBaseName(assign.getDocumentName());
                        String extension = FilenameUtils.getExtension(assign.getDocumentName());
                        Path file = Files.createTempFile(UPLOAD_FOLDER, filename + "-", "." + extension);
                        try (InputStream input = new ByteArrayInputStream(assign.getDocument())) {
                            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
                        }

                        assign.setFile(file.toFile());
                    }

                } catch (SQLException | IOException exc) {
                    try {
                        FacesContext.getCurrentInstance().getExternalContext()
                                .dispatch("error.xhtml?message=" + exc.getMessage());
                    } catch (IOException exc1) {
                    }
                }
            }
        }
        return res;
    }

    /**
     * Deletes given assignment.
     *
     * @param assign the given assignment
     * @return user.xhtml if success, otherwise error.xhtml
     */
    public String delete(Assignment assign) {

        try {

            DB.deleteAssignment(assign.getId());
        } catch (SQLException exc) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .dispatch("error.xhtml?message=" + exc.getMessage());
            } catch (IOException exc1) {
            }
        }

        return "user";
    }

    /**
     * Adds the new Assignment to the database.
     *
     * @return user.xhtml if success, otherwise error.xhtml
     */
    public String add() {
        HttpSession session = (HttpSession) FacesContext
                .getCurrentInstance().getExternalContext()
                .getSession(false); // Get current session.
        if (session != null) {

            Object obj = session.getAttribute("user");
            if (obj != null) {
                User user = (User) obj;
                assignment.setEmail(user.getEmail());
                assignment.setDocumentName(assignment.getPart().getSubmittedFileName());
                try {

                    // Here we convert uploaded document encapsulated in Part object into
                    // array of bytes to store them as Blob in SQLite database using
                    // InputStream.
                    try (InputStream input = assignment.getPart().getInputStream()) {
                        assignment.setDocument(IOUtils.toByteArray(input));
                    }

                    DB.addAssignment(assignment);
                } catch (IOException | SQLException exc) {
                    try {
                        FacesContext.getCurrentInstance().getExternalContext()
                                .dispatch("error.xhtml?message=" + exc.getMessage());
                    } catch (IOException exc1) {
                    }
                }
            }
        }

        return "user";
    }

    /**
     * Gets the assignment.
     *
     * @return the assignment
     */
    public Assignment getAssignment() {
        return assignment;
    }

    /**
     * Sets the assignment.
     *
     * @param assignment the new assignment
     */
    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

}
