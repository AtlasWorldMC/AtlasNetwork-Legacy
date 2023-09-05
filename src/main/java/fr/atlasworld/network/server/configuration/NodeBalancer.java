package fr.atlasworld.network.server.configuration;

import java.util.Random;
import java.util.function.BiFunction;

public enum NodeBalancer {
    ROUND_ROBIN((nodes, lastSelected) -> (lastSelected + 1) % nodes.length),
    RANDOM((nodes, lastSelected) -> new Random().nextInt(nodes.length));

    private final BiFunction<Integer[], Integer, Integer> selectorFunc;
    private int lastSelected = 0;

    NodeBalancer(BiFunction<Integer[], Integer, Integer> selectorFunc) {
        this.selectorFunc = selectorFunc;
    }

    public int selector(Integer[] nodes){
        int selected = this.selectorFunc.apply(nodes, this.lastSelected);
        this.lastSelected = selected;
        return selected;
    }

    public static NodeBalancer fromString(String str) {
        return valueOf(str.toUpperCase());
    }
}
