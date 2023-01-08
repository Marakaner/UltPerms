package net.marakaner.ultperms.database;

import net.marakaner.ultperms.UltPerms;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.document.gson.JsonDocument;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private Connection databaseConnection;

    public boolean isConnected() {
        return this.databaseConnection != null;
    }


    public Connection getConnection() {
        return databaseConnection;
    }

    public DatabaseManager() {
    }


    public boolean connect() {

        System.out.println("Init database connect");

        File configFile = new File("plugins/UltPerms/MySQL.json");

        IDocument document;

        if(!configFile.exists()) {
            document = new JsonDocument();
            document.append("Hostname", "localhost");
            document.append("Port", "3306");
            document.append("Database", "UltPerms");
            document.append("Username", "root");
            document.append("Password", "PASSWORD");
            document.write(configFile.toPath());
            return false;
        } else {
            document = JsonDocument.newDocument(configFile.toPath());
        }

        if(!isConnected() && document != null) {

            System.out.println("Connecting to database...");

            try {
                this.databaseConnection = DriverManager.getConnection(
                        "jdbc:mysql://"
                                + document.getString("Hostname")
                                + ":"
                                + document.getString("Port")
                                + "/"
                                + document.getString("Database")
                                + "?autoReconnect=true",
                        document.getString("Username"), document.getString("Password")
                );

                System.out.println("Connected to database.");
                return true;
            } catch (SQLException e) {
                System.out.println("Connection to database failed");
                e.printStackTrace();
                return false;
            }

        } else {
            System.out.println("Already connected to database");
            return false;
        }

    }

    private boolean disconnect() {

        System.out.println("Disconnecting...");

        try {
            this.databaseConnection.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to disconnect from database.");
        }

        return false;

    }

}
