/* 
 * Verify_Rows Version 1.0
 * 
 * Copyright (c) 2021 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl/
 *
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            /* Read Database credentials */
            BufferedReader credentials = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter Oracle Database Username:");
            final String DB_USERNAME = credentials.readLine();
            System.out.println("Enter Oracle Database Password:");
            final String DB_PASSWORD = credentials.readLine();
            this.connection = DriverManager.getConnection(getJDBCUrl(prop.getProperty("hostname"), Integer.parseInt(prop.getProperty("port")), prop.getProperty("oracle_sid")), DB_USERNAME, DB_PASSWORD);
        } catch (IOException | SQLException e) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, e);
        } catch (NumberFormatException ex) {
            System.err.println("PORT MUST BE AN INTEGER");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /* Return Database Connection */
    public Connection getConnection() {
        return connection;
    }

    /* Return the DB Connection Instance */
    public static DBConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DBConnection();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    /* Close the database connection */
    public void closeConnection() {
        try {
            if (instance != null && !instance.getConnection().isClosed()) {
                instance.getConnection().close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getJDBCUrl(String hostname, Integer port, String oracle_sid) {
        return "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + oracle_sid;
    }
}