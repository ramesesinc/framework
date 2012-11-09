/*
 * SessionContext.java
 *
 * Created on July 6, 2012, 3:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class SessionContext {
    
    //this is used to store any kind of information that will be retained
    //until the request is completed
    protected Map info = new HashMap();
   
    public Map getInfo() {
        return info;
    }
    
    public abstract Map getEnv();
    
    
    public abstract String getSessionid();
    public abstract Map createSession(Map info);
    public abstract Map destroySession();
    public abstract Map getSession();
    public abstract Map getUserPrincipal();
    public abstract boolean checkPermission(String domain, String role, String key);
    

    public boolean isLoggedIn() {
        return ( getSession()!=null );
    }
    
    
}

