/*
 * PageFlowController.java
 *
 * Created on November 5, 2012, 8:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.osiris2.flow.Transition;
import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 */
public class PageFlowController {
    
    @Controller
    protected WorkUnitUIController workunit;
    
    public PageFlowController() {
    }
    
    public void start() {
        workunit.getWorkunit().start();
    }
    
    public String signal(String msg) {
        workunit.getWorkunit().signal(msg);
        if(workunit.getWorkunit().isPageFlowCompleted()) {
            return "_close";
        }
        String pageName = workunit.getWorkunit().getCurrentNode().getName();
        if(pageName==null) {
            throw new RuntimeException("Page name must not be null");    
        }
        return pageName;
    }

    public String signal() {
        workunit.getWorkunit().signal();
        String pageName= workunit.getWorkunit().getCurrentPage().getName();
        if(pageName==null)
            throw new RuntimeException("Page name must not be null");
        return pageName;
    }
    
    public List<Action> getActions() {
        List<Action> actions = new ArrayList();
        List<Transition> transitions = workunit.getWorkunit().getTransitions();
        for(Transition t: transitions) {
            TransitionAction a = new TransitionAction(t.getName());
            String caption = (String)t.getProperties().get("title");
            if(caption==null) caption = (String)t.getProperties().get("caption");
            if(caption==null) caption = t.getName();
            a.setCaption( caption );
            
            a.setPermission(t.getPermission());
            a.setRole(t.getRole());
            a.setConfirm( t.getConfirm() );
            String domain = t.getDomain();
            if(domain==null)domain = workunit.getWorkunit().getModule().getDomain();
            a.setDomain(domain);
            actions.add(a );
        }  
        return actions;
    }
    
    public class TransitionAction extends Action {
        private String confirm;
        
        public TransitionAction(String name) {
            super(name);
        }
        public String getConfirm() {
            return confirm;
        }
        public void setConfirm(String confirm) {
            this.confirm = confirm;
        }
        public Object execute() {
            if( confirm !=null ) {
                if( MsgBox.confirm(confirm)) {
                     return PageFlowController.this.signal( getName() );
                }
            }
            return PageFlowController.this.signal( getName() );
        }
    }
    
    public String getPageTitle() {
        return workunit.getWorkunit().getStateTitle();
    }
    
}
