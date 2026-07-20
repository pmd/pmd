/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types.internal;

import java.util.List;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameters;
import net.sourceforge.pmd.lang.kotlin.types.InternalApiBridge;

import nl.stokpop.typemapper.model.ParameterAst;

/**
 * Annotates {@code KtFunctionValueParameter} nodes with resolved parameter types.
 *
 * <p>Type data is set by position, matching each AST parameter node against the
 * {@link ParameterAst} list from kotlin-type-mapper.
 *
 * @since 7.27.0
 */
final class FunctionParameterAnnotator {

    private FunctionParameterAnnotator() {
    }

    /**
     * Sets type data on each {@code KtFunctionValueParameter} child of the given
     * function declaration, matching by position against the {@code parameters}
     * list from the kotlin-type-mapper {@link nl.stokpop.typemapper.model.DeclarationAst}.
     */
    static void setFunctionParameterTypes(KtFunctionDeclaration funcNode, List<ParameterAst> parameters) {
        List<KtFunctionValueParameter> functionValueParameters = funcNode.children(KtFunctionValueParameters.class)
                .take(1).children(KtFunctionValueParameter.class).toList();
        if (functionValueParameters.size() != parameters.size()) {
            throw new IllegalStateException(
                    "Parameter count mismatch in " + funcNode
                            + ": PMD saw " + functionValueParameters.size() + ", ktm reported " + parameters.size());
        }
        annotateParameterNodes(functionValueParameters, parameters);
    }

    private static void annotateParameterNodes(List<KtFunctionValueParameter> pmdParameters, List<ParameterAst> ktmParameters) {
        for (int j = 0; j < pmdParameters.size(); j++) {
            String type = ktmParameters.get(j).getType();
            InternalApiBridge.setTypeName(pmdParameters.get(j), type);
        }
    }
}
