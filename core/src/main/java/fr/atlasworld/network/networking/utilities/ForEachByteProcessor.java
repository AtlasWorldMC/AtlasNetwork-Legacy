package fr.atlasworld.network.networking.utilities;

import io.netty.util.ByteProcessor;

import java.util.function.Function;

public class ForEachByteProcessor implements ByteProcessor {
    private final Function<Byte, Boolean> process;

    public ForEachByteProcessor(Function<Byte, Boolean> process) {
        this.process = process;
    }

    @Override
    public boolean process(byte value) throws Exception {
        return this.process.apply(value);
    }
}
