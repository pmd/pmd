/*
 * Created on 28 déc. 2005
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

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 * Default implementation of a RuleSetProxy
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/04/10 20:55:58  phherlin
 * Update to PMD 3.6
 *
 * Revision 1.1  2005/12/30 16:26:30  phherlin
 * Implement a new preferences model
 *
 *
 */

public class RuleSetProxyImpl implements RuleSetProxy {
    private boolean override;
    private String ruleSetUrl;
    private RuleSet ruleSet;

    /**
     * @see net.sourceforge.pmd.eclipse.model.RuleSetProxy#isOverride()
     */
    public boolean isOverride() throws ModelException {
        return this.override;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.RuleSetProxy#setOverride(boolean)
     */
    public void setOverride(boolean override) throws ModelException {
        this.override = override;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.RuleSetProxy#getRuleSetUrl()
     */
    public String getRuleSetUrl() throws ModelException {
        return this.ruleSetUrl;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.RuleSetProxy#setRuleSetUrl(java.lang.String)
     */
    public void setRuleSetUrl(String ruleSetUrl) throws ModelException {
        this.ruleSetUrl = ruleSetUrl;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.RuleSetProxy#getRuleSet()
     */
    public RuleSet getRuleSet() throws ModelException {
        try {
            if ((this.ruleSet == null) && (!this.override)) {
                final RuleSetFactory factory = new RuleSetFactory();
                this.ruleSet = factory.createRuleSets(this.ruleSetUrl).getAllRuleSets()[0];
            }
            
            return this.ruleSet;
            
        } catch (RuleSetNotFoundException e) {
            throw new ModelException(e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.RuleSetProxy#setRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setRuleSet(final RuleSet ruleSet) throws ModelException {
        if (this.override) {
            this.ruleSet = ruleSet;
        }
    }

}
