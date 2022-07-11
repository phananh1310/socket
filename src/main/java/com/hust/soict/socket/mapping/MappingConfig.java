package com.hust.soict.socket.mapping;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfig {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
