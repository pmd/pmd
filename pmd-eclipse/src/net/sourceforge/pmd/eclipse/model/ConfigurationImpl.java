/*
 * Created on 11 juil. 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.RuleSet;

/**
 * Configration object implementation.
 * A configuration is a container for a set of rulesets.
 * A configuration is identified by its name. This name should
 * be unique.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2005/12/30 16:26:30  phherlin
 * Implement a new preferences model
 *
 * Revision 1.1  2005/10/24 22:41:57  phherlin
 * Refactor preferences management
 *
 *
 */

public class ConfigurationImpl implements Configuration {
    private String name;
    private boolean readOnly;
    private final Set ruleSets = new HashSet();
    
    /**
     * Default Constructor
     *
     */
    public ConfigurationImpl() {
        super();
    }
    
    /**
     * Constructor with a name
     * @param Name the configuration name
     */
    public ConfigurationImpl(final String name) {
        super();
        this.name = name;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#getName()
     */
    public String getName() throws ModelException {
        return this.name;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#setName(java.lang.String)
     */
    public void setName(final String name) throws ModelException {
        if ((name == null) || ("".equals(name))) {
            throw new ModelException("Invalid configuration name"); // TODO NLS
        }

        if (this.name != null) {
            throw new ModelException("Invalid state: a configuration name can be set only once."); // TODO NLS
        }
        
        this.name = name;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#getRuleSets()
     */
    public RuleSetProxy[] getRuleSets() throws ModelException {
        return (RuleSetProxy[]) this.ruleSets.toArray(new RuleSetProxy[this.ruleSets.size()]);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#setRuleSets(net.sourceforge.pmd.RuleSet[])
     */
    public void setRuleSets(final RuleSetProxy[] ruleSets) throws ModelException {
        if ((ruleSets == null) || (ruleSets.length == 0)) {
            throw new ModelException("Rulesets cannot be null nor empty"); // TODO NLS
        }
        
        if (this.ruleSets.size() != 0) {
            throw new ModelException("Invalid state: rulesets can only be set once"); // TODO NLS
        }
        
        for (int i = 0; i < ruleSets.length; i++) {
            this.ruleSets.add(ruleSets[i]);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#addRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void addRuleSet(final RuleSetProxy ruleSet) throws ModelException {
        if (ruleSet == null) {
            throw new ModelException("RuleSet cannot be null"); //TODO NLS
        }
        
        if (this.readOnly) {
            throw new ModelException("Invalid state: configuration is read only"); // TODO NLS
        }
        
        this.ruleSets.add(ruleSet);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#removeRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void removeRuleSet(final RuleSetProxy ruleSet) throws ModelException {
        if (ruleSet == null) {
            throw new ModelException("RuleSet cannot be null"); //TODO NLS
        }
        
        if (this.readOnly) {
            throw new ModelException("Invalid state: configuration is read only"); // TODO NLS
        }
        
        this.ruleSets.remove(ruleSet);

    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#getMergedRuleSet()
     */
    public RuleSet getMergedRuleSet() throws ModelException {
        final RuleSet mergedRuleSet = new RuleSet();
        mergedRuleSet.setName("Merged ruleset from configuration " + this.name); // TODO NLS
        final Iterator i = this.ruleSets.iterator();
        while (i.hasNext()) {
            final RuleSet ruleSet = (RuleSet) i.next();
            mergedRuleSet.addRuleSet(ruleSet);
        }
        
        return mergedRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#isReadOnly()
     */
    public boolean isReadOnly() throws ModelException {
        return this.readOnly;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.Configuration#setReadOnly(boolean)
     */
    public void setReadOnly(final boolean readOnly) throws ModelException {
        this.readOnly = readOnly;
    }

}
