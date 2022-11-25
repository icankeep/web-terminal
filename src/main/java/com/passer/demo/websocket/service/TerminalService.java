package com.passer.demo.websocket.service;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author passer
 * @time 2022/11/24 23:05
 */
@Slf4j
@Component
@Scope("prototype")
public class TerminalService {

    private static final String USER_HOME_ENV = "user.home";

    private boolean ready;

    private WebSocketSession session;

    private PtyProcess process;

    private BufferedReader reader;

    private BufferedReader errReader;

    private BufferedWriter writer;

    private LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

    private void init() {
        try {
            String[] cmd = {"/bin/sh", "-l"};
            Map<String, String> env = new HashMap<>(System.getenv());
            env.put("TERM", "xterm");
            process = new PtyProcessBuilder()
                    .setCommand(cmd)
                    .setEnvironment(env)
                    .setDirectory(System.getenv(USER_HOME_ENV))
                    .start();
            process.setWinSize(new WinSize(200, 100));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            new Thread(() -> {
                char[] buf = new char[1024];
                try {
                    while (errReader.read(buf, 0, buf.length) != -1) {
                        this.session.sendMessage(new TextMessage(new String(buf)));
                        buf = new char[1024];
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                char[] buf = new char[1024];
                try {
                    while (reader.read(buf, 0, buf.length) != -1) {
                        this.session.sendMessage(new TextMessage(new String(buf)));
                        buf = new char[1024];
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Throwable t) {
            log.error("TerminalService init error", t);
        }
    }

    public void setWebSocketSession(WebSocketSession session) {
        this.session = session;
        init();
    }

    public void onCommand(String command) throws InterruptedException {
        if (command == null) {
            return;
        }

        if (writer == null) {
            throw new IllegalStateException("writer not initialized");
        }

        commandQueue.put(command);
        new Thread(() -> {
            String cmd = commandQueue.poll();
            try {
                writer.write(cmd);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onClose() {
        if (this.process != null && this.process.isAlive()) {
            this.process.destroy();
        }
    }
}
