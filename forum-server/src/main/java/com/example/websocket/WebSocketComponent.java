package com.example.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket工具类，用于向客户端推送消息
 * @author zhw
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketComponent {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketUtil对象
     */
    private static final CopyOnWriteArraySet<WebSocketComponent> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 存储用户ID和WebSocket连接的映射关系
     */
    private static final ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        this.session = session;
        this.userId = userId;
        sessionPool.put(userId, session);
        webSocketSet.add(this);
        log.info("【WebSocket消息】有新的连接，总数：{}, 用户ID：{}", webSocketSet.size(), userId);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        sessionPool.remove(this.userId);
        log.info("【WebSocket消息】连接断开，总数：{}", webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【WebSocket消息】来自客户端的消息：{}", message);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【WebSocket消息】发生错误", error);
    }

    /**
     * 群发消息
     */
    public static void sendAll(String message) {
        log.info("【WebSocket消息】群发消息：{}", message);
        for (WebSocketComponent webSocketUtil : webSocketSet) {
            try {
                webSocketUtil.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("【WebSocket消息】发送消息失败", e);
            }
        }
    }

    /**
     * 给指定用户发送消息
     */
    public static void sendOne(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("【WebSocket消息】发送消息失败", e);
            }
        } else {
            log.warn("【WebSocket消息】用户 {} 不在线", userId);
        }
    }

    /**
     * 发送对象消息
     */
    public static void sendOneObject(String userId, Object object) {
        Session session = sessionPool.get(userId);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(JSON.toJSONString(object));
            } catch (IOException e) {
                log.error("【WebSocket消息】发送对象消息失败", e);
            }
        } else {
            log.warn("【WebSocket消息】用户 {} 不在线", userId);
        }
    }
}

