package net.marakaner.ultperms.sign;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.marakaner.ultperms.document.IDocument;
import net.marakaner.ultperms.utils.SimpleLocation;
import org.bukkit.block.Sign;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class UltSign {

    private UUID id;
    private Sign sign;

    private SimpleLocation location;
    private UUID user;
}
