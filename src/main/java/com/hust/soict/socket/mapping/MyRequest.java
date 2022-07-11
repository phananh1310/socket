package com.hust.soict.socket.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyRequest {
    private String type;
    private String arguments;
}
