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
        } else if (format.equals("xml")) {
            return renderToXML();
        }
        return renderToHTML();
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
                if (buf.length() != 0) {
                    buf.append(System.getProperty("line.separator"));
                }
                buf.append(filename + ":" + rv.getText());
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

    private String renderToHTML() {
        StringBuffer buf = new StringBuffer("<html><head><title>PMD</title></head><body>" + System.getProperty("line.separator")+ "<table><tr>" + System.getProperty("line.separator")+ "<th>File</th><th>Line</th><th>Problem</th></tr>" + System.getProperty("line.separator"));
        for (Iterator i = fileToViolationsMap.keySet().iterator(); i.hasNext();) {
            String filename = (String)i.next();
            List violations = (List)fileToViolationsMap.get(filename);
            if (violations.isEmpty()) {
                continue;
            }
            for (Iterator iterator = violations.iterator(); iterator.hasNext();) {
                RuleViolation rv = (RuleViolation) iterator.next();
                buf.append("<tr>" + System.getProperty("line.separator")+ "<td>" + filename + "</td>" + System.getProperty("line.separator"));
                buf.append(rv.getHTML());
                buf.append("</tr>" + System.getProperty("line.separator"));
            }
        }
        buf.append("</table></body></html>");
        return buf.toString();
    }

    public boolean isEmpty() {
        return fileToViolationsMap.isEmpty();
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
