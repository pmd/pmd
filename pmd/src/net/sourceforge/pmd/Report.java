package net.sourceforge.pmd;

import java.util.*;

public class Report {

    private Map fileToViolationsMap = new HashMap();
    private String currentFile;
    private String format;

    public Report(String format, String name) {
        this.format = format;
        setCurrentFile(name);
    }

    public Report(String format) {
        this.format = format;
    }

    public void setCurrentFile(String name) {
        fileToViolationsMap.put(name, new ArrayList());
        currentFile = name;
    }

    public void addRuleViolation(RuleViolation violation) {
        List violations = (List)fileToViolationsMap.get(currentFile);
        violations.add(violation);
        fileToViolationsMap.put(currentFile, violations);
    }

    public String render() {
        if (format.equals("text")) {
            return renderToText();
        }
        return renderToXML();
    }

    private String renderToText() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = fileToViolationsMap.keySet().iterator(); i.hasNext();) {
            String filename = (String)i.next();
            List violations = (List)fileToViolationsMap.get(filename);
            if (violations.isEmpty()) {
                continue;
            }
            for (Iterator iterator = violations.iterator(); iterator.hasNext();) {
                RuleViolation rv = (RuleViolation) iterator.next();
                buf.append(filename + ":" + rv.getText());
                buf.append(System.getProperty("line.separator"));
            }
        }
        return buf.toString();
    }

    private String renderToXML() {
        StringBuffer buf = new StringBuffer("<pmd>" + System.getProperty("line.separator"));
        for (Iterator i = fileToViolationsMap.keySet().iterator(); i.hasNext();) {
            String filename = (String)i.next();
            List violations = (List)fileToViolationsMap.get(filename);
            if (violations.isEmpty()) {
                continue;
            }
            buf.append("<file>" + System.getProperty("line.separator") + "<name>" + filename + "</name>" + System.getProperty("line.separator"));
            for (Iterator iterator = violations.iterator(); iterator.hasNext();) {
                RuleViolation rv = (RuleViolation) iterator.next();
                buf.append(rv.getXML());
                buf.append(System.getProperty("line.separator"));
            }
            buf.append("</file>" + System.getProperty("line.separator"));
        }
        buf.append("</pmd>");
        return buf.toString();
    }

    public boolean isEmpty() {
        return this.fileToViolationsMap.isEmpty();
    }

    public boolean currentFileHasNoViolations() {
        return ((List)fileToViolationsMap.get(currentFile)).isEmpty();
    }

    public Iterator violationsInCurrentFile() {
        return ((List)fileToViolationsMap.get(currentFile)).iterator();
    }

    public int countViolationsInCurrentFile() {
        return ((List)fileToViolationsMap.get(currentFile)).size();
    }
}
