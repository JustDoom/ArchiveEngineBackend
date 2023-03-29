package com.imjustdoom.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleUrlResponse {

    private String url;
    private String timestamp;
    private String mimeType;
    private String statusCode;
}
