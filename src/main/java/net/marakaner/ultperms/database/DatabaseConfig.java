package net.marakaner.ultperms.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class DatabaseConfig {

    public String hostname;
    public String port;
    public String database;
    public String username;
    public String password;


}
