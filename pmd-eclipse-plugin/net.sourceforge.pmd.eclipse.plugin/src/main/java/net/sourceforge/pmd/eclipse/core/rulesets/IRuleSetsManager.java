/*
 * Created on 21 juin 2006
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

package net.sourceforge.pmd.eclipse.core.rulesets;

import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.pmd.eclipse.core.PMDCoreException;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSet;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSets;

/**
 * Interface of a rule sets manager. A rule sets manager is
 * responsible to build a rule sets structure from PMD rulesets,
 * to manage the persistence in the preferences store and finally
 * to manage the export in the PMD ruleset format.
 * 
 * @author Herlin
 *
 */

public interface IRuleSetsManager {

    /**
     * Build a plug-in rule set from PMD rule sets.
     * The result is a single rule set (plug-in format) that is composed
     * of the list of all rules from the input rule sets.
     * 
     * @param ruleSetUrls an array of standard PMD rule sets.
     * @return a plug-in specific rulesets structure.
     * 
     * @throws PMDCoreException if an error occurred. Check the root cause for details.
     */
    RuleSet valueOf(String[] ruleSetUrls) throws PMDCoreException;

    /**
     * Serialize a rule sets structure to an output stream.
     * 
     * @param ruleSets a rule sets structure.
     * @param output an open output stream.
     * 
     * @throws PMDCoreException if an error occurred. Check the root cause for details.
     */
    void writeToXml(RuleSets ruleSets, OutputStream output) throws PMDCoreException;
    
    /**
     * Load a rule sets structure from an input stream than contains an XML
     * rule sets specification.
     * 
     * @param input a valid XML input stream.
     * @return a rulesets structure ; this is never null.
     * 
     * @throws PMDCoreException if an error occurred. Check the root cause for details.
     */
    RuleSets readFromXml(InputStream input) throws PMDCoreException;

}
