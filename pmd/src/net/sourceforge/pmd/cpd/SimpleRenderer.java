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
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;

public class SimpleRenderer implements Renderer {

    public String render(Iterator matches) {
        StringBuffer rpt = new StringBuffer();
        while (matches.hasNext()) {
            Match match = (Match)matches.next();
            rpt.append("=====================================================================" + PMD.EOL);
            rpt.append("Found a " + match.getLineCount() + " line (" + match.getTokenCount() + " tokens) duplication in the following files: " + PMD.EOL);
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                Mark mark = (Mark)occurrences.next();
                rpt.append("Starting at line " + mark.getBeginLine() + " of " + mark.getTokenSrcID() + PMD.EOL);
            }
            rpt.append(match.getSourceCodeSlice() + PMD.EOL);
        }
        return rpt.toString();
    }
}
