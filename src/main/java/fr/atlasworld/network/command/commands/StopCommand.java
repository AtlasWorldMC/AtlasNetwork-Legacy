package fr.atlasworld.network.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.command.CommandSource;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("stop").executes(ctx -> executes()));
    }

    private static int executes() {
        AtlasNetwork.logger.info("Stopping AtlasNetwork!");
        System.exit(0);

        return Command.SINGLE_SUCCESS;
    }
}
