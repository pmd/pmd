/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.codestyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.constraints.NumericConstraints;

public class LineLengthRule extends AbstractPLSQLRule {

    private static final PropertyDescriptor<Integer> MAX_LINE_LENGTH = PropertyFactory.intProperty("maxLineLength")
            .desc("The maximum allowed line length")
            .defaultValue(80)
            .require(NumericConstraints.inRange(10, 200))
            .build();
    private static final PropertyDescriptor<Boolean> EACH_LINE = PropertyFactory.booleanProperty("eachLine")
            .desc("Whether to report each line that is longer only the first line")
            .defaultValue(false)
            .build();

    public LineLengthRule() {
        definePropertyDescriptor(MAX_LINE_LENGTH);
        definePropertyDescriptor(EACH_LINE);
        addRuleChainVisit(ASTInput.class);
    }

    @Override
    public Object visit(ASTInput node, Object data) {
        boolean eachLine = getProperty(EACH_LINE);
        int maxLineLength = getProperty(MAX_LINE_LENGTH);

        try (BufferedReader in = new BufferedReader(new StringReader(node.getSourcecode()))) {
            String line;
            int lineNumber = 0;
            while ((line = in.readLine()) != null) {
                lineNumber++;
                if (line.length() > maxLineLength) {
                    addViolationWithMessage(data, null, "The line is too long. Only " + maxLineLength + " characters are allowed.",
                            lineNumber, lineNumber);

                    if (!eachLine) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while executing rule LineLengthRule", e);
        }
        return data;
    }
}
