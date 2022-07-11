package com.hust.soict.socket.mapping;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MyResponse {
    String reply = "REPL";

    String type;
    int code ;
    String body;

    public MyResponse(int code, String body){
        this.code = code;
        this.body = body;
    }

    public MyResponse(int code, String body, String type){
        this.code = code;
        this.body = body;
        this.type = type;
    }
    @Override
    public String toString(){
        if (type != null)
            return reply+' '+type+' '+code+' '+body;
        else
            throw new RuntimeException("missing type from server response");
    }
}
