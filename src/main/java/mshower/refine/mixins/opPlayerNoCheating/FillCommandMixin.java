package mshower.refine.mixins.opPlayerNoCheating;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FillCommand.class)
public class FillCommandMixin {
    @ModifyReturnValue(
            method = "method_13351",
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
