/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;

import java.text.MessageFormat;
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
    private static final String METHOD_PROP_DEPRECATION = DEPRECATED_RULE_PROPERTY_MARKER + "When set, "
            + "minimumVisibility is ignored for methods and this property decides whether {0} methods should have comments. "
            + "Setting this together with methodCOmmentRequirement triggers an error. "
            + "Will be removed in PMD 8.0.0. Use methodCommentRequirement and minimumVisibility instead.";
    private static final Map<String, CommentRequirement> COMMENT_REQUIREMENT_DEPRECATED_MAPPING =
            CollectionUtil.mapOf("Required", CommentRequirement.REQUIRED, "Ignored", CommentRequirement.IGNORED,
                    "Unwanted", CommentRequirement.UNWANTED);

    private static final PropertyDescriptor<NodeVisibility> MINIMUM_VISIBILITY
            = PropertyFactory.conventionalEnumProperty("minimumVisibility", NodeVisibility.class)
            .desc("Minimum visibility for types and members that need to be documented")
            .defaultValue(NodeVisibility.PROTECTED)
            .build();

    private static final PropertyDescriptor<CommentRequirement> ACCESSOR_CMT_DESCRIPTOR
            = requirementPropertyBuilder("accessorCommentRequirement",
            "Comments on {0} getters and setters are {1}", "Comments on getters and setters",
            CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> OVERRIDE_CMT_DESCRIPTOR
            = requirementPropertyBuilder("methodWithOverrideCommentRequirement",
            "Comments on {0} overriding methods are {1}", "Comments on overriding methods",
            CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> TYPE_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("typeCommentRequirement", "{0} type comments are {1}", "Type comments",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("fieldCommentRequirement", "{0} field comments are {1}", "Field comments",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("methodCommentRequirement", "{0} method and constructor comments are {1}",
            "Method and constructor comments, excluding getters, setters and overrides", CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("serialVersionUIDCommentRequired", "Serial version UID comments",
            CommentRequirement.IGNORED).build();
    private static final PropertyDescriptor<CommentRequirement> SERIAL_PERSISTENT_FIELDS_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("serialPersistentFieldsCommentRequired", "Serial persistent fields comments",
            CommentRequirement.IGNORED).build();

    // Deprecated properties: these are not mapped to any message
    private static final PropertyDescriptor<CommentRequirement> CLASS_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("classCommentRequirement", null,
            DEPRECATED_RULE_PROPERTY_MARKER + "Type comments, excluding enums. "
                    + "Setting this together with typeCommentRequirement triggers an error. "
                    + "Will be removed in PMD 8.0. Use typeCommentRequirement instead.",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> ENUM_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("enumCommentRequirement", null,
            DEPRECATED_RULE_PROPERTY_MARKER + "Comments for enums. "
                    + "Setting this together with typeCommentRequirement triggers an error. "
                    + "Will be removed in PMD 8.0. Use typeCommentRequirement instead.",
            CommentRequirement.WHEN_VISIBLE).build();
    private static final PropertyDescriptor<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("publicMethodCommentRequirement", null,
            MessageFormat.format(METHOD_PROP_DEPRECATION, "public"), CommentRequirement.REQUIRED).build();
    private static final PropertyDescriptor<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("protectedMethodCommentRequirement", null,
            MessageFormat.format(METHOD_PROP_DEPRECATION, "protected"), CommentRequirement.REQUIRED).build();
    private static final PropertyDescriptor<CommentRequirement> PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR
            = requirementPropertyBuilder("packageMethodCommentRequirement", null,
            MessageFormat.format(METHOD_PROP_DEPRECATION, "package-private"), CommentRequirement.IGNORED).build();


    /** stores the resolved property values. This is necessary in order to transparently use deprecated properties. */
    private final Map<PropertyDescriptor<CommentRequirement>, CommentRequirement> propertyValues = new HashMap<>();
    private final Map<Visibility, CommentRequirement> legacyMethodRequirements = new HashMap<>();
    private CommentRequirement enumRequirement;
    private Visibility minimumVisibility = Visibility.V_PROTECTED;
    private final Map<PropertyDescriptor<CommentRequirement>, PropertyDescriptor<CommentRequirement>> replacements = CollectionUtil.buildMap(m -> {
        m.put(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR, METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        m.put(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR, METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        m.put(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR, METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        m.put(CLASS_CMT_REQUIREMENT_DESCRIPTOR, TYPE_CMT_REQUIREMENT_DESCRIPTOR);
        m.put(ENUM_CMT_REQUIREMENT_DESCRIPTOR, TYPE_CMT_REQUIREMENT_DESCRIPTOR);
    });

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
        minimumVisibility = getProperty(MINIMUM_VISIBILITY).visibility;
    }

    private void fillLegacyPropertyValues() {
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
            enumRequirement = CommentRequirement.WHEN_VISIBLE;
        } else {
            propertyValues.put(TYPE_CMT_REQUIREMENT_DESCRIPTOR, getProperty(TYPE_CMT_REQUIREMENT_DESCRIPTOR));
        }
        if (isPropertyOverridden(ENUM_CMT_REQUIREMENT_DESCRIPTOR)) {
            enumRequirement = getProperty(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        }
        if (isPropertyOverridden(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR)
                || isPropertyOverridden(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR)
                || isPropertyOverridden(PACKAGE_METHOD_CMT_REQUIREMENT_DESCRIPTOR)) {
            fillLegacyPropertyValues();
            propertyValues.put(METHOD_CMT_REQUIREMENT_DESCRIPTOR, CommentRequirement.IGNORED);
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
            break;
        }
    }


    // Adds a violation
    private void commentRequiredViolation(RuleContext ctx, JavaNode node, String requirementType,
                                          PropertyDescriptor<CommentRequirement> descriptor, ModifierOwner.Visibility effectiveVisibility) {
        String message = MessageFormat.format(DESCRIPTOR_NAME_TO_COMMENT_TYPE.get(descriptor.name()),
                effectiveVisibility.toString(),
                requirementType);
        ctx.addViolationWithMessage(node, StringUtils.capitalize(message));
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
            CommentRequirement requirement = legacyMethodRequirements.getOrDefault(
                    decl.getEffectiveVisibility(), CommentRequirement.IGNORED);
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
            CommentRequirement requirement = legacyMethodRequirements.getOrDefault(
                    decl.getEffectiveVisibility(), CommentRequirement.IGNORED);
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
        return propertyValues.values().stream().allMatch(CommentRequirement.IGNORED::equals)
                && (enumRequirement == null || enumRequirement == CommentRequirement.IGNORED)
                && (legacyMethodRequirements.isEmpty() || legacyMethodRequirements.values().stream().allMatch(CommentRequirement.IGNORED::equals));
    }

    @Override
    public String dysfunctionReason() {
        for (Map.Entry<PropertyDescriptor<CommentRequirement>, PropertyDescriptor<CommentRequirement>> entry
                : replacements.entrySet()) {
            if (isPropertyOverridden(entry.getKey()) && isPropertyOverridden(entry.getValue())) {
                return MessageFormat.format("Property {0} should not be set together with {1}",
                        entry.getKey().name(), entry.getValue().name());
            }
        }

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
        return requirementPropertyBuilder(name, commentType + " are {1}", commentType, defaultValue);
    }

    private static GenericPropertyBuilder<CommentRequirement> requirementPropertyBuilder(String name, String commentType, String description, CommentRequirement defaultValue) {
        if (commentType != null) {
            DESCRIPTOR_NAME_TO_COMMENT_TYPE.put(name, commentType);
        }
        return PropertyFactory.enumPropertyTransitional(name, CommentRequirement.class, COMMENT_REQUIREMENT_DEPRECATED_MAPPING)
                              .desc(description)
                              .defaultValue(defaultValue);
    }
}
