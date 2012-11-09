/*
 * CmsStartup.java
 *
 * Created on June 27, 2012, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.web;

import com.rameses.anubis.ContentUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author Elmo
 *  
 * The key is finding where the anubis host is. It will look for an entry 
 * in the system properties named 'anubis.host'. If none found, we will look 
 * for the anubis.hosts file located in the user.dir. 
 */

public class CmsStartup extends HttpServlet {
    
    private static String ANUBIS_HOSTS = "anubis.hosts";
    
    private InputStream findHostsConf(String url) {
        try {
            url = ContentUtil.replaceSysProperty(url);
            URL u = new URL(url);
            return u.openStream();
        }
        catch(Exception e) {
            return null;
        }
    }
    
    public void init(ServletConfig config) throws ServletException {
        System.out.println("************* STARTING ANUBIS CMS WEB SERVER *************** ");
        InputStream is = null;
        try {
            ServletContext appContext = config.getServletContext();
            
            String confPath = config.getServletContext().getInitParameter( ANUBIS_HOSTS );
            if(confPath==null) confPath = System.getProperty( ANUBIS_HOSTS );
            
            if( confPath != null ) {
                is = findHostsConf( confPath );
            }
            
            if(is == null ) {
                String userDir = System.getProperty("user.dir");
                System.out.println("User dir is " + userDir);
                File file = new File( userDir + "/" + ANUBIS_HOSTS);
                is = findHostsConf(file.toURI().toURL().toString());
            }
            
            //check if is a single file, load the default
            if(is==null) {
                String defaultConf = config.getServletContext().getInitParameter("anubis.default");
                if(defaultConf==null) defaultConf = System.getProperty("anubis.default");
                if(defaultConf!=null) {
                    String s = "default="+defaultConf;
                    is = new ByteArrayInputStream(s.getBytes());
                }
            }
            
            if(is==null) {
                throw new ServletException("Anubis startup failed. Cannot find the anubis.hosts file");
            }
            
            //load the url patterns here.
            ProjectResolver resolver = new ProjectResolver(is);
            
            URL u = appContext.getResource("/anubis.lib");
            resolver.setDefaultModule( new AnubisDefaultModule( u.toString()) );
            resolver.setDefaultTheme( new AnubisDefaultTheme( u.toString()) );
            
            appContext.setAttribute( ProjectResolver.class.getName(), resolver );
            
        } catch(Exception ex) {
            throw new ServletException(ex);
        } finally {
            try { is.close(); } catch(Exception e) {;}
        }
    }
    
}
