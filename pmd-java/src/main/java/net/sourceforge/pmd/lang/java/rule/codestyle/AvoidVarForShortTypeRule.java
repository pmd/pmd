package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JTypeVisitor;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.OptionalBool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AvoidVarForShortTypeRule extends AbstractJavaRulechainRule {
    private static final PropertyDescriptor<Integer> LIMIT_LENGTH_DESCRIPTOR =
            PropertyFactory.intProperty("lengthLimit")
                    .desc("The rule will be triggered when the length is below this limit")
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

        final List<String> currentClassDeclarationCanonicalPrefixes =
                computeCurrentClassDeclarationCanonicalPrefixes(node);

        final Integer limitLength = this.getProperty(LIMIT_LENGTH_DESCRIPTOR);
        Optional.ofNullable(node.firstChild(ASTVariableDeclarator.class))
                .map(n -> n.firstChild(ASTVariableId.class))
                .map(ASTVariableId::getTypeMirror)
                .map(variableTypeMirror -> {
                    final Printer printer = new Printer(currentClassDeclarationCanonicalPrefixes);
                    variableTypeMirror.acceptVisitor(Visitor.INSTANCE, printer);
                    return printer.consumeResult();
                })
                .filter(s -> s.length() < limitLength)
                .ifPresent(type -> this.asCtx(data).addViolationWithMessage(
                        node,
                        "The declared type ''{0}'' is not long enough (<{1}) to justify the usage of var",
                        type,
                        limitLength));

        return data;
    }

    private List<String> computeCurrentClassDeclarationCanonicalPrefixes(ASTLocalVariableDeclaration node) {
        final List<String> currentClassDeclarationCanonicalPrefixes = new ArrayList<>();
        final Set<JavaNode> alreadyProcessed = new HashSet<>();

        JavaNode parent = node.getParent();
        while (parent != null && !alreadyProcessed.contains(parent)) {
            if (parent instanceof ASTClassDeclaration) {
                ASTClassDeclaration classDeclaration = (ASTClassDeclaration) parent;
                Optional.ofNullable(classDeclaration.getCanonicalName())
                        .map(s -> s + ".")
                        .ifPresent(currentClassDeclarationCanonicalPrefixes::add);
            }
            alreadyProcessed.add(parent);
            parent = parent.getParent();
        }

        return currentClassDeclarationCanonicalPrefixes;
    }

    static class Printer {
        final List<String> currentClassDeclarationCanonicalPrefixes;
        final StringBuilder sb = new StringBuilder();
        OptionalBool printTypeVarBounds = OptionalBool.UNKNOWN;

        public Printer(final List<String> currentClassDeclarationCanonicalPrefixes) {
            this.currentClassDeclarationCanonicalPrefixes = currentClassDeclarationCanonicalPrefixes;
        }

        StringBuilder append(final char o) {
            return this.sb.append(o);
        }

        StringBuilder append(final String o) {
            return this.sb.append(o);
        }

        String consumeResult() {
            return this.sb.toString();
        }
    }


    static class Visitor implements JTypeVisitor<Void, Printer> {
        static final Visitor INSTANCE = new Visitor();

        @Override
        public Void visit(final JTypeMirror t, final Printer sb) {
            sb.append(t.toString());
            return null;
        }

        @Override
        public Void visitClass(final JClassType t, final Printer sb) {
            final JClassType enclosing = t.getEnclosingType();
            final boolean isAnon = t.getSymbol().isAnonymousClass();

            if (enclosing != null && !isAnon) {
                this.visitClass(enclosing, sb);
                sb.append('#');
            } else if (t.hasErasedSuperTypes() && !t.isRaw()) {
                sb.append("(erased) ");
            }

            if (t.getSymbol().isUnresolved()) {
                sb.append('*'); // a small marker to spot them
            }

            this.processClassName(t, sb);

            final List<JTypeMirror> targs = t.getTypeArgs();
            if (t.isRaw() || targs.isEmpty()) {
                return null;
            }

            if (t.isGenericTypeDeclaration() && sb.printTypeVarBounds != OptionalBool.NO) {
                sb.printTypeVarBounds = OptionalBool.YES;
            }
            this.join(sb, targs, ", ", "<", ">");
            return null;
        }

        private void processClassName(final JClassType t, final Printer sb) {
            final String canonicalName = t.getSymbol().getCanonicalName();
            if (canonicalName == null) {
                return;
            }

            if (!sb.currentClassDeclarationCanonicalPrefixes.isEmpty()) {
                // last -> top-most
                final String topMostCurrentClassDeclarationBinaryName =
                        sb.currentClassDeclarationCanonicalPrefixes.get(
                                sb.currentClassDeclarationCanonicalPrefixes.size() - 1);
                if (canonicalName.startsWith(topMostCurrentClassDeclarationBinaryName)) {
                    final Optional<String> optMostMatchedCanonicalClassPrefix =
                            sb.currentClassDeclarationCanonicalPrefixes.stream()
                                    .filter(canonicalName::startsWith)
                                    .findFirst();
                    if (optMostMatchedCanonicalClassPrefix.isPresent()) {
                        sb.append(canonicalName.substring(optMostMatchedCanonicalClassPrefix.get().length()));
                        return;
                    }
                }
            }

            final String packageName = t.getSymbol().getPackageName();
            sb.append(canonicalName.startsWith(packageName) && canonicalName.length() > packageName.length()
                    ? canonicalName.substring(packageName.length() + 1)
                    : canonicalName);
        }

        @Override
        public Void visitWildcard(final JWildcardType t, final Printer sb) {
            sb.append("?");
            if (t.isUnbounded()) {
                return null;
            }

            sb.append(t.isUpperBound() ? " extends " : " super ");

            t.getBound().acceptVisitor(this, sb);
            return null;
        }

        @Override
        public Void visitPrimitive(final JPrimitiveType t, final Printer sb) {
            sb.append(t.getSimpleName());
            return null;
        }

        @Override
        public Void visitTypeVar(final JTypeVar t, final Printer sb) {
            sb.append(t.getName());

            if (sb.printTypeVarBounds == OptionalBool.YES) {
                sb.printTypeVarBounds = OptionalBool.NO;
                if (!t.getUpperBound().isTop()) {
                    sb.append(" extends ");
                    t.getUpperBound().acceptVisitor(this, sb);
                }
                if (!t.getLowerBound().isBottom()) {
                    sb.append(" super ");
                    t.getLowerBound().acceptVisitor(this, sb);
                }
                sb.printTypeVarBounds = OptionalBool.YES;
            }
            return null;
        }

        @Override
        public Void visitIntersection(final JIntersectionType t, final Printer sb) {
            return this.join(sb, t.getComponents(), " & ", "", "");
        }

        @Override
        public Void visitNullType(final JTypeMirror t, final Printer sb) {
            sb.append("null");
            return null;
        }

        @Override
        public Void visitInferenceVar(final InferenceVar t, final Printer sb) {
            sb.append(t.getName());
            return null;
        }

        private Void join(
                final Printer sb,
                final List<? extends JTypeMirror> types,
                final String delim,
                final String prefix,
                final String suffix) {
            final boolean empty = types.isEmpty();
            sb.append(prefix);
            if (!empty) {
                for (int i = 0; i < types.size() - 1; i++) {
                    types.get(i).acceptVisitor(this, sb);
                    sb.append(delim);
                }
                types.get(types.size() - 1).acceptVisitor(this, sb);
            }
            sb.append(suffix);
            return null;
        }
    }
}
