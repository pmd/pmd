/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.GenericSigBase.LazyClassSignature;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.GenericSigBase.LazyMethodType;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeParamsParser.TypeParametersBuilder;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeSigParser.ParseFunction;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeSigParser.TypeScanner;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.LexicalScope;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Parses type signatures. This is basically convenience wrappers for
 * the different signature parsers ({@link TypeParamsParser},
 * {@link TypeSigParser}).
 */
class SignatureParser {

    private final AsmSymbolResolver loader;

    SignatureParser(AsmSymbolResolver loader) {
        this.loader = loader;
    }

    TypeSystem getTypeSystem() {
        return loader.getTypeSystem();
    }

    public JTypeMirror parseFieldType(LexicalScope scope, String signature) {
        TypeScanner b = new MyTypeBuilder(scope, signature);
        parseFully(b, TypeSigParser::typeSignature);
        return b.pop();
    }


    public JTypeMirror parseTypeVarBound(LexicalScope scope, String boundSig) {
        MyTypeBuilder b = new MyTypeBuilder(scope, boundSig);
        parseFully(b, TypeSigParser::typeVarBound);
        return b.pop();
    }

    public void parseClassSignature(LazyClassSignature type, String genericSig) {
        TypeScanner b = typeParamsWrapper(type, genericSig);
        parseFully(b, TypeSigParser::classHeader);
        type.setSuperInterfaces((List) b.popList());
        type.setSuperClass((JClassType) b.pop());
    }


    public void parseMethodType(LazyMethodType type, String genericSig) {
        TypeScanner b = typeParamsWrapper(type, genericSig);
        parseFully(b, TypeSigParser::methodType);
        type.setExceptionTypes(b.popList());
        type.setReturnType(b.pop());
        type.setParameterTypes(b.popList());
    }

    /**
     * Parses the (optional) type parameters prefixing the given signature,
     * and set the resulting type params on the owner. Returns a type scanner
     * positioned at the start of the rest of the signature, with a
     * full lexical scope.
     *
     * <p>Type var bounds are parsed lazily, since they may refer to type
     * parameters that are further right in the signature. So we build
     * first a lexical scope, then the type var bound can
     */
    private TypeScanner typeParamsWrapper(GenericSigBase<?> owner, String sig) {
        if (TypeParamsParser.hasTypeParams(sig)) {
            TypeParametersBuilder b = new TypeParametersBuilder(owner, sig);

            int tparamsEnd = TypeParamsParser.typeParams(b.start, b);

            List<JTypeVar> sigTypeParams = b.getOwnerTypeParams();
            owner.setTypeParams(sigTypeParams);

            LexicalScope lexScope = owner.getEnclosingTypeParams().andThen(sigTypeParams);

            // the new type builder has the owner's type parameters in scope
            return new MyTypeBuilder(lexScope, b.chars, tparamsEnd, b.end);
        } else {
            owner.setTypeParams(CollectionUtil.emptyList());
            return new MyTypeBuilder(owner.getEnclosingTypeParams(), sig);
        }
    }


    private static void parseFully(TypeScanner scanner, ParseFunction parser) {
        int endOffset = parser.parse(scanner.start, scanner);
        scanner.expectEoI(endOffset);
    }

    private class MyTypeBuilder extends TypeSigParser.TypeScanner {

        MyTypeBuilder(LexicalScope lexicalScope, String descriptor) {
            super(getTypeSystem(), lexicalScope, descriptor);
        }

        MyTypeBuilder(LexicalScope lexicalScope, String chars, int start, int end) {
            super(getTypeSystem(), lexicalScope, chars, start, end);
        }

        @Override
        public @NonNull JClassSymbol makeClassSymbol(String internalName, int observedArity) {
            return loader.resolveFromInternalNameCannotFail(internalName, observedArity);
        }
    }
}
