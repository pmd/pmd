/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UnnecessaryFullyQualifiedNameRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassOrInterfaceType.class);
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
            bestReason = checkMeansSame(next);
        }

        while (bestReason != null && segmentIsIrrelevant(next) && next.getParent() instanceof ASTClassOrInterfaceType) {
            ASTClassOrInterfaceType nextParent = (ASTClassOrInterfaceType) next.getParent();
            ScopeInfo newBestReason = checkMeansSame(nextParent);
            if (newBestReason == null) {
                break;
            } else {
                bestReason = newBestReason;
                next = nextParent;
            }
        }

        // maybe a method call can still take precedence?
        if (next.getParent() instanceof ASTTypeExpression
            && next.getParent().getParent() instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) next.getParent().getParent();
            if (methodProbablyMeansSame(next, methodCall)) {
                // we don't actually know where the method came from
                String simpleName = methodCall.getMethodName();
                String unnecessary = produceQualifier(deepest, next, true);
                addViolation(data, next, new Object[] {unnecessary, simpleName, ""});
                return null;
            }
        }

        if (bestReason != null) {

            String simpleName = next.getSimpleName();
            String reasonToString = unnecessaryReasonForType(bestReason);
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
        while (nextSimpleName != stopExcluded) {
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


    private static ScopeInfo checkMeansSame(ASTClassOrInterfaceType typeNode) {
        JTypeDeclSymbol sym = typeNode.getTypeMirror().getSymbol();
        if (sym == null) {
            return null;
        }
        ShadowChainIterator<JTypeMirror, ScopeInfo> iter =
            typeNode.getSymbolTable().types().iterateResults(sym.getSimpleName());

        if (iter.hasNext()) {
            iter.next();
            List<JTypeMirror> results = iter.getResults();
            if (results.size() == 1) { // otherwise ambiguous
                if (sym.equals(results.get(0).getSymbol())) {
                    return iter.getScopeTag();
                }
            }
            // not unnecessary
            return null;
        }
        // unknown symbol
        return null;
    }

    private static boolean methodProbablyMeansSame(ASTClassOrInterfaceType qualifier, ASTMethodCall call) {
        JTypeDeclSymbol sym = qualifier.getTypeMirror().getSymbol();
        if (sym == null) {
            return false;
        }

        // todo filter by potential applicability (ideally, do a complete inference run)
        //  this may have false negatives
        List<JMethodSig> accessibleMethods = call.getSymbolTable().methods().resolve(call.getMethodName());
        if (accessibleMethods.isEmpty()) {
            return false;
        }
        for (JMethodSig m : accessibleMethods) {
            if (!m.getSymbol().getEnclosingClass().equals(sym)) {
                return false;
            }
        }
        return true;
    }

    private static String unnecessaryReasonForType(ScopeInfo scopeInfo) {
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
