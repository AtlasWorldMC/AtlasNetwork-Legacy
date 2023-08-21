package fr.atlasworld.network.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.config.EggConfig;
import fr.atlasworld.network.config.PanelConfig;
import fr.atlasworld.network.integration.ptero.PteroManager;
import fr.atlasworld.network.integration.ptero.ServerCreationResult;

public class ServersCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("servers")
                .then(LiteralArgumentBuilder.<CommandSource>literal("create")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("server")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(ctx -> createServer(ctx.getSource(), ctx.getArgument("name", String.class)))
                                )
                        )
                        .then(LiteralArgumentBuilder.<CommandSource>literal("proxy")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(ctx -> createProxy(ctx.getSource(), ctx.getArgument("name", String.class)))
                                )
                        )
                        .then(LiteralArgumentBuilder.<CommandSource>literal("egg")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.word())
                                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("egg", StringArgumentType.word())
                                                .executes(ctx -> createServerUsingEgg(ctx.getSource(), ctx.getArgument("name", String.class), ctx.getArgument("egg", String.class)))
                                        )
                                )
                        )
                )
        );
    }

    private static int createServer(CommandSource source, String name) {
        PteroManager manager = AtlasNetwork.getPanelApplication();

        source.sendMessage("Creating server..");
        ServerCreationResult result = manager.createServer(name);
        if (result.success()) {
            source.sendMessage("Server created!");
        } else {
            source.sendError("Unable to create server: " + result.msg());
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int createProxy(CommandSource source, String name) {
        PteroManager manager = AtlasNetwork.getPanelApplication();

        source.sendMessage("Creating proxy..");
        ServerCreationResult result = manager.createProxy(name);
        if (result.success()) {
            source.sendMessage("Proxy created!");
        } else {
            source.sendError("Unable to create proxy: " + result.msg());
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int createServerUsingEgg(CommandSource source, String name, String egg) {
        PteroManager manager = AtlasNetwork.getPanelApplication();
        PanelConfig config = AtlasNetwork.getConfig().panel();

        EggConfig eggConfig = config.eggs().stream()
                .filter(eggFilter -> eggFilter.key().equals(egg))
                .findFirst().orElse(null);

        if (eggConfig == null) {
            source.sendError("Cannot find '{}' server egg.", egg);
            return Command.SINGLE_SUCCESS;
        }

        source.sendMessage("Creating server..");
        ServerCreationResult result = manager.createServer(eggConfig, name, true);
        if (result.success()) {
            source.sendMessage("Server created!");
        } else {
            source.sendError("Unable to create server: " + result.msg());
        }

        return Command.SINGLE_SUCCESS;
    }
}
