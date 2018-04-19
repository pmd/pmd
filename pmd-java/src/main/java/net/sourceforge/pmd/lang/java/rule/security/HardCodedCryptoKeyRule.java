/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Finds hard coded encryption keys that are passed to
 * javax.crypto.spec.SecretKeySpec(key, algorithm).
 * 
 * @author sergeygorbaty
 *
 */
public class HardCodedCryptoKeyRule extends AbstractJavaRule {

    public HardCodedCryptoKeyRule() {
        addRuleChainVisit(ASTClassOrInterfaceBodyDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        Set<ASTLocalVariableDeclaration> foundLocalVars = new HashSet<>();

        // find new javax.crypto.spec.SecretKeySpec("literal".getBytes(),...);

        List<ASTAllocationExpression> allocations = node.findDescendantsOfType(ASTAllocationExpression.class);
        for (ASTAllocationExpression allocation : allocations) {

            ASTClassOrInterfaceType declClassName = allocation.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
            if (declClassName != null) {
                Class<?> foundClass = declClassName.getType();
                if (foundClass != null && javax.crypto.spec.SecretKeySpec.class.isAssignableFrom(foundClass)) {
                    ASTPrimaryExpression init = allocation.getFirstDescendantOfType(ASTPrimaryExpression.class);
                    if (init != null) {
                        ASTPrimaryPrefix prefix = init.getFirstChildOfType(ASTPrimaryPrefix.class);
                        if (prefix != null) {
                            ASTLiteral literal = prefix.getFirstChildOfType(ASTLiteral.class);
                            if (literal != null) {
                                addViolation(data, literal);
                            }
                        }
                    }
                }
            }
        }

        // variables that were passed to SecretKeySpec
        Set<String> passedInIvVarNames = Util.findVariablesPassedToAnyParam(node,
                javax.crypto.spec.SecretKeySpec.class);

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
                    validateProperKey(data, var.getFirstDescendantOfType(ASTVariableInitializer.class));
                }
            }
        }

        for (ASTLocalVariableDeclaration foundLocalVar : foundLocalVars) {
            if (passedInIvVarNames.contains(foundLocalVar.getVariableName())) {
                validateProperKey(data, foundLocalVar.getFirstDescendantOfType(ASTVariableInitializer.class));
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
            if (type.hasImageEqualTo("byte") || type.hasImageEqualTo("String")) {
                ASTReferenceType parent = type.getFirstParentOfType(ASTReferenceType.class);
                if (parent != null) {
                    retVal.add(field);
                }
            }
        }
    }

    private void validateProperKey(Object data, ASTVariableInitializer varInit) {
        if (varInit == null) {
            return;
        }

        ASTPrimaryExpression primaryExpression = varInit.getFirstDescendantOfType(ASTPrimaryExpression.class);

        if (primaryExpression != null) {
            validateProperKeyInner(data, primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class));
        }

    }

    private void validateProperKeyInner(Object data, ASTPrimaryPrefix firstArgument) {
        if (firstArgument == null) {
            return;
        }

        // named variable
        ASTName namedVar = firstArgument.getFirstDescendantOfType(ASTName.class);
        if (namedVar != null) {
            // find where it's declared, if possible
            if (namedVar.getNameDeclaration() != null) {
                ASTVariableDeclarator varDecl = namedVar.getNameDeclaration().getNode()
                        .getFirstParentOfType(ASTVariableDeclarator.class);
                if (varDecl != null) {
                    validateProperKey(data, varDecl.getFirstChildOfType(ASTVariableInitializer.class));
                }
            }
        }

        // hard coded array
        ASTArrayInitializer arrayInit = firstArgument.getFirstDescendantOfType(ASTArrayInitializer.class);
        if (arrayInit != null) {
            addViolation(data, arrayInit);
        }

        // string literal
        ASTLiteral literal = firstArgument.getFirstDescendantOfType(ASTLiteral.class);
        if (literal != null && literal.isStringLiteral()) {
            addViolation(data, literal);
        }
    }

}
