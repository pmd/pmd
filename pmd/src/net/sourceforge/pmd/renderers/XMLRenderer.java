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
 *
 * CHANGE RECORD
 * - 17 Nov 2003: modified by Olivier Mengu\u00E9 to implement correct escaping
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

public class XMLRenderer implements Renderer {

    public String render(Report report) {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?><pmd>" + PMD.EOL);
        String filename = null;

        // rule violations
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            if (!rv.getFilename().equals(filename)) { // New File
                if (filename != null) // Not first file ?
                    buf.append("</file>");
                filename = rv.getFilename();
                buf.append("<file name=\"");
                StringUtil.appendXmlEscaped(buf, filename);
                buf.append("\">")
                   .append(PMD.EOL);
            }

            buf.append("<violation line=\"")
               .append(rv.getLine()) // int
               .append("\" rule=\"");
            StringUtil.appendXmlEscaped(buf, rv.getRule().getName());
            buf.append("\">")
               .append(PMD.EOL);
            StringUtil.appendXmlEscaped(buf, rv.getDescription());

            buf.append(PMD.EOL);
            buf.append("</violation>");
            buf.append(PMD.EOL);
        }
        if (filename != null) { // Not first file ?
            buf.append("</file>");
        }

        // errors
        for (Iterator i = report.errors(); i.hasNext();) {
            Report.ProcessingError pe = (Report.ProcessingError) i.next();
            buf.append(PMD.EOL)
               .append("<error ")
               .append(PMD.EOL)
               .append("filename=\"");
            StringUtil.appendXmlEscaped(buf, pe.getFile());
            buf.append("\" msg=\"");
            StringUtil.appendXmlEscaped(buf, pe.getMsg());
            buf.append("\">")
               .append(PMD.EOL)
               .append("/>")
               .append(PMD.EOL);
        }

        buf.append("</pmd>");
        return buf.toString();
    }

}
