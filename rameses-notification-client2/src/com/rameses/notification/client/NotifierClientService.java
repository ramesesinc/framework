/*
 * ServiceNotifier.java
 *
 * Created on September 6, 2012, 3:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.notification.client;

import com.rameses.server.common.AppContext;
import com.rameses.server.common.JndiUtil;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.naming.InitialContext;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 *
 * @author Elmo
 */

public class NotifierClientService implements NotifierClientServiceMBean, Serializable, WebSocket.OnTextMessage {
    
    private String id;
    private String wSHost;
    private String jndiName;
    private Long maxConnectTime = new Long(10);
    private Long retryTime = new Long(10);
    private String appHost;
    private String appContext;
    private String protocol = "test";
    private Integer maxIdleTime;
    
    private static String NOTIFIER = "NOTIFIER";
    
    private WebSocketClientFactory factory;
    private WebSocket.Connection connection;
    private WebSocketClient webSocketClient;
    private ScriptMessageExecutor scriptExecutor;
    private ScheduledExecutorService executorService;
    private String web_socket_host;
    private boolean cancelled;
    
    public void start() throws Exception {
        System.out.println("STARTING NOTIFIER CLIENT 2...@" + new Date());
        InitialContext ctx = new InitialContext();
        JndiUtil.bind( ctx,jndiName, this );
        
        
        //setup the script executor
        if(appContext==null) {
            appContext = AppContext.getName();
        }
        if(appHost==null) {
            appHost = AppContext.getHost();
        }
        if(appContext==null || appHost==null)
            throw new Exception("App Context or App Host must be provided in the NotifierClientService");
        System.out.println("app host is " + appHost + " app context is " + appContext);
        scriptExecutor = new ScriptMessageExecutor(id,appHost,appContext);
        
        //setup the websocket client
        if(wSHost==null)
            throw new Exception("WS Host must be provided");
        
        factory =  new WebSocketClientFactory();
        factory.start();
        
        webSocketClient = factory.newWebSocketClient();
        webSocketClient.setProtocol(protocol);
        webSocketClient.setOrigin(id);
        web_socket_host = "ws://" + wSHost + "/";
        executorService = Executors.newSingleThreadScheduledExecutor();
        cancelled = false;
        openSocket();
    }
    
    public void stop() throws Exception {
        cancelled = true;
        System.out.println("STOPPING NOTIFIER SERVICE");
        if(connection!=null) {
            connection.close();
            connection = null;
        }
        factory.stop();
        factory = null;
        executorService.shutdown();
        InitialContext ctx = new InitialContext();
        JndiUtil.unbind( ctx, jndiName );
    }
    
    private class TaskRunnable implements Runnable {
        public void run() {
            try {
                if( connection ==null  ) {
                    webSocketClient.open( new URI(web_socket_host), NotifierClientService.this, maxConnectTime.longValue(), TimeUnit.SECONDS );
                } else {
                    System.out.println("ATTEMPTING TO RECONNECT from A null connection");
                    openSocket();
                }
            } catch (URISyntaxException ex) {
                System.out.println("URI SYNTAX EXCEPTION");
            } catch (IOException ex) {
                System.out.println("ATTEMPTING TO RECONNECT from IOException");
                openSocket();
            } catch (TimeoutException ex) {
                System.out.println("ATTEMPTING TO RECONNECT from Timeout");
                openSocket();
            } catch (InterruptedException ex) {
                System.out.println("INTERRUPTED EXCEPTION");
                ex.printStackTrace();
            }
        }
    }
    
    private void openSocket() {
        if( cancelled ) return;
        executorService.schedule( new TaskRunnable(), retryTime, TimeUnit.SECONDS);
    }
    
    
    public void sendMessage(String msg) {
        try {
            if(this.connection!=null) {
                connection.sendMessage( msg );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getWSHost() {
        return wSHost;
    }
    
    public void setWSHost(String host) {
        this.wSHost = host;
    }
    
    public String getJndiName() {
        return jndiName;
    }
    
    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
    
    public String getAppHost() {
        return appHost;
    }
    
    public String getAppContext() {
        return appContext;
    }
    
    public void setAppHost(String appHost) {
        this.appHost = appHost;
    }
    
    public void setAppContext(String appContext) {
        this.appContext = appContext;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public Long getMaxConnectTime() {
        return maxConnectTime;
    }
    
    public void setMaxConnectTime(Long maxConnectTime) {
        this.maxConnectTime = maxConnectTime;
    }
    
    public Long getRetryTime() {
        return retryTime;
    }
    
    public void setRetryTime(Long retryTime) {
        this.retryTime = retryTime;
    }
    
    public Integer getMaxIdleTime() {
        return maxIdleTime;
    }
    
    public void setMaxIdleTime(Integer maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }
    
    /************************************************************************
     * IMPLEMENTATION OF WEBSOCKET CONNECTION.
     **********************************************************************/
    public void onOpen(WebSocket.Connection connection) {
        this.connection = connection;
        if(maxIdleTime!=null) connection.setMaxIdleTime(maxIdleTime.intValue());
        try {
            String msg = "joined:"+id;
            connection.sendMessage( msg );
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void onMessage(String msg) {
        try {
            this.scriptExecutor.onMessage( msg );
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void onClose(int i, String msg) {
        System.out.println("closing "+i+ " "+msg);
        if(this.connection!=null) {
            /*
            try {
                connection.sendMessage( "close:"+id );
            } catch(Exception e) {
                System.out.println("error send message on close ->"+e.getMessage());
            }
             */
            connection.close();
            connection = null;
            if( i == 1006 || i == 1000 || i == 1005 ) {
                openSocket();
            }
        }
    }
    
}
