/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:11:11 PM
 */
package net.sourceforge.pmd.reports;

import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public class XMLReport extends AbstractReport {

    public String render() {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?><pmd>" + System.getProperty("line.separator"));
        for (Iterator i = super.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append("<ruleviolation>" + System.getProperty("line.separator"));
            buf.append("<file>" + rv.getFilename() + "</file>" + System.getProperty("line.separator"));
            buf.append("<line>" + Integer.toString(rv.getLine()) + "</line>" + System.getProperty("line.separator"));
            buf.append("<description>" + rv.getDescription() + "</description>" + System.getProperty("line.separator"));
            buf.append("</ruleviolation>");
            buf.append(System.getProperty("line.separator"));
        }
        buf.append("</pmd>");
        return buf.toString();
    }

}
