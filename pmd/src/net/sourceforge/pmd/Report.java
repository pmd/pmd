package net.sourceforge.pmd;

import java.util.*;

public class Report {
    
    private List violations = new ArrayList();
    private String filename;
    
    public Report(String filename) {
        this.filename = filename;
    }
    
    public void addRuleViolation(RuleViolation violation) {
       violations.add(violation); 
    }
    
    public String renderToText() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = violations.iterator(); i.hasNext();) {
            if (buf.length() != 0) {
                buf.append(System.getProperty("line.separator"));
            }
            buf.append(filename + ":" + ((RuleViolation)i.next()).getText());
        }
        return buf.toString();
    }

    public String renderToXML() {
        StringBuffer buf = new StringBuffer();
        for (Iterator iterator = violations.iterator(); iterator.hasNext();) {
            if (buf.length() != 0) {
                buf.append(System.getProperty("line.separator"));
            }
            RuleViolation violation = (RuleViolation) iterator.next();
            buf.append("<ruleviolation>");
            buf.append("<file>" + filename + "</file>");
            buf.append(violation.getXML());
            buf.append("</ruleviolation>");
        }
        return "<pmd>" + System.getProperty("line.separator") + buf.toString() + System.getProperty("line.separator") + "</pmd>";
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
