/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
    private static final String METHOD_PROP_DEPRECATION = DEPRECATED_RULE_PROPERTY_MARKER + "When set, methodCommentRequirement is ignored and this property decides whether %s methods should have comments. Will be removed in PMD 8.0.0. Use methodCommentRequirement and minimumVisibility instead.";
    private static final Map<String, CommentRequirement> COMMENT_REQUIREMENT_DEPRECATED_MAPPING =
            CollectionUtil.mapOf("Required", CommentRequirement.REQUIRED, "Ignored", CommentRequirement.IGNORED,
                    "Unwanted", CommentRequirement.UNWANTED);

    private static final PropertyDescriptor<CommentRequirement> ACCESSOR_CMT_DESCRIPTOR
            = requirementPropertyBuilder("accessorCommentRequirement", "Comments on getters and setters",
            CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> OVERRIDE_CMT_DESCRIPTOR
            = requirementPropertyBuilder("methodWithOverrideCommentRequirement", "Comments on @Override methods",
            CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> CLASS_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("classCommentRequirement", "%s type comments",
            DEPRECATED_RULE_PROPERTY_MARKER + "When set, overrides typeCommentRequirement for types other than enums. Will be removed in PMD 8.0. Use typeCommentRequirement instead.",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> TYPE_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("typeCommentRequirement", "%s type comments", "Type comments",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("fieldCommentRequirement", "%s field comments", "Field comments",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("publicMethodCommentRequirement", "Public method and constructor comments",
            String.format(METHOD_PROP_DEPRECATION, "public"), CommentRequirement.REQUIRED).build();
    private static final PropertyDescriptor<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("protectedMethodCommentRequirement", "Protected method and constructor comments",
            String.format(METHOD_PROP_DEPRECATION, "protected"), CommentRequirement.REQUIRED).build();
    private static final PropertyDescriptor<CommentRequirement> PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("packageMethodCommentRequirement", "Package-private method and constructor comments",
            String.format(METHOD_PROP_DEPRECATION, "package-private"), CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("methodCommentRequirement", "%s method and constructor comments",
            "Method and constructor comments, excluding getters, setters and overrides", CommentRequirement.WHEN_VISIBLE).build();

    private static final PropertyDescriptor<CommentRequirement> ENUM_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("enumCommentRequirement", "%s enum comments",
            DEPRECATED_RULE_PROPERTY_MARKER + "When set, overrides typeCommentRequirement for enums. Will be removed in PMD 8.0. Use typeCommentRequirement instead.",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("serialVersionUIDCommentRequired", "Serial version UID comments",
            CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("serialPersistentFieldsCommentRequired", "Serial persistent fields comments",
            CommentRequirement.IGNORED).build();

    private static final PropertyDescriptor<NodeVisibility> MINIMUM_VISIBILITY
            = PropertyFactory.conventionalEnumProperty("minimumVisibility", NodeVisibility.class)
            .desc("Minimum visibility for types and members that need to be documented")
            .defaultValue(NodeVisibility.PROTECTED)
            .build();


    /** stores the resolved property values. This is necessary in order to transparently use deprecated properties. */
    private final Map<PropertyDescriptor<CommentRequirement>, CommentRequirement> propertyValues = new HashMap<>();
    private final Map<Visibility, CommentRequirement> legacyMethodRequirements = new HashMap<>();
    private CommentRequirement enumRequirement;
    private Visibility minimumVisibility = Visibility.V_PROTECTED;

    public CommentRequiredRule() {
        super(ASTBodyDeclaration.class);
        definePropertyDescriptor(OVERRIDE_CMT_DESCRIPTOR);
        definePropertyDescriptor(ACCESSOR_CMT_DESCRIPTOR);
        definePropertyDescriptor(FIELD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(TYPE_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(MINIMUM_VISIBILITY);
        // deprecated properties
        definePropertyDescriptor(CLASS_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        fillPropertyValues();
        if (isPropertyOverridden(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR)
            || isPropertyOverridden(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR)
            || isPropertyOverridden(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR)) {
            fillLegacyPropertyValues();
        }
        minimumVisibility = getProperty(MINIMUM_VISIBILITY).visibility;
    }

    private void fillLegacyPropertyValues() {
        legacyMethodRequirements.put(Visibility.V_PRIVATE, CommentRequirement.IGNORED);
        legacyMethodRequirements.put(Visibility.V_PACKAGE, getProperty(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR));
        legacyMethodRequirements.put(Visibility.V_PROTECTED, getProperty(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR));
        legacyMethodRequirements.put(Visibility.V_PUBLIC, getProperty(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR));
    }

    private void fillPropertyValues() {
        propertyValues.put(ACCESSOR_CMT_DESCRIPTOR, getProperty(ACCESSOR_CMT_DESCRIPTOR));
        propertyValues.put(OVERRIDE_CMT_DESCRIPTOR, getProperty(OVERRIDE_CMT_DESCRIPTOR));
        propertyValues.put(FIELD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(METHOD_CMT_REQUIREMENT_DESCRIPTOR, getProperty(METHOD_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR,
                getProperty(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR));
        propertyValues.put(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR,
                getProperty(SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR));
        if (isPropertyOverridden(CLASS_CMT_REQUIREMENT_DESCRIPTOR)) {
            propertyValues.put(TYPE_CMT_REQUIREMENT_DESCRIPTOR, getProperty(CLASS_CMT_REQUIREMENT_DESCRIPTOR));
        } else {
            propertyValues.put(TYPE_CMT_REQUIREMENT_DESCRIPTOR, getProperty(TYPE_CMT_REQUIREMENT_DESCRIPTOR));
        }
        if (isPropertyOverridden(ENUM_CMT_REQUIREMENT_DESCRIPTOR)) {
            enumRequirement = getProperty(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        }
    }

    private void checkCommentMeetsRequirement(RuleContext ctx, JavadocCommentOwner node,
                                              PropertyDescriptor<CommentRequirement> descriptor,
                                              ModifierOwner modifierOwner) {
        checkCommentMeetsRequirement(ctx, node, descriptor, modifierOwner, propertyValues.get(descriptor));
    }

    private void checkCommentMeetsRequirement(RuleContext ctx,
                                              JavadocCommentOwner node,
                                              PropertyDescriptor<CommentRequirement> descriptor,
                                              ModifierOwner modifierOwner,
                                              CommentRequirement requirement) {
        Visibility effectiveVisibility = modifierOwner.getEffectiveVisibility();
        switch (requirement) {
        case IGNORED:
            break;
        case REQUIRED:
            if (node.getJavadocComment() == null) {
                commentRequiredViolation(ctx, node, "required", descriptor, effectiveVisibility);
            }
            break;
        case UNWANTED:
            if (node.getJavadocComment() != null) {
                commentRequiredViolation(ctx, node, "unwanted", descriptor, effectiveVisibility);
            }
            break;
        case WHEN_VISIBLE:
            if (node.getJavadocComment() == null) {
                if (effectiveVisibility.compareTo(minimumVisibility) >= 0) {
                    commentRequiredViolation(ctx, node, "required", descriptor, effectiveVisibility);
                }
            }
        }
    }


    // Adds a violation
    private void commentRequiredViolation(RuleContext ctx, JavaNode node, String requirementType,
                                          PropertyDescriptor<CommentRequirement> descriptor, ModifierOwner.Visibility effectiveVisibility) {
        ctx.addViolationWithMessage(node,
            String.format(DESCRIPTOR_NAME_TO_COMMENT_TYPE.get(descriptor.name()), StringUtils.capitalize(effectiveVisibility.toString()))
            + " are "
            + requirementType);
    }


    @Override
    public Object visit(ASTClassDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data; 
        checkCommentMeetsRequirement(ctx, decl, TYPE_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkCommentMeetsRequirement(ctx, decl, TYPE_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return null;
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkCommentMeetsRequirement(ctx, decl, TYPE_CMT_REQUIREMENT_DESCRIPTOR, decl);
        return null;
    }


    @Override
    public Object visit(ASTConstructorDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (legacyMethodRequirements.isEmpty()) {
            checkMethodOrConstructorComment(decl, ctx);
        } else {
            CommentRequirement requirement = legacyMethodRequirements.get(decl.getEffectiveVisibility());
            checkCommentMeetsRequirement(ctx, decl, METHOD_CMT_REQUIREMENT_DESCRIPTOR, decl, requirement);
        }
        return null;
    }

    @Override
    public Object visit(ASTMethodDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (decl.isOverride()) {
            checkCommentMeetsRequirement(ctx, decl, OVERRIDE_CMT_DESCRIPTOR, decl);
        } else if (JavaRuleUtil.isGetterOrSetter(decl)) {
            checkCommentMeetsRequirement(ctx, decl, ACCESSOR_CMT_DESCRIPTOR, decl);
        } else if (legacyMethodRequirements.isEmpty()) {
            checkMethodOrConstructorComment(decl, ctx);
        } else {
            CommentRequirement requirement = legacyMethodRequirements.get(decl.getEffectiveVisibility());
            checkCommentMeetsRequirement(ctx, decl, METHOD_CMT_REQUIREMENT_DESCRIPTOR, decl, requirement);
        }
        return null;
    }

    private void checkMethodOrConstructorComment(ASTExecutableDeclaration decl, RuleContext data) {
        checkCommentMeetsRequirement(data, decl, METHOD_CMT_REQUIREMENT_DESCRIPTOR, decl);
    }

    @Override
    public Object visit(ASTFieldDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (JavaRuleUtil.isSerialVersionUID(decl)) {
            checkCommentMeetsRequirement(ctx, decl, SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR, decl);
        } else if (JavaRuleUtil.isSerialPersistentFields(decl)) {
            checkCommentMeetsRequirement(ctx, decl, SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR, decl);
        } else {
            checkCommentMeetsRequirement(ctx, decl, FIELD_CMT_REQUIREMENT_DESCRIPTOR, decl);
        }

        return null;
    }


    @Override
    public Object visit(ASTEnumDeclaration decl, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (enumRequirement != null) {
            checkCommentMeetsRequirement(ctx, decl, TYPE_CMT_REQUIREMENT_DESCRIPTOR, decl, enumRequirement);
        } else {
            checkCommentMeetsRequirement(ctx, decl, TYPE_CMT_REQUIREMENT_DESCRIPTOR, decl);
        }
        return null;
    }

    private boolean allCommentsAreIgnored() {
        fillPropertyValues();
        return propertyValues.values().stream().allMatch(CommentRequirement.IGNORED::equals) && enumRequirement == null;
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
    private static GenericPropertyBuilder<CommentRequirement> requirementPropertyBuilder(String name, String commentType, CommentRequirement defaultValue) {
        return requirementPropertyBuilder(name, commentType, commentType, defaultValue);
    }

    private static GenericPropertyBuilder<CommentRequirement> requirementPropertyBuilder(String name, String commentType, String description, CommentRequirement defaultValue) {
        DESCRIPTOR_NAME_TO_COMMENT_TYPE.put(name, commentType);
        return PropertyFactory.enumPropertyTransitional(name, CommentRequirement.class, COMMENT_REQUIREMENT_DEPRECATED_MAPPING)
                              .desc(description)
                              .defaultValue(defaultValue);
    }
}
