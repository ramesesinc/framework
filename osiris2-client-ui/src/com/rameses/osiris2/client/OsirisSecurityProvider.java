/*
 * OsirisSecurityProvider.java
 *
 * Created on October 17, 2009, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.osiris2.SecurityProvider;
import com.rameses.rcp.framework.ClientSecurityProvider;
import java.util.Map;

public class OsirisSecurityProvider implements SecurityProvider, ClientSecurityProvider {
    
    public OsirisSecurityProvider() {
    }


    public boolean checkPermission(String domain, String role, String name) {
        Map roles = (Map) OsirisContext.getEnv().get("ROLES");
        if(roles!=null && role!=null ) {
            if(domain!=null) role = domain+"."+role;
            if(! roles.containsKey(role)) return false;
            if(name==null || name.trim().length()==0) return true;
            String disallowed = (String)roles.get(role);
            if(disallowed!=null && name.matches(disallowed))return false; 
            return true;
        } 
        if(roles!=null) {
            String allowed = (String)roles.get("ALLOWED");
            if(allowed!=null && name.matches(allowed)) return true;
            return false;
        }
        
        if (name == null) return true;
        return false;
    }

    
}
