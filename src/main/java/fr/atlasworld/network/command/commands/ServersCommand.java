package fr.atlasworld.network.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.config.PanelConfig;
import fr.atlasworld.network.panel.PanelManager;

public class ServersCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("servers")
                .then(LiteralArgumentBuilder.<CommandSource>literal("list")
                        .executes(ctx -> listServers(ctx.getSource()))
                )
        );
    }

    private static int listServers(CommandSource source) {
        PanelManager manager = AtlasNetwork.getPanelManager();
        PanelConfig config = AtlasNetwork.getPanelManager().getConfig();;

        source.sendMessage("Showing {} servers: ", manager.getServers().size());
        source.sendMessage("<---------------->");
        manager.getServers().keySet().forEach(server -> {
            StringBuilder serverUrl = new StringBuilder(config.url());
            if (!config.url().endsWith("/")) {
                serverUrl.append("/");
            }
            serverUrl.append("server/");
            serverUrl.append(server.getIdentifier());
            source.sendMessage("Name: {}", server.getName());
            source.sendMessage("Id: {}", server.getUUID().toString());
            source.sendMessage("Status: {}", server.getStatus().toString().toLowerCase());
            source.sendMessage("Egg: {}", server.retrieveEgg().execute().getName());
            source.sendMessage("Url: {}", serverUrl.toString());
            source.sendMessage("<---------------->");
        });

        return Command.SINGLE_SUCCESS;
    }
}
