package mshower.refine;

import mshower.refine.command.RefineCommand;
import mshower.refine.command.SpecCommand;
import mshower.refine.config.RefineConfig;
import mshower.refine.utils.EndermanGriefingBlacklistControl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

//#if MC >= 11900
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//#endif
//#if MC >= 11802
//$$ import com.mojang.logging.LogUtils;
//$$ import org.slf4j.Logger;
//#else
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//#endif

public class Refine implements ModInitializer
{
    public static final Logger LOGGER =
            //#if MC >= 11802
            //$$ LogUtils.getLogger();
            //#else
            LogManager.getLogger();
    //#endif

    public static final String MOD_ID = "refine";
    public static String MOD_VERSION = "0.1.0";
    public static String MOD_NAME = "Refine";

    @Override
    public void onInitialize()
    {
        ModMetadata metadata = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata();
        MOD_NAME = metadata.getName();
        MOD_VERSION = metadata.getVersion().getFriendlyString();

        //#if MC<11900
        //$$ net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> SpecCommand.register(dispatcher));
        //$$ net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> RefineCommand.register(dispatcher));
        //#else
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SpecCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RefineCommand.register(dispatcher));

        //#endif
        RefineConfig.load();
        EndermanGriefingBlacklistControl.loadFromYaml(RefineConfig.INSTANCE.endermanGriefingBlacklistConfig.EndermanGriefingBlacklist);
        LOGGER.info("Refine Loaded!");
    }
}
