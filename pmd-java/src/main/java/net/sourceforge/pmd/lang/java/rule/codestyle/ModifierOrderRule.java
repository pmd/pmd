/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVoidType;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.OptionalBool;

public class ModifierOrderRule extends AbstractJavaRulechainRule {

    private static final String MSG_KEYWORD_ORDER =
        "Missorted modifiers `{0} {1}`.";

    private static final String MSG_ANNOTATIONS_SHOULD_BE_BEFORE_MODS =
        "Missorted modifiers `{0} {1}`. Annotations should be placed before modifiers.";

    private static final String MSG_TYPE_ANNOT_SHOULD_BE_BEFORE_TYPE =
        "Missorted modifiers `{0} {1}`. Type annotations should be placed before the type they qualify.";

    private static final PropertyDescriptor<TypeAnnotationPosition> TYPE_ANNOT_POLICY
        = PropertyFactory.enumProperty("typeAnnotations", TypeAnnotationPosition.class, TypeAnnotationPosition::label)
                         .desc("Whether type annotations should be placed next to the type they qualify and not before modifiers.")
                         .defaultValue(TypeAnnotationPosition.ANYWHERE)
                         .build();

    public enum TypeAnnotationPosition {
        ON_TYPE,
        ON_DECL,
        ANYWHERE;

        String label() {
            switch (this) {
            case ON_TYPE:
                return "ontype";
            case ON_DECL:
                return "ondecl";
            case ANYWHERE:
                return "anywhere";
            default:
                throw AssertionUtil.shouldNotReachHere("exhaustive switch");
            }
        }
    }

    private TypeAnnotationPosition typeAnnotPosition;

    public ModifierOrderRule() {
        super(ASTModifierList.class);
        definePropertyDescriptor(TYPE_ANNOT_POLICY);
    }

    @Override
    public void start(RuleContext ctx) {
        this.typeAnnotPosition = getProperty(TYPE_ANNOT_POLICY);
    }

    /** Wrapper around a mod to do "double dispatch". */
    abstract static class LastModSeen {
        abstract boolean checkNextKeyword(KwMod next, RuleContext ctx);

        abstract boolean checkNextAnnot(AnnotMod next, RuleContext ctx);

        @Override
        public abstract String toString();
    }

    class KwMod extends LastModSeen {
        private final JModifier mod;
        private final JavaccToken token;
        private final JavaNode reportNode;

        KwMod(JModifier mod, JavaccToken token, JavaNode reportNode) {
            this.mod = mod;
            this.token = token;
            this.reportNode = reportNode;
        }

        @Override
        boolean checkNextKeyword(KwMod next, RuleContext ctx) {
            if (mod.compareTo(next.mod) > 0) {
                ctx.addViolationWithPosition(reportNode, token, MSG_KEYWORD_ORDER, this, next);
                return true;
            }
            return false;
        }

        @Override
        boolean checkNextAnnot(AnnotMod next, RuleContext ctx) {
            // keyword before annot
            if (next.isTypeAnnot != OptionalBool.NO && typeAnnotPosition != TypeAnnotationPosition.ON_DECL) {
                return false;
            }
            ctx.addViolationWithPosition(reportNode, token, MSG_ANNOTATIONS_SHOULD_BE_BEFORE_MODS, this, next);
            return true;

        }

        @Override
        public String toString() {
            return mod.getToken();
        }
    }

    class AnnotMod extends ModifierOrderRule.LastModSeen {
        private final @Nullable LastModSeen previous;
        private final ASTAnnotation annot;
        private final OptionalBool isTypeAnnot;

        AnnotMod(@Nullable LastModSeen previous, ASTAnnotation annot, boolean contextsAcceptsTypeAnnot) {
            this.previous = previous;
            this.annot = annot;
            this.isTypeAnnot = !contextsAcceptsTypeAnnot ? OptionalBool.NO : isTypeAnnotation(annot);
        }


        @Override
        boolean checkNextKeyword(KwMod next, RuleContext ctx) {
            if (isTypeAnnot.isTrue() && typeAnnotPosition == TypeAnnotationPosition.ON_TYPE) {
                ctx.addViolationWithMessage(annot, MSG_TYPE_ANNOT_SHOULD_BE_BEFORE_TYPE, this, next);
                return true;
            }

            if (previous instanceof KwMod) {
                // annotation sandwiched between keywords
                if (isTypeAnnot.isTrue() && typeAnnotPosition != TypeAnnotationPosition.ON_DECL) {
                    ctx.addViolationWithMessage(annot, MSG_TYPE_ANNOT_SHOULD_BE_BEFORE_TYPE, this, next);
                } else {
                    ctx.addViolationWithMessage(annot, MSG_ANNOTATIONS_SHOULD_BE_BEFORE_MODS, previous, this);
                }
                return true;
            }

            return false;
        }

        @Override
        boolean checkNextAnnot(AnnotMod next, RuleContext ctx) {
            // todo we could sort annotations (alphabetically or by length)
            return false;
        }

        @Override
        public String toString() {
            return PrettyPrintingUtil.prettyPrintAnnot(annot);
        }
    }

