package net.sourceforge.pmd;

import java.util.*;

public class Report {
    
    private List violations = new ArrayList();
    private String filename;
    private String format;

    public Report(String filename, String format) {
        this.filename = filename;
        this.format = format;
    }
    
    public void addRuleViolation(RuleViolation violation) {
       violations.add(violation); 
    }

    public String render() {
        if (format.equals("text")) {
            return renderToText();
        }
        return renderToXML();
    }

    private String renderToText() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = violations.iterator(); i.hasNext();) {
            if (buf.length() != 0) {
                buf.append(System.getProperty("line.separator"));
            }
            buf.append(filename + ":" + ((RuleViolation)i.next()).getText());
        }
        return buf.toString();
    }

    private String renderToXML() {
        StringBuffer buf = new StringBuffer("<pmd>" + System.getProperty("line.separator") + "<file>" + System.getProperty("line.separator") + "<name>" + filename + "</name>" + System.getProperty("line.separator"));
        for (Iterator iterator = violations.iterator(); iterator.hasNext();) {
            buf.append(((RuleViolation) iterator.next()).getXML());
        }
        buf.append(System.getProperty("line.separator") + "</file>" + System.getProperty("line.separator"));
        buf.append("</pmd>");
        return buf.toString();
    }

    public boolean empty() {
        return violations.size() == 0;
    }

    public Iterator iterator() {
        return violations.iterator();
    }

    public int getSize() {
        return violations.size();
    }
}
