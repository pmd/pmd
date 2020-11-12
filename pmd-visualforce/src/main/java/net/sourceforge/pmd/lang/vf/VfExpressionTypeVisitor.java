/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTAttributeValue;
import net.sourceforge.pmd.lang.vf.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.vf.ast.ASTDotExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.ast.VfParserVisitorAdapter;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySource;

/**
 * Visits {@link ASTElExpression} nodes and stores type information for all {@link ASTIdentifier} child nodes.
 */
public class VfExpressionTypeVisitor extends VfParserVisitorAdapter {
    private static final Logger LOGGER = Logger.getLogger(VfExpressionTypeVisitor.class.getName());

    static final List<String> DEFAULT_APEX_DIRECTORIES = Collections.singletonList(".." + File.separator + "classes");
    static final List<String> DEFAULT_OBJECT_DIRECTORIES = Collections.singletonList(".." + File.separator + "objects");

    /**
     * Directory that contains Apex classes that may be referenced from a Visualforce page.
     */
    public static final PropertyDescriptor<List<String>> APEX_DIRECTORIES_DESCRIPTOR =
            PropertyFactory.stringListProperty("apexDirectories")
                    .desc("Location of Apex Class directories. Absolute or relative to the Visualforce directory.")
                    .defaultValue(DEFAULT_APEX_DIRECTORIES)
                    .delim(',')
                    .build();

    /**
     * Directory that contains Object definitions that may be referenced from a Visualforce page.
     */
    public static final PropertyDescriptor<List<String>> OBJECTS_DIRECTORIES_DESCRIPTOR =
            PropertyFactory.stringListProperty("objectsDirectories")
                    .desc("Location of CustomObject directories. Absolute or relative to the Visualforce directory.")
                    .defaultValue(DEFAULT_OBJECT_DIRECTORIES)
                    .delim(',')
                    .build();

    private static final String APEX_PAGE = "apex:page";
    private static final String CONTROLLER_ATTRIBUTE = "controller";
    private static final String STANDARD_CONTROLLER_ATTRIBUTE = "standardcontroller";
    private static final String EXTENSIONS_ATTRIBUTE = "extensions";

    private final ApexClassPropertyTypes apexClassPropertyTypes;
    private final ObjectFieldTypes objectFieldTypes;

    private String fileName;
    private String standardControllerName;

    /**
     * List of all Apex Class names that the VF page might refer to. These values come from either the
     * {@code controller} or {@code extensions} attribute.
     */
    private final List<String> apexClassNames;
    private final List<String> apexDirectories;
    private final List<String> objectsDirectories;

    public VfExpressionTypeVisitor(PropertySource propertySource) {
        this.apexDirectories = propertySource.getProperty(APEX_DIRECTORIES_DESCRIPTOR);
        this.objectsDirectories = propertySource.getProperty(OBJECTS_DIRECTORIES_DESCRIPTOR);
        this.apexClassNames = new ArrayList<>();
        this.apexClassPropertyTypes = new ApexClassPropertyTypes();
        this.objectFieldTypes = new ObjectFieldTypes();
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        this.fileName = AbstractTokenManager.getFileName();
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
     * Find all {@link ASTIdentifier} child nodes of {@code node} and attempt to resolve their type. The order of
     * precedence is Controller, Extensions, StandardController.
     */
    @Override
    public Object visit(ASTElExpression node, Object data) {
        for (Map.Entry<ASTIdentifier, String> entry : getExpressionIdentifierNames(node).entrySet()) {
            String name = entry.getValue();
            IdentifierType type = null;
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
                type = apexClassPropertyTypes.getVariableType(fullName, fileName, apexDirectories);
                if (type != null) {
                    break;
                }
            }

            // Try to find the identifier in a CustomField if it wasn't found in an Apex class and the identifier corresponds
            // to the StandardController.
            if (type == null) {
                if (parts.length >= 2 && standardControllerName != null && standardControllerName.equalsIgnoreCase(parts[0])) {
                    type = objectFieldTypes.getVariableType(name, fileName, objectsDirectories);
                }
            }

            if (type != null) {
                entry.getKey().setIdentifierType(type);
            } else {
                LOGGER.fine("Unable to determine type for: " + name);
            }
        }
        return super.visit(node, data);
    }

    /**
     * Parse the expression returning all of the identifiers in that expression mapped to its string represenation.
     * An {@code ASTElExpression} can contain multiple {@code ASTExpressions} in cases of logical operators.
     */
    private Map<ASTIdentifier, String> getExpressionIdentifierNames(ASTElExpression elExpression) {
        Map<ASTIdentifier, String> identifierToName = new IdentityHashMap<>();

        for (ASTExpression expression : elExpression.findChildrenOfType(ASTExpression.class)) {
            for (ASTIdentifier identifier : expression.findChildrenOfType(ASTIdentifier.class)) {
                StringBuilder sb = new StringBuilder(identifier.getImage());

                for (ASTDotExpression dotExpression : expression.findChildrenOfType(ASTDotExpression.class)) {
                    sb.append(".");

                    List<ASTIdentifier> childIdentifiers = dotExpression.findChildrenOfType(ASTIdentifier.class);
                    if (childIdentifiers.isEmpty()) {
                        continue;
                    } else if (childIdentifiers.size() > 1) {
                        // The grammar guarantees tha there should be at most 1 child identifier
                        // <EXP_DOT> (Identifier() | Literal() )
                        throw new RuntimeException("Unexpected number of childIdentifiers: size=" + childIdentifiers.size());
                    }

                    ASTIdentifier childIdentifier = childIdentifiers.get(0);
                    sb.append(childIdentifier.getImage());
                }

                identifierToName.put(identifier, sb.toString());
            }
        }

        return identifierToName;
    }
}
