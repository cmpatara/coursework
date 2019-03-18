/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.sqlite.SQLiteDataSource;
import pojo.Assignment;
import pojo.User;
import security.Password;

/**
 * This class handles persistence using SQLite.
 */
public class DB {

    /**
     * The Constant DataSource.
     */
    private static final SQLiteDataSource DS = new SQLiteDataSource();

    static {
        ServletContext servletContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("/");
        DS.setUrl("jdbc:sqlite:" + path + "/resources/db/coursework.db");
    }

    /**
     * The Constant CREATE_USER.
     */
    private static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS user_tbl ("
            + "email TEXT PRIMARY KEY, "
            + "password TEXT NOT NULL, "
            + "first_name TEXT NOT NULL, "
            + "last_name TEXT NOT NULL, "
            + "phone TEXT);";

    /**
     * The Constant CREATE_ASSIGNMENT.
     */
    private static final String CREATE_ASSIGNMENT = "CREATE TABLE IF NOT EXISTS assignment_tbl ("
            + " id INTEGER PRIMARY KEY,"
            + " details TEXT,"
            + " date TEXT NOT NULL DEFAULT (datetime('now')),"
            + " document BLOB NOT NULL,"
            + " document_name TEXT NOT NULL,"
            + " email TEXT NOT NULL,"
            + "FOREIGN KEY (email) REFERENCES user_tbl (email)"
            + ");";

    /**
     * The Constant INSERT_ADMIN.
     */
    private static final String INSERT_ADMIN = "INSERT OR IGNORE INTO user_tbl VALUES ("
            + " 'admin',"
            + " '" + Password.getHash("admin") + "',"
            + " 'admin',"
            + " 'admin',"
            + " null"
            + ");";

    /**
     * Initialises SQLite database. Creates user and assignment tables (if they
     * don't exist) and inserts default admin account.
     *
     * @throws SQLException the SQL exception when problems with database
     * connection or sql syntax is wrong
     */
    public static void init() throws SQLException {

        try (Connection conn = DS.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_USER);
            stmt.execute(CREATE_ASSIGNMENT);
            stmt.executeUpdate(INSERT_ADMIN);
        }
    }

    /**
     * Tries to find User by given email and password. First, it retrieves row
     * with given email and then uses Password#checkPswrds method to check
     * whether password is correct and if so, assign found User.
     *
     * @param email given email
     * @param pswrd given password
     * @return User object, or null if not found
     * @throws SQLException when some problems with sql syntax or database
     * connection
     */
    public static User findUser(String email, String pswrd) throws SQLException {
        User res = null;
        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM user_tbl u WHERE u.email=?;")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (Password.checkPswrds(pswrd, rs.getString(2))) {
                        res = new User();
                        res.setEmail(rs.getString(1));
                        res.setPswrd(rs.getString(2));
                        res.setFname(rs.getString(3));
                        res.setLname(rs.getString(4));
                        res.setPhone(rs.getString(5));
                    }
                }
            }
        }
        return res;
    }

    /**
     * Finds user only by email (needed to update or delete user).
     *
     * @param email given email of user to be found
     * @return found user or null if not exists
     * @throws SQLException when some problems with sql syntax or database
     * connection
     */
    public static User findUser(String email) throws SQLException {
        User res = null;
        System.out.println("DB findUser email: " + email);
        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM user_tbl u WHERE u.email=?;")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    res = new User();
                    res.setEmail(rs.getString(1));
                    res.setPswrd(rs.getString(2));
                    res.setFname(rs.getString(3));
                    res.setLname(rs.getString(4));
                    res.setPhone(rs.getString(5));
                }
            }
        }
        return res;
    }

    /**
     * Updates existing user. Note, before saving updated user in database,
     * given (updated) password is hashed usin BCrypt.
     *
     * @param user given User object to be updated
     * @throws SQLException when something wrong in SQL syntax or problems with
     * database connection
     */
    public static void editUser(User user) throws SQLException {

        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE user_tbl SET password=?,"
                        + "first_name=?, last_name=?, phone=? WHERE email=?;")) {
            ps.setString(1, Password.getHash(user.getPswrd()));
            ps.setString(2, user.getFname());
            ps.setString(3, user.getLname());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getEmail());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes user with the given email.
     *
     * @param email the given email
     * @throws SQLException when something wrong with database connection or
     * with sql syntax
     */
    public static void deleteUser(String email) throws SQLException {

        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM user_tbl WHERE email=?;")) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }

    /**
     * Adds given user into SQLite database. Note, before saving user password
     * is converted into hash using BCrypt.
     *
     * @param user given User object to be added
     * @throws SQLException when something wrong in SQL syntax or problems with
     * database connection
     */
    public static void addUser(User user) throws SQLException {
        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO user_tbl "
                        + "VALUES (?,?,?,?,?);")) {
            ps.setString(1, user.getEmail());
            ps.setString(2, Password.getHash(user.getPswrd()));
            ps.setString(3, user.getFname());
            ps.setString(4, user.getLname());
            ps.setString(5, user.getPhone());
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves all User objects from user_tbl.
     *
     * @return the list of User objects
     * @throws SQLException when something wrong with database connection or sql
     * syntax
     */
    public static List<User> users() throws SQLException {
        List<User> res = new ArrayList<>();
        try (Connection conn = DS.getConnection();
                Statement ps = conn.createStatement();
                ResultSet rs = ps.executeQuery("SELECT * FROM user_tbl;")) {
            while (rs.next()) {
                User user = new User();
                user.setEmail(rs.getString(1));
                user.setPswrd(rs.getString(2));
                user.setFname(rs.getString(3));
                user.setLname(rs.getString(4));
                user.setPhone(rs.getString(5));
                res.add(user);
            }
        }
        return res;
    }

    /**
     * This method uses IOUtils class from apache commons.io external library.
     *
     * @param email given email for which all assignments are being searched
     * @return list of assignments belongs to the user with given email
     * @throws SQLException when sql syntax is wrong or problem with database
     * connection
     * @throws IOException when problems with converting document into byte
     * array (maybe document is broken)
     */
    public static List<Assignment> assignments(String email) throws SQLException, IOException {
        List<Assignment> res = new ArrayList<>();
        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM assignment_tbl a WHERE a.email=?;")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment assign = new Assignment();
                    assign.setId(rs.getInt(1));
                    assign.setDetails(rs.getString(2));
                    assign.setDate(rs.getString(3));
                    InputStream is = rs.getBinaryStream(4);
                    assign.setDocument(IOUtils.toByteArray(is));
                    assign.setDocumentName(rs.getString(5));
                    assign.setEmail(email);
                    res.add(assign);
                }
            }
        }
        return res;
    }

    /**
     * Adds the assignment to the database.
     *
     * @param assign the assignment to be added
     * @throws SQLException when something wrong with database connection or sql
     * syntax
     */
    public static void addAssignment(Assignment assign) throws SQLException {
        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO assignment_tbl "
                        + "(details,document,document_name,email) VALUES (?,?,?,?);")) {
            ps.setString(1, assign.getDetails());
            ps.setBytes(2, assign.getDocument());
            ps.setString(3, assign.getDocumentName());
            ps.setString(4, assign.getEmail());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes the assignment with the given id.
     *
     * @param id the given id number
     * @throws SQLException when something wrong with database connection or sql
     * syntax
     */
    public static void deleteAssignment(Integer id) throws SQLException {
        try (Connection conn = DS.getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM assignment_tbl WHERE id=?;")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
