package net.sourceforge.pmd;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * Writes an XML file containing information about rule set and each rule within the rule set.
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetWriter
{

    private String m_fileName;
    private PrintWriter m_writer;
    private StringBuffer m_line = new StringBuffer(500);
    private int m_indent;

    /**
     ******************************************************************************
     *
     * @param fileName
     *
     * @throws FileNotFoundException
     */
    public RuleSetWriter(String fileName)
        throws FileNotFoundException
    {
        try
        {
            m_fileName = fileName;
            m_writer = new PrintWriter(new FileOutputStream(fileName));

            m_line.append("<?xml version=\"1.0\" ?>");
        }
        catch (FileNotFoundException exception)
        {
            String message;

            message = "Could not create file \""
                    + fileName
                    + "\".  The file path may be incorrect.";
            exception = new FileNotFoundException(message);

            throw exception;
        }
    }

    /**
     *******************************************************************************
     *
     * @param ruleSet
     */
    public void write(RuleSet ruleSet)
    {
        // <ruleset name="xxxxxx" >
        indent();
        setupNewLine();
        m_line.append("<ruleset name=\"");
        m_line.append(ruleSet.getName());
        m_line.append("\" >");
        writeLine();

        // <description>
        //    xxxxxxxxx
        // </description>
        indent();
        writeDescription(ruleSet.getDescription());
        outdent();

        // </ruleset>
        setupNewLine();
        m_line.append("</ruleset>");
        writeLine();
        outdent();
    }

    /**
     *******************************************************************************
     *
     * @param rule
     */
    public void write(Rule rule)
    {
        // Write a blank line to separate rules for easier reading.
        m_writer.write("");

        // <rule name="xxxxxx"
        indent();
        setupNewLine();
        m_line.append("<rule name=\"");
        m_line.append(rule.getName());
        m_line.append('"');
        writeLine();

        // message="xxxxxx"
        m_indent += 6;
        setupNewLine();
        m_line.append("message=\"");
        m_line.append(rule.getMessage());
        m_line.append('"');
        writeLine();

        // class="xxxx"
        setupNewLine();
        m_line.append("class=\"");
        m_line.append(rule.getClass().getName());
        m_line.append('"');
        writeLine();

        // include="yes"
        setupNewLine();
        m_line.append("include=\"");
        m_line.append(rule.isInclude() ? "yes" : "no");
        m_line.append("\" >");
        writeLine();
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

        // <properties>
        //    <property name="xxxx" value="yyyyy" />
        // </properties>
        indent();
        writeProperties(rule);
        outdent();

        // </ruleset>
        setupNewLine();
        m_line.append("</rule>");
        writeLine();
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
        writeLine();

        {
            // xxxxxxxx
            indent();

            for (int n = 0; n < description.length(); n += 50)
            {
                setupNewLine();
                m_line.append(description.substring(n, n + 50));
                writeLine();
            }

            outdent();
        }

        // </description>
        setupNewLine();
        m_line.append("</description");
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
        writeLine();

        {
            // xxxxxxxx
            indent();

            int beginIndex = 0;
            int endIndex = example.indexOf('\n');

            while (beginIndex < example.length())
            {
                setupNewLine();
                m_line.append(example.substring(beginIndex, endIndex));
                writeLine();

                beginIndex = endIndex + 1;
                endIndex = example.indexOf('\n', beginIndex);
            }

            outdent();
        }

        // </description>
        setupNewLine();
        m_line.append("</description");
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
        m_line.append("properties");
        writeLine();

        Map properties = rule.getProperties();
        Iterator iterator = properties.keySet().iterator();

        while (iterator.hasNext())
        {
            String propertyName = (String) iterator.next();
            String propertyValue = (String) properties.get(propertyName);

            // <property name="xxxxx" value="yyyyy" />
            indent();
            setupNewLine();
            m_line.append("<property name=\"");
            m_line.append(propertyName);
            m_line.append("\" value=\"");
            m_line.append(propertyValue);
            m_line.append("\" >");
        }
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
    private void writeLine()
    {
        m_line.append('\n');
        m_writer.write(m_line.toString());
    }

    /**
     *******************************************************************************
     *
     */
    public void finished()
    {
        m_writer.close();
    }
}