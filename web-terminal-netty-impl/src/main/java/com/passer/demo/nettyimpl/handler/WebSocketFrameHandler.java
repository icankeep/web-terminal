package com.passer.demo.nettyimpl.handler;

import com.passer.demo.nettyimpl.domain.Message;
import com.passer.demo.nettyimpl.service.TerminalService;
import com.passer.demo.nettyimpl.utils.GsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author passer
 * @time 2022/12/4 18:55
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketFrameHandler.class);

//    private static final Map<ChannelHandlerContext, TerminalHandler> ctxTerminals = new ConcurrentHashMap<>();

    private final TerminalService terminalService = new TerminalService();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;

            String text = textWebSocketFrame.text();
            log.debug("ws receive text: {}", text);
            Message message = GsonUtil.fromJson(text, Message.class);
            switch (message.getType()) {
                case OPEN:
                    terminalService.onOpen(ctx);
                    break;
                case RESIZE:
                    terminalService.onResize(message.getData());
                    break;
                case TERMINAL_INPUT:
                    terminalService.onInput(message.getData());
                    break;
                case CLOSE:
                    terminalService.onClose();
                    break;
                default:
                    throw new IllegalStateException("Message type[ " + message.getType() + " ] is invalid.");
            }
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

}
