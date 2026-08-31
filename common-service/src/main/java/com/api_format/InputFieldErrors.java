package com.api_format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InputFieldErrors {

    private String field;
    private String message;
}
