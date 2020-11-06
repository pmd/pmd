/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * Computes Atfd.
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated Is internal API, will be moved in 7.0.0
 */
@Deprecated
@InternalApi
public class AtfdBaseVisitor extends JavaParserVisitorAdapter {


    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (isForeignAttributeOrMethod(node)) {
            if (isAttributeAccess(node)) {
                ((MutableInt) data).increment();
            } else {
                ((MutableInt) data).add(countForeignGetterSetterCalls(node));
            }
        }
        return super.visit(node, data);
    }


    private boolean isForeignGetterSetterCall(ASTPrimaryExpression node) {
        String methodOrAttributeName = getMethodOrAttributeName(node);
        return isForeignGetterSetterCall(methodOrAttributeName);
    }


    private boolean isForeignGetterSetterCall(String methodOrAttributeName) {
        return methodOrAttributeName != null && StringUtils.startsWithAny(methodOrAttributeName, "get", "is", "set");
    }


    private int countForeignGetterSetterCalls(ASTPrimaryExpression node) {
        if (!isForeignGetterSetterCall(node) || !isForeignAttributeOrMethod(node)) {
            return 0;
        }

        List<ASTPrimarySuffix> suffixes = node.findDescendantsOfType(ASTPrimarySuffix.class);
        int result = 0;
        for (ASTPrimarySuffix suffix : suffixes) {
            if (suffix.isArguments()) {
                result++;
            } else {
                String methodOrAttributeName = getMethodOrAttributeName(suffix);
                if (!isForeignGetterSetterCall(methodOrAttributeName)) {
                    break;
                }
            }
        }
        return result;
    }


    private boolean isForeignAttributeOrMethod(ASTPrimaryExpression node) {
        boolean result;
        String nameImage = getNameImage(node);

        if (nameImage != null && (!nameImage.contains(".") || nameImage.startsWith("this."))) {
            result = false;
        } else if (nameImage == null && node.getFirstDescendantOfType(ASTPrimaryPrefix.class).usesThisModifier()) {
            result = false;
        } else if (nameImage == null && node.hasDescendantOfAnyType(ASTLiteral.class, ASTAllocationExpression.class)) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }


    private String getNameImage(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String image = null;
        if (name != null) {
            image = name.getImage();
        }
        return image;
    }


    private String getMethodOrAttributeName(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String methodOrAttributeName = null;

        if (name != null) {
            int dotIndex = name.getImage().indexOf(".");
            if (dotIndex > -1) {
                methodOrAttributeName = name.getImage().substring(dotIndex + 1);
            }
        }

        return methodOrAttributeName;
    }


    private String getMethodOrAttributeName(ASTPrimarySuffix node) {
        return node.getImage();
    }


    private boolean isAttributeAccess(ASTPrimaryExpression node) {
        return !node.hasDescendantOfType(ASTPrimarySuffix.class);
    }

}
