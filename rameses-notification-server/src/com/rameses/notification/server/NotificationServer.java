/*
 * NotificationServer.java
 *
 * Created on September 12, 2012, 1:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.notification.server;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

/**
 *
 * @author Elmo
 */
public class NotificationServer implements NotificationServerMBean, Serializable  {
    
    private int port = 8080;
    private Server server;
    private String channels;
    private WebsocketChannelHandler socketHandler;
    
    public void start() throws Exception {
        System.out.println("STARTING NOTIFICATION SERVER TEST @"+ new Date());
        
        ExecutorService svc = Executors.newSingleThreadExecutor();
        svc.submit( new Runnable(){
            public void run() {
                try {
                    server = new Server(port);
                    System.out.println("Notification Server port is " + port);
                    String _channels = "test";
                    if(channels!=null) {
                        _channels += ","+channels;
                    }
                    HandlerList handlers = new HandlerList();
                    socketHandler = new WebsocketChannelHandler(_channels.split(","));
                    handlers.addHandler( socketHandler );    
                    
                    server.setHandler(handlers);
                    server.start();
                    server.join();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    
    public void stop() throws Exception {
        System.out.println("STOPPING NOTIFICATION SERVER @"+ new Date());
        socketHandler.closeAll();
        server.stop();
        server = null;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    
}
