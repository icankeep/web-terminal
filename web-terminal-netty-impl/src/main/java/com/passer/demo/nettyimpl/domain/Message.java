package com.passer.demo.nettyimpl.domain;

import com.passer.demo.nettyimpl.constant.MessageTypeEnum;

/**
 * @author passer
 * @time 2022/11/20 18:20
 */
public class Message {
    private MessageTypeEnum type;
    private String data;

    public Message() {
    }

    public Message(MessageTypeEnum type, String data) {
        this.type = type;
        this.data = data;
    }

    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
