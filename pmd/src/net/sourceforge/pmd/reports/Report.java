package net.sourceforge.pmd.reports;

import net.sourceforge.pmd.RuleViolation;

import java.util.*;

public class Report {

    private List violations = new ArrayList();
    private String format;

    public Report(String format) {
        this.format = format;
    }

    public void addRuleViolation(RuleViolation violation) {
        violations.add(violation);
    }

    public String render() {
        if (format.equals("xml")) {
            return renderToXML();
        }
        return renderToHTML();
    }

    private String renderToXML() {
        StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?><pmd>" + System.getProperty("line.separator"));
        for (Iterator i = violations.iterator(); i.hasNext();) {
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

    private String renderToHTML() {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + System.getProperty("line.separator")+ "<table><tr>" + System.getProperty("line.separator")+ "<th>File</th><th>Line</th><th>Problem</th></tr>" + System.getProperty("line.separator"));
        for (Iterator i = violations.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append("<tr>" + System.getProperty("line.separator")+ "<td>" + rv.getFilename() + "</td>" + System.getProperty("line.separator"));
            buf.append("<td>" + Integer.toString(rv.getLine()) + "</td>" + System.getProperty("line.separator"));
            buf.append("<td>" + rv.getDescription() + "</td>" + System.getProperty("line.separator"));
            buf.append("</tr>" + System.getProperty("line.separator"));
        }
        buf.append("</table></body></html>");
        return buf.toString();
    }

    public boolean isEmpty() {
        return violations.isEmpty();
    }

    public Iterator iterator() {
        return violations.iterator();
    }

    public int size() {
        return violations.size();
    }
}
