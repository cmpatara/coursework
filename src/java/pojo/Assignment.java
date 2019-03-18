/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.io.File;
import javax.servlet.http.Part;

/**
 * The Class Assignment. Encapsulates simple Assignment with unique id number,
 * assignment details, date of uploading and uploaded document as byte array to
 * store it to the SQLite database as Blob.
 */
public class Assignment {

    /**
     * The unique id number.
     */
    private Integer id;

    /**
     * The assignment details.
     */
    private String details;

    /**
     * The date of upload.
     */
    private String date;

    /**
     * The document.
     */
    private byte[] document;

    /**
     * Stores name of uploaded file separately since variable 'document' stores
     * only content of the file.
     */
    private String documentName;

    /**
     * Stores uploaded file by the user to be saved into database.
     */
    private File file;

    /**
     * The email.
     */
    private String email;

    /**
     * The Part object since document is uploaded from browser as multipart
     * object.
     */
    private Part part;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the details.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the details.
     *
     * @param details the new details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the document.
     *
     * @return the document
     */
    public byte[] getDocument() {
        return document;
    }

    /**
     * Sets the document.
     *
     * @param document the new document
     */
    public void setDocument(byte[] document) {
        this.document = document;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the document name.
     *
     * @return the document name
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Sets the document name.
     *
     * @param documentName the new document name
     */
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setFile(File file) {

        this.file = file;
    }

    /**
     * Gets the part.
     *
     * @return the part
     */
    public Part getPart() {
        return part;
    }

    /**
     * Sets the part.
     *
     * @param part the new part
     */
    public void setPart(Part part) {
        this.part = part;
    }

}
