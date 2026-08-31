package com.api_format;


import com.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse<T> {
    private int code;
    private String status;
    private T data;
    private String message;
    private Instant timeStamp;
    private List<InputFieldErrors> inputFieldErrors;

    public APIResponse(int code, T data, String status ,String message) {
        this.code = code;
        this.status = status;
        this.data = data;
        this.message = message;
        this.timeStamp = Instant.now();
    }

    public static <T> APIResponse<Object> apiResponse(int code, T data, String status, String message) {
        return  APIResponse.builder()
                .code(code)
                .data(data)
                .status(status)
                .message(message)
                .timeStamp(Instant.now())
                .build();

    }

    public static <T> APIResponse<Object> apiResponse(int code , T data, String status, String message, List<InputFieldErrors> inputFieldErrors){
        return APIResponse.builder()
                .code(code)
                .data(data)
                .status(status)
                .message(message)
                .inputFieldErrors(inputFieldErrors)
                .timeStamp(Instant.now())
                .build();
    }


}