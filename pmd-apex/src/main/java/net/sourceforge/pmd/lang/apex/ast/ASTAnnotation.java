/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.NavigableSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.summit.ast.modifier.AnnotationModifier;

public final class ASTAnnotation extends AbstractApexNode.Single<AnnotationModifier> {

    /**
     * Valid annotations in the Apex language.
     * <p>
     * Includes all annotations from the <a href="https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm">official
     * documentation</a>, plus
     * <ul>
     *     <li>{@code AllowCertifiedApex}</li>
     *     <li>{@code HiddenFromDoc}</li>
     *     <li>{@code NamespaceGuard}</li>
     *     <li>{@code PermGuard}</li>
     *     <li>{@code PrivateApi}</li>
     *     <li>{@code SfdcOnly}</li>
     *     <li>{@code UseConnectDeserializer}</li>
     *     <li>{@code UseConnectSerializer}</li>
     *     <li>{@code VisibleApiVersion}</li>
     * </ul>
     * for backward compatibility.
     */
    private static final NavigableSet<String> NORMALIZED_ANNOTATION_NAMES =
        ImmutableSortedSet.orderedBy(String.CASE_INSENSITIVE_ORDER).add(
            "AllowCertifiedApex",
            "AuraEnabled",
            "Deprecated",
            "Future",
            "HiddenFromDoc",
            "HttpDelete",
            "HttpGet",
            "HttpPatch",
            "HttpPost",
            "HttpPut",
            "InvocableMethod",
            "InvocableVariable",
            "IsTest",
            "JsonAccess",
            "NamespaceAccessible",
            "NamespaceGuard",
            "PermGuard",
            "PrivateApi",
            "ReadOnly",
            "RemoteAction",
            "RestResource",
            "SfdcOnly",
            "SuppressWarnings",
            "TestSetup",
            "TestVisible",
            "UseConnectDeserializer",
            "UseConnectSerializer",
            "VisibleApiVersion"
    ).build();

    ASTAnnotation(AnnotationModifier annotationModifier) {
        super(annotationModifier);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        // If resolvable to a known name, return the case-normalized name.
        String rawName = node.getName().getString();
        if (NORMALIZED_ANNOTATION_NAMES.contains(rawName)) {
            return NORMALIZED_ANNOTATION_NAMES.floor(rawName);
        }
        return rawName;
    }
    
    @Override
    public String getImage() {
        return getName();
    }

    public boolean isResolved() {
        return NORMALIZED_ANNOTATION_NAMES.contains(node.getName().getString());
    }
}
