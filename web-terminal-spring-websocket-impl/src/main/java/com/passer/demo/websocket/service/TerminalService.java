package com.passer.demo.websocket.service;

import com.passer.demo.websocket.constant.MessageTypeEnum;
import com.passer.demo.websocket.domain.Message;
import com.passer.demo.websocket.utils.GsonUtil;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.passer.demo.websocket.constant.MessageTypeEnum.TERMINAL_OUTPUT;

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

    private int columns = 20;

    private int rows = 10;

    private WebSocketSession session;

    private PtyProcess process;

    private BufferedReader reader;

    private BufferedWriter writer;

    private final LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

    private final static Pattern RESIZE_DATA_PATTERN = Pattern.compile("columns=([1-9]+\\d*);rows=([1-9]+\\d*)");

    private void init() {
        if (isReady()) {
            return;
        }

        try {
            String[] cmd = {"/bin/sh", "-l"};
            if (isWindows()) {
                cmd = new String[]{"cmd.exe"};
            }

            Map<String, String> env = new HashMap<>(System.getenv());
            env.put("TERM", "xterm");
            process = new PtyProcessBuilder()
                    .setCommand(cmd)
                    .setEnvironment(env)
                    .setDirectory(System.getenv(USER_HOME_ENV))
                    .setRedirectErrorStream(true)
                    .start();
//            process.setWinSize(new WinSize(columns, rows));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            new Thread(() -> {
                char[] buf = new char[1024];
                try {
                    int index;
                    while ((index = reader.read(buf, 0, buf.length)) != -1) {
                        String msg = GsonUtil.toJson(new Message(TERMINAL_OUTPUT, new String(buf, 0, index)));
                        this.session.sendMessage(new TextMessage(msg));
                    }
                } catch (IOException e) {
                    log.error("read from process err", e);
                }
            }).start();
            this.ready = true;
        } catch (Throwable t) {
            log.error("TerminalService init error", t);
        }
    }

    public boolean isReady() {
        if (!this.ready) {
            return false;
        }

        return this.process != null && this.process.isAlive();
    }

    public void setWebSocketSession(WebSocketSession session) {
        this.session = session;
    }

    public void onOpen(WebSocketSession session) {
        setWebSocketSession(session);
        init();

        printHelloWorld();
    }

    public void onResize(String data) {
        if (!isReady()) {
            return;
        }

        Matcher matcher = RESIZE_DATA_PATTERN.matcher(data);
        if (matcher.find()) {
            try {
                int columns = Integer.parseInt(matcher.group(1));
                int rows = Integer.parseInt(matcher.group(2));
                if (columns == this.columns && rows == this.rows) {
                    log.debug("current columns is {}, current rows is {}, don't need resize", columns, rows);
                    return;
                }
                process.setWinSize(new WinSize(columns, rows));
                log.debug("ptyprocess resize, columns: {}, rows: {}", columns, rows);
                this.columns = columns;
                this.rows = rows;
            } catch (Throwable t) {
                throw new IllegalArgumentException("resize data is invalid.");
            }
        } else {
            throw new IllegalArgumentException("resize data is invalid.");
        }
    }

    public void onInput(String command) throws InterruptedException {
        if (!isReady()) {
            return;
        }

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
                log.error("onCommand err", e);
            }
        }).start();
    }

    public void onClose() {
        if (this.session != null) {
            try {
                this.session.close();
            } catch (IOException e) {
                log.error("close session err", e);
            }
        }

        if (this.process != null && this.process.isAlive()) {
            this.process.destroy();
        }
    }

    private void printHelloWorld() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sb = "======================\n" +
                "Current Time: " +
                df.format(new Date()) +
                "\n" +
                "Welcome to Terminal\n" +
                "======================\n";

        String[] lines = sb.split("\n");
        try {
            for (String line : lines) {
                String msg = GsonUtil.toJson(new Message(TERMINAL_OUTPUT, line + "\r\n"));
                this.session.sendMessage(new TextMessage(msg));
            }
        } catch (IOException e) {
            log.error("printHelloWorld err", e);
        }
    }

    private boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }
}
