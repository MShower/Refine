package mshower.refine.command;

import com.mojang.brigadier.CommandDispatcher;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.*;


//#if MC>=12002
import net.minecraft.nbt.NbtCompound;
//#endif
//#if MC>=12000
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
//#endif


public class SpecCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spec")
                .requires(source -> RefineConfig.INSTANCE.featureConfig.SpecCommand)
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (isSpec(player)) {
                        stopSpec(player);
                    } else {
                        startSpec(player);
                    }
                    return 1;
                })
        );
    }

    private static final Map<UUID, SpecData> specMap = new HashMap<>();

    public static class SpecData {
        public BlockPos pos;
        public GameMode gameMode;

        public SpecData(BlockPos pos, GameMode gameMode) {
            this.pos = pos;
            this.gameMode = gameMode;
        }
    }

    public static boolean isSpec(PlayerEntity player) {
        return specMap.containsKey(player.getUuid());
    }

    public static void startSpec(ServerPlayerEntity player) {
        specMap.put(player.getUuid(), new SpecData(player.getBlockPos(), getGameMode(player)));
        setGameMode(player, GameMode.SPECTATOR);
    }

    public static void stopSpec(ServerPlayerEntity player) {
        SpecData data = specMap.remove(player.getUuid());
        if (data != null) {
            teleport(player, data.pos);
            if (!(data.gameMode ==GameMode.SPECTATOR)){
            setGameMode(player, data.gameMode);
            }
            else {
                setGameMode(player, Objects.requireNonNull(player.getServer()).getDefaultGameMode());
            }
        }
    }

    private static GameMode getGameMode(ServerPlayerEntity player) {
        //#if MC>=11400
        return player.interactionManager.getGameMode();
        //#else
        //$$ return player.interactionManager.getGameMode();
        //#endif
    }

    private static void setGameMode(ServerPlayerEntity player, GameMode mode) {
        //#if MC<11800
        //$$ player.setGameMode(mode);
        //#elseif MC<12002
        player.changeGameMode(mode);
        //#elseif MC<12102
        //$$ player.changeGameMode(mode, (NbtCompound) null);
        //#else
        //$$ player.changeGameMode(mode);
        //#endif
    }

    private static void teleport(ServerPlayerEntity player, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        //#if MC<11800
        //$$ player.teleport(x, y, z);
        //#elseif MC<12100
        player.teleport(
                player.getServerWorld(),
                x, y, z,
                java.util.EnumSet.noneOf(net.minecraft.network.packet.s2c.play.PositionFlag.class),
                player.getYaw(), player.getPitch()
        );
        //#else
        //$$ TeleportTarget.PostDimensionTransition noOpTransition = entity -> { };
        //$$TeleportTarget target = new TeleportTarget(
        //$$        player.getServerWorld(),
        //$$        new Vec3d(x, y, z),
        //$$        player.getVelocity(),
        //$$        player.getYaw(),
        //$$        player.getPitch(),
        //$$        Set.of(),
        //$$        noOpTransition
        //$$);
        //$$player.teleportTo(target);
        //#endif
    }
}
