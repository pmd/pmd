package net.sourceforge.pmd;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Writes an XML file containing information about rule set and each rule within the rule set.
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetWriter
{

    private PrintStream m_outputStream;
    private StringBuffer m_line = new StringBuffer(500);
    private int m_indent;

    /**
     *******************************************************************************
     *
     * @param outputStream
     */
    public RuleSetWriter(OutputStream outputStream)
    {
        m_outputStream = new PrintStream(outputStream);

        m_line.append("<?xml version=\"1.0\" ?>");
        outputLine();
    }

    /**
     *******************************************************************************
     *
     * @param ruleSet
     */
    public void write(RuleSet ruleSet)
    {
        // <ruleset name="xxxxxx"
        indent();
        setupNewLine();
        m_line.append("<ruleset name=\"");
        m_line.append(ruleSet.getName());
        m_line.append('"');

        //
        // include="true">
        //
        m_line.append(" include=\"");
        m_line.append(ruleSet.include() ? "true" : "false");
        m_line.append("\">");
        outputLine();

        // <description>
        //    xxxxxxxxx
        // </description>
        indent();
        writeDescription(ruleSet.getDescription());
        outdent();

        //
        // Write each rule.
        //
        Iterator rules = ruleSet.getRules().iterator();

        while (rules.hasNext())
        {
            write((Rule) rules.next());
        }

        // </ruleset>
        setupNewLine();
        m_line.append("</ruleset>");
        outputLine();
        outdent();
    }

    /**
     *******************************************************************************
     *
     * @param rule
     */
    private void write(Rule rule)
    {
        // Write a blank line to separate rules for easier reading.
        m_outputStream.println("");

        // <rule name="xxxxxx"
        indent();
        setupNewLine();
        m_line.append("<rule name=\"");
        m_line.append(rule.getName());
        m_line.append('"');
        outputLine();

        // message="xxxxxx"
        m_indent += 6;
        setupNewLine();
        m_line.append("message=\"");
        m_line.append(rule.getMessage());
        m_line.append('"');
        outputLine();

        // class="xxxx"
        setupNewLine();
        m_line.append("class=\"");
        m_line.append(rule.getClass().getName());
        m_line.append('"');
        outputLine();

        // include="true">
        setupNewLine();
        m_line.append("include=\"");
        m_line.append(rule.include() ? "true" : "false");
        m_line.append("\">");
        outputLine();
        m_indent -= 6;

        // <description>
        //    xxxxxxxxx
        // </description>
        indent();
        writeDescription(rule.getDescription());
        outdent();

        // <example>
        //    xxxxxxxxxxx
        // </example>
        indent();
        writeExample(rule.getExample());
        outdent();

        // <priority>
        //   xxx
        // </priority>
        indent();
        writePriority(rule.getPriority());
        outdent();

        // <properties>
        //    <property name="xxxx" value="yyyyy" />
        // </properties>
        indent();
        writeProperties(rule);
        outdent();

        // </ruleset>
        setupNewLine();
        m_line.append("</rule>");
        outputLine();
        outdent();
    }

    /**
     *******************************************************************************
     *
     * @param description
     */
    private void writeDescription(String description)
    {
        // <description>
        setupNewLine();
        m_line.append("<description>");
        outputLine();

        {
            // xxxxxxxx
            indent();
            setupNewLine();
            m_line.append(description);
            outputLine();
            outdent();
        }

        // </description>
        setupNewLine();
        m_line.append("</description>");
        outputLine();
    }

    /**
     *******************************************************************************
     *
     * @param example
     */
    private void writeExample(String example)
    {
        // <example>
        setupNewLine();
        m_line.append("<example>");
        outputLine();

        {
            // xxxxxxxx
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
        }

        // </description>
        setupNewLine();
        m_line.append("</example>");
        outputLine();
    }

    /**
     *******************************************************************************
     *
     * @param priority
     */
    private void writePriority(int priority)
    {
        // <priority>
        setupNewLine();
        m_line.append("<priority>");
        outputLine();

        {
            // xx
            indent();
            setupNewLine();
            m_line.append(String.valueOf(priority));
            outputLine();
            outdent();
        }

        // </priority>
        setupNewLine();
        m_line.append("</priority>");
        outputLine();
    }

    /**
     *******************************************************************************
     *
     * @param rule
     */
    private void writeProperties(Rule rule)
    {
        // <properties>
        setupNewLine();
        m_line.append("<properties>");
        outputLine();
        indent();

        RuleProperties properties = rule.getProperties();
        Enumeration keys = properties.keys();

        while (keys.hasMoreElements())
        {
            String name = (String) keys.nextElement();
            String value = properties.getValue(name);
            String valueType = properties.getValueType(name);

            // <property name="xxxxx" value="yyyyy" />
            setupNewLine();
            m_line.append("<property name=\"");
            m_line.append(name);
            m_line.append("\" value=\"");
            m_line.append(value);
            m_line.append("\" type=\"");
            m_line.append(valueType);
            m_line.append("\"/>");
            outputLine();
        }

        outdent();
        setupNewLine();
        m_line.append("</properties>");
        outputLine();
    }

    /**
     *******************************************************************************
     *
     */
    private void indent()
    {
        m_indent += 3;
    }


    /**
     *******************************************************************************
     *
     */
    private void outdent()
    {
        m_indent -= 3;
    }

    /**
     *******************************************************************************
     *
     */
    private void setupNewLine()
    {
        m_line.setLength(0);

        for (int n = 0; n < m_indent; n++)
        {
            m_line.append(' ');
        }
    }

    /**
     *******************************************************************************
     *
     */
    private void setupNewLineWithoutIndent()
    {
        m_line.setLength(0);
    }

    /**
     *******************************************************************************
     *
     */
    private void outputLine()
    {
        m_outputStream.println(m_line.toString());
    }
}