/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

/**
 * This class encapsulates user with unique email, password to login and some
 * private information.
 */
public class User {

    /**
     * The email.
     */
    private String email;

    /**
     * The password.
     */
    private String pswrd;

    /**
     * The first name.
     */
    private String fname;

    /**
     * The last name.
     */
    private String lname;

    /**
     * The phone.
     */
    private String phone;

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
     * Gets the pswrd.
     *
     * @return the pswrd
     */
    public String getPswrd() {
        return pswrd;
    }

    /**
     * Sets the pswrd.
     *
     * @param pswrd the new pswrd
     */
    public void setPswrd(String pswrd) {
        this.pswrd = pswrd;
    }

    /**
     * Gets the fname.
     *
     * @return the fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * Sets the fname.
     *
     * @param fname the new fname
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * Gets the lname.
     *
     * @return the lname
     */
    public String getLname() {
        return lname;
    }

    /**
     * Sets the lname.
     *
     * @param lname the new lname
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone the new phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

}
