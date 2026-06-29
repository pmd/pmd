/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.util.List;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameters;

import nl.stokpop.typemapper.model.ParameterAst;

/**
 * Annotates {@code KtFunctionValueParameter} nodes with resolved parameter types.
 *
 * <p>Type data is set by position, matching each AST parameter node against the
 * {@link ParameterAst} list from kotlin-type-mapper.
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
        if (parameters.isEmpty()) {
            return;
        }
        KtFunctionValueParameters paramsNode = funcNode.children(KtFunctionValueParameters.class).first();
        if (paramsNode != null) {
            annotateParameterNodes(paramsNode, parameters);
        }
    }

    private static void annotateParameterNodes(KtFunctionValueParameters paramsNode, List<ParameterAst> parameters) {
        int paramIdx = 0;
        for (int j = 0; j < paramsNode.getNumChildren(); j++) {
            KotlinNode sub = paramsNode.getChild(j);
            if (sub instanceof KotlinParser.KtFunctionValueParameter) {
                if (paramIdx < parameters.size()) {
                    String type = parameters.get(paramIdx).getType();
                    if (type != null) {
                        KotlinNodeTypeData.setTypeName(sub, type);
                    }
                }
                paramIdx++;
            }
        }
        if (paramIdx != parameters.size()) {
            throw new IllegalStateException(
                    "Parameter count mismatch in " + paramsNode
                    + ": PMD saw " + paramIdx + ", ktm reported " + parameters.size());
        }
    }
}
