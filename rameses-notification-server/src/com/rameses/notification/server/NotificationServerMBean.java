/*
 * NotificationServerMBean.java
 *
 * Created on September 12, 2012, 1:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.notification.server;

/**
 *
 * @author Elmo
 */
public interface NotificationServerMBean {
    
    void start() throws Exception;
    void stop() throws Exception;
    int getPort();
    void setPort(int port);
    
    void setChannels(String channels);
}
