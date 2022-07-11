package com.hust.soict.socket.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.OneToMany;

@Data
@AllArgsConstructor
public class ServerResponse {
    private String type;
    private String arguments;

    @Override
    public String toString() {
        return type + ' '+arguments;
    }
}
