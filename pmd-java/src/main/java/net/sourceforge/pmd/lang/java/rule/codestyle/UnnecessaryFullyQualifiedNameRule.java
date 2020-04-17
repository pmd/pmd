/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UnnecessaryFullyQualifiedNameRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassOrInterfaceType.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (node.getQualifier() != null) {
            // the child will be visited instead
            return data;
        }

        // here we're the deepest FQCN, this iterator contains the sequence
        // of class names from outermost to innermost. The first one may also
        // be package-qualified.
        Iterator<ASTClassOrInterfaceType> qualifiers = node.ancestorsOrSelf()
                                                           .takeWhile(it -> it instanceof ASTClassOrInterfaceType)
                                                           .filterIs(ASTClassOrInterfaceType.class)
                                                           .iterator();
        ASTClassOrInterfaceType first = qualifiers.next();
        Deque<String> acc = new ArrayDeque<>();
        ScopeInfo bestReason = bestReasonStart(acc, first, qualifiers, node.getSymbolTable());

        if (bestReason != null) {
            String simpleName = acc.removeLast();
            String unnecessary = String.join(".", acc);
            addViolation(data, node, new Object[] {unnecessary, simpleName, unnecessaryReason(bestReason)});
        }
        return data;
    }


    private @Nullable ScopeInfo bestReasonStart(Deque<String> qualifier,
                                                ASTClassOrInterfaceType node,
                                                Iterator<ASTClassOrInterfaceType> followers,
                                                JSymbolTable symTable) {
        ScopeInfo bestReason = null;
        if (node.isFullyQualified()) {
            // then it is qualified with a package name
            JTypeDeclSymbol symbol = node.getTypeMirror().getSymbol();
            bestReason = checkMeansSame(symbol, symTable);

            if (bestReason == null) {
                return null;
            }

            qualifier.addLast(symbol.getPackageName());
        }
        qualifier.addLast(node.getSimpleName());


        if (followers.hasNext()) {
            return bestReasonNext(qualifier, node, followers.next(), followers, symTable, bestReason);
        } else {
            return bestReason;
        }
    }


    private @Nullable ScopeInfo bestReasonNext(Deque<String> acc,
                                               ASTClassOrInterfaceType lhs,
                                               ASTClassOrInterfaceType node,
                                               Iterator<ASTClassOrInterfaceType> followers,
                                               JSymbolTable symTable,
                                               ScopeInfo bestReason) {


        if (lhs.getTypeArguments() != null) {
            return bestReason;
        }

        ScopeInfo reason = checkMeansSame(node.getTypeMirror().getSymbol(), symTable);
        if (reason == null) {
            return bestReason;
        }

        acc.addLast(node.getSimpleName());

        if (followers.hasNext()) {
            return bestReasonNext(acc, node, followers.next(), followers, symTable, reason);
        } else {
            return bestReason;
        }
    }


    public ScopeInfo checkMeansSame(@Nullable JTypeDeclSymbol sym, @NonNull JSymbolTable symbolTable) {
        if (sym == null) {
            return null;
        }
        ShadowChainIterator<JTypeMirror, ScopeInfo> iter =
            symbolTable.types().iterateResults(sym.getSimpleName());

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

    public ScopeInfo checkMethodMeansSame(JClassSymbol enclosingType, @Nullable JExecutableSymbol sym, @NonNull JSymbolTable symbolTable) {
        if (sym == null) {
            return null;
        }

        symbolTable.methods()
        ShadowChainIterator<JTypeMirror, ScopeInfo> iter =
            symbolTable.methods().iterateResults(sym.getSimpleName());

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

    private String unnecessaryReason(ScopeInfo scopeInfo) {
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
            throw new IllegalStateException();
        }
    }
}
