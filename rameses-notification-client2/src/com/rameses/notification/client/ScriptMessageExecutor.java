/*
 * ScriptMessageExecutor.java
 *
 * Created on October 4, 2012, 9:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.notification.client;

import com.rameses.service.ScriptServiceContext;
import com.rameses.util.ExceptionManager;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptMessageExecutor {
    
    public final static String SERVICE_NAME = "MessageListenerService";
    private String id;
    private ScriptMessageListener messageListenerService;
    
    public ScriptMessageExecutor(String id, String appHost, String appContext ) {
        this.id = id;
        Map map = new HashMap();
        map.put("app.context", appContext);
        map.put("app.host", appHost);
        try {
            ScriptServiceContext sctx = new ScriptServiceContext(map);
            this.messageListenerService = sctx.create( SERVICE_NAME , ScriptMessageListener.class);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void onMessage(Object msg) {
        try {
            Map map = new HashMap();
            map.put("id", id);
            map.put("message", msg);
            this.messageListenerService.onMessage( msg );
        } catch(Exception e) {
            System.out.println("receive message error." + ExceptionManager.getOriginal(e).getMessage());
        }
    }
    
    
}
