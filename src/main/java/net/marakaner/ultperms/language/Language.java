package net.marakaner.ultperms.language;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class Language {

    private String unicode;
    private String display;

}
