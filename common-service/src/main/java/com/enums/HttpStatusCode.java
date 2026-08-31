package com.enums;

import lombok.Getter;

@Getter
public enum HttpStatusCode {

    // 2xx Success
    SUCCESS("OK", 200),
    CREATED("Created", 201),
    ACCEPTED("Accepted", 202),
    NO_CONTENT("No Content", 204),

    // 3xx Redirection
    MOVED_PERMANENTLY("Moved Permanently", 301),
    FOUND("Found", 302),
    NOT_MODIFIED("Not Modified", 304),

    // 4xx Client Errors
    BAD_REQUEST("Bad Request", 400),
    UNAUTHORIZED("Unauthorized", 401),
    FORBIDDEN("Forbidden", 403),
    NOT_FOUND("Not Found", 404),
    METHOD_NOT_ALLOWED("Method Not Allowed", 405),
    CONFLICT("Conflict", 409),
    UNSUPPORTED_MEDIA_TYPE("Unsupported Media Type", 415),
    UNPROCESSABLE_ENTITY("Unprocessable Entity", 422),
    TOO_MANY_REQUESTS("Too Many Requests", 429),

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    NOT_IMPLEMENTED("Not Implemented", 501),
    BAD_GATEWAY("Bad Gateway", 502),
    SERVICE_UNAVAILABLE("Service Unavailable", 503),
    GATEWAY_TIMEOUT("Gateway Timeout", 504),

    LOCKED("Account Locked", 423);

    private final String message;
    private final int code;

    HttpStatusCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
