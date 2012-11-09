/*
 * SimpleWebsocketHandler.java
 *
 * Created on September 12, 2012, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.notification.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketHandler;

/**
 *
 * @author Elmo
 */
public class WebsocketChannelHandler extends WebSocketHandler {
    
    private final Map<String, Set<BasicWebSocket>> channels = new HashMap();
    
    public WebsocketChannelHandler(String[] channelNames) {
        for(String s: channelNames) {
            channels.put( s.trim(),  new CopyOnWriteArraySet<BasicWebSocket>() );
        }
    }
    
    /**
     *if subprotocol starts with ':' we treat this as a dynamic channel.
     *if channel is dynamic, we'll add it if it does not yet exist
     */
    public WebSocket doWebSocketConnect(HttpServletRequest hreq, String channel) {
        if(channel==null) channel = "test";
        if(channel.startsWith(":")) {
            synchronized(channels) {
                if(!channels.containsKey(channel)) {
                    channels.put( channel, new CopyOnWriteArraySet<BasicWebSocket>() );
                }
            }
        }
        if(!channels.containsKey(channel)) {
            return null;
        }
        return new BasicWebSocket(channel);
    }
    
    private class BasicWebSocket  implements WebSocket.OnTextMessage {
        
        private Connection connection;
        private String protocol;
        
        public BasicWebSocket(String p) {
            this.protocol = p;
        }
        
        public void onOpen(Connection connection) {
            this.connection = connection;
            channels.get( protocol ).add( this );
        }
        
        public void onMessage(String data) {
            try {
                Set<BasicWebSocket> webSockets = channels.get(protocol);
                for (BasicWebSocket webSocket : webSockets) {
                    webSocket.connection.sendMessage(data);
                }
            } catch (IOException x) {
                try {
                    // Error was detected, close the ChatWebSocket client side
                    System.out.println("error on message in server->"+x.getMessage());
                    this.connection.close(-1,"server-error");
                } finally {
                }
            }
        }
        public void onClose(int closeCode, String message) {
            channels.get(protocol).remove(this);
            if(protocol.startsWith(":")) {
                synchronized(channels) {
                    if(channels.get(protocol).size()<0) channels.remove(protocol);
                }
            }
            
        }
    }
    
    public void closeAll() {
        for (Set<BasicWebSocket> webSockets : channels.values()) {
            for(BasicWebSocket bs : webSockets) {
                bs.connection.close();
            }
        }
    }
    
    
}
