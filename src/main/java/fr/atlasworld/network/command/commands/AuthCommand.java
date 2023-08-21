package fr.atlasworld.network.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.database.DatabaseManager;
import fr.atlasworld.network.entities.auth.AuthProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;

import java.util.Set;
import java.util.UUID;

public class AuthCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("auth")
                .then(LiteralArgumentBuilder.<CommandSource>literal("profile")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("list")
                                .executes(ctx -> executeListProfiles(ctx.getSource())))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("create")
                                .executes(ctx -> executeCreateProfile(ctx.getSource(), UUID.randomUUID().toString()))
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("uuid", StringArgumentType.word())
                                        .executes(ctx -> executeCreateProfile(ctx.getSource(), ctx.getArgument("uuid", String.class)))))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("delete")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("uuid", StringArgumentType.word())
                                        .executes(ctx -> executeDeleteProfile(ctx.getSource(), ctx.getArgument("uuid", String.class)))))));
    }

    public static int executeListProfiles(CommandSource source) {
        try {
            Set<AuthProfile> profiles = AtlasNetwork.getDatabaseManager().getAuthProfiles();
            source.sendMessage("Showing {} Profiles:", profiles.size());
            profiles.forEach(profile -> source.sendMessage("{} - {}", profile.profileId(), profile.tokenHash()));
        } catch (DatabaseException e) {
            source.sendError("Failed to retrieve authentication profiles", e);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int executeCreateProfile(CommandSource source, String strUuid) {
        UUID uuid;
        try {
            uuid = UUID.fromString(strUuid);
        } catch (Exception e) {
            source.sendError("Please provide a valid uuid!");
            return Command.SINGLE_SUCCESS;
        }

        DatabaseManager database = AtlasNetwork.getDatabaseManager();

        try {
            if (database.authProfileExists(uuid)) {
                source.sendError("Profile with this uuid already exists!");
                return Command.SINGLE_SUCCESS;
            }
        } catch (DatabaseException e) {
            source.sendError("Something went wrong trying to create a new profile", e);
            return Command.SINGLE_SUCCESS;
        }

        String token = AtlasNetwork.getSecurityManager().generateAuthenticationToken();
        AuthProfile profile = new AuthProfile(uuid, AtlasNetwork.getSecurityManager().hash(token));

        try {
            database.saveAuthProfile(profile);
        } catch (DatabaseException e) {
            source.sendError("Something went wrong trying to create a new profile", e);
            return Command.SINGLE_SUCCESS;
        }

        source.sendMessage("Profile Created:");
        source.sendMessage("ID: {}", uuid.toString());
        source.sendMessage("Token: {} <-- COPY THIS, THIS IS THE ONLY TIME YOU WILL BE ABLE TO SEE IT!", token);

        return Command.SINGLE_SUCCESS;
    }

    public static int executeDeleteProfile(CommandSource source, String strUuid) {
        UUID uuid;
        try {
            uuid = UUID.fromString(strUuid);
        } catch (Exception e) {
            source.sendError("Please provide a valid uuid!");
            return Command.SINGLE_SUCCESS;
        }

        DatabaseManager database = AtlasNetwork.getDatabaseManager();

        try {
            if (!database.authProfileExists(uuid)) {
                source.sendError("Unknown profile!");
                return Command.SINGLE_SUCCESS;
            }

            database.deleteAuthProfile(uuid);
            source.sendMessage("Successfully deleted profile!");
        } catch (DatabaseException e) {
            source.sendError("Something went wrong trying to delete profile", e);
        }
        return Command.SINGLE_SUCCESS;
    }
}
