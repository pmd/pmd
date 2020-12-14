/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;

import apex.jorje.semantic.symbol.member.method.Generated;
import apex.jorje.semantic.symbol.member.method.MethodInfo;
import apex.jorje.semantic.symbol.type.BasicType;

/**
 * Visits an Apex class to determine a mapping of referenceable expressions to expression type.
 */
final class ApexClassPropertyTypesVisitor extends ApexParserVisitorAdapter {

    /**
     * Prefix for standard bean type getters, i.e. getFoo
     */
    private static final String BEAN_GETTER_PREFIX = "get";
    /**
     * This is the prefix assigned to automatic get/set properties such as String myProp { get; set; }
     */
    private static final String PROPERTY_PREFIX_ACCESSOR = "__sfdc_";

    private static final String RETURN_TYPE_VOID = "void";

    /**
     * Pairs of (variableName, BasicType)
     */
    private final List<Pair<String, BasicType>> variables;

    ApexClassPropertyTypesVisitor() {
        this.variables = new ArrayList<>();
    }

    public List<Pair<String, BasicType>> getVariables() {
        return this.variables;
    }

    /**
     * Stores the return type of the method in {@link #variables} if the method is referenceable from a
     * Visualforce page.
     */
    @Override
    public Object visit(ASTMethod node, Object data) {
        MethodInfo mi = node.getNode().getMethodInfo();
        if (mi.getParameterTypes().isEmpty()
                && isVisibleToVisualForce(node)
                && !RETURN_TYPE_VOID.equalsIgnoreCase(mi.getReturnType().getApexName())
                && (mi.getGenerated().equals(Generated.USER) || mi.isPropertyAccessor())) {
            StringBuilder sb = new StringBuilder();
            List<ASTUserClass> parents = node.getParentsOfType(ASTUserClass.class);
            Collections.reverse(parents);
            for (ASTUserClass parent : parents) {
                sb.append(parent.getImage()).append(".");
            }
            String name = node.getImage();
            for (String prefix : new String[]{BEAN_GETTER_PREFIX, PROPERTY_PREFIX_ACCESSOR}) {
                if (name.startsWith(prefix)) {
                    name = name.substring(prefix.length());
                }
            }
            sb.append(name);

            variables.add(Pair.of(sb.toString(), mi.getReturnType().getBasicType()));
        }
        return super.visit((ApexNode<?>) node, data);
    }

    /**
     * Used to filter out methods that aren't visible to the Visualforce page.
     *
     * @return true if the method is visible to Visualforce.
     */
    private boolean isVisibleToVisualForce(ASTMethod node) {
        ASTModifierNode modifier = node.getFirstChildOfType(ASTModifierNode.class);
        return modifier.isGlobal() | modifier.isPublic();
    }
}
