package mshower.refine.mixins.opPlayerNoCheating;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameModeCommand.class)
public class GameModeCommandMixin {
    @ModifyReturnValue(
            method = "method_13389",
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
