package com.hust.soict.socket.response_object;

import com.hust.soict.socket.domain.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RoomResonse {
    String roomid;
    Boolean isPassword;
    private int total;
    private int maximum = 2;
    // only for custom

    public RoomResonse(String key, Boolean isPassword, int total) {
        this.isPassword = isPassword;
        this.total = total;
        this.roomid = key;
    }
}
