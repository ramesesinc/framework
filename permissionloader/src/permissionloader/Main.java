/*
 * Main.java
 *
 * Created on October 13, 2012, 4:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package permissionloader;

import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Elmo
 */
public class Main {
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        URL uroot = new URL("file:///c:/tests/modules");
        URLDirectory dir = new URLDirectory(uroot);
        URL[] urls = dir.list( new URLIncluder() );
        if(urls.length==0) return;
        URLClassLoader ucl = new URLClassLoader(urls);
        URL u = ucl.findResource( "workunits" );
        URLDirectory ud = new URLDirectory(u);
        SAXParser sx = SAXParserFactory.newInstance().newSAXParser();
        URL[] uls = ud.list( new MetaParser(sx) );
    }
    
    
    public static class URLIncluder implements URLFilter {
        public boolean accept(URL u, String filter) {
            return u.getFile().endsWith(".jar");
        }
    }
    
    
    public static class MetaParser extends DefaultHandler implements URLFilter {
        private SAXParser parser;
        
        public MetaParser(SAXParser sp) {
            this.parser = sp;
        }
        
        public boolean accept(URL u, String filter) {
            if(!u.getFile().endsWith(".xml")) return false;
            try {
                parser.parse(u.openStream(), this );
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(!qName.equalsIgnoreCase("invoker")) return;
            System.out.println("role->"+attributes.getValue("role")+" permkey->"+attributes.getValue("permission"));
        }
    }
    
    
}
