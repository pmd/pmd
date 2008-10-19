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
package net.sourceforge.pmd.eclipse.core;

/**
 * This interface is a container for all the constants of that plugin
 * 
 * @author Philippe Herlin
 *
 */
public class PluginConstants {
    
    private static final String PMD_JAVA_RULESET_PATH = "rulesets/java/";
    
    public static final String[] PMD_JAVA_RULESETS = {
        PMD_JAVA_RULESET_PATH + "basic.xml", 
        PMD_JAVA_RULESET_PATH + "braces.xml", 
        PMD_JAVA_RULESET_PATH + "clone.xml",
        PMD_JAVA_RULESET_PATH + "codesize.xml",
        PMD_JAVA_RULESET_PATH + "controversial.xml", 
        PMD_JAVA_RULESET_PATH + "rulesets/coupling.xml", 
        PMD_JAVA_RULESET_PATH + "design.xml", 
        PMD_JAVA_RULESET_PATH + "finalizers.xml",
        PMD_JAVA_RULESET_PATH + "imports.xml",
        PMD_JAVA_RULESET_PATH + "j2ee.xml",
        PMD_JAVA_RULESET_PATH + "javabeans.xml",
        PMD_JAVA_RULESET_PATH + "junit.xml",
        PMD_JAVA_RULESET_PATH + "logging-jakarta-commons.xml",
        PMD_JAVA_RULESET_PATH + "logging-java.xml",
        PMD_JAVA_RULESET_PATH + "migrating.xml", 
        PMD_JAVA_RULESET_PATH + "naming.xml",
        PMD_JAVA_RULESET_PATH + "optimizations.xml",
        PMD_JAVA_RULESET_PATH + "strictexception.xml",
        PMD_JAVA_RULESET_PATH + "strings.xml",
        PMD_JAVA_RULESET_PATH + "sunsecure.xml",
        PMD_JAVA_RULESET_PATH + "typeresolution.xml",
        PMD_JAVA_RULESET_PATH + "unusedcode.xml"
        };
    
    /**
     * This class is not meant to be instanciated
     *
     */
    private PluginConstants() {
        super();
    }
}