    @Override
    public Object visit(ASTModifierList modList, Object data) {
        RuleContext ctx = asCtx(data);
        boolean acceptsTypeAnnot = contextCanHaveTypeAnnots(modList);
        ModifierOrderEvents eventHandler = new ModifierOrderEvents() {

            private @Nullable LastModSeen lastModSeen;


            @Override
            public boolean recordAnnotation(ASTAnnotation annot) {
                AnnotMod annotMod = new AnnotMod(lastModSeen, annot, acceptsTypeAnnot);
                if (lastModSeen != null) {
                    if (lastModSeen.checkNextAnnot(annotMod, ctx)) {
                        return true;
                    }
                }
                lastModSeen = annotMod;
                return false;
            }

            @Override
            public boolean recordModifier(JModifier mod, JavaccToken token) {
                KwMod kwMod = new KwMod(mod, token, modList);
                if (lastModSeen != null) {
                    if (lastModSeen.checkNextKeyword(kwMod, ctx)) {
                        return true;
                    }
                }
                lastModSeen = kwMod;
                return false;
            }
        };

        readModifierList(modList, eventHandler);
        return null;
    }

    private static boolean contextCanHaveTypeAnnots(ASTModifierList modList) {
        ASTType followingType = getFollowingType(modList);
        return followingType != null && !(followingType instanceof ASTVoidType)
            || isFollowedByVarKeyword(modList)
            || modList.getParent() instanceof ASTConstructorDeclaration;
    }

    private static OptionalBool isTypeAnnotation(ASTAnnotation node) {
        JTypeDeclSymbol sym = node.getTypeNode().getTypeMirror().getSymbol();
        if (sym instanceof JClassSymbol) {
            return ((JClassSymbol) sym).mayBeTypeAnnotation(node.getLanguageVersion());
        }
        return OptionalBool.UNKNOWN;
    }

    private static @Nullable ASTType getFollowingType(ASTModifierList node) {
        JavaNode nextSibling = node.getNextSibling();
        if (nextSibling instanceof ASTType) {
            return (ASTType) nextSibling;
        }
        return null;
    }

    private static boolean isFollowedByVarKeyword(ASTModifierList node) {
        JavaNode parent = node.getParent();
        if (parent instanceof ASTLambdaParameter) {
            return ((ASTLambdaParameter) parent).hasVarKeyword();
        } else if (parent instanceof ASTLocalVariableDeclaration) {
            return ((ASTLocalVariableDeclaration) parent).isTypeInferred();
        }
        return false;
    }

    /**
     * Receives modifier events in order and checks their order. Methods return
     * true if we found a violation and need to stop.
     */
    interface ModifierOrderEvents {

        /** Record that the next modifier is the given annotation. */
        boolean recordAnnotation(ASTAnnotation annot);

        /** Record that the next modifier is the given one occurring at the given token. */
        boolean recordModifier(JModifier mod, JavaccToken token);
    }

    /**
     * Reads a modifier list in order, to recover the order of declared tokens.
     * Records annotations and modifiers in source order on the given callback interface.
     */
    private static void readModifierList(ASTModifierList modList, ModifierOrderEvents events) {

        JavaccToken tok = modList.getFirstToken();
        final JavaccToken lastTok = modList.getLastToken();

        int nextAnnotIndex = 0;
        List<ASTAnnotation> children = modList.children(ASTAnnotation.class).toList();

        while (tok != lastTok.getNext()) {
            if (tok.isImplicit()) {
                tok = tok.getNext();
                continue;
            }

            if (tok.kind == JavaTokenKinds.AT) {
                // this is an annotation
                assert nextAnnotIndex < children.size() : "annotation token was not parsed?";
                ASTAnnotation annotation = children.get(nextAnnotIndex);
                assert annotation.getFirstToken() == tok : "next annot index didn't match token";

                nextAnnotIndex++;
                if (events.recordAnnotation(annotation)) {
                    return;
                }
                tok = annotation.getLastToken();
            } else {
                JModifier mod = getModFromToken(tok);
                assert mod != null : "Token is not a modifier token? " + tok;
                if (events.recordModifier(mod, tok)) {
                    return;
                }
                if (mod == JModifier.NON_SEALED) {
                    // advance until the sealed token
                    tok = tok.getNext();
                    assert tok.kind == JavaTokenKinds.MINUS;
                    tok = tok.getNext();
                    assert tok.kind == JavaTokenKinds.IDENTIFIER && tok.getImageCs().contentEquals("sealed");
                }
            }

            tok = tok.getNext();
        }


    }

    private static JModifier getModFromToken(JavaccToken tok) {
        switch (tok.kind) {
        case JavaTokenKinds.PUBLIC:
            return JModifier.PUBLIC;
        case JavaTokenKinds.PROTECTED:
            return JModifier.PROTECTED;
        case JavaTokenKinds.PRIVATE:
            return JModifier.PRIVATE;
        case JavaTokenKinds.STATIC:
            return JModifier.STATIC;
        case JavaTokenKinds.FINAL:
            return JModifier.FINAL;
        case JavaTokenKinds.ABSTRACT:
            return JModifier.ABSTRACT;
        case JavaTokenKinds.SYNCHRONIZED:
            return JModifier.SYNCHRONIZED;
        case JavaTokenKinds.NATIVE:
            return JModifier.NATIVE;
        case JavaTokenKinds.TRANSIENT:
            return JModifier.TRANSIENT;
        case JavaTokenKinds.VOLATILE:
            return JModifier.VOLATILE;
        case JavaTokenKinds.STRICTFP:
            return JModifier.STRICTFP;
        case JavaTokenKinds._DEFAULT:
            return JModifier.DEFAULT;
        case JavaTokenKinds.IDENTIFIER:
            if (tok.getImageCs().contentEquals("non")) {
                return JModifier.NON_SEALED;
            } else if (tok.getImageCs().contentEquals("sealed")) {
                return JModifier.SEALED;
            }
        // fallthrough
        default:
            return null;
        }
    }

}
