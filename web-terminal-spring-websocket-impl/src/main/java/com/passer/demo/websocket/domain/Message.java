package com.passer.demo.websocket.domain;

import com.passer.demo.websocket.constant.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author passer
 * @time 2022/11/20 18:20
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    private MessageTypeEnum type;
    private String data;
}
