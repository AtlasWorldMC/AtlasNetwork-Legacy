package fr.atlasworld.network.exceptions.panel;

/**
 * Thrown when the panel has not enough allocations for a server
 */
public class PanelNotEnoughAllocationException extends PanelException {
    public PanelNotEnoughAllocationException(String message) {
        super(message);
    }
}