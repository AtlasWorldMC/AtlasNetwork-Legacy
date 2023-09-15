package fr.atlasworld.network.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.atlasworld.network.AtlasNetwork;
import fr.atlasworld.network.command.CommandSource;
import fr.atlasworld.network.command.arguments.UuidArgumentType;
import fr.atlasworld.network.database.Database;
import fr.atlasworld.network.database.entities.authentification.AuthenticationProfile;
import fr.atlasworld.network.exceptions.database.DatabaseException;

import java.util.Set;
import java.util.UUID;

/**
 * Auth command for handling authentication profile
 * <p>
 * Usage:
 * <li>auth profile list - Lists all the authentication profile</li>
 * <li>auth profile create (optional: [uuid]) - Creates a new profile</li>
 * <li>auth profile delete [uuid] - Delete a profile with a specific uuid</li>
 *
 * @deprecated : Marked for removal
 */
@Deprecated
public class AuthCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("auth")
                .then(LiteralArgumentBuilder.<CommandSource>literal("profile")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("list")
                                .executes(ctx -> executeListProfiles(ctx.getSource())))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("create")
                                .executes(ctx -> executeCreateProfile(ctx.getSource(), UUID.randomUUID()))
                                .then(RequiredArgumentBuilder.<CommandSource, UUID>argument("uuid", UuidArgumentType.UUID())
                                        .executes(ctx -> executeCreateProfile(ctx.getSource(), ctx.getArgument("uuid", UUID.class)))))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("delete")
                                .then(RequiredArgumentBuilder.<CommandSource, UUID>argument("uuid", UuidArgumentType.UUID())
                                        .executes(ctx -> executeDeleteProfile(ctx.getSource(), ctx.getArgument("uuid", UUID.class)))))));
    }

    /**
     * Displays all the profiles to the console, including the token hash
     * @param source command executor
     */
    public static int executeListProfiles(CommandSource source) {
        try {
            Set<AuthenticationProfile> profiles = AtlasNetwork.getDatabaseManager().getAuthenticationProfileDatabase()
                    .getAllEntries();

            source.sendMessage("Showing {} Profiles:", profiles.size());
            profiles.forEach(profile -> source.sendMessage("{} - {}", profile.getId(), profile.getHashedToken()));
        } catch (DatabaseException e) {
            source.sendError("Failed to retrieve authentication profiles", e);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * Creates a new profile
     * @param source command executor
     * @param uuid profile id
     */
    public static int executeCreateProfile(CommandSource source, UUID uuid) {
        try {
            Database<AuthenticationProfile> database = AtlasNetwork.getDatabaseManager().getAuthenticationProfileDatabase();

            if (database.has(uuid.toString())) {
                source.sendError("Profile with this uuid already exists!");
                return Command.SINGLE_SUCCESS;
            }

            String token = AtlasNetwork.getSecurityManager().generateAuthenticationToken();
            AuthenticationProfile profile = new AuthenticationProfile(uuid, AtlasNetwork.getSecurityManager().hash(token));

            database.save(profile);

            source.sendMessage("Profile Created:");
            source.sendMessage("ID: {}", uuid.toString());
            source.sendMessage("Token: {} <-- COPY THIS, THIS IS THE ONLY TIME YOU WILL BE ABLE TO SEE IT!", token);
        } catch (DatabaseException e) {
            source.sendError("Something went wrong trying to create a new profile", e);
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Deletes/invalidates a profile
     * @param source command executor
     * @param uuid profile id
     */
    public static int executeDeleteProfile(CommandSource source, UUID uuid) {
        try {
            Database<AuthenticationProfile> database = AtlasNetwork.getDatabaseManager().getAuthenticationProfileDatabase();
            if (!database.has(uuid.toString())) {
                source.sendError("Unknown profile!");
                return Command.SINGLE_SUCCESS;
            }

            database.remove(uuid.toString());
            source.sendMessage("Successfully deleted profile!");
        } catch (DatabaseException e) {
            source.sendError("Something went wrong trying to delete profile", e);
        }
        return Command.SINGLE_SUCCESS;
    }
}
