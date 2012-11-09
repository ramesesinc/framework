/*
 * ServiceNotifierMBean.java
 *
 * Created on September 6, 2012, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.notification.client;

/**
 * @author Elmo
 */
public interface NotifierClientServiceMBean {
    
    void start() throws Exception;
    void stop() throws Exception;
    void sendMessage(String msg);
    String getId();
    void setId(String id);
    
    /**
     * Host is the name of the host to which it will connect to.
     */
    String getWSHost();
    void setWSHost(String host);
    void setJndiName(String name);
    String getJndiName();
    
    /**
     * AppHost and AppContext refers to the script service to connect.
     */
    String getAppHost();
    String getAppContext();
    void setAppHost(String appHost);
    void setAppContext(String appContext);
    
    /**
     * Protocol is necessary to know where to connect
     */
    String getProtocol();
    void setProtocol(String protocol);
    
    void setMaxConnectTime( Long timeInSeconds );
    Long getMaxConnectTime();
    void setRetryTime( Long timeInSeconds );
    Long  getRetryTime();
    
    Integer getMaxIdleTime();
    void setMaxIdleTime(Integer t);
    
}
