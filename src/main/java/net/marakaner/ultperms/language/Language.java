package net.marakaner.ultperms.language;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Language {

    private String code;
    private String description;

    public String getFilePath() {
        return "plugins/UltPerms/languages/" + code + ".json";
    }

}
