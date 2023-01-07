package net.marakaner.ultperms.language.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
public class MessagesConfig {

    public String language;
    public HashMap<String, String> messages;
}
