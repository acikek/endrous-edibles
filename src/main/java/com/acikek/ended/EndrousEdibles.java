package com.acikek.ended;

import com.acikek.ended.command.EdibleCommand;
import com.acikek.ended.load.EdibleLoader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndrousEdibles implements ModInitializer {

    public static final String ID = "ended";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Thanks for getting a taste of Endrous Edibles!");
        EdibleLoader.register();
        EdibleCommand.register();
    }
}
