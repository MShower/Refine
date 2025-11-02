package mshower.refine.mixins.opPlayerNoCheating;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin (ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    //#if MC>=12106
    //$$ @WrapWithCondition(
    //$$         method = "onChangeGameMode",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/server/command/GameModeCommand;execute(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/GameMode;)V"
    //$$         )
    //$$ )
    //$$ private static boolean check(boolean hasPermission, ServerCommandSource source) {
    //$$     if (source.getPlayer() == null) return hasPermission;
//$$
    //$$     if (RefineConfig.INSTANCE.featureConfig.OPNoCheating) {
    //$$         return false;
    //$$     }
    //$$     else {
    //$$         return hasPermission;
    //$$     }
    //$$ }
}
