package fr.atlasworld.network.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import fr.atlasworld.network.networking.exceptions.requests.RequestException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Web Request Builder, creates a request before sending it
 * @param <T> data expected to receive
 */
public class Request<T> {
    private final OkHttpClient client;
    private String url;
    private Function<JsonElement, T> valueBuilder;
    private final Map<String, String> headers;
    private final Map<String, String> query;
    private String action = "";
    private RequestBody body;

    /**
     * Creates a new request
     * @param client client
     */
    public Request(OkHttpClient client) {
        this.client = client;
        this.headers = new HashMap<>();
        this.query = new HashMap<>();
    }

    /**
     * Creates a new request
     */
    public Request() {
        this(new OkHttpClient());
    }

    /**
     * Sets the request url
     * @param url request endpoint
     * @return instance of the request
     */
    public Request<T> url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets the value builder, used to transform the raw json into a java object
     * @param builder value builder
     * @return instance of the request
     */
    public Request<T> builder(Function<JsonElement, T> builder) {
        this.valueBuilder = builder;
        return this;
    }

    /**
     * Sets the request timeout in millis
     * @param timeout timeout in millis
     * @return instance of the request
     */
    public Request<T> timeout(long timeout) {
        this.client.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * Sets the request to be a POST request
     * @param body request body
     * @return instance of the request
     */
    public Request<T> post(RequestBody body) {
        this.action = "POST";
        this.body = body;

        return this;
    }

    /**
     * Sets the request to be a PUT request
     * @param body request body
     * @return instance of the request
     */
    public Request<T> put(RequestBody body) {
        this.action = "PUT";
        this.body = body;

        return this;
    }

    /**
     * Sets the request to be a PATCH request
     * @param body request body
     * @return instance of the request
     */
    public Request<T> patch(RequestBody body) {
        this.action = "PATCH";
        this.body = body;

        return this;
    }

    /**
     * Sets the request to be a DELETE request
     * @return instance of the request
     */
    public Request<T> delete() {
        this.action = "DELETE";

        return this;
    }

    /**
     * Adds a header to the request
     * @param key header key
     * @param value header value
     * @return instance of the request
     */
    public Request<T> addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * Adds a query search to the request
     * @param key query key
     * @param value query value
     * @return instance of the request
     */
    public Request<T> addQuery(String key, String value) {
        this.query.put(key, value);
        return this;
    }

    /**
     * Executes the requests
     * @exception RequestException thrown if something went wrong with the request
     */
    public T execute() throws RequestException {
        if (this.url == null) {
            throw new RequestException("Url cannot be null!");
        }

        if (this.valueBuilder == null) {
            throw new RequestException("Value builder cannot be null!");
        }

        com.squareup.okhttp.Request.Builder requestBuilder = new com.squareup.okhttp.Request.Builder()
                .headers(Headers.of(this.headers))
                .url(this.buildUrl());

        switch (this.action) {
            case "POST" -> requestBuilder.post(this.body);
            case "PUT" -> requestBuilder.put(this.body);
            case "PATCH" -> requestBuilder.patch(this.body);
            case "DELETE" -> requestBuilder.delete();
        }

        com.squareup.okhttp.Request request = requestBuilder.build();

        try {
            Response resp = this.client.newCall(request).execute();

            if (resp.isSuccessful()) {
                return this.valueBuilder.apply(JsonParser.parseString(resp.body().string()));
            }

            throw new RequestException("Request failed to " + this.buildUrl() + " with code " + resp.code() + ": " + resp.body().string());
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Executes the request async
     * @return the future value of the request
     */
    public CompletableFuture<T> executeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute();
            } catch (RequestException e) {
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Builds the url with all the arguments
     * @return the full url
     */
    private String buildUrl() {
        if (this.query.isEmpty()) {
            return this.url;
        }

        StringBuilder urlBuilder = new StringBuilder(this.url);

        urlBuilder.append("?");

        this.query.forEach((key, value) -> urlBuilder
                .append("&")
                .append(key)
                .append("=")
                .append(value));

        return urlBuilder.toString();
    }
}
