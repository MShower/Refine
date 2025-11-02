package mshower.refine.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import mshower.refine.config.RefineConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RefineCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("refine")
                .then(literal("EnableEndermanGriefingBlacklist")
                        .then(argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean value = BoolArgumentType.getBool(ctx, "value");
                                    RefineConfig.INSTANCE.setValue("featureConfig.EnableEndermanGriefingBlacklist", value);
                                    return 1;
                                })
                        ))
                .then(literal("OPNoCheating")
                        .then(argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean value = BoolArgumentType.getBool(ctx, "value");
                                    RefineConfig.INSTANCE.setValue("featureConfig.OPNoCheating", value);
                                    return 1;
                                })
                        ))
                .then(literal("SpecCommand")
                        .then(argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean value = BoolArgumentType.getBool(ctx, "value");
                                    RefineConfig.INSTANCE.setValue("featureConfig.SpecCommand", value);
                                    return 1;
                                })
                        ))
                .then(literal("SpectatorTeleport")
                        .then(argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean value = BoolArgumentType.getBool(ctx, "value");
                                    RefineConfig.INSTANCE.setValue("featureConfig.SpectatorTeleport", value);
                                    return 1;
                                })
                        ))
        );
    }
}
