/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavadocCommentOwner;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * @author Brian Remedios
 */
public class CommentRequiredRule extends AbstractJavaRulechainRule {
    private static final Logger LOG = LoggerFactory.getLogger(CommentRequiredRule.class);

    // Used to pretty print a message
    private static final Map<String, String> DESCRIPTOR_NAME_TO_COMMENT_TYPE = new HashMap<>();

    private static final PropertyDescriptor<CommentRequirement> ACCESSOR_CMT_DESCRIPTOR
        = requirementPropertyBuilder("accessorCommentRequirement", "Comments on getters and setters\"")
        .defaultValue(CommentRequirement.Ignored).build();
    private static final PropertyDescriptor<CommentRequirement> OVERRIDE_CMT_DESCRIPTOR
        = requirementPropertyBuilder("methodWithOverrideCommentRequirement", "Comments on @Override methods")
        .defaultValue(CommentRequirement.Ignored).build();
    private static final PropertyDescriptor<CommentRequirement> HEADER_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("headerCommentRequirement", "Deprecated! Header comments. Please use the property \"classCommentRequired\" instead.").build();
    private static final PropertyDescriptor<CommentRequirement> CLASS_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("classCommentRequirement", "Class comments").build();
    private static final PropertyDescriptor<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("fieldCommentRequirement", "Field comments").build();
    private static final PropertyDescriptor<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("publicMethodCommentRequirement", "Public method and constructor comments").build();
    private static final PropertyDescriptor<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("protectedMethodCommentRequirement", "Protected method constructor comments").build();
    private static final PropertyDescriptor<CommentRequirement> ENUM_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("enumCommentRequirement", "Enum comments").build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("serialVersionUIDCommentRequired", "Serial version UID comments")
        .defaultValue(CommentRequirement.Ignored).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("serialPersistentFieldsCommentRequired", "Serial persistent fields comments")
        .defaultValue(CommentRequirement.Ignored).build();

    /** stores the resolved property values. This is necessary in order to transparently use deprecated properties. */
    private final Map<PropertyDescriptor<CommentRequirement>, CommentRequirement> propertyValues = new HashMap<>();

