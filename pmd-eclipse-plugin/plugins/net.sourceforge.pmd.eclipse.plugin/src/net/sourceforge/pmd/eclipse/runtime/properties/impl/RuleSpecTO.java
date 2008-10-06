/*
 * Created on 28 mai 2005
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
package net.sourceforge.pmd.eclipse.runtime.properties.impl;

/**
 * This class is a simple data bean to help serialize project properties. Is
 * used by the ProjectPropertiesTO to handle project selected rules. This
 * class holds single rule information.
 * 
 * @author Philippe Herlin
 *  
 */
public class RuleSpecTO {
    private String name;
    private String ruleSetName;

    /**
     * Default constructor
     *  
     */
    public RuleSpecTO() {
        super();
    }

    /**
     * Constructor with fields
     * 
     * @param name
     *            a rule name
     * @param ruleSetName
     *            the name of the ruleset where the rule is defined
     */
    public RuleSpecTO(final String name, final String ruleSetName) {
        super();
        this.name = name;
        this.ruleSetName = ruleSetName;
    }

    /**
     * @return name a rule name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the rule name
     * 
     * @param name
     *            the rule name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return ruleSetName the name of ruleset the rule come from
     */
    public String getRuleSetName() {
        return ruleSetName;
    }

    /**
     * Set the ruleSet name the rule come from
     * 
     * @param ruleSetName
     *            a ruleSet name
     */
    public void setRuleSetName(final String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }
}