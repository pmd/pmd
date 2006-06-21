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

package net.sourceforge.pmd.core.rulesets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.core.rulesets.vo.RuleSet;
import net.sourceforge.pmd.core.rulesets.vo.RuleSets;

/**
 * Interface of a rule sets manager. A rule sets manager is
 * responsible to build a rule sets structure from PMD rulesets,
 * to manage the persistence in the preferences store and finally
 * to manage the export in the PMD ruleset format.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/21 23:06:41  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
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
     * @throws RuleSetNotFoundException if one of the rule set url is incorrect.
     */
    RuleSet valueOf(String[] ruleSetUrls) throws RuleSetNotFoundException;

    /**
     * Serialize a rule sets structure to an output stream.
     * 
     * @param ruleSets a rule sets structure.
     * @param output an open output stream.
     * 
     * @throws IOException if an error occurs while writing the stream.
     */
    void writeToXml(RuleSets ruleSets, OutputStream output) throws IOException;
    
    /**
     * Load a rule sets structure from an input stream than contains an XML
     * rule sets specification.
     * 
     * @param input a valid XML input stream.
     * @return a rulesets structure ; this is never null.
     * 
     * @throws IOException if an error occurs while reading from the stream.
     */
    RuleSets readFromXml(InputStream input) throws IOException;

}
