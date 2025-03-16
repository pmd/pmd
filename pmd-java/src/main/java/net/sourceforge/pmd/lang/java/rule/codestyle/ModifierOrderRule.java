package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayType;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
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
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.AssertionUtil;

public class ModifierOrderRule extends AbstractJavaRulechainRule {

    private static final String MSG_TOKEN_ORDER =
        "Missorted modifiers `{0} {1}`.";

    private static final String MSG_ANNOTATIONS_SHOULD_BE_BEFORE_MODS =
        "Missorted modifiers `{0} {1}`. Annotations should be placed before modifiers.";

    private static final String MSG_TYPE_ANNOT_SHOULD_BE_AFTER_MODS =
        "Missorted modifiers `{0} {1}`. Type annotations should be placed before the type they qualify.";

    private static final PropertyDescriptor<TypeAnnotationPolicy> TYPE_ANNOT_POLICY
        = PropertyFactory.enumProperty("typeAnnotations", TypeAnnotationPolicy.class, TypeAnnotationPolicy::label)
                         .desc("Whether type annotations should be placed next to the type they qualify and not before modifiers.")
                         .defaultValue(TypeAnnotationPolicy.EITHER)
                         .build();

    public enum TypeAnnotationPolicy {
        ON_TYPE,
        ON_DECL,
        EITHER;

        String label() {
            switch (this) {
            case ON_TYPE:
                return "on type";
            case ON_DECL:
                return "on decl";
            case EITHER:
                return "anywhere";
            }
            throw AssertionUtil.shouldNotReachHere("exhaustive switch");
        }
    }

    private TypeAnnotationPolicy sortTypeAnnotations;

    public ModifierOrderRule() {
        super(ASTModifierList.class);
        definePropertyDescriptor(TYPE_ANNOT_POLICY);
    }

    @Override
    public void start(RuleContext ctx) {
        this.sortTypeAnnotations = getProperty(TYPE_ANNOT_POLICY);
    }


    @Override
    public Object visit(ASTModifierList modList, Object data) {
        RuleContext ctx = asCtx(data);
        ModifierOrderEvents eventHandler = new ModifierOrderEvents() {

            private @Nullable JModifier lastModSeen;
            private @Nullable ASTAnnotation typeAnnotationSeen;

            private final TypeAnnotContext typeAnnotContext = new TypeAnnotContext(modList);

            @Override
            public boolean recordAnnotation(ASTAnnotation annot) {
                if (sortTypeAnnotations == TypeAnnotationPolicy.ON_TYPE
                    && typeAnnotContext.acceptsTypeAnnots() && isTypeAnnotation(annot)) {
                    typeAnnotationSeen = annot;
                    return false;
                }

                if (checkTypeAnnotationProblem()) {
                    return true;
                }

                if (lastModSeen != null) {
                    // this annotation comes after some modifiers
                    String annotString = PrettyPrintingUtil.prettyPrintAnnot(annot);
                    ctx.addViolationWithMessage(annot, MSG_ANNOTATIONS_SHOULD_BE_BEFORE_MODS, annotString, lastModSeen);
                    return true;
                }
                return false;
            }

            @Override
            public boolean recordModifier(JModifier mod, JavaccToken token) {
                if (checkTypeAnnotationProblem()) {
                    return true;
                }
                if (lastModSeen != null && mod.compareTo(lastModSeen) < 0) {
                    ctx.addViolationWithPosition(modList, token, MSG_TOKEN_ORDER, lastModSeen, mod);
                    return true;
                }
                lastModSeen = mod;
                return false;
            }

            private boolean checkTypeAnnotationProblem() {
                if (sortTypeAnnotations == TypeAnnotationPolicy.ON_TYPE && typeAnnotationSeen != null) {
                    String annotString = PrettyPrintingUtil.prettyPrintAnnot(typeAnnotationSeen);
                    String typeStr = typeAnnotContext.getTypeNodeDescription();
                    String note = typeAnnotContext.getSyntaxNote();
                    // this modifier comes after a type annotation. Report the annotation though
                    ctx.addViolationWithMessage(typeAnnotationSeen, MSG_TYPE_ANNOT_SHOULD_BE_AFTER_MODS, annotString, typeStr, note);
                    return true;
                }
                return false;
            }
        };

        readModifierList(modList, eventHandler);
        return null;
    }