    public CommentRequiredRule() {
        super(ASTBodyDeclaration.class);
        definePropertyDescriptor(OVERRIDE_CMT_DESCRIPTOR);
        definePropertyDescriptor(ACCESSOR_CMT_DESCRIPTOR);
        definePropertyDescriptor(CLASS_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(HEADER_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(FIELD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        propertyValues.put(ACCESSOR_CMT_DESCRIPTOR, getProperty(ACCESSOR_CMT_DESCRIPTOR));
        propertyValues.put(OVERRIDE_CMT_DESCRIPTOR, getProperty(OVERRIDE_CMT_DESCRIPTOR));
        propertyValues.put(FIELD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(ENUM_CMT_REQUIREMENT_DESCRIPTOR, getProperty(ENUM_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR,
                getProperty(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR,
                getProperty(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR));

        CommentRequirement headerCommentRequirementValue = getProperty(HEADER_CMT_REQUIREMENT_DESCRIPTOR);
        boolean headerCommentRequirementValueOverridden = headerCommentRequirementValue != CommentRequirement.Required;
        CommentRequirement classCommentRequirementValue = getProperty(CLASS_CMT_REQUIREMENT_DESCRIPTOR);
        boolean classCommentRequirementValueOverridden = classCommentRequirementValue != CommentRequirement.Required;

        if (headerCommentRequirementValueOverridden && !classCommentRequirementValueOverridden) {
            LOG.warn("Rule CommentRequired uses deprecated property 'headerCommentRequirement'. "
                    + "Future versions of PMD will remove support for this property. "
                    + "Please use 'classCommentRequirement' instead!");
            propertyValues.put(CLASS_CMT_REQUIREMENT_DESCRIPTOR, headerCommentRequirementValue);
        } else {
            propertyValues.put(CLASS_CMT_REQUIREMENT_DESCRIPTOR, classCommentRequirementValue);
        }
    }

    private void checkCommentMeetsRequirement(Object data, JavadocCommentOwner node,
                                              PropertyDescriptor<CommentRequirement> descriptor) {
        switch (propertyValues.get(descriptor)) {
        case Ignored:
            break;
        case Required:
            if (node.getJavadocComment() == null) {
                commentRequiredViolation(data, node, descriptor);
            }
            break;
        case Unwanted:
            if (node.getJavadocComment() != null) {
                commentRequiredViolation(data, node, descriptor);
            }
            break;
        default:
            break;
        }
    }


    // Adds a violation
    private void commentRequiredViolation(Object data, JavaNode node,
                                          PropertyDescriptor<CommentRequirement> descriptor) {


        addViolationWithMessage(data, node,
            DESCRIPTOR_NAME_TO_COMMENT_TYPE.get(descriptor.name())
            + " are "
            + getProperty(descriptor).label.toLowerCase(Locale.ROOT));
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, CLASS_CMT_REQUIREMENT_DESCRIPTOR);
        return data;
    }


    @Override
    public Object visit(ASTConstructorDeclaration decl, Object data) {
        checkMethodOrConstructorComment(decl, data);
        return data;
    }


    @Override
    public Object visit(ASTMethodDeclaration decl, Object data) {
        if (decl.isOverridden()) {
            checkCommentMeetsRequirement(data, decl, OVERRIDE_CMT_DESCRIPTOR);
        } else if (JavaRuleUtil.isGetterOrSetter(decl)) {
            checkCommentMeetsRequirement(data, decl, ACCESSOR_CMT_DESCRIPTOR);
        } else {
            checkMethodOrConstructorComment(decl, data);
        }
        return data;
    }


    private void checkMethodOrConstructorComment(ASTMethodOrConstructorDeclaration decl, Object data) {
        if (decl.getVisibility() == Visibility.V_PUBLIC) {
            checkCommentMeetsRequirement(data, decl, PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        } else if (decl.getVisibility() == Visibility.V_PROTECTED) {
            checkCommentMeetsRequirement(data, decl, PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        }
    }


    @Override
    public Object visit(ASTFieldDeclaration decl, Object data) {
        if (JavaRuleUtil.isSerialVersionUID(decl)) {
            checkCommentMeetsRequirement(data, decl, SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR);
        } else if (JavaRuleUtil.isSerialPersistentFields(decl)) {
            checkCommentMeetsRequirement(data, decl, SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR);
        } else {
            checkCommentMeetsRequirement(data, decl, FIELD_CMT_REQUIREMENT_DESCRIPTOR);
        }

        return data;
    }


    @Override
    public Object visit(ASTEnumDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        return data;
    }

    private boolean allCommentsAreIgnored() {

        return getProperty(OVERRIDE_CMT_DESCRIPTOR) == CommentRequirement.Ignored
                && getProperty(ACCESSOR_CMT_DESCRIPTOR) == CommentRequirement.Ignored
                && (getProperty(CLASS_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
                        || getProperty(HEADER_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored)
                && getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
                && getProperty(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
                && getProperty(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
                && getProperty(ENUM_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
                && getProperty(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
                && getProperty(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored;
    }

    @Override
    public String dysfunctionReason() {
        return allCommentsAreIgnored() ? "All comment types are ignored" : null;
    }

    private enum CommentRequirement {
        Required("Required"), Ignored("Ignored"), Unwanted("Unwanted");

        private final String label;

        CommentRequirement(String theLabel) {
            label = theLabel;
        }
    }


    // pre-filled builder
    private static GenericPropertyBuilder<CommentRequirement> requirementPropertyBuilder(String name, String commentType) {
        DESCRIPTOR_NAME_TO_COMMENT_TYPE.put(name, commentType);
        return PropertyFactory.enumProperty(name, CommentRequirement.class, cr -> cr.label)
                              .desc(commentType + ". Possible values: " + CollectionUtil.map(CommentRequirement.values(), cr -> cr.label))
                              .defaultValue(CommentRequirement.Required);
    }
}
