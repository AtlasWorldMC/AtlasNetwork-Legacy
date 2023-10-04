package fr.atlasworld.network.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atlasworld.network.command.CommandSource;

/**
 * Stops AtlasNetwork
 * Usage:
 * <li>stop - Stops AtlasNetwork</li>
 */
@Deprecated
public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("stop")
                .executes(ctx -> stop(ctx.getSource()))
        );
    }

    private static int stop(CommandSource source) {
        source.sendMessage("Stopping AtlasNetwork!");
        System.exit(0);

        return Command.SINGLE_SUCCESS;
    }
}
