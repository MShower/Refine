package mshower.refine.utils;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

//#if MC>=11700
import net.minecraft.registry.Registries;
//#else
//$$ import net.minecraft.util.registry.Registry;
//#endif

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EndermanGriefingBlacklistControl {
    public static final Set<Identifier> BLACKLIST = new HashSet<>();

    public static void loadFromYaml(List<String> list) {
        BLACKLIST.clear();
        for (String id : list) {
            Identifier identifier = Identifier.tryParse(id);
            if (identifier != null) {
                BLACKLIST.add(identifier);
            }
        }
    }

    public static boolean isBlacklisted(Block block) {
        Identifier id;
        //#if MC>=11700
        id = Registries.BLOCK.getId(block);
        //#else
        //$$ id = Registry.BLOCK.getId(block);
        //#endif
        return BLACKLIST.contains(id);
    }
}
