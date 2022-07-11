package com.hust.soict.socket.dto;

import com.hust.soict.socket.domain.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CRRMdto {
// create room
    private String id;
    // can send without password
    private String password;
    GameType type;


}
