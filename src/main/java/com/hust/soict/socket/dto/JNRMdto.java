package com.hust.soict.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JNRMdto {
    // join room
    private String roomid;
    private String password;
}
