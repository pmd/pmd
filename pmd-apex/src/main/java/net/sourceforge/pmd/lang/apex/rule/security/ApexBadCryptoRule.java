/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

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

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node)) {
            return data;
        }

        List<ASTFieldDeclaration> fieldDecl = node.descendants(ASTFieldDeclaration.class).toList();
        for (ASTFieldDeclaration var : fieldDecl) {
            findSafeVariables(var);
        }

        List<ASTVariableDeclaration> variableDecl = node.descendants(ASTVariableDeclaration.class).toList();
        for (ASTVariableDeclaration var : variableDecl) {
            findSafeVariables(var);
        }

        List<ASTMethodCallExpression> methodCalls = node.descendants(ASTMethodCallExpression.class).toList();
        for (ASTMethodCallExpression methodCall : methodCalls) {
            if (Helper.isMethodName(methodCall, CRYPTO, ENCRYPT) || Helper.isMethodName(methodCall, CRYPTO, DECRYPT)
                    || Helper.isMethodName(methodCall, CRYPTO, ENCRYPT_WITH_MANAGED_IV)
                    || Helper.isMethodName(methodCall, CRYPTO, DECRYPT_WITH_MANAGED_IV)) {

                validateStaticIVorKey(methodCall, data);
            }
        }

        potentiallyStaticBlob.clear();

        return data;
    }

    private void findSafeVariables(ApexNode<?> var) {
        ASTMethodCallExpression methodCall = var.firstChild(ASTMethodCallExpression.class);
        if (methodCall != null && Helper.isMethodName(methodCall, BLOB, VALUE_OF)) {
            ASTVariableExpression variable = var.firstChild(ASTVariableExpression.class);
            if (variable != null) {
                potentiallyStaticBlob.add(Helper.getFQVariableName(variable));
            }
        }
    }

    private void validateStaticIVorKey(ASTMethodCallExpression methodCall, Object data) {
        // .encrypt('AES128', key, exampleIv, data);
        int numberOfChildren = methodCall.getNumChildren();
        switch (numberOfChildren) {
        // matching signature to encrypt(
        case 5:
            Object potentialIV = methodCall.getChild(3);
            reportIfHardCoded(data, potentialIV);
            Object potentialKey = methodCall.getChild(2);
            reportIfHardCoded(data, potentialKey);
            break;

        // matching signature to encryptWithManagedIV(
        case 4:
            Object key = methodCall.getChild(2);
            reportIfHardCoded(data, key);
            break;

        default:
            break;
        }

    }

    private void reportIfHardCoded(Object data, Object potentialIV) {
        if (potentialIV instanceof ASTMethodCallExpression) {
            ASTMethodCallExpression expression = (ASTMethodCallExpression) potentialIV;
            if (expression.getNumChildren() > 1) {
                Object potentialStaticIV = expression.getChild(1);
                if (potentialStaticIV instanceof ASTLiteralExpression) {
                    ASTLiteralExpression variable = (ASTLiteralExpression) potentialStaticIV;
                    if (variable.isString()) {
                        asCtx(data).addViolation(variable);
                    }
                }
            }
        } else if (potentialIV instanceof ASTVariableExpression) {
            ASTVariableExpression variable = (ASTVariableExpression) potentialIV;
            if (potentiallyStaticBlob.contains(Helper.getFQVariableName(variable))) {
                asCtx(data).addViolation(variable);
            }
        }
    }
}
