package fr.atlasworld.network.networking.auth;

public class AuthResponses {
    public static final String SUCCESS = "SUCCESS";

    public static final String UNKNOWN_AUTHENTIFICATION_METHOD = "UNKNOWN_AUTHENTICATION_METHOD";
    public static final String INVALID_OR_MISSING_TOKEN = "INVALID_OR_MISSING_TOKEN";
    public static final String UNKNOWN_ACCOUNT = "UNKNOWN_ACCOUNT";

    //UserMode Auth
    public static final String INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";
    public static final String ACCOUNT_VALIDATION_FAILED = "ACCOUNT_VALIDATION_FAILED";

    //ServiceMode Auth
    public static final String TOKEN_INVALID = "TOKEN_INVALID";

}
