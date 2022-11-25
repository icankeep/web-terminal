package com.passer.demo.websocket.domain;

import lombok.Data;

/**
 * @author passer
 * @time 2022/11/20 18:20
 */
@Data
public class Message {
    private String action;
    private String data;
}
