/*
 * ModuleServlet.java
 *
 * Created on October 8, 2012, 1:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.moduleserver;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class ModuleServlet extends HttpServlet {
   
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("loading the file");
        resp.setContentType("text/xml");
        Writer w = null;
        try {
            
        }
        catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
        finally {
            try { w.close(); } catch(Exception e1){;}
        }
    }
    
}
