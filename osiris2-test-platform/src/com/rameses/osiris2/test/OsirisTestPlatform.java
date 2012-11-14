/*
 * OsirisTestPlatform.java
 *
 * Created on October 27, 2009, 5:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.test;

import com.rameses.osiris2.client.*;
import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.framework.ClientContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elmo
 */
public final class OsirisTestPlatform {
    
    public static void setEnv(Map env) {
        ClientContext.getCurrentContext().getHeaders().putAll(env);
    }
    
    public static void runTest(Map env, Map roles) throws Exception {
        if(env==null) env = new HashMap();
        
        if( env.get("app.title") == null )
            env.put("app.title", "Osiris Test Platform");
        if( env.get("app.debugMode") == null )
            env.put("app.debugMode", true);
        
        //add the client env here.    
        Map clientEnv = new HashMap();
        if(roles==null) roles = new HashMap();
        roles.put("ALLOWED", "system.*");
        clientEnv.put("ROLES", roles);
        
        env.put("CLIENT_ENV", clientEnv);
        OsirisAppLoader loader = new OsirisAppLoader();
        Platform platform = ClientContext.getCurrentContext().getPlatform();
        loader.load(Thread.currentThread().getContextClassLoader(), env, platform);
        platform.getMainWindow().show();
    }
    
    
}
