package fr.atlasworld.network.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UuidArgumentType implements ArgumentType<UUID> {
    public static UuidArgumentType UUID() {
        return new UuidArgumentType();
    }

    @Override
    public UUID parse(StringReader reader) throws CommandSyntaxException {
        try {
            return UUID.fromString(reader.readUnquotedString());
        } catch (Exception e) {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(() -> "Invalid UUID"), () -> "Please provide a valid uuid!");
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ArgumentType.super.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.singletonList(UUID.randomUUID().toString());
    }
}
