/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Finds hardcoded static Initialization Vectors vectors used with cryptographic
 * operations.
 * 
 * //bad: byte[] ivBytes = new byte[] {32, 87, -14, 25, 78, -104, 98, 40};
 * //bad: byte[] ivBytes = "hardcoded".getBytes(); //bad: byte[] ivBytes =
 * someString.getBytes();
 * 
 * javax.crypto.spec.IvParameterSpec must not be created from a static sources
 * 
 * @author sergeygorbaty
 * @since 6.3.0
 *
 */
public class InsecureCryptoIvRule extends AbstractJavaRule {

    public InsecureCryptoIvRule() {
        addRuleChainVisit(ASTClassOrInterfaceBodyDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        Set<ASTLocalVariableDeclaration> foundLocalVars = new HashSet<>();
        // find new javax.crypto.spec.IvParameterSpec(...)
        Set<String> passedInIvVarNames = Util.findVariablesPassedToAnyParam(node, javax.crypto.spec.IvParameterSpec.class);

        List<ASTLocalVariableDeclaration> localVars = node.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        for (ASTLocalVariableDeclaration localVar : localVars) {
            foundLocalVars.addAll(extractPrimitiveTypes(localVar));
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> globalDecls = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);

        for (VariableNameDeclaration fieldVar : globalDecls.keySet()) {
            if (passedInIvVarNames.contains(fieldVar.getNode().getImage())) {
                ASTVariableDeclarator var = fieldVar.getNode().getFirstParentOfType(ASTVariableDeclarator.class);
                if (var != null) {
                    validateProperIv(data, var.getFirstDescendantOfType(ASTVariableInitializer.class));
                }
            }
        }

        for (ASTLocalVariableDeclaration foundLocalVar : foundLocalVars) {
            if (passedInIvVarNames.contains(foundLocalVar.getVariableName())) {
                validateProperIv(data, foundLocalVar.getFirstDescendantOfType(ASTVariableInitializer.class));
            }
        }

        return data;
    }

    private Set<ASTLocalVariableDeclaration> extractPrimitiveTypes(ASTLocalVariableDeclaration localVar) {
        List<ASTPrimitiveType> types = localVar.findDescendantsOfType(ASTPrimitiveType.class);
        Set<ASTLocalVariableDeclaration> retVal = new HashSet<>();
        extractPrimitiveTypesInner(retVal, localVar, types);

        return retVal;
    }

    private <T> void extractPrimitiveTypesInner(Set<T> retVal, T field, List<ASTPrimitiveType> types) {
        for (ASTPrimitiveType type : types) {
            if (type.hasImageEqualTo("byte")) {
                ASTReferenceType parent = type.getFirstParentOfType(ASTReferenceType.class);
                if (parent != null) {
                    retVal.add(field);
                }
            }
        }
    }

    private void validateProperIv(Object data, ASTVariableInitializer varInit) {
        // hard coded array
        ASTArrayInitializer arrayInit = varInit.getFirstDescendantOfType(ASTArrayInitializer.class);
        if (arrayInit != null) {
            addViolation(data, varInit);
        }

        // string literal
        ASTLiteral literal = varInit.getFirstDescendantOfType(ASTLiteral.class);
        if (literal != null && literal.isStringLiteral()) {
            addViolation(data, varInit);
        }

    }

}
