package com.acikek.ended;

import com.acikek.ended.api.EndrousEdiblesAPI;
import com.acikek.ended.api.builder.DestinationBuilder;
import com.acikek.ended.api.builder.EdibleBuilder;
import com.acikek.ended.api.builder.RuleBuilder;
import com.acikek.ended.command.EdibleCommand;
import com.acikek.ended.edible.Edible;
import com.acikek.ended.load.EdibleLoader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
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
        Edible edible = EdibleBuilder.create()
                .edible(Ingredient.ofItems(Items.MELON_SLICE))
                .addRule(RuleBuilder.create()
                        .addSource(new Identifier("minecraft:overworld"), false)
                        .addDestination("spawn", DestinationBuilder.create()
                                .world(World.OVERWORLD)
                                .worldSpawn()
                                .build())
                        .build())
                .build(new Identifier("example:overworld_melon"));
        System.out.println(edible);
        EndrousEdiblesAPI.registerEdible(edible);

        LOGGER.info("Thanks for getting a taste of Endrous Edibles!");
        EdibleLoader.register();
        EdibleCommand.register();
    }
}
