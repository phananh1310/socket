package com.hust.soict.socket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Matches {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String white;
    String black;
    String state;
    String winner;
    GameType type;

    public void addMove(String move) {
        this.state =this.state+ move + ' ';
    }
}
