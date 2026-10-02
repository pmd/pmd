/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JTypeVisitor;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.0.0 (as XPath)
 * @since 7.28.0 (as Java rule)
 */
public class UseExplicitTypesRule extends AbstractJavaRulechainRule {
    private static final PropertyDescriptor<Boolean> ALLOW_LITERALS = PropertyFactory
            .booleanProperty("allowLiterals")
            .desc("Allow when variables are directly initialized with literals")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Boolean> ALLOW_CTORS = PropertyFactory
            .booleanProperty("allowCtors")
            .desc("Allow when variables are directly initialized with a constructor call")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Boolean> ALLOW_CASTS = PropertyFactory
            .booleanProperty("allowCasts")
            .desc("Allow when variables are directly initialized with a the result of a cast")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Boolean> ALLOW_LOOP_VARIABLE = PropertyFactory
            .booleanProperty("allowLoopVariable")
            .desc("Allow when variables are used as loop variables in enhanced for loops")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Integer> ALLOW_LONG_TYPE_NAMES = PropertyFactory
            .intProperty("allowLongTypeNames")
            .desc("Allow when the type name would be longer than ...")
            .defaultValue(Integer.MAX_VALUE)
            .build();

    public UseExplicitTypesRule() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(ALLOW_LITERALS);
        definePropertyDescriptor(ALLOW_CTORS);
        definePropertyDescriptor(ALLOW_CASTS);
        definePropertyDescriptor(ALLOW_LOOP_VARIABLE);
        definePropertyDescriptor(ALLOW_LONG_TYPE_NAMES);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (!node.isTypeInferred()) {
            return null;
        }

        if (getProperty(ALLOW_LITERALS) && node.children(ASTVariableDeclarator.class).descendants(ASTLiteral.class).nonEmpty()) {
            return null;
        }
        if (getProperty(ALLOW_CTORS) && node.children(ASTVariableDeclarator.class).children(ASTConstructorCall.class).nonEmpty()) {
            return null;
        }
        if (getProperty(ALLOW_CASTS) && node.children(ASTVariableDeclarator.class).children(ASTCastExpression.class).nonEmpty()) {
            return null;
        }
        if (getProperty(ALLOW_LOOP_VARIABLE) && node.getParent() instanceof ASTForeachStatement) {
            return null;
        }

        int requiredLongTypeNamesLength = getProperty(ALLOW_LONG_TYPE_NAMES);

        // note: var declarations have exactly one varId
        ASTVariableId firstVarId = node.getVarIds().first();
        assert firstVarId != null : "Invalid java syntax? the local var declaration should have one varId";
        JTypeMirror typeMirror = firstVarId.getTypeMirror();

        List<@NonNull JClassSymbol> enclosingTypeSymbols = node.ancestors(ASTClassDeclaration.class)
                .toStream()
                .map(ASTClassDeclaration::getTypeMirror)
                .map(JClassType::getSymbol)
                .collect(Collectors.toList());
        SimpleNameVisitor visitor = new SimpleNameVisitor(enclosingTypeSymbols);

        StringBuilder sb = new StringBuilder();
        typeMirror.acceptVisitor(visitor, sb);
        String typeName = sb.toString();

        boolean allowLongTypeNames = requiredLongTypeNamesLength < Integer.MAX_VALUE;
        if (allowLongTypeNames && typeName.length() >= requiredLongTypeNamesLength) {
            return null;
        }

        if (allowLongTypeNames) {
            ctx.addViolationWithMessage(node, "The explicit type ''{0}'' is not long enough (< {1}) to justify the use of var",
                    typeName, requiredLongTypeNamesLength);
        } else {
            ctx.addViolation(node);
        }

        return null;
    }

    private static class SimpleNameVisitor implements JTypeVisitor<Void, StringBuilder> {
        private final List<JClassSymbol> enclosingTypeSymbols;

        private SimpleNameVisitor(List<JClassSymbol> enclosingTypeSymbols) {
            this.enclosingTypeSymbols = enclosingTypeSymbols;
        }

        @Override
        public Void visit(JTypeMirror t, StringBuilder stringBuilder) {
            return null;
        }

        @Override
        public Void visitClass(JClassType classType, StringBuilder sb) {
            JClassSymbol symbol = classType.getSymbol();
            JClassSymbol enclosingClass = symbol.getEnclosingClass();
            if (enclosingClass != null && !enclosingTypeSymbols.contains(enclosingClass)) {
                sb.append(enclosingClass.getSimpleName());
                sb.append(".");
            }
            sb.append(symbol.getSimpleName());

            List<JTypeMirror> typeArgs = classType.getTypeArgs();
            if (!typeArgs.isEmpty()) {
                sb.append("<");
                for (int i = 0; i < typeArgs.size(); i++) {
                    typeArgs.get(i).acceptVisitor(this, sb);
                    if (i != typeArgs.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(">");
            }
            return null;
        }

        @Override
        public Void visitPrimitive(JPrimitiveType t, StringBuilder sb) {
            sb.append(t.getSimpleName());
            return null;
        }

        @Override
        public Void visitTypeVar(JTypeVar t, StringBuilder sb) {
            sb.append(t.getName());
            return null;
        }

        @Override
        public Void visitWildcard(JWildcardType wildcardType, StringBuilder sb) {
            sb.append("?");
            if (wildcardType.isUpperBound()) {
                sb.append(" extends ");
            } else {
                sb.append(" super ");
            }
            wildcardType.getBound().acceptVisitor(this, sb);
            return null;
        }
    }
}
