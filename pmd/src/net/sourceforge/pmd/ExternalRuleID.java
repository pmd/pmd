/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd;

public class ExternalRuleID {

    private String filename;
    private String ruleName;

    public ExternalRuleID(String id) {
        int afterXML = id.indexOf(".xml") + 5;
        if (afterXML >= id.length()) {
            throw new RuntimeException("Unable to parse reference to external rule " + id + ".  These references need to be in the form <rulesetname>/<rulename>, i.e., <rule ref=\"rulesets/unusedcode.xml/UnusedPrivateField\"/>");
        }
        filename = id.substring(0, afterXML - 1);
        ruleName = id.substring(afterXML);
    }

    public String getFilename() {
        return filename;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String toString() {
        return filename + "/" + ruleName;
    }
}
