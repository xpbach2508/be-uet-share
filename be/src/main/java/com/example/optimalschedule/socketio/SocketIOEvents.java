package com.example.optimalschedule.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocketIOEvents {
    public static final String MESSAGE_FROM_SERVER = "schedule";
    public final SocketIOServer server;
    public Boolean isRunningDemo = false;
    public Integer groupId = 0;

    @Autowired
    public SocketIOEvents(SocketIOServer server) {
        this.server = server;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("choose_group", Message.class, onChooseGroup());
        server.addEventListener("start_time", Message.class, onStartTime());
        server.addEventListener("end_time", Message.class, onEndTime());
        server.addEventListener("stop_time", Message.class, onStopTime());
    }

    private DataListener<Message> onChooseGroup() {
        return (client, message, ackSender) -> {
            System.out.println("Group: " + message);
            groupId = Integer.parseInt(message.chatMessage);
//            server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, message.chatMessage);
        };
    }

    private DataListener<Message> onStartTime() {
        return (client, message, ackSender) -> {
            System.out.println("Start Time: " + message);
            isRunningDemo = true;
//            server.getBroadcastOperations().sendEvent(MESSAGE_FROM_SERVER, message.chatMessage);
        };
    }

    private DataListener<Message> onEndTime() {
        return (client, message, ackSender) -> {
            System.out.println("End Time: " + message);
            isRunningDemo = false;
        };
    }

    private DataListener<Message> onStopTime() {
        return (client, message, ackSender) -> {
            System.out.println("Stop Time: " + message);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            System.out.println("Client disconnected: " + client.getSessionId());
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            System.out.println("Client connected: " + client.getSessionId());
            client.joinRoom("room");
        };
    }

    record Message(String id, String chatMessage, String timestamp) {
    }
}
