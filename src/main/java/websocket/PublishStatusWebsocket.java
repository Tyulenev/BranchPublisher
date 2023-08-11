package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.frontend.PublishStatus;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import model.dto.frontend.PublishStatusWithPrefix;

import javax.ejb.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/websocket/publish-status")
@Singleton
@Log
public class PublishStatusWebsocket {

    private PublishStatusWithPrefix publishStatus = new PublishStatusWithPrefix();

    Set<Session> sessions = new HashSet<>();

    public void updatePublishStatusInWebsocket(PublishStatusWithPrefix publishStatus) {
        this.publishStatus = publishStatus;
        broadcastPublishStatus();
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        log.info("Session " + session + " connected successfully");
        session.getAsyncRemote().sendObject(convertPublishStatusToJson());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        log.info("Session " + session + " disconnected");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        log.severe("Something wrong with session " + session);
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        session.getAsyncRemote().sendObject(convertPublishStatusToJson());
    }

    @SneakyThrows
    private void broadcastPublishStatus() {
        for (Session session : sessions) {
            session.getAsyncRemote().sendObject(convertPublishStatusToJson());
        }
    }

    @SneakyThrows
    private String convertPublishStatusToJson() {
        return new ObjectMapper().writeValueAsString(publishStatus);
    }

}
