package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

import java.io.StringWriter;
import java.util.Iterator;

/**
 *
 * Converts the violations list into a text string for viewing.
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class TextRenderer {

    private boolean m_reportNoViolations;
    private StringWriter m_writer;

    /**
     *******************************************************************************
     *
     */
    protected void beginRendering(boolean reportNoViolations) {
        m_reportNoViolations = reportNoViolations;
        m_writer = new StringWriter(25000);
    }

    /**
     *******************************************************************************
     *
     * @return Results text.
     */
    protected String endRendering() {
        return m_writer.toString();
    }

    /**
     *******************************************************************************
     *
     * @param report
     */
    public void render(String fileName, Report report) {
        Iterator violations = report.iterator();

        if (violations.hasNext() == false) {
            if (m_reportNoViolations) {
                m_writer.write("Source File: ");
                m_writer.write(fileName);
                m_writer.write('\n');
                m_writer.write("\nNo rule violations detected.");
            }

            return;
        }

        m_writer.write("Source File: ");
        m_writer.write(fileName);
        m_writer.write('\n');

        while (violations.hasNext()) {
            RuleViolation ruleViolation = (RuleViolation) violations.next();
            Rule rule = ruleViolation.getRule();

            //
            // Line Number
            //
            m_writer.write('\n');
            m_writer.write("Line: ");
            m_writer.write(ruleViolation.getLine());
            m_writer.write('\n');

            //
            // Rule Message
            //
            String ruleMessage = ruleViolation.getDescription();

            if (ruleMessage == null) {
                ruleMessage = "";
            } else {
                ruleMessage = ruleMessage.replace('\n', ' ').trim();
            }

            m_writer.write("Rule: ");
            m_writer.write(ruleMessage);
            m_writer.write('\n');

            //
            // Rule Priority
            //
            m_writer.write("Rule Priority: ");
            m_writer.write(rule.getPriorityName());
            m_writer.write('\n');

            //
            // Rule Description
            //
            String description = rule.getDescription();

            if (description == null) {
                description = "";
            } else {
                description = description.replace('\n', ' ').trim();
            }

            m_writer.write("Description: ");
            m_writer.write(description);
            m_writer.write('\n');

            //
            // Rule Example
            //
            String example = rule.getExample();

            if ((example != null) && (example.length() > 0)) {
                m_writer.write("Example: ");
                m_writer.write(example);
                m_writer.write('\n');
            }
        }

        //
        // Space separation between rules.
        //
        m_writer.write("\n\n\n");
    }
}