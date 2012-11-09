package com.rameses.invoker.server;


import com.rameses.server.common.AppContext;
import com.rameses.server.common.JsonUtil;
import com.rameses.server.common.LocalScriptServiceProxyProvider;
import com.rameses.service.ServiceProxy;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

public class JsonScriptInvoker extends HttpServlet {
    
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Writer w = res.getWriter();
        try {
            Object[] args = null;
            String parm = req.getParameter("args");
            if(parm!=null && parm.length()>0) {
                if(!parm.startsWith("["))
                    throw new Exception("args must be enclosed with []");
                args = JsonUtil.toObjectArray( parm );
            }
            else {
                //just guess. If there is no args parameter, we assume 
                //you are referring to a map variable where a service method only
                //accepts a map parameter
                Map map = new HashMap();
                Enumeration e = req.getParameterNames();
                while(e.hasMoreElements()) {
                    String n = (String)e.nextElement();
                    map.put( n, req.getParameter(n) );
                }
                if(map.size()>0) {
                    args = new Object[1];
                    args[0] = map;
                }
            }
            
            String path = req.getServletPath();
            int sepIndex = path.indexOf("/", 1);
            String serviceName = path.substring(1, sepIndex);
            String action = path.substring( sepIndex+1, path.indexOf(".") );
            
            LocalScriptServiceProxyProvider lp = new LocalScriptServiceProxyProvider();
            Map conf = new HashMap();
            conf.put( "app.context", AppContext.getName() );
            ServiceProxy p = (ServiceProxy) lp.create( serviceName, new HashMap(),conf );
            Object response = p.invoke(action, args );
            
            String s = JsonUtil.toString( response );
            w.write(s);
        } 
        catch(Exception e) {
            throw new ServletException(e);
        } 
        finally {
            try { w.close(); } catch (Exception ex) {;}
        }
    }
    
    
    
}
