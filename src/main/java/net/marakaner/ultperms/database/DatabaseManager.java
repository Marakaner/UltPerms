package net.marakaner.ultperms.database;

import net.marakaner.ultperms.UltPerms;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        connect();
    }


    private void connect() {

        System.out.println("Init database connect");

        File folder = new File("UltPerms");

        if(!folder.exists()) {
            folder.mkdir();
        }

        File file = new File("UltPerms/mysql.json");

        DatabaseConfig databaseConfig = null;

        if(!file.exists()) {
            try {
                generateConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Bukkit.getConsoleSender().sendMessage("§4Please your credentials into the mysql.json file!");

            return;
        } else {
            databaseConfig = UltPerms.getInstance().getGson().fromJson(file.getPath(), DatabaseConfig.class);
        }

        if(!isConnected() && databaseConfig != null) {

            System.out.println("Connecting to database...");

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

                System.out.println("Connected to database.");

            } catch (SQLException e) {
                System.out.println("Connection to database failed");
                e.printStackTrace();
            }

        }
        else {
            System.out.println("Already connected to database");
        }

    }

    private void generateConfig() throws IOException {

        File file = new File("plugins/UltPerms/mysql.json");

        FileWriter fileWriter = new FileWriter(file);


        DatabaseConfig config = new DatabaseConfig();

        config.hostname = "localhost";
        config.port = "3306";
        config.database = "UltPerms";
        config.username = "root";
        config.password = "PASSWORD";

        fileWriter.write(UltPerms.getInstance().getGson().toJson(config));

        fileWriter.close();

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
