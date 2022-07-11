package com.hust.soict.socket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Room {
    String id;
    String white;
    String black;
    Boolean isWhiteOwner;
    String password;
    GameType type;

    public Boolean isFull() {
        if (white!=null && black!=null)
            return true;
        return false;
    }

    public void switchOwner() {
        isWhiteOwner = !isWhiteOwner;
    }
}
