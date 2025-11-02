package mshower.refine.mixins.opPlayerNoCheating;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GiveCommand.class)
public class GiveCommandMixin {
    @ModifyReturnValue(
            method = "method_13404",
            at = @At("TAIL"),
            remap = false
    )
    private static boolean check(boolean hasPermission, ServerCommandSource source) {
        if (source.getPlayer() == null) return hasPermission;

        if (RefineConfig.INSTANCE.featureConfig.OPNoCheating) {
            return false;
        }
        else {
            return hasPermission;
        }
    }
}
