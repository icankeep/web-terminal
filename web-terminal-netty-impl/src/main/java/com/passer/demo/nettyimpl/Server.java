package com.passer.demo.nettyimpl;

import com.passer.demo.nettyimpl.handler.TerminalServer;

/**
 * @author passer
 * @time 2022/12/4 17:09
 */
public class Server {
    public static void main(String[] args) {
        TerminalServer terminalServer = new TerminalServer();
        terminalServer.start();
    }
}
