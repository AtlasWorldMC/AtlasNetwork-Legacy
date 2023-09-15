package fr.atlasworld.network.networking;

/**
 * Errors for remote connection when network could not fulfill or handle the received packets
 */
public class NetworkErrors {
    /**
     * Your request has been understood but could not be fulfilled because of an internal exception
     */
    public static final String INTERNAL_EXCEPTION = "INTERNAL_EXCEPTION";

    //Auth
    /**
     * The received uuid is not in the database
     */
    public static final String UNKNOWN_OR_MISSING_PROFILE = "UNKNOWN_OR_MISSING_PROFILE";

    /**
     * The profile your trying to connect with is already used
     */
    public static final String PROFILE_ALREADY_INUSE = "PROFILE_ALREADY_INUSE";

    /**
     * The token doesn't match the saved token hash
     */
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    /**
     * You tried to execute a request while not authenticated
     */
    public static final String NOT_AUTHED = "NOT_AUTHED";

    /**
     * You tried to authenticate your connection while already connected
     */
    public static final String ALREADY_AUTHED = "ALREADY_AUTHED";

    //Packet
    /**
     * An unknown packet id has been received, request could not be fulfilled
     */
    public static final String UNKNOWN_PACKET = "UNKNOWN_PACKET";

    /**
     * Your packet could not be understood
     */
    public static final String INCORRECT_PACKET_FORMAT = "INCORRECT_PACKET_FORMAT";
}
