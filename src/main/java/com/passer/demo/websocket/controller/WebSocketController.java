package com.passer.demo.websocket.controller;

import com.passer.demo.websocket.domain.Message;
import com.passer.demo.websocket.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.WsSession;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author passer
 * @time 2022/11/20 16:21
 */
@Slf4j
@ServerEndpoint("/ws1")
@Component
public class WebSocketController {

    private volatile WsSession session;

    @OnOpen
    public void onOpen(Session session) {
        log.info("websocket open... id: {}, uri: {}", session.getId(), session.getRequestURI());
        this.session = (WsSession) session;
        this.session.getAsyncRemote().sendText(">> ");
    }

    @OnClose
    public void onClose(Session session) {
        log.info("websocket close... id: {}, uri: {}", session.getId(), session.getRequestURI());
    }

    @OnMessage
    public void onMessage(String text) {
        log.info("websocket receive message ... msg: {}", text);
        Message message = GsonUtil.fromJson(text, Message.class);
        this.session.getAsyncRemote().sendText(message.getData());
    }
}
