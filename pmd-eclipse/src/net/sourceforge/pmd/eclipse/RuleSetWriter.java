package net.sourceforge.pmd.eclipse;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

/**
 * Generate an XML rule set file from a rule set
 * This class is a rewritting of the original from PMD engine
 * that doesn't support xpath properties !
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/10/14 21:26:32  phherlin
 * Upgrading to PMD 1.2.2
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RuleSetWriter {
    private PrintStream m_outputStream = null;
    private StringBuffer m_line = null;
    private int m_indent = 0;

    /**
     * Constructor
     */
    public RuleSetWriter(OutputStream outputStream)
    {
        m_line = new StringBuffer(500);
        m_outputStream = new PrintStream(outputStream);
        m_line.append("<?xml version=\"1.0\" ?>");
        outputLine();
    }

    public void write(RuleSet ruleSet)
    {
        indent();
        setupNewLine();
        m_line.append("<ruleset name=\"");
        m_line.append(ruleSet.getName());
        m_line.append('"');
        m_line.append(" include=\"");
        m_line.append(ruleSet.include() ? "true" : "false");
        m_line.append("\">");
        outputLine();
        indent();
        writeDescription(ruleSet.getDescription());
        outdent();
        for(Iterator rules = ruleSet.getRules().iterator(); rules.hasNext(); write((Rule)rules.next()));
        setupNewLine();
        m_line.append("</ruleset>");
        outputLine();
        outdent();
    }

    private void write(Rule rule)
    {
        m_outputStream.println("");
        indent();
        setupNewLine();
        m_line.append("<rule name=\"");
        m_line.append(rule.getName());
        m_line.append('"');
        outputLine();
        m_indent += 6;
        setupNewLine();
        m_line.append("message=\"");
        m_line.append(rule.getMessage());
        m_line.append('"');
        outputLine();
        setupNewLine();
        m_line.append("class=\"");
        m_line.append(rule.getClass().getName());
        m_line.append('"');
        outputLine();
        setupNewLine();
        m_line.append("include=\"");
        m_line.append(rule.include() ? "true" : "false");
        m_line.append("\">");
        outputLine();
        m_indent -= 6;
        indent();
        writeDescription(rule.getDescription());
        outdent();
        indent();
        writeExample(rule.getExample());
        outdent();
        indent();
        writePriority(rule.getPriority());
        outdent();
        indent();
        writeProperties(rule);
        outdent();
        setupNewLine();
        m_line.append("</rule>");
        outputLine();
        outdent();
    }

    private void writeDescription(String description)
    {
        setupNewLine();
        m_line.append("<description>");
        outputLine();
        indent();
        setupNewLine();
        m_line.append(description);
        outputLine();
        outdent();
        setupNewLine();
        m_line.append("</description>");
        outputLine();
    }

    private void writeExample(String example)
    {
        setupNewLine();
        m_line.append("<example>");
        outputLine();
        indent();
        setupNewLineWithoutIndent();
        m_line.append("<![CDATA[");
        outputLine();
        setupNewLineWithoutIndent();
        m_line.append(example);
        outputLine();
        setupNewLineWithoutIndent();
        m_line.append("]]>");
        outputLine();
        outdent();
        setupNewLine();
        m_line.append("</example>");
        outputLine();
    }

    private void writePriority(int priority)
    {
        setupNewLine();
        m_line.append("<priority>");
        outputLine();
        indent();
        setupNewLine();
        m_line.append(String.valueOf(priority));
        outputLine();
        outdent();
        setupNewLine();
        m_line.append("</priority>");
        outputLine();
    }

    private void writeProperties(Rule rule)
    {
        setupNewLine();
        m_line.append("<properties>");
        outputLine();
        indent();
        Properties properties = rule.getProperties();
        for(Enumeration keys = properties.keys(); keys.hasMoreElements(); outputLine())
        {
            String name = (String)keys.nextElement();
            String value = properties.getProperty(name);
            setupNewLine();
            m_line.append("<property name=\"");
            m_line.append(name);
            m_line.append("\"");
            
            // considering xpath property to be output in CDATA format
            if (name.equals("xpath")) {
                m_line.append(">");
                outputLine();
                indent();
                writeValue(value);
                outdent();
                setupNewLine();
                m_line.append("</property>");
            } else {
                m_line.append(" value=\"");
                m_line.append(value);
                m_line.append("\"/>");
            }
            
        }

        outdent();
        setupNewLine();
        m_line.append("</properties>");
        outputLine();
    }

    private void writeValue(String value)
    {
        setupNewLine();
        m_line.append("<value>");
        outputLine();
        indent();
        setupNewLineWithoutIndent();
        m_line.append("<![CDATA[");
        outputLine();
        setupNewLineWithoutIndent();
        m_line.append(value);
        outputLine();
        setupNewLineWithoutIndent();
        m_line.append("]]>");
        outputLine();
        outdent();
        setupNewLine();
        m_line.append("</value>");
        outputLine();
    }

    private void indent()
    {
        m_indent += 3;
    }

    private void outdent()
    {
        m_indent -= 3;
    }

    private void setupNewLine()
    {
        m_line.setLength(0);
        for(int n = 0; n < m_indent; n++)
            m_line.append(' ');

    }

    private void setupNewLineWithoutIndent()
    {
        m_line.setLength(0);
    }

    private void outputLine()
    {
        m_outputStream.println(m_line.toString());
    }

}
