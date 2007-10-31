/*
 * Created on 7 juin 2005
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
package net.sourceforge.pmd.core;

/**
 * This interface is a container for all the constants of that plugin
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2007/01/18 20:58:27  phherlin
 * Integrate PMD v3.9
 *
 * Revision 1.6  2006/10/06 17:07:39  phherlin
 * Upgrade to PMD v3.8
 *
 * Revision 1.5  2006/06/20 21:04:49  phherlin
 * Enable PMD and fix error level violations
 *
 * Revision 1.4  2006/01/17 21:23:20  phherlin
 * Update the PMD 3.4 librairy.
 * Add jakarta ORO for Regexp support.
 *
 * Revision 1.3  2005/10/24 22:35:20  phherlin
 * Upgrading to PMD v3.3
 *
 * Revision 1.2  2005/07/02 14:33:05  phherlin
 * Implement the RuleSets extension point
 *
 * Revision 1.1  2005/06/07 22:39:57  phherlin
 * Implementing extra ruleset declaration
 *
 *
 */
public class PluginConstants {
    public static final String[] PMD_RULESETS = {
            "rulesets/basic.xml", 
            "rulesets/braces.xml", 
            "rulesets/clone.xml",
            "rulesets/codesize.xml",
            "rulesets/controversial.xml", 
            "rulesets/coupling.xml", 
            "rulesets/design.xml", 
            "rulesets/finalizers.xml",
            "rulesets/imports.xml",
            "rulesets/j2ee.xml",
            "rulesets/javabeans.xml",
            "rulesets/junit.xml",
            "rulesets/logging-jakarta-commons.xml",
            "rulesets/logging-java.xml",
            "rulesets/migrating.xml", 
            "rulesets/naming.xml",
            "rulesets/optimizations.xml",
            "rulesets/strictexception.xml",
            "rulesets/strings.xml",
            "rulesets/sunsecure.xml",
            "rulesets/typeresolution.xml",
            "rulesets/unusedcode.xml"};
    
    /**
     * This class is not meant to be instanciated
     *
     */
    private PluginConstants() {
        super();
    }
}
