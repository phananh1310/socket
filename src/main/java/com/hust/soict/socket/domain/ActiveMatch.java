package com.hust.soict.socket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveMatch {
    Matches match;
    Boolean isWhiteTurn;
    public void switchTurn(){
        this.isWhiteTurn = !this.isWhiteTurn;
    }
}
