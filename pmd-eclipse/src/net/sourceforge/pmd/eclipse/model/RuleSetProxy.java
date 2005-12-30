/*
 * Created on 27 déc. 2005
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

/**
 * Interface for a reference to a ruleset
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/12/30 16:26:30  phherlin
 * Implement a new preferences model
 *
 *
 */

public interface RuleSetProxy {
    
    /**
     * @return wether the ruleset is overriden
     */
    boolean isOverride() throws ModelException;
    
    /**
     * Set wether the ruleset is overriden
     * @param override
     */
    void setOverride(boolean override) throws ModelException;
    
    /**
     * @return the ruleset URL
     */
    String getRuleSetUrl() throws ModelException;
    
    /**
     * Set the ruleSet URL.
     * @param ruleSetUrl
     */
    void setRuleSetUrl(String ruleSetUrl) throws ModelException;
    
    /**
     * @return the ruleset
     * @throws ModelException
     */
    RuleSet getRuleSet() throws ModelException;
    
    /**
     * Set the ruleset.
     * If the ruleset is not overriden, this method has no effect
     * @param ruleSet
     * @throws ModelException
     */
    void setRuleSet(RuleSet ruleSet) throws ModelException;
}