    // https://docs.oracle.com/javase/specs/jls/se22/html/jls-9.html#jls-9.6.4.1
    static class TypeAnnotContext {

        private final boolean isTypeAnnotContext;
        private final ASTModifierList modList;
        private final @Nullable ASTType followingType;
        private final boolean hasExtraDimensions;
        private final boolean followedByVar;


        TypeAnnotContext(ASTModifierList modList) {
            this.modList = modList;
            followingType = getFollowingType(modList);
            followedByVar = isFollowedByVarKeyword(modList);
            hasExtraDimensions = hasFollowingExtraBracketPairs(modList);
            this.isTypeAnnotContext =
                followingType != null && !(followingType instanceof ASTVoidType)
                    || followedByVar
                    || modList.getParent() instanceof ASTConstructorDeclaration;
        }

        private boolean acceptsTypeAnnots() {
            return isTypeAnnotContext;
        }

        String getTypeNodeDescription() {
            assert acceptsTypeAnnots();
            if (followedByVar) {
                return "var";
            } else if (followingType != null) {
                return PrettyPrintingUtil.prettyPrintType(followingType);
            } else if (modList.getParent() instanceof ASTConstructorDeclaration) {
                return ((ASTConstructorDeclaration) modList.getParent()).getName();
            }
            throw AssertionUtil.shouldNotReachHere("not a type annot context");
        }

        String getSyntaxNote() {
            assert acceptsTypeAnnots();
            if (hasExtraDimensions || followingType instanceof ASTArrayType) {
                return "note: type annotations on arrays are placed right before the corresponding square bracket pair, eg `int @A []` or `int varname @A[]`. To annotate the element type, write `@A int[]`.";
            } else if (followingType instanceof ASTClassType) {
                ASTClassType classType = (ASTClassType) followingType;
                if (classType.isFullyQualified() || classType.getQualifier() != null) {
                    return "note: type annotations class types that have a qualifier must go before the type simple name, eg `java.util.@A List` or `Map.@Nullable Entry<...>`.";
                }
            }
            return "";
        }
    }


    private static boolean isTypeAnnotation(ASTAnnotation node) {
        JTypeDeclSymbol sym = node.getTypeNode().getTypeMirror().getSymbol();
        if (sym instanceof JClassSymbol && !sym.isUnresolved()) {
            JClassSymbol classSym = (JClassSymbol) sym;
            SymAnnot target = classSym.getDeclaredAnnotation(Target.class);
            if (target == null) {
                return false;
            }
            SymbolicValue value = target.getAttribute("value");
            if (!(value instanceof SymArray)) {
                return false;
            }

            // note that the annotation could apply to BOTH the type and the declaration,
            // and in that case we would need to pick a side. Here I pick the side of the
            // type annotation for simplicity.
            return ((SymArray) value).containsValue(ElementType.TYPE_USE);
        }
        return false;
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

    private static boolean hasFollowingExtraBracketPairs(ASTModifierList node) {
        JavaNode parent = node.getParent();
        if (parent instanceof ASTLocalVariableDeclaration) {
            return ((ASTLocalVariableDeclaration) parent).getVarIds().any(it -> it.getExtraDimensions() != null);
        } else if (parent instanceof ASTFieldDeclaration) {
            return ((ASTFieldDeclaration) parent).getVarIds().any(it -> it.getExtraDimensions() != null);
        } else if (parent instanceof ASTLambdaParameter) {
            return ((ASTLambdaParameter) parent).getVarId().getExtraDimensions() != null;
        } else if (parent instanceof ASTFormalParameter) {
            return ((ASTFormalParameter) parent).getVarId().getExtraDimensions() != null;
        } else if (parent instanceof ASTMethodDeclaration) {
            return ((ASTMethodDeclaration) parent).getExtraDimensions() != null;
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
        }
        return null;
    }

}
