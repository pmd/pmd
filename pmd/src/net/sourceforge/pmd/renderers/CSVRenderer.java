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
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

public class CSVRenderer implements Renderer {
    public String render(Report report) {
        StringBuffer buf = new StringBuffer(quoteAndCommify("Problem"));
        buf.append(quoteAndCommify("File"));
        buf.append(quoteAndCommify("Line"));
        buf.append(quote("Description"));
        buf.append(PMD.EOL);

        int violationCount = 1;
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(quoteAndCommify(Integer.toString(violationCount)));
            buf.append(quoteAndCommify(rv.getFilename()));
            buf.append(quoteAndCommify(Integer.toString(rv.getLine())));
            buf.append(quote(StringUtil.replaceString(rv.getDescription(), '\"', "'")));
            buf.append(PMD.EOL);
            violationCount++;
        }
        return buf.toString();
    }

    private String quote(String d) {
        return "\"" + d + "\"";
    }

    private String quoteAndCommify(String d) {
        return quote(d) + ",";
    }

}
