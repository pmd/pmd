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

public class AvoidTabCharacterRule extends AbstractPLSQLRule {

    private static final PropertyDescriptor<Boolean> EACH_LINE = PropertyFactory.booleanProperty("eachLine")
            .desc("Whether to report each line with a tab character or only the first line")
            .defaultValue(false)
            .build();

    public AvoidTabCharacterRule() {
        definePropertyDescriptor(EACH_LINE);
        addRuleChainVisit(ASTInput.class);
    }

    @Override
    public Object visit(ASTInput node, Object data) {
        boolean eachLine = getProperty(EACH_LINE);

        try (BufferedReader in = new BufferedReader(new StringReader(node.getSourcecode()))) {
            String line;
            int lineNumber = 0;
            while ((line = in.readLine()) != null) {
                lineNumber++;
                if (line.indexOf('\t') != -1) {
                    addViolationWithMessage(data, null, "Tab characters are not allowed. Use spaces for indentation",
                            lineNumber, lineNumber);

                    if (!eachLine) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while executing rule AvoidTabCharacter", e);
        }
        return data;
    }
}
