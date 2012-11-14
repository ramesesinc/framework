/*
 * AbstractListForm.java
 *
 * Created on November 13, 2012, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.StyleRule;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 */
public abstract class MasterListFormController {
    
    @Controller
    private WorkUnitUIController controller;
    
    public static String MODE_CREATE = "create";
    public static String MODE_READ = "read";
    public static String MODE_EDIT = "edit";
    
    public abstract Object getEntity();
    public abstract String getEntityName();
    public abstract String getRole();
    
    public String getDomain() {
        return controller.getWorkunit().getModule().getDomain();
    }
    
    public abstract void doCreateObject();
    public abstract void doEditObject();
    public abstract void doSaveNew();
    public abstract void doSaveUpdate();
    
    private String mode = MODE_READ;
    
    public StyleRule[] getStyleRules() {
        List styles = new ArrayList();
        styles.add( new StyleRule("entity.*", "mode=='create'").add("enabled",true));
        styles.add( new StyleRule("entity.*", "mode=='edit'" ).add("enabled",true));
        styles.add( new StyleRule("entity.*", "mode=='read'" ).add("enabled",false));
        return (StyleRule[]) styles.toArray();
    }
    
    public Opener getForm() {
        Opener o = new Opener();
        o.setOutcome( MODE_READ );
        return o;
    }
    
    private void addAction( String name, String caption, List<Action> actions ) {
        if(!OsirisContext.getSession().getSecurityProvider().checkPermission( getDomain(),getRole(),name+getEntityName() )) {
            return;
        }
        Action a = new Action(name, caption,null);
        actions.add(a);
    }
    
    public List<Action> getActions() {
        List<Action> actions = new ArrayList();
        if(mode.equals(MODE_CREATE)) {
            addAction( "save", "Save", actions );
            addAction( "cancel", "Cancel", actions );
        } else if(mode.equals(MODE_READ)) {
            addAction( "create", "New", actions );
            addAction( "edit", "Edit",actions );
        } else if(mode.equals(MODE_EDIT)) {
            addAction( "save", "Save", actions );
            addAction( "cancel", "Cancel", actions );
        }
        return actions;
    }
    
    public String save() {
        if( MsgBox.confirm("You are about to save this record. Continue?")) {
            if( this.mode.equals(MODE_CREATE)) {
                doSaveNew();
            } else if( this.mode.equals(MODE_EDIT)) {
                doSaveUpdate();
            }
            this.mode = MODE_READ;
            return this.mode;
        }
        return null;
    }
    
    public String create() {
        this.mode = MODE_CREATE;
        doCreateObject();
        return this.mode;
    }
    
    public String edit() {
        this.mode = MODE_EDIT;
        doEditObject();
        return this.mode;
    }
    
    public String cancel() {
        this.mode = MODE_READ;
        return this.mode;
    }

    public String getMode() {
        return mode;
    }
    
}
