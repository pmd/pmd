/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavadocCommentOwner;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Brian Remedios
 */
public class CommentRequiredRule extends AbstractJavaRulechainRule {
    // Used to pretty print a message
    private static final Map<String, String> DESCRIPTOR_NAME_TO_COMMENT_TYPE = new HashMap<>();
    private static final Map<String, CommentRequirement> COMMENT_REQUIREMENT_DEPRECATED_MAPPING =
            CollectionUtil.mapOf("Required", CommentRequirement.REQUIRED, "Ignored", CommentRequirement.IGNORED,
                    "Unwanted", CommentRequirement.UNWANTED);

    private static final PropertyDescriptor<CommentRequirement> ACCESSOR_CMT_DESCRIPTOR
            = requirementPropertyBuilder("accessorCommentRequirement", "Comments on getters and setters")
            .defaultValue(CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> OVERRIDE_CMT_DESCRIPTOR
            = requirementPropertyBuilder("methodWithOverrideCommentRequirement", "Comments on @Override methods")
            .defaultValue(CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> CLASS_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("classCommentRequirement", "Class comments")
            .defaultValue(CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("fieldCommentRequirement", "Field comments").build();
    private static final PropertyDescriptor<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("publicMethodCommentRequirement",
            DEPRECATED_RULE_PROPERTY_MARKER + "Has no effect and will be removed in PMD 8.0.0. Use methodCommentRequirement and minimumVisibility instead.").build();
    private static final PropertyDescriptor<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("protectedMethodCommentRequirement",
            DEPRECATED_RULE_PROPERTY_MARKER + "Has no effect and will be removed in PMD 8.0.0. Use methodCommentRequirement and minimumVisibility instead.").build();
    private static final PropertyDescriptor<CommentRequirement> PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("packageMethodCommentRequirement",
            DEPRECATED_RULE_PROPERTY_MARKER + "For backwards compatibility, setting this to 'required' overrides minimumVisibility. Will be removed in PMD 8.0. Use methodCommentRequirement and minimumVisibility instead.")
            .defaultValue(CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("methodCommentRequirement", "Method and constructor comments")
            .defaultValue(CommentRequirement.WHEN_VISIBLE).build();

    private static final PropertyDescriptor<CommentRequirement> ENUM_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("enumCommentRequirement", "Enum comments")
            .defaultValue(CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("serialVersionUIDCommentRequired", "Serial version UID comments")
            .defaultValue(CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("serialPersistentFieldsCommentRequired", "Serial persistent fields comments")
            .defaultValue(CommentRequirement.IGNORED).build();

    private static final PropertyDescriptor<NodeVisibility> MINIMUM_VISIBILITY
            = PropertyFactory.conventionalEnumProperty("minimumVisibility", NodeVisibility.class)
            .desc("Minimum visibility for types and members that need to be documented")
            .defaultValue(NodeVisibility.PROTECTED)
            .build();


    /** stores the resolved property values. This is necessary in order to transparently use deprecated properties. */
    private final Map<PropertyDescriptor<CommentRequirement>, CommentRequirement> propertyValues = new HashMap<>();
    private Visibility minimumVisibility = Visibility.V_PROTECTED;

    public CommentRequiredRule() {
        super(ASTBodyDeclaration.class);
        definePropertyDescriptor(OVERRIDE_CMT_DESCRIPTOR);
        definePropertyDescriptor(ACCESSOR_CMT_DESCRIPTOR);
        definePropertyDescriptor(CLASS_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(FIELD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(MINIMUM_VISIBILITY);
        // deprecated properties
        definePropertyDescriptor(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        fillPropertyValues();
        if (isPropertyOverridden(MINIMUM_VISIBILITY)) {
            minimumVisibility = getProperty(MINIMUM_VISIBILITY).visibility;
        } else if (getProperty(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.REQUIRED) {
            minimumVisibility = Visibility.V_PACKAGE;
        }
    }

    private void fillPropertyValues() {
        propertyValues.put(ACCESSOR_CMT_DESCRIPTOR, getProperty(ACCESSOR_CMT_DESCRIPTOR));
        propertyValues.put(OVERRIDE_CMT_DESCRIPTOR, getProperty(OVERRIDE_CMT_DESCRIPTOR));
        propertyValues.put(FIELD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(METHOD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(METHOD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(ENUM_CMT_REQUIREMENT_DESCRIPTOR, getProperty(ENUM_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR,
                getProperty(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR,
                getProperty(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(CLASS_CMT_REQUIREMENT_DESCRIPTOR, getProperty(CLASS_CMT_REQUIREMENT_DESCRIPTOR));
    }

    private void checkCommentMeetsRequirement(Object data, JavadocCommentOwner node,
                                              PropertyDescriptor<CommentRequirement> descriptor,
                                              ModifierOwner modifierOwner) {
        switch (propertyValues.get(descriptor)) {
        case IGNORED:
            break;
        case REQUIRED:
            if (node.getJavadocComment() == null) {
                commentRequiredViolation(data, node, "required", descriptor);
            }
            break;
        case UNWANTED:
            if (node.getJavadocComment() != null) {
                commentRequiredViolation(data, node, "unwanted", descriptor);
            }
            break;
        case WHEN_VISIBLE:
            if (node.getJavadocComment() == null) {
                Visibility effectiveVisibility = modifierOwner.getEffectiveVisibility();
                if (effectiveVisibility.compareTo(minimumVisibility) >= 0) {
                    commentRequiredViolation(data, node, "required", descriptor);
                }
            }

        }
    }


    // Adds a violation
    private void commentRequiredViolation(Object data, JavaNode node, String requirementType,
                                          PropertyDescriptor<CommentRequirement> descriptor) {
        asCtx(data).addViolationWithMessage(node,
            DESCRIPTOR_NAME_TO_COMMENT_TYPE.get(descriptor.name())
            + " are "
            + requirementType);
    }


    @Override
    public Object visit(ASTClassDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, CLASS_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return data;
    }

    @Override
    public Object visit(ASTRecordDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, CLASS_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return data;
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, CLASS_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return data;
    }


    @Override
    public Object visit(ASTConstructorDeclaration decl, Object data) {
        checkMethodOrConstructorComment(decl, data);
        return data;
    }


    @Override
    public Object visit(ASTMethodDeclaration decl, Object data) {
        if (decl.isOverride()) {
            checkCommentMeetsRequirement(data, decl, OVERRIDE_CMT_DESCRIPTOR, decl);
        } else if (JavaRuleUtil.isGetterOrSetter(decl)) {
            checkCommentMeetsRequirement(data, decl, ACCESSOR_CMT_DESCRIPTOR, decl);
        } else {
            checkMethodOrConstructorComment(decl, data);
        }
        return data;
    }


    private void checkMethodOrConstructorComment(ASTExecutableDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, METHOD_CMT_REQUIREMENT_DESCRIPTOR, decl);
    }

    @Override
    public Object visit(ASTFieldDeclaration decl, Object data) {
        if (JavaRuleUtil.isSerialVersionUID(decl)) {
            checkCommentMeetsRequirement(data, decl, SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR, decl);
        } else if (JavaRuleUtil.isSerialPersistentFields(decl)) {
            checkCommentMeetsRequirement(data, decl, SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR, decl);
        } else {
            checkCommentMeetsRequirement(data, decl, FIELD_CMT_REQUIREMENT_DESCRIPTOR, decl);
        }

        return data;
    }


    @Override
    public Object visit(ASTEnumDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, ENUM_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return data;
    }

    private boolean allCommentsAreIgnored() {
        fillPropertyValues();
        return propertyValues.values().stream().allMatch(CommentRequirement.IGNORED::equals);
    }

    @Override
    public String dysfunctionReason() {
        return allCommentsAreIgnored() ? "All comment types are ignored" : null;
    }

    private enum CommentRequirement {
        REQUIRED, IGNORED, WHEN_VISIBLE, UNWANTED
    }

    private enum NodeVisibility {
        PRIVATE(Visibility.V_PRIVATE),
        PACKAGE(Visibility.V_PACKAGE),
        PROTECTED(Visibility.V_PROTECTED),
        PUBLIC(Visibility.V_PUBLIC);

        private final Visibility visibility;

        NodeVisibility(Visibility visibility) {
            this.visibility = visibility;
        }
    }

    // pre-filled builder
    private static GenericPropertyBuilder<CommentRequirement> requirementPropertyBuilder(String name, String commentType) {
        DESCRIPTOR_NAME_TO_COMMENT_TYPE.put(name, commentType);
        return PropertyFactory.enumPropertyTransitional(name, CommentRequirement.class, COMMENT_REQUIREMENT_DEPRECATED_MAPPING)
                              .desc(commentType)
                              .defaultValue(CommentRequirement.REQUIRED);
    }
}
