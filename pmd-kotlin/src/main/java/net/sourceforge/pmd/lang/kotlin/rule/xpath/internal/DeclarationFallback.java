/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;

import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.DeclarationKind;

/**
 * Shared filtering for the line-based declaration fallback used by type XPath functions
 * when the context node carries no type attribute of its own.
 */
final class DeclarationFallback {

    private DeclarationFallback() {
    }

    /**
     * Removes declarations that never describe the context node itself. A destructuring
     * {@code PropertyDeclaration} ({@code val (a, b) = ...}) has no single type; its
     * components are recorded as {@code DESTRUCTURED_VARIABLE} on the same line and must
     * not leak onto the whole declaration. Query the component {@code VariableDeclaration}
     * nodes instead.
     */
    static List<DeclarationAst> relevantTo(Node contextNode, List<DeclarationAst> decls) {
        if (!(contextNode instanceof KtPropertyDeclaration)) {
            return decls;
        }
        return decls.stream()
                .filter(d -> d.getKind() != DeclarationKind.DESTRUCTURED_VARIABLE)
                .collect(Collectors.toList());
    }
}
