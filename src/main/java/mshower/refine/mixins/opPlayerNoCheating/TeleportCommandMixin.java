package mshower.refine.mixins.opPlayerNoCheating;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @ModifyReturnValue(
            method = {
                    "method_13763",
                    "method_13764",
            },
            at = @At("TAIL"),
            remap = false
    )
    private static boolean check(boolean hasPermission, ServerCommandSource source) {
        if (source.getPlayer() == null) return hasPermission;

        if (Objects.requireNonNull(source.getPlayer()).interactionManager.getGameMode() == GameMode.SPECTATOR && RefineConfig.INSTANCE.featureConfig.SpectatorTeleport) {
            return true;
        }
        if (!hasPermission) {
            return false;
        }
        return !RefineConfig.INSTANCE.featureConfig.OPNoCheating;
    }
}
