/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;
import static net.sourceforge.pmd.properties.PropertyFactory.conventionalEnumProperty;

import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class SortVariableDeclarationsAtStartOfBlockRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<SortBy> SORT_BY =
            conventionalEnumProperty("sortBy", SortBy.class)
                    .desc("Enforce lexicographic sorting of variable declarations. When sorting by type declarations of the same type will be ordered by name.")
                    .defaultValue(SortBy.NAME)
                    .build();

    private static final PropertyDescriptor<Boolean> CASE_SENSITIVE_SORTING =
            PropertyFactory.booleanProperty("caseSensitiveSorting")
                    .desc("Use case sensitive sorting")
                    .defaultValue(false)
                    .build();

    private static final PropertyDescriptor<Boolean> IGNORE_FIELD_DECLARATIONS =
            PropertyFactory.booleanProperty("ignoreFieldDeclarations")
                    .desc("Do not check the ordering of field declarations.")
                    .defaultValue(false)
                    .build();

    private static final PropertyDescriptor<Boolean> IGNORE_LOCAL_VARIABLE_DECLARATIONS =
            PropertyFactory.booleanProperty("ignoreLocalVariableDeclarations")
                    .desc("Do not check the ordering of local variable declarations.")
                    .defaultValue(false)
                    .build();


    private enum SortBy {
        NAME, TYPE
    }

    public SortVariableDeclarationsAtStartOfBlockRule() {
        super(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        return data;
    }
}
