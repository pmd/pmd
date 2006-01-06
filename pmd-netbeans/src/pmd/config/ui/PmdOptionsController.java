/*
 *  Copyright (c) 2002-2006, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import pmd.config.PMDOptionsSettings;

/**
 * The PropertyEditor of the Rule property
 */
public class PmdOptionsController extends OptionsPanelController {
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private PmdOptionsComponent comp;
    
    private boolean changed = false;
    
    public PmdOptionsController() {
    }
    
    void dataChanged () {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(PROP_CHANGED, null, null);
        }
    }
    
    public void update() {
        PMDOptionsSettings setting = PMDOptionsSettings.getDefault();
        comp.setEnableScan(setting.isScanEnabled().booleanValue());
        comp.setScanInterval(setting.getScanInterval().intValue());
        comp.setRules( new RulesConfig (setting.getRules(), setting.getRuleProperties()));
        comp.setRulesets( setting.getRulesets());
        changed = false;
    }
    
    public void applyChanges() {
        PMDOptionsSettings setting = PMDOptionsSettings.getDefault();
        setting.setScanEnabled(Boolean.valueOf(comp.isEnableScan()));
        setting.setScanInterval(Integer.valueOf(comp.getScanInterval()));
        RulesConfig data = comp.getRules();
        setting.setRules(data.getRules());
        setting.setRuleProperties(data.getProperties());
        setting.setRulesets(comp.getRulesets());
        changed = false;
    }
    
    public void cancel() {
        // do nothing
    }
    
    public boolean isValid() {
        return true; // XXX
    }
    
    public boolean isChanged() {
        return changed; // XXX
    }
    
    public synchronized JComponent getComponent(Lookup masterLookup) {
        if (comp == null) {
            comp = new PmdOptionsComponent(this);
        }
        return comp;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
}