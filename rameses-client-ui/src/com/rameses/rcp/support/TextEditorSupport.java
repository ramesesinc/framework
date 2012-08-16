package com.rameses.rcp.support;

import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;


public class TextEditorSupport {
    
    public static TextEditorSupport install(JTextComponent component) {
        TextEditorSupport s = new TextEditorSupport(component);
        component.putClientProperty(TextEditorSupport.class, s);
        return s;
    }
    
    private JTextComponent component;
    private Color origBackground;
    private Color focusBackground;
    private FocusListener focusListener;
    
    private TextEditorSupport(JTextComponent component) {
        this.component = component;
        
        focusListener = new SupportFocusListener();
        component.addFocusListener(focusListener);
        
        Insets margin = UIManager.getInsets("TextField.margin");
        if (margin != null) {
            Insets ins = new Insets(margin.top, margin.left, margin.bottom, margin.right);
            component.setMargin(ins);
        }
        
        focusBackground = ThemeUI.getColor("XTextField.focusBackground");
    }
    
    private class SupportFocusListener implements FocusListener {
        public void focusGained(FocusEvent focusEvent) {
            try {
                origBackground = component.getBackground();
                if ( component.isEditable() ) {
                    component.setBackground( focusBackground );
                }
                if ( component instanceof JTextField )
                    component.selectAll();
                
            } catch(Exception ign) {;}
        }
        
        public void focusLost(FocusEvent focusEvent) {
            try {
                if ( !ValueUtil.isEqual(component.getBackground(), origBackground) ) {
                    component.setBackground( origBackground );
                }
            } catch(Exception ign) {;}
        }
    }
}
