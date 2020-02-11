/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;

import static net.sourceforge.pmd.lang.java.symbols.internal.impl.asm.TypeSigParser.identifier;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.types.JTypeVar;

class TypeParamsParser {

    private TypeParamsParser() {

    }

    static boolean hasTypeParams(String descriptor) {
        return descriptor.charAt(0) == '<';
    }

    static int typeParams(final int start, BaseTypeParamsBuilder b) {
        int cur = b.consumeChar(start, '<', "type parameters");

        do {
            int idStart = cur;
            cur = identifier(cur, b, null);
            String tvarName = b.bufferToString(idStart, cur);

            final int boundStart = cur;

            cur = scanTypeBound(cur, b);

            b.addTypeParam(tvarName, b.bufferToString(boundStart, cur));
        } while (b.charAt(cur) != '>');

        cur = b.consumeChar(cur, '>');

        return cur;
    }


    /**
     * This just skips the bound entirely, they will be parsed lazily
     * later (because they may be self-referential).
     */
    private static int scanTypeBound(final int start, SignatureScanner b) {
        int cur = b.consumeChar(start, ':', "class bound");

        char next = b.charAt(cur);
        if (next == '[' || next == 'L' || next == 'T') {
            // If the character after the ':' class bound marker is not the start of a
            // ReferenceTypeSignature, it means the class bound is empty (which is a valid case).
            cur = skipReferenceType(cur, b);
        }

        while (b.charAt(cur) == ':') {
            cur = b.consumeChar(cur, ':');
            cur = skipReferenceType(cur, b);
        }

        return cur;
    }

    static int skipReferenceType(final int start, SignatureScanner b) {
        int cur = start;
        int targDepth = 0;
        char c;
        while (cur < b.end) {
            c = b.charAt(cur);
            switch (c) {
            case 'T':
                cur = b.nextIndexOf(cur, ';');
                continue;
            case '.':
            case 'L':
                cur = b.nextIndexOfAny(cur, ';', '<');
                continue;
            case '<':
                targDepth++;
                cur++;
                break;
            case '>':
                targDepth--;
                cur++;
                break;
            case ';':
                if (targDepth == 0) {
                    return cur + 1;
                }
                cur++;
                break;
            case '[':
            case '+':
            case '-':
            case '*':
                // pass
                cur++;
                break;
            default:
                throw b.expected("reference type part TL<;>[+-*.", cur);
            }
        }
        return cur;
    }

    /**
     * Boilerplate interface used to mock a builder for testing.
     */
    abstract static class BaseTypeParamsBuilder extends SignatureScanner {

        BaseTypeParamsBuilder(String descriptor) {
            super(descriptor);
        }

        abstract void addTypeParam(String id, String bound);
    }

    static class TypeParametersBuilder extends BaseTypeParamsBuilder {

        private final GenericSigBase<?> sig;
        private final List<JTypeVar> ownTypeParams = new ArrayList<>(1);

        public TypeParametersBuilder(GenericSigBase<?> sig, String descriptor) {
            super(descriptor);
            this.sig = sig;
            assert hasTypeParams(descriptor) : "No type parameters in this signature";
        }

        List<JTypeVar> getOwnerTypeParams() {
            return ownTypeParams;
        }

        @Override
        void addTypeParam(String id, String bound) {
            ownTypeParams.add(new TParamStub(id, sig, bound).getTypeMirror());
        }
    }
}
