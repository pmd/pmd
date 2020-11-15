/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.codestyle;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.document.Chars;

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

        int lineNumber = 1;
        for (Chars line : node.getText().lines()) {
            if (line.indexOf('\t', 0) != -1) {
                addViolationWithMessage(data, node,
                                        "Tab characters are not allowed. Use spaces for indentation",
                                        lineNumber, lineNumber);

                if (!eachLine) {
                    break;
                }
            }
            lineNumber++;
        }
        return data;
    }
}
