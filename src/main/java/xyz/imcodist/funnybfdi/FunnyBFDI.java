package xyz.imcodist.funnybfdi;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.imcodist.funnybfdi.other.Config;

public class FunnyBFDI implements ModInitializer {
    public static final String MOD_ID = "funnybfdi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, Config.class);
    }
}
