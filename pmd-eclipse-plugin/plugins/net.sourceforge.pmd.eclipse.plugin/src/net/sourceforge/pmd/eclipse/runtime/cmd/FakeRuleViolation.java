/*
 * Created on 11 avr. 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.eclipse.runtime.cmd;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolation;

/**
 * This is an implementation of IRuleViolation.
 * It is meant to rebuild a RuleViolation from a PMD Marker.
 * This object is used to generate violation reports.
 * 
 * @author Herlin
 * @author Brian Remedios
 */

class FakeRuleViolation extends AbstractRuleViolation {
//    private String filename = "";
//    private int beginLine;
//    private int beginColumn;
//    private int endLine;
//    private int endColumn;
//    private Rule rule;
//    private String description = "";
//    private String packageName = "";
//    private String methodName = "";
//    private String className = "";
//    private String variableName = "";
    
	private static final RuleContext DummyContext = new RuleContext();
	
    /**
     * Default constructor take a rule object to initialize.
     * All other variables have default values to empty;
     * @param rule
     */
    public FakeRuleViolation(Rule theRule) {
        super(theRule, DummyContext, null, null);
    }

//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getFilename()
//     */
//    public String getFilename() {
//        return filename;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getBeginLine()
//     */
//    public int getBeginLine() {
//        return beginLine;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getBeginColumn()
//     */
//    public int getBeginColumn() {
//        return beginColumn;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getEndLine()
//     */
//    public int getEndLine() {
//        return endLine;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getEndColumn()
//     */
//    public int getEndColumn() {
//        return endColumn;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getRule()
//     */
//    public Rule getRule() {
//        return rule;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getDescription()
//     */
//    public String getDescription() {
//        return description;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getPackageName()
//     */
//    public String getPackageName() {
//        return packageName;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getMethodName()
//     */
//    public String getMethodName() {
//        return methodName;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getClassName()
//     */
//    public String getClassName() {
//        return className;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#isSuppressed()
//     */
//    public boolean isSuppressed() {
//        return false;
//    }
//
//    /**
//     * @see net.sourceforge.pmd.IRuleViolation#getVariableName()
//     */
//    public String getVariableName() {
//        return variableName;
//    }
//
//    /**
//     * @param beginColumn The beginColumn to set.
//     */
//    public void setBeginColumn(int beginColumn) {
//        this.beginColumn = beginColumn;
//    }
//
    /**
     * @param beginLine The beginLine to set.
     */
    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param endColumn The endColumn to set.
     */
    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    /**
     * @param endLine The endLine to set.
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    /**
     * @param filename The filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @param methodName The methodName to set.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @param packageName The packageName to set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @param variableName The variableName to set.
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

}
