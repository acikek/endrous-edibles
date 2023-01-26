package com.acikek.ended.command;

import com.acikek.ended.api.EndrousEdiblesAPI;
import com.acikek.ended.edible.Edible;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EdibleCommand {

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "targets");
        Identifier edibleId = IdentifierArgumentType.getIdentifier(context, "edible");
        Edible edible = EndrousEdiblesAPI.getEdibleById(edibleId);
        if (edible == null) {
            throw new CommandException(Text.literal("Edible '" + edibleId + "' does not exist"));
        }
        for (ServerPlayerEntity player : players) {
            edible.trigger(player);
        }
        return 0;
    }

    public static CompletableFuture<Suggestions> suggestEdibleIds(CommandContext<ServerCommandSource> source, SuggestionsBuilder builder) {
        for (Edible edible : EndrousEdiblesAPI.getEdibles()) {
            builder.suggest(edible.id().toString());
        }
        return builder.buildFuture();
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("edible")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                            .then(CommandManager.argument("edible", IdentifierArgumentType.identifier())
                                    .suggests(EdibleCommand::suggestEdibleIds)
                                    .executes(EdibleCommand::execute)))
                    .requires(source -> source.hasPermissionLevel(4)))
        );
    }
}
