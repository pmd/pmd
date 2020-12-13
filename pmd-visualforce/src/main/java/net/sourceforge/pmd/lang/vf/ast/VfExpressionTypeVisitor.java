/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.vf.DataType;
import net.sourceforge.pmd.lang.vf.VfHandler;

/**
 * Visits {@link ASTExpression} nodes and stores type information for
 * {@link net.sourceforge.pmd.lang.vf.ast.ASTIdentifier} children that represent an IdentifierDotted construct. An
 * IdentifierDotted is of the form {@code MyObject__c.MyField__c}.
 */
class VfExpressionTypeVisitor extends VfParserVisitorAdapter {
    private static final Logger LOGGER = Logger.getLogger(VfExpressionTypeVisitor.class.getName());

    private static final String APEX_PAGE = "apex:page";
    private static final String CONTROLLER_ATTRIBUTE = "controller";
    private static final String STANDARD_CONTROLLER_ATTRIBUTE = "standardcontroller";
    private static final String EXTENSIONS_ATTRIBUTE = "extensions";

    private final ApexClassPropertyTypes apexClassPropertyTypes;
    private final ObjectFieldTypes objectFieldTypes;
    private final String fileName;

    private String standardControllerName;

    /**
     * List of all Apex Class names that the VF page might refer to. These values come from either the
     * {@code controller} or {@code extensions} attribute.
     */
    private final List<String> apexClassNames;
    private final List<String> apexDirectories;
    private final List<String> objectsDirectories;

    VfExpressionTypeVisitor(ParserTask task) {
        this.fileName = task.getFileDisplayName();
        this.apexDirectories = task.getProperties().getProperty(VfHandler.APEX_DIRECTORIES_DESCRIPTOR);
        this.objectsDirectories = task.getProperties().getProperty(VfHandler.OBJECTS_DIRECTORIES_DESCRIPTOR);
        this.apexClassNames = new ArrayList<>();
        this.apexClassPropertyTypes = new ApexClassPropertyTypes();
        this.objectFieldTypes = new ObjectFieldTypes();
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        if (StringUtils.isBlank(fileName)) {
            // Skip visiting if there isn't a file that can anchor the directories
            return data;
        }

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
    public Object visit(ASTElement node, Object data) {
        if (APEX_PAGE.equalsIgnoreCase(node.getName())) {
            List<ASTAttribute> attribs = node.findChildrenOfType(ASTAttribute.class);

            for (ASTAttribute attr : attribs) {
                String lowerAttr = attr.getName().toLowerCase(Locale.ROOT);
                if (CONTROLLER_ATTRIBUTE.equals(lowerAttr)) {
                    // Controller Name should always take precedence
                    apexClassNames.add(0, attr.getFirstChildOfType(ASTAttributeValue.class)
                            .getFirstChildOfType(ASTText.class).getImage());
                    break;
                } else if (STANDARD_CONTROLLER_ATTRIBUTE.equals(lowerAttr)) {
                    standardControllerName = attr.getFirstChildOfType(ASTAttributeValue.class)
                            .getFirstChildOfType(ASTText.class).getImage().toLowerCase(Locale.ROOT);
                } else if (EXTENSIONS_ATTRIBUTE.equalsIgnoreCase(lowerAttr)) {
                    for (String extension : attr.getFirstChildOfType(ASTAttributeValue.class)
                            .getFirstChildOfType(ASTText.class).getImage().split(",")) {
                        apexClassNames.add(extension.trim());
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    /**
     * Invoke {@link ASTExpression#getDataNodes()} on all children of {@code node} and attempt to determine the
     * {@link DataType} by looking at Apex or CustomField metadata.
     */
    @Override
    public Object visit(ASTElExpression node, Object data) {
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
                type = apexClassPropertyTypes.getDataType(fullName, fileName, apexDirectories);
                if (type != null) {
                    break;
                }
            }

            // Try to find the identifier in a CustomField if it wasn't found in an Apex class and the identifier corresponds
            // to the StandardController.
            if (type == null) {
                if (parts.length >= 2 && standardControllerName != null && standardControllerName.equalsIgnoreCase(parts[0])) {
                    type = objectFieldTypes.getDataType(name, fileName, objectsDirectories);
                }
            }

            if (type != null) {
                VfAstInternals.setDataType(entry.getKey(), type);
            } else {
                LOGGER.fine("Unable to determine type for: " + name);
            }
        }
        return super.visit(node, data);
    }

    /**
     * Invoke {@link ASTExpression#getDataNodes()} for all {@link ASTExpression} children of {@code node} and return
     * the consolidated results.
     */
    private IdentityHashMap<VfTypedNode, String> getDataNodeNames(ASTElExpression node) {
        IdentityHashMap<VfTypedNode, String> dataNodeToName = new IdentityHashMap<>();

        for (ASTExpression expression : node.findChildrenOfType(ASTExpression.class)) {
            try {
                dataNodeToName.putAll(expression.getDataNodes());
            } catch (ASTExpression.DataNodeStateException ignore) {
                // Intentionally left blank
                continue;
            }
        }

        return dataNodeToName;
    }
}
