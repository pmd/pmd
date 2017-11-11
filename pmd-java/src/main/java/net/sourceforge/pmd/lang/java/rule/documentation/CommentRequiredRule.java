/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static net.sourceforge.pmd.lang.java.rule.documentation.CommentRequiredRule.CommentRequirement.Ignored;
import static net.sourceforge.pmd.lang.java.rule.documentation.CommentRequiredRule.CommentRequirement.Required;
import static net.sourceforge.pmd.lang.java.rule.documentation.CommentRequiredRule.CommentRequirement.mappings;
import static net.sourceforge.pmd.lang.java.rule.documentation.CommentRequiredRule.CommentRequirement.values;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.properties.EnumeratedProperty;
import net.sourceforge.pmd.properties.EnumeratedProperty.EnumPBuilder;


/**
 * @author Brian Remedios
 */
public class CommentRequiredRule extends AbstractCommentRule {

    // Used to pretty print a message
    private static final Map<String, String> DESCRIPTOR_NAME_TO_COMMENT_TYPE = new HashMap<>();

    private static final EnumeratedProperty<CommentRequirement> OVERRIDE_CMT_DESCRIPTOR
        = requirementPropertyBuilder("methodWithOverrideRequirement", "Comments on @Override methods")
        .defaultValue(Ignored).build();
    private static final EnumeratedProperty<CommentRequirement> HEADER_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("headerCommentRequirement", "Header comments").uiOrder(1.0f).build();
    private static final EnumeratedProperty<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("fieldCommentRequirement", "Field comments").uiOrder(2.0f).build();
    private static final EnumeratedProperty<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("publicMethodCommentRequirement", "Public method and constructor comments")
        .uiOrder(3.0f).build();
    private static final EnumeratedProperty<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("protectedMethodCommentRequirement", "Protected method constructor comments")
        .uiOrder(4.0f).build();
    private static final EnumeratedProperty<CommentRequirement> ENUM_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("enumCommentRequirement", "Enum comments").uiOrder(5.0f).build();
    private static final EnumeratedProperty<CommentRequirement> SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR
        = requirementPropertyBuilder("serialVersionUIDCommentRequired", "Serial version UID comments")
        .defaultValue(Ignored).uiOrder(6.0f).build();


    public CommentRequiredRule() {
        definePropertyDescriptor(OVERRIDE_CMT_DESCRIPTOR);
        definePropertyDescriptor(HEADER_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(FIELD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        definePropertyDescriptor(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR);
    }


    private void checkCommentMeetsRequirement(Object data, AbstractJavaNode node,
                                              EnumeratedProperty<CommentRequirement> descriptor) {
        switch (getProperty(descriptor)) {
        case Ignored:
            break;
        case Required:
            if (node.comment() == null) {
                commentRequiredViolation(data, node, descriptor);
            }
            break;
        case Unwanted:
            if (node.comment() != null) {
                commentRequiredViolation(data, node, descriptor);
            }
            break;
        default:
            break;
        }
    }


    // Adds a violation
    private void commentRequiredViolation(Object data, AbstractJavaNode node,
                                          EnumeratedProperty<CommentRequirement> descriptor) {


        addViolationWithMessage(data, node,
            DESCRIPTOR_NAME_TO_COMMENT_TYPE.get(descriptor.name()) + " are " + getProperty(descriptor).label.toLowerCase());
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, HEADER_CMT_REQUIREMENT_DESCRIPTOR);
        return super.visit(decl, data);
    }


    @Override
    public Object visit(ASTConstructorDeclaration decl, Object data) {
        checkMethodOrConstructorComment(decl, data);
        return super.visit(decl, data);
    }


    @Override
    public Object visit(ASTMethodDeclaration decl, Object data) {
        if (isAnnotatedOverride(decl)) {
            checkCommentMeetsRequirement(data, decl, OVERRIDE_CMT_DESCRIPTOR);
        } else {
            checkMethodOrConstructorComment(decl, data);
        }
        return super.visit(decl, data);
    }


    private void checkMethodOrConstructorComment(AbstractJavaAccessNode decl, Object data) {
        if (decl.isPublic()) {
            checkCommentMeetsRequirement(data, decl, PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        } else if (decl.isProtected()) {
            checkCommentMeetsRequirement(data, decl, PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
        }
    }


    private boolean isAnnotatedOverride(ASTMethodDeclaration decl) {
        List<ASTMarkerAnnotation> annotations = decl.jjtGetParent().findDescendantsOfType(ASTMarkerAnnotation.class);
        for (ASTMarkerAnnotation ann : annotations) { // TODO consider making a method to get the annotations of a method
            if (ann.getFirstChildOfType(ASTName.class).getImage().equals("Override")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Object visit(ASTFieldDeclaration decl, Object data) {
        if (isSerialVersionUID(decl)) {
            checkCommentMeetsRequirement(data, decl, SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR);
        } else {
            checkCommentMeetsRequirement(data, decl, FIELD_CMT_REQUIREMENT_DESCRIPTOR);
        }

        return super.visit(decl, data);
    }


    private boolean isSerialVersionUID(ASTFieldDeclaration field) {
        return "serialVersionUID".equals(field.getVariableName())
               && field.isStatic()
               && field.isFinal()
               && field.getType() == long.class;
    }


    @Override
    public Object visit(ASTEnumDeclaration decl, Object data) {
        checkCommentMeetsRequirement(data, decl, ENUM_CMT_REQUIREMENT_DESCRIPTOR);
        return super.visit(decl, data);
    }


    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        assignCommentsToDeclarations(cUnit);
        return super.visit(cUnit, data);
    }


    public boolean allCommentsAreIgnored() {

        return getProperty(HEADER_CMT_REQUIREMENT_DESCRIPTOR) == Ignored
               && getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR) == Ignored
               && getProperty(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == Ignored
               && getProperty(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == Ignored
               && getProperty(SERIAL_VERSION_UID_CMT_REQUIREMENT_DESCRIPTOR) == Ignored;
    }


    @Override
    public String dysfunctionReason() {
        return allCommentsAreIgnored() ? "All comment types are ignored" : null;
    }


    enum CommentRequirement {
        Required("Required"), Ignored("Ignored"), Unwanted("Unwanted");

        private static final Map<String, CommentRequirement> MAPPINGS;
        private final String label;

        static {
            Map<String, CommentRequirement> tmp = new HashMap<>();
            for (CommentRequirement r : values()) {
                tmp.put(r.label, r);
            }
            MAPPINGS = Collections.unmodifiableMap(tmp);
        }


        CommentRequirement(String theLabel) {
            label = theLabel;
        }


        public static Map<String, CommentRequirement> mappings() {
            return MAPPINGS;
        }
    }


    // pre-filled builder
    private static EnumPBuilder<CommentRequirement> requirementPropertyBuilder(String name, String commentType) {
        DESCRIPTOR_NAME_TO_COMMENT_TYPE.put(name, commentType);
        return EnumeratedProperty.<CommentRequirement>named(name)
            .desc(commentType + ". Possible values: " + Arrays.toString(values()))
            .mappings(mappings())
            .defaultValue(Required)
            .type(CommentRequirement.class);
    }
}
