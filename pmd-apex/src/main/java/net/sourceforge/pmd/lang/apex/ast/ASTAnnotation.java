/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.modifier.AnnotationModifier;

public class ASTAnnotation extends AbstractApexNode.Single<AnnotationModifier> {

    private static final String[] VALID_ANNOTATION_NAMES = {
            "SfdcOnly",
            "Deprecated",
            "AuraEnabled",
            "ReadOnly",
            "HiddenFromDoc",
            "UseConnectSerializer",
            "UseConnectDeserializer",
            "VisibleApiVersion",
            "RemoteAction",
            "IsTest",
            "Future",
            "TestSetup",
            "InvocableMethod",
            "InvocableVariable",
            "TestVisible",
            "RestResource",
            "HttpDelete",
            "HttpGet",
            "HttpPut",
            "HttpPost",
            "HttpPatch",
            "PermGuard",
            "NamespaceGuard",
            "PrivateApi",
            "AllowCertifiedApex",
            "SuppressWarnings",
            "NamespaceAccessible",
            "JsonAccess"
    };

    @Deprecated
    @InternalApi
    public ASTAnnotation(AnnotationModifier annotationModifier) {
        super(annotationModifier);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getName().getString();
    }

    /**
     * @deprecated Will be removed in 7.0, the AST shouldn't know about rules
     */
    @Deprecated
    public boolean suppresses(Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();

        if (hasImageEqualTo("SuppressWarnings")) {
            for (ASTAnnotationParameter param : findChildrenOfType(ASTAnnotationParameter.class)) {
                String image = param.getImage();

                if (image != null) {
                    Set<String> paramValues = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    paramValues.addAll(Arrays.asList(image.replaceAll("\\s+", "").split(",")));
                    if (paramValues.contains("PMD") || paramValues.contains(ruleAnno) || paramValues.contains("all")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isResolved() {
        return Arrays.stream(VALID_ANNOTATION_NAMES).anyMatch(name -> node.getName().getString().equalsIgnoreCase(name));
    }
}
