/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class AvoidVarForShortTypeRule extends AbstractJavaRulechainRule {
    private static final PropertyDescriptor<Integer> LIMIT_LENGTH_DESCRIPTOR =
        PropertyFactory.intProperty("lengthLimit")
            .desc("The length of a type must be above this limit to allow using var")
            .defaultValue(32)
            .build();

    public AvoidVarForShortTypeRule() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(LIMIT_LENGTH_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (!node.isTypeInferred()) {
            // not var
            return data;
        }

        // These prefixes are used to correctly shorten types that are declared by subclasses
        final List<String> currentClassDeclarationCanonicalPrefixes = node.ancestors(ASTClassDeclaration.class)
            .toStream()
            .map(ASTClassDeclaration::getCanonicalName)
            .filter(Objects::nonNull)
            .map(s -> s + ".")
            .collect(Collectors.toList());

        final int limitLength = this.getProperty(LIMIT_LENGTH_DESCRIPTOR);
        Optional.ofNullable(node.firstChild(ASTVariableDeclarator.class))
            .map(n -> n.firstChild(ASTVariableId.class))
            .map(ASTVariableId::getTypeMirror)
            .map(variableTypeMirror -> {
                final VisitState state = new VisitState(limitLength, currentClassDeclarationCanonicalPrefixes);
                try {
                    variableTypeMirror.acceptVisitor(Visitor.INSTANCE, state);
                } catch (OverLengthLimitException ignored) {
                    // Quick exit/abort to not compute full string length
                    return null;
                }
                return state;
            })
            .map(TypePrettyPrint.TypePrettyPrinter::consumeResult)
            .ifPresent(type -> this.asCtx(data).addViolation(node, type, limitLength));

        return data;
    }

    static class OverLengthLimitException extends RuntimeException {

    }

    static class VisitState extends TypePrettyPrint.TypePrettyPrinter {
        final int limitLength;
        final List<String> currentClassDeclarationCanonicalPrefixes;

        int currentLength;

        VisitState(
            final int limitLength,
            final List<String> currentClassDeclarationCanonicalPrefixes) {
            this.limitLength = limitLength;
            this.currentClassDeclarationCanonicalPrefixes = currentClassDeclarationCanonicalPrefixes;

            printMethodHeader(false);
            printMethodResult(false);
            printAnnotations(false);
        }

        void throwIfCurrentLengthOverLimit() {
            if (currentLength >= limitLength) {
                throw new OverLengthLimitException();
            }
        }

        @Override
        public StringBuilder append(char o) {
            currentLength++;
            throwIfCurrentLengthOverLimit();
            return super.append(o);
        }

        @Override
        public StringBuilder append(String o) {
            currentLength += o.length();
            throwIfCurrentLengthOverLimit();
            return super.append(o);
        }
    }

    static class Visitor extends TypePrettyPrint.PrettyPrintVisitor<VisitState> {
        static final Visitor INSTANCE = new Visitor();

        @Override
        protected void appendClassName(
            JClassType t,
            VisitState s,
            JClassType enclosing,
            boolean isAnon) {
            final String canonicalName = t.getSymbol().getCanonicalName();
            if (canonicalName == null) {
                return;
            }

            // Correct resolve subclasses e.g. Foo.Bar needs to be Bar when inside Foo
            if (!s.currentClassDeclarationCanonicalPrefixes.isEmpty()) {
                // last -> top-most
                final String topMostCurrentClassDeclarationBinaryName =
                    s.currentClassDeclarationCanonicalPrefixes.get(
                        s.currentClassDeclarationCanonicalPrefixes.size() - 1);
                if (canonicalName.startsWith(topMostCurrentClassDeclarationBinaryName)) {
                    final Optional<String> optMostMatchedCanonicalClassPrefix =
                        s.currentClassDeclarationCanonicalPrefixes.stream()
                            .filter(canonicalName::startsWith)
                            .findFirst();
                    if (optMostMatchedCanonicalClassPrefix.isPresent()) {
                        s.append(canonicalName.substring(optMostMatchedCanonicalClassPrefix.get().length()));
                        return;
                    }
                }
            }

            final String packageName = t.getSymbol().getPackageName();
            s.append(canonicalName.startsWith(packageName) && canonicalName.length() > packageName.length()
                ? canonicalName.substring(packageName.length() + 1)
                : canonicalName);
        }
    }
}
