/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.PSet;

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
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JTypeVisitable;
import net.sourceforge.pmd.lang.java.types.JTypeVisitor;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
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

        StringBuilder sb = typeMirror.acceptVisitor(visitor, new StringBuilder());
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

    /**
     * This type visitor is very similar to {@link TypePrettyPrint#prettyPrintWithSimpleNames(JTypeVisitable)}
     * with the following differences:
     * <ul>
     *     <li>For nested types, display the enclosing type in canonical format (with dots) unless the nested type
     *         is defined within the same compilation unit and therefore doesn't need to be qualified.</li>
     *     <li>TypePrettyPrint would display only the simple name of the nested type (e.g. {@code Entry} instead
     *         of {@code Map.Entry})</li>
     * </ul>
     */
    static class SimpleNameVisitor implements JTypeVisitor<StringBuilder, StringBuilder> {
        private final List<JClassSymbol> enclosingTypeSymbols;

        /**
         * @param enclosingTypeSymbols List of type symbols, which are in scope. Nested types of these don't need
         *                             to be qualified.
         */
        SimpleNameVisitor(List<JClassSymbol> enclosingTypeSymbols) {
            this.enclosingTypeSymbols = enclosingTypeSymbols;
        }

        @Override
        public StringBuilder visit(JTypeMirror t, StringBuilder sb) {
            return sb;
        }

        @Override
        public StringBuilder visitClass(JClassType classType, StringBuilder sb) {
            PSet<SymbolicValue.SymAnnot> typeAnnotations = classType.getTypeAnnotations();
            if (typeAnnotations != null) {
                for (SymbolicValue.SymAnnot annot : typeAnnotations) {
                    sb.append("@").append(annot.getSimpleName()).append(" ");
                }
            }

            JClassSymbol symbol = classType.getSymbol();
            JClassSymbol enclosingClass = symbol.getEnclosingClass();
            List<String> enclosingNames = new ArrayList<>();
            while (enclosingClass != null) {
                if (!enclosingTypeSymbols.contains(enclosingClass)) {
                    enclosingNames.add(enclosingClass.getSimpleName());
                }
                enclosingClass = enclosingClass.getEnclosingClass();
            }
            if (!enclosingNames.isEmpty()) {
                sb.append(String.join(".", enclosingNames));
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
            return sb;
        }

        @Override
        public StringBuilder visitPrimitive(JPrimitiveType t, StringBuilder sb) {
            sb.append(t.getSimpleName());
            return sb;
        }

        @Override
        public StringBuilder visitTypeVar(JTypeVar t, StringBuilder sb) {
            sb.append(t.getName());
            return sb;
        }

        @Override
        public StringBuilder visitWildcard(JWildcardType wildcardType, StringBuilder sb) {
            sb.append("?");
            if (wildcardType.isUpperBound()) {
                sb.append(" extends ");
            } else {
                sb.append(" super ");
            }
            wildcardType.getBound().acceptVisitor(this, sb);
            return sb;
        }
    }
}
