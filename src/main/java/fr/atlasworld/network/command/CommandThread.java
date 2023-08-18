package fr.atlasworld.network.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.atlasworld.network.AtlasNetwork;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandThread extends Thread {
    private final CommandDispatcher<CommandSource> dispatcher;
    private final ExecutorService executor;

    public CommandThread(CommandDispatcher<CommandSource> dispatcher) {
        this(dispatcher, Executors.newFixedThreadPool(5));
    }

    public CommandThread(CommandDispatcher<CommandSource> dispatcher, ExecutorService executor) {
        this.dispatcher = dispatcher;
        this.executor = executor;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.isEmpty()) {
                continue;
            }

            this.executor.submit(() -> {
                try {
                    int result = this.dispatcher.execute(command.trim(), new CommandSource(AtlasNetwork.logger));
                    if (result != Command.SINGLE_SUCCESS) {
                        AtlasNetwork.logger.error("Something went wrong trying to execute '{}'", command);
                    }
                } catch (CommandSyntaxException e) {
                    AtlasNetwork.logger.info("Unknown or incomplete command.");
                    AtlasNetwork.logger.info("{} <--[HERE]", command);
                }
            });
        }
    }
}
