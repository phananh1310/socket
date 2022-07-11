package com.hust.soict.socket.response_object;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponse {
    private Boolean isOnline;
    private String ingame;
}
