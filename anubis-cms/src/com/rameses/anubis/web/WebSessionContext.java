/*
 * WebSessionContext.java
 *
 * Created on July 6, 2012, 4:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.web;

import com.rameses.anubis.ActionManager;
import com.rameses.anubis.AnubisContext;
import com.rameses.anubis.SessionContext;
import com.rameses.anubis.UserPrincipal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class WebSessionContext extends SessionContext {
    
    private static String CREATE_SESSION = "session/createSession";
    private static String DESTROY_SESSION = "session/destroySession";
    private static String GET_USER = "session/getUserPrincipal";
    private static String GET_SESSION = "session/getSession";
    private static String REMOVE_SESSION = "session/removeSession";
    private static String HAS_PERMISSION = "session/checkPermission";
     
    private Map session;
    private UserPrincipal user;
    private String sessionid;
    
    WebSessionContext() {
        
    }
    
    
    /**
     * Gets the sessionid from the cookie. If the cookie's sessionid does not exist,
     * try to check if you can get the session. if you cant get the session,
     * then destroy the cookie.
     */
    public String getSessionid() {
        if(sessionid!=null) return sessionid;
        Map session = getSession();
        if(session==null) return null;
        this.sessionid = (String)session.get(CmsWebConstants.SESSIONID);
        return this.sessionid;
    }
    
    public Map createSession(Map userinfo) {
        Map params = new HashMap();
        params.put( "USER", userinfo );
        Map session =  (Map)execute(CREATE_SESSION, params);
        this.sessionid = (String)session.get(CmsWebConstants.SESSIONID);
        return session;
    }
    
    public Map destroySession() {
        return (Map)execute(DESTROY_SESSION, null);
    }
    
    public Map getSession() {
        return (Map)execute(GET_SESSION, null);
    }
    
    public Map getUserPrincipal() {
        //if( this.sessionid == null  ) return null;
        Map m = (Map)execute(GET_USER, null);
        return m;
    }
    
    
    public boolean checkPermission(String domain, String role, String permission) {
        Map map = new HashMap();
        map.put("domain", domain );
        map.put("role", role );
        map.put("permission", permission );
        return (Boolean) execute(HAS_PERMISSION, map);
    }
    
    
    private Object execute(  String action, Map params ) {
        try {
            ActionManager actionManager = AnubisContext.getCurrentContext().getProject().getActionManager();
            return actionManager.getActionCommand(action).execute( params, null );
        } catch(Exception e) {
            System.out.println("error executing action->"+action+": " +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private Map env;
    
    public Map getEnv() {
        if(env==null) {
            env = new HashMap();
            env.put(CmsWebConstants.SESSIONID, this.sessionid);
        }
        return env;
    }
}
