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
