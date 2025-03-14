/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.ast;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.visualforce.DataType;
import net.sourceforge.pmd.lang.visualforce.VfLanguageProperties;

/**
 * Visits {@link ASTExpression} nodes and stores type information for
 * {@link net.sourceforge.pmd.lang.visualforce.ast.ASTIdentifier} children that represent an IdentifierDotted construct. An
 * IdentifierDotted is of the form {@code MyObject__c.MyField__c}.
 */
class VfExpressionTypeVisitor extends VfVisitorBase<Void, Void> {
    private static final Logger LOG = LoggerFactory.getLogger(VfExpressionTypeVisitor.class);

    private static final String APEX_PAGE = "apex:page";
    private static final String CONTROLLER_ATTRIBUTE = "controller";
    private static final String STANDARD_CONTROLLER_ATTRIBUTE = "standardcontroller";
    private static final String EXTENSIONS_ATTRIBUTE = "extensions";

    private final ApexClassPropertyTypes apexClassPropertyTypes;
    private final ObjectFieldTypes objectFieldTypes;
    private final FileId fileId;

    private String standardControllerName;

    /**
     * List of all Apex Class names that the VF page might refer to. These values come from either the
     * {@code controller} or {@code extensions} attribute.
     */
    private final List<String> apexClassNames;
    private final List<String> apexDirectories;
    private final List<String> objectsDirectories;

    VfExpressionTypeVisitor(ParserTask task, VfLanguageProperties vfProperties) {
        this.fileId = task.getFileId();
        this.apexDirectories = vfProperties.getProperty(VfLanguageProperties.APEX_DIRECTORIES_DESCRIPTOR);
        this.objectsDirectories = vfProperties.getProperty(VfLanguageProperties.OBJECTS_DIRECTORIES_DESCRIPTOR);
        this.apexClassNames = new ArrayList<>();
        this.apexClassPropertyTypes = new ApexClassPropertyTypes(task.getLpRegistry());
        this.objectFieldTypes = new ObjectFieldTypes();
    }

    @Override
    public Void visit(ASTCompilationUnit node, Void data) {
        if (apexDirectories.isEmpty() && objectsDirectories.isEmpty()) {
            // Skip visiting if there aren't any directories to look in
            return data;
        }
        return super.visit(node, data);
    }

    /**
     * Gather names of Controller, Extensions, and StandardController. Each of these may contain the identifier
     * referenced from the Visualforce page.
     */
    @Override
    public Void visit(ASTElement node, Void data) {
        if (APEX_PAGE.equalsIgnoreCase(node.getName())) {

            for (ASTAttribute attr : node.children(ASTAttribute.class)) {
                String lowerAttr = attr.getName().toLowerCase(Locale.ROOT);
                if (CONTROLLER_ATTRIBUTE.equals(lowerAttr)) {
                    // Controller Name should always take precedence
                    apexClassNames.add(0, getAttrValue(attr));
                    break;
                } else if (STANDARD_CONTROLLER_ATTRIBUTE.equals(lowerAttr)) {
                    standardControllerName = getAttrValue(attr).toLowerCase(Locale.ROOT);
                } else if (EXTENSIONS_ATTRIBUTE.equalsIgnoreCase(lowerAttr)) {
                    for (String extension : getAttrValue(attr).split(",")) {
                        apexClassNames.add(extension.trim());
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    private static String getAttrValue(ASTAttribute attr) {
        return attr.firstChild(ASTAttributeValue.class)
                   .firstChild(ASTText.class)
                   .getImage();
    }

    /**
     * Invoke {@link ASTExpression#getDataNodes()} on all children of {@code node} and attempt to determine the
     * {@link DataType} by looking at Apex or CustomField metadata.
     */
    @Override
    public Void visit(ASTElExpression node, Void data) {
        for (Map.Entry<VfTypedNode, String> entry : getDataNodeNames(node).entrySet()) {
            String name = entry.getValue();
            DataType type = null;
            String[] parts = name.split("\\.");

            // Apex extensions take precedence over Standard controllers.
            // The example below will display "Name From Inner Class" instead of the Account name
            // public class AccountExtension {
            //    public AccountExtension(ApexPages.StandardController controller) {
            //    }
            //
            //    public InnerClass getAccount() {
            //        return new InnerClass();
            //    }
            //
            //    public class InnerClass {
            //        public String getName() {
            //            return 'Name From Inner Class';
            //        }
            //    }
            // }
            //<apex:page standardController="Account" extensions="AccountExtension">
            //    <apex:outputText value="{!Account.Name}" escape="false"/>
            //</apex:page>

            // Try to find the identifier in an Apex class
            for (String apexClassName : apexClassNames) {
                String fullName = apexClassName + "." + name;
                type = apexClassPropertyTypes.getDataType(fullName, fileId, apexDirectories);
                if (type != null) {
                    break;
                }
            }

            // Try to find the identifier in a CustomField if it wasn't found in an Apex class and the identifier corresponds
            // to the StandardController.
            if (type == null) {
                if (parts.length >= 2 && standardControllerName != null && standardControllerName.equalsIgnoreCase(parts[0])) {
                    type = objectFieldTypes.getDataType(name, fileId, objectsDirectories);
                }
            }

            if (type != null) {
                InternalApiBridge.setDataType(entry.getKey(), type);
            } else {
                LOG.debug("Unable to determine type for: {}", name);
            }
        }
        return super.visit(node, data);
    }

    /**
     * Invoke {@link ASTExpression#getDataNodes()} for all {@link ASTExpression} children of {@code node} and return
     * the consolidated results.
     */
    private Map<VfTypedNode, String> getDataNodeNames(ASTElExpression node) {
        Map<VfTypedNode, String> dataNodeToName = new IdentityHashMap<>();

        for (ASTExpression expression : node.children(ASTExpression.class)) {
            try {
                dataNodeToName.putAll(expression.getDataNodes());
            } catch (ASTExpression.DataNodeStateException ignored) {
                // Intentionally left blank
            }
        }

        return dataNodeToName;
    }
}
