/*
 * PageManager.java
 *
 * Created on July 1, 2012, 2:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Elmo
 * this can only be created through a project
 */
public abstract class FileManager {
    
    protected Project project;
    
    private Map<String, File> fileCache = new HashMap();
    private Map<String, Folder> folderCache = new HashMap();
    
    private Map<String, FileHandler> fileHandlers = new HashMap();
    
    protected abstract Set<String> getFolderItems(String sname);
    protected abstract Map getFileSource(String name, String moduleName);
    
    public FileManager(Project project) {
        this.project = project;
    }
    
    protected void addFileHandler(FileHandler h) {
        fileHandlers.put( h.getExt(), h );
    }
    
    //basic
    public Folder getFolder(String name) {
        if(!name.equals("/") && name.endsWith("/")) name = name.substring(0, name.length()-1);
        if( !folderCache.containsKey(name) ) {
            Map map = new HashMap();
            Folder folder = new Folder(map);
            Set<String> items = getFolderItems( name );
            for(String subPage : items) {
                folder.getChildren().add( getFileInfo(subPage) );
            }
            Collections.sort( folder.getChildren() );
            folderCache.put(name, folder);
        }
        return folderCache.get(name);
    }
    
    //basic function to get the page
    public File getFileInfo(String name) {
        if( !fileCache.containsKey(name) ) {
            //extract the module name
            String fileName = name;
            String moduleName = null;
            String[] str = ProjectUtils.getModuleNameFromFile(name, project);
            if( str!=null) {
                moduleName = str[0];
                fileName = str[1];
            }
            Map map = getFileSource( fileName, moduleName );
            if(map == null )
                throw new com.rameses.anubis.FileNotFoundException(name);
            
            map.put("id", name);
            map.put( "ext", name.substring(name.lastIndexOf(".")+1));
            if( moduleName !=null ) {
                map.put( "module", moduleName );
            }
            
            //check if file has items. This is done by checking if folders exist.
            
            
            fileCache.put(name,new File(map));
        }
        return fileCache.get(name);
    }
    
    private FileInstance createInstance(File file, Map params) {
        FileHandler handler = fileHandlers.get(file.getExt());
        if(handler==null)
            throw new RuntimeException("FileHandler " + file.getId() + " not found");
        FileInstance afi = handler.createInstance(file);
        afi.setProject(project);
        afi.setParams(params);
        return afi;
    }
    
    /**
     * this method is called from the web server.
     * check first of file exists having the correct name.
     * If not, test the registered url mapping if any matches
     * otherwise throw a file not found error.
     */
    public FileInstance getFile(String name, Map params) {
        if(params==null) params = new HashMap();
        
        File file = null;
        try {
            file = getFileInfo(name);
        } catch( com.rameses.anubis.FileNotFoundException fe) {
            //do nothing
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        //test the url mappings if file is null.
        if(file==null) {
            String pageName = name.substring(0, name.lastIndexOf("."));
            String resolvedName = null;
            for(NameParser m: project.getUrlMapping()) {
                if( m.matches(pageName)) {
                    NameParser.MatchResult mr = m.buildResult(pageName);
                    resolvedName = mr.getResolvedPath();
                    params.putAll( mr.getTokens() );
                    break;
                }
            }
            if(resolvedName == null)
                throw new com.rameses.anubis.FileNotFoundException(name);
            file = getFileInfo(resolvedName);
        }
        
        return createInstance( file, params );
    }
    
    
    
}
