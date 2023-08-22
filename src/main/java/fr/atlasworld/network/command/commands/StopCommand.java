package fr.atlasworld.network.command.commands;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.exceptions.database.DatabaseException;
import fr.atlasworld.network.exceptions.panel.PanelException;
import fr.atlasworld.network.panel.PanelManager;
import fr.atlasworld.network.panel.ServerInfo;

import java.util.Iterator;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("stop")
                .executes(ctx -> stop(ctx.getSource()))
                .then(LiteralArgumentBuilder.<CommandSource>literal("clean")
                        .executes(ctx -> stopWithCleanArg(ctx.getSource())))
        );
    }

    private static int stop(CommandSource source) {
        source.sendMessage("Stopping AtlasNetwork!");
        System.exit(0);

        return Command.SINGLE_SUCCESS;
    }

    public static int stopWithCleanArg(CommandSource source) {
        PanelManager manager = AtlasNetwork.getPanelManager();
        try {
            Database<ServerInfo> database = AtlasNetwork.getDatabaseManager().getServerDatabase();
            source.sendMessage("Deleting servers...");
            for (ApplicationServer server : manager.getServers().keySet()) {
                database.remove(server.getUUID().toString());
                server.getController().delete(false).execute();
                source.sendMessage("Deleted '{}'", server.getName());
            }
        } catch (DatabaseException e) {
            source.sendError("Unable to delete server", e);
        }

        return stop(source);
    }
}
