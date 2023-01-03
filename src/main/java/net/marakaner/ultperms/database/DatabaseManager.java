package net.marakaner.ultperms.database;

import net.marakaner.ultperms.UltPerms;

import java.io.File;
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

    private void connect() {

        System.out.println("Init db connect");

        File folder = new File("UltPerms");

        if(!folder.exists()) {
            folder.mkdir();
        }

        File file = new File("UltPerms/mysql.json");

        DatabaseConfig databaseConfig = null;

        if(!file.exists()) {

        } else {
            databaseConfig = UltPerms.getInstance().getGson().fromJson(file.getPath(), DatabaseConfig.class);
        }

        if(!isConnected() && databaseConfig != null) {

            System.out.println("connecting...");

            try {
                this.databaseConnection = DriverManager.getConnection(
                        "jdbc:mysql://"
                                + databaseConfig.hostname
                                + ":"
                                + databaseConfig.port
                                + "/"
                                + databaseConfig.database
                                + "?autoReconnect=true",
                        databaseConfig.username, databaseConfig.password
                );

                System.out.println("connected.");

            } catch (SQLException e) {
                System.out.println("Connection failed");
                e.printStackTrace();
            }

        }
        else {
            System.out.println("Already connected");
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
