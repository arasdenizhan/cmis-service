package com.solviads.cmis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReturnFileDto {
    private String mimeType;
    private byte[] content;
}
