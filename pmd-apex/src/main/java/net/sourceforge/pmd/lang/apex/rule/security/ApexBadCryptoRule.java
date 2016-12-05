/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Finds encryption schemes using hardcoded IV, hardcoded key
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexBadCryptoRule extends AbstractApexRule {
    private static final String VALUE_OF = "valueOf";
    private static final String BLOB = "Blob";
    private static final String ENCRYPT = "encrypt";
    private static final String DECRYPT = "decrypt";
    private static final String CRYPTO = "Crypto";
    private static final String ENCRYPT_WITH_MANAGED_IV = "encryptWithManagedIV";
    private static final String DECRYPT_WITH_MANAGED_IV = "decryptWithManagedIV";

    private final Set<String> potentiallyStaticBlob = new HashSet<>();

    public ApexBadCryptoRule() {
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node)) {
            return data;
        }

        List<ASTFieldDeclaration> fieldDecl = node.findDescendantsOfType(ASTFieldDeclaration.class);
        for (ASTFieldDeclaration var : fieldDecl) {
            findSafeVariables(var);
        }

        List<ASTVariableDeclaration> variableDecl = node.findDescendantsOfType(ASTVariableDeclaration.class);
        for (ASTVariableDeclaration var : variableDecl) {
            findSafeVariables(var);
        }

        List<ASTMethodCallExpression> methodCalls = node.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression methodCall : methodCalls) {
            if (Helper.isMethodName(methodCall, CRYPTO, ENCRYPT) || Helper.isMethodName(methodCall, CRYPTO, DECRYPT)
                    || Helper.isMethodName(methodCall, CRYPTO, ENCRYPT_WITH_MANAGED_IV)
                    || Helper.isMethodName(methodCall, CRYPTO, DECRYPT_WITH_MANAGED_IV)) {

                validateStaticIVorKey(methodCall, data);
            }
        }
        return data;
    }

    private void findSafeVariables(AbstractApexNode<?> var) {
        ASTMethodCallExpression methodCall = var.getFirstChildOfType(ASTMethodCallExpression.class);
        if (methodCall != null && Helper.isMethodName(methodCall, BLOB, VALUE_OF)) {
            ASTVariableExpression variable = var.getFirstChildOfType(ASTVariableExpression.class);
            StringBuilder sb = new StringBuilder().append(variable.getNode().getDefiningType()).append(":")
                    .append(variable.getNode().getIdentifier().value);
            potentiallyStaticBlob.add(sb.toString());
        }
    }

    private void validateStaticIVorKey(ASTMethodCallExpression methodCall, Object data) {
        // .encrypt('AES128', key, exampleIv, data);
        int numberOfChildren = methodCall.jjtGetNumChildren();
        switch (numberOfChildren) {
        // matching signature to encrypt(
        case 5:
            Object potentialIV = methodCall.jjtGetChild(3);
            reportIfHardCoded(data, potentialIV);
            // no break on purpose

            // matching signature to encryptWithManagedIV(
            // fallthrough
        case 4:
            Object potentialKey = methodCall.jjtGetChild(2);
            reportIfHardCoded(data, potentialKey);
            break;

        default:
            break;
        }

    }

    private void reportIfHardCoded(Object data, Object potentialIV) {
        if (potentialIV instanceof ASTVariableExpression) {
            ASTVariableExpression variable = (ASTVariableExpression) potentialIV;
            StringBuilder sb = new StringBuilder().append(variable.getNode().getDefiningType()).append(":")
                    .append(variable.getNode().getIdentifier().value);
            if (potentiallyStaticBlob.contains(sb.toString())) {
                addViolation(data, variable);
            }
        }
    }

}
