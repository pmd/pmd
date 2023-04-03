/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.AssertionUtil;

public class UnnecessaryFullyQualifiedNameRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> REPORT_METHODS =
        booleanProperty("reportStaticMethods")
            .desc("Report unnecessary static method qualifiers like in `Collections.emptyList()`, if the method is imported or inherited.")
            .defaultValue(true)
            .build();

    private static final PropertyDescriptor<Boolean> REPORT_FIELDS =
        booleanProperty("reportStaticFields")
            .desc("Report unnecessary static field qualifiers like in `Math.PI`, if the field is imported or inherited.")
            .defaultValue(true)
            .build();

    public UnnecessaryFullyQualifiedNameRule() {
        super(ASTClassOrInterfaceType.class);
        definePropertyDescriptor(REPORT_METHODS);
        definePropertyDescriptor(REPORT_FIELDS);
    }

    @Override
    public Object visit(final ASTClassOrInterfaceType deepest, Object data) {
        if (deepest.getQualifier() != null) {
            // the child will be visited instead
            return data;
        }

        ASTClassOrInterfaceType next = deepest;
        ScopeInfo bestReason = null;
        if (next.isFullyQualified()) {
            bestReason = typeMeansSame(next);
        }

        // try to find the longest prefix that can be removed
        while (bestReason != null && segmentIsIrrelevant(next) && next.getParent() instanceof ASTClassOrInterfaceType) {
            ASTClassOrInterfaceType nextParent = (ASTClassOrInterfaceType) next.getParent();
            ScopeInfo newBestReason = typeMeansSame(nextParent);
            if (newBestReason == null) {
                break;
            } else {
                bestReason = newBestReason;
                next = nextParent;
            }
        }

        // maybe a method call/field can still take precedence
        if (next.getParent() instanceof ASTTypeExpression) {

            JavaNode opa = next.getParent().getParent();
            if (getProperty(REPORT_METHODS) && opa instanceof ASTMethodCall) {
                ASTMethodCall methodCall = (ASTMethodCall) opa;
                if (methodCall.getExplicitTypeArguments() == null
                    && methodProbablyMeansSame(methodCall)) {
                    // we don't actually know where the method came from
                    String simpleName = formatMemberName(next, methodCall.getMethodType().getSymbol());
                    String unnecessary = produceQualifier(deepest, next, true);
                    addViolation(data, next, new Object[] {unnecessary, simpleName, ""});
                    return null;
                }
            } else if (getProperty(REPORT_FIELDS) && opa instanceof ASTFieldAccess) {
                ASTFieldAccess fieldAccess = (ASTFieldAccess) opa;
                ScopeInfo reasonForFieldInScope = fieldMeansSame(fieldAccess);
                if (reasonForFieldInScope != null) {
                    String simpleName = formatMemberName(next, fieldAccess.getReferencedSym());
                    String reasonToString = unnecessaryReasonWrapper(reasonForFieldInScope);
                    String unnecessary = produceQualifier(deepest, next, true);
                    addViolation(data, next, new Object[] {unnecessary, simpleName, reasonToString});
                    return null;
                }
            }
        }

        if (bestReason != null) {
            String simpleName = next.getSimpleName();
            String reasonToString = unnecessaryReasonWrapper(bestReason);
            String unnecessary = produceQualifier(deepest, next, false);
            addViolation(data, next, new Object[] {unnecessary, simpleName, reasonToString});
        }
        return null;
    }


    private String produceQualifier(ASTClassOrInterfaceType startIncluded, ASTClassOrInterfaceType stopExcluded, boolean includeLast) {
        StringBuilder sb = new StringBuilder();
        if (startIncluded.isFullyQualified()) {
            sb.append(startIncluded.getTypeMirror().getSymbol().getPackageName());
        }
        ASTClassOrInterfaceType nextSimpleName = startIncluded;
        while (nextSimpleName != stopExcluded) { // NOPMD we want identity comparison
            sb.append('.').append(nextSimpleName.getSimpleName());
            nextSimpleName = (ASTClassOrInterfaceType) nextSimpleName.getParent();
        }
        if (includeLast) {
            if (sb.length() == 0) {
                return nextSimpleName.getSimpleName();
            }
            sb.append('.').append(nextSimpleName.getSimpleName());
        }
        return sb.toString();
    }

    private boolean segmentIsIrrelevant(ASTClassOrInterfaceType type) {
        return type.getTypeArguments() == null && type.getDeclaredAnnotations().isEmpty();
    }

    /**
     * Checks that the type name can be referred to by simple name in the
     * given scope, which means, that the qualification can be dropped.
     * If the symbol table for types yields the same symbol when referred
     * to by simple name, then this is true.
     *
     * @return The reason why the type is in scope. Null if it's not in scope.
     */
    private static @Nullable ScopeInfo typeMeansSame(@NonNull ASTClassOrInterfaceType typeNode) {
        JTypeDeclSymbol sym = typeNode.getTypeMirror().getSymbol();
        if (sym == null || sym.isUnresolved()) {
            return null;
        }

        JSymbolTable symTable = typeNode.getSymbolTable();
        if (symTable.variables().resolveFirst(sym.getSimpleName()) != null) {
            return null; //name is obscured: https://docs.oracle.com/javase/specs/jls/se15/html/jls-6.html#jls-6.4.2
        }

        return fieldOrTypeMeansSame(
            sym,
            typeNode.getSymbolTable(),
            JSymbolTable::types,
            (s, t) -> s.equals(t.getSymbol())
        );
    }

    private static boolean methodProbablyMeansSame(ASTMethodCall call) {

        // todo at least filter by potential applicability
        //  (ideally, do a complete inference run)
        //  this may have false negatives
        List<JMethodSig> accessibleMethods = call.getSymbolTable().methods().resolve(call.getMethodName());
        if (accessibleMethods.isEmpty() || call.getOverloadSelectionInfo().isFailed()) {
            return false;
        }

        JClassSymbol methodOwner = call.getMethodType().getSymbol().getEnclosingClass();

        for (JMethodSig m : accessibleMethods) {
            if (!m.getSymbol().getEnclosingClass().equals(methodOwner)) {
                return false;
            }
        }
        return true;
    }

    private static ScopeInfo fieldMeansSame(ASTFieldAccess field) {
        JFieldSymbol sym = field.getReferencedSym();
        if (sym == null || sym.isUnresolved()) {
            return null;
        }

        return fieldOrTypeMeansSame(
            sym,
            field.getSymbolTable(),
            JSymbolTable::variables,
            (s, t) -> s.equals(t.getSymbol())
        );
    }

    private static <S extends JElementSymbol, T> ScopeInfo fieldOrTypeMeansSame(@NonNull S originalSym,
                                                                                JSymbolTable symTable,
                                                                                Function<JSymbolTable, ShadowChain<T, ScopeInfo>> shadowChainGetter,
                                                                                BiPredicate<S, T> areEqual) {

        ShadowChainIterator<T, ScopeInfo> iter =
            shadowChainGetter.apply(symTable).iterateResults(originalSym.getSimpleName());

        if (iter.hasNext()) {
            iter.next();
            List<T> results = iter.getResults();
            if (results.size() == 1) { // otherwise ambiguous
                if (areEqual.test(originalSym, results.get(0))) {
                    return iter.getScopeTag();
                }
            }
            // not unnecessary
            return null;
        }
        // unknown symbol
        return null;
    }

    private static String formatMemberName(ASTClassOrInterfaceType qualifier, JAccessibleElementSymbol call) {
        JClassSymbol methodOwner = call.getEnclosingClass();
        if (methodOwner != null && !methodOwner.equals(qualifier.getTypeMirror().getSymbol())) {
            return methodOwner.getSimpleName() + "::" + call.getSimpleName();
        }
        return call.getSimpleName();
    }

    private static String unnecessaryReasonWrapper(ScopeInfo scopeInfo) {
        return " because it is " + unnecessaryReason(scopeInfo);
    }

    private static String unnecessaryReason(ScopeInfo scopeInfo) {
        switch (scopeInfo) {
        case JAVA_LANG:
            return "declared in java.lang";
        case SAME_PACKAGE:
        case SAME_FILE:
            return "declared in the same package";
        case SINGLE_IMPORT:
        case IMPORT_ON_DEMAND:
            return "imported in this file";
        case INHERITED:
            return "inherited by an enclosing type";
        case ENCLOSING_TYPE_MEMBER:
        case ENCLOSING_TYPE:
            return "declared in an enclosing type";
        default:
            throw AssertionUtil.shouldNotReachHere("unknown constant" + scopeInfo);
        }
    }
}
