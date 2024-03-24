package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    protected static final String taskPath= "/tasks";
    private static final String subtasksPath= "/subtasks";
    private static final String epicsPath= "/epics";
    private static final String historyPath= "/history";
    private static final String prioritizedPath= "/prioritized";

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        httpServer.createContext(taskPath, new TaskHandler());
        httpServer.createContext(subtasksPath, new SubTaskHandler());
        httpServer.createContext(epicsPath, new EpicHandler());
        httpServer.createContext(historyPath, new HistoryHandler());
        httpServer.createContext(prioritizedPath, new PrioritizedHandler());

        httpServer.start();
        System.out.println("start");
    }



}
