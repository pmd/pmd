/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;

import static java.util.Collections.emptyList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.LexicalScope;
import net.sourceforge.pmd.lang.java.types.SubstVar;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Implementation of the signature parser.
 */
class TypeSigParser {


    static int classHeader(final int start, TypeScanner b) {
        int cur = classType(start, b); // superclass

        if (b.charAt(cur) == 'L') {
            List<JTypeMirror> superItfs = new ArrayList<>(1);
            do {
                cur = classType(cur, b);
                superItfs.add(b.pop());
            } while (b.charAt(cur) == 'L');
            b.pushList(superItfs);
        } else {
            b.pushList(emptyList());
        }
        return cur;
    }

    static int methodType(final int start, TypeScanner b) {
        int cur = parameterTypes(start, b);
        cur = typeSignature(cur, b, true); // return type
        cur = throwsSignaturesOpt(cur, b);
        return cur;
    }

    private static int parameterTypes(int start, TypeScanner b) {
        int cur = b.consumeChar(start, '(', "parameter types");
        if (b.charAt(cur) == ')') {
            b.pushList(emptyList()); // empty parameters
        } else {
            List<JTypeMirror> params = new ArrayList<>(1);
            do {
                cur = typeSignature(cur, b);
                params.add(b.pop());
            } while (b.charAt(cur) != ')');
            b.pushList(params);
        }
        cur = b.consumeChar(cur, ')');
        return cur;
    }

    private static int throwsSignaturesOpt(final int start, TypeScanner b) {
        int cur = start;
        if (b.charAt(cur) == '^') {
            List<JTypeMirror> thrown = new ArrayList<>(1);
            do {
                cur = b.consumeChar(cur, '^', "throws signature");
                char next = b.charAt(cur);
                if (next != 'T' && next != 'L') {
                    // shouldn't be a base type
                    throw b.expected("an exception type", cur);
                }
                cur = typeSignature(cur, b);
                thrown.add(b.pop());
            } while (b.charAt(cur) == '^');
            b.pushList(thrown);
        } else {
            b.pushList(emptyList());
        }
        return cur;
    }

    static int typeVarBound(final int start, TypeScanner b) {
        List<JTypeMirror> bounds = new ArrayList<>();
        int cur = b.consumeChar(start, ':', "class bound");

        char next = b.charAt(cur);
        if (next == '[' || next == 'L' || next == 'T') {
            // If the character after the ':' class bound marker is not the start of a
            // ReferenceTypeSignature, it means the class bound is empty (which is a valid case).
            cur = typeSignature(cur, b);
            bounds.add(b.pop());
        }

        while (b.charAt(cur) == ':') {
            cur = b.consumeChar(cur, ':');
            cur = typeSignature(cur, b);
            bounds.add(b.pop());
        }

        if (bounds.isEmpty()) {
            b.push(b.ts.OBJECT);
        } else {
            b.push(b.ts.intersect(bounds));
        }
        return cur;
    }

    static int typeSignature(final int start, TypeScanner b) {
        return typeSignature(start, b, false);
    }

    private static int typeSignature(final int start, TypeScanner b, boolean acceptVoid) {
        char firstChar = b.charAt(start);
        switch (firstChar) {
        case 'V':
            if (!acceptVoid) {
                throw b.expected("a type, got void", start);
            }
            // intentional fallthrough
        case 'Z':
        case 'C':
        case 'B':
        case 'S':
        case 'I':
        case 'F':
        case 'J':
        case 'D':
            b.push(b.getBaseType(firstChar));
            return start + 1;
        case '[':
            return arrayType(start, b);
        case 'L':
            return classType(start, b);
        case 'T':
            return typeVar(start, b);
        default:
            throw b.expected("type", start);
        }
    }

    private static int classType(final int start, TypeScanner b) {

        StringBuilder internalName = new StringBuilder();

        int cur = b.consumeChar(start, 'L', "object type");
        cur = classId(cur, b, internalName);
        cur = typeArgsOpt(cur, b);

        JClassType t = b.makeClassType(internalName.toString(), b.popList());

        while (b.charAt(cur) == '.') {
            internalName.append('.');
            cur += 1;
            cur = identifier(cur, b, internalName);
            cur = typeArgsOpt(cur, b);

            t = b.parameterize(t, internalName.toString(), b.popList());
        }

        b.push(t);
        return b.consumeChar(cur, ';', "semicolon");
    }

    private static int typeArgsOpt(final int start, TypeScanner b) {
        if (b.charAt(start) == '<') {
            List<JTypeMirror> l = new ArrayList<>(2);
            int cur = b.consumeChar(start, '<');
            while (b.charAt(cur) != '>') {
                cur = typeArg(cur, b);
                l.add(b.pop());
            }
            cur = b.consumeChar(cur, '>');
            b.pushList(l);
            return cur;
        } else {
            b.pushList(emptyList());
            return start;
        }
    }

    private static int typeArg(final int start, TypeScanner b) {
        int cur = start;
        char firstChar = b.charAt(cur);
        switch (firstChar) {
        case '*':
            b.push(b.ts.UNBOUNDED_WILD);
            return cur + 1;
        case '+':
        case '-':
            cur = typeSignature(cur + 1, b);
            b.push(b.ts.wildcard(firstChar == '+', b.pop()));
            return cur;
        default:
            return typeSignature(cur, b);
        }
    }


    private static int arrayType(final int start, TypeScanner b) {
        int cur = b.consumeChar(start, '[', "array type");
        cur = typeSignature(cur, b);
        b.push(b.ts.arrayType(b.pop(), 1));
        return cur;
    }


    private static int typeVar(final int start, TypeScanner b) {
        int cur = b.consumeChar(start, 'T', "type variable");
        StringBuilder nameBuilder = new StringBuilder();

        cur = identifier(cur, b, nameBuilder);
        cur = b.consumeChar(cur, ';');

        b.push(b.lookupTvar(nameBuilder.toString()));
        return cur;
    }


    private static int classId(final int start, SignatureScanner b, StringBuilder internalName) {
        int cur = start;
        cur = identifier(cur, b, null);
        while (b.charAt(cur) == '/') { // package specifier
            cur = cur + 1; // the slash
            cur = identifier(cur, b, null);
        }
        b.dumpChars(start, cur, internalName);
        return cur;
    }

    static int identifier(final int start, SignatureScanner b, @Nullable StringBuilder sb) {
        int cur = start;
        if (!isIdentifierChar(b.charAt(cur))) {
            throw b.expected("identifier", cur);
        }
        do {
            cur++;
        } while (isIdentifierChar(b.charAt(cur)));

        if (sb != null) {
            b.dumpChars(start, cur, sb);
        }
        return cur;
    }


    private static boolean isIdentifierChar(char c) {
        switch (c) {
        case '.':
        case ';':
        case ':':
        case '[':
        case '/':
        case '<':
        case '>':
            return false;
        default:
            return true;
        }
    }

    interface ParseFunction {

        int parse(int offset, TypeScanner scanner);
    }

    abstract static class TypeScanner extends SignatureScanner {

        // those stacks usually are 0..1
        private final ArrayDeque<JTypeMirror> typeStack = new ArrayDeque<>(0);
        private final ArrayDeque<List<JTypeMirror>> listStack = new ArrayDeque<>(0);

        private final TypeSystem ts;
        private final LexicalScope lexicalScope;

        TypeScanner(TypeSystem ts, LexicalScope lexicalScope, String descriptor) {
            super(descriptor);
            this.ts = ts;
            this.lexicalScope = lexicalScope;
        }

        TypeScanner(TypeSystem ts, LexicalScope lexicalScope, char[] chars, int start, int end) {
            super(chars, start, end);
            this.ts = ts;
            this.lexicalScope = lexicalScope;
        }

        void pushList(List<JTypeMirror> l) {
            listStack.push(l);
        }

        void push(JTypeMirror mirror) {
            typeStack.push(mirror);
        }

        JTypeMirror pop() {
            return typeStack.pop();
        }

        List<JTypeMirror> popList() {
            return listStack.pop();
        }

        /**
         * Makes a class symbol from its internal name. This should return
         * non-null, if the symbol is not found (linkage error) then return
         * an unresolved symbol.
         */
        @NonNull
        public abstract JClassSymbol makeClassSymbol(String internalName, int observedArity);


        public JTypeMirror getBaseType(char baseType) {
            // this is used for tests but is dead code in production.
            // the override uses a more efficient implementation
            // TODO the override is more elegant, but probably not significantly more efficient
            switch (baseType) {
            case 'V': return ts.NO_TYPE;
            case 'Z': return ts.BOOLEAN;
            case 'C': return ts.CHAR;
            case 'B': return ts.BYTE;
            case 'S': return ts.SHORT;
            case 'I': return ts.INT;
            case 'F': return ts.FLOAT;
            case 'J': return ts.LONG;
            case 'D': return ts.DOUBLE;
            default: throw new IllegalArgumentException("'" + baseType + "' is not a valid base type descriptor");
            }
        }

        public JTypeMirror lookupTvar(String name) {
            @Nullable SubstVar mapped = lexicalScope.apply(name);
            if (mapped == null) {
                throw new IllegalArgumentException(
                    "The lexical scope " + lexicalScope + " does not contain an entry for type variable " + name
                );
            }
            return mapped;
        }

        public JClassType makeClassType(String internalName, List<JTypeMirror> targs) {
            return (JClassType) ts.parameterise(makeClassSymbol(internalName, targs.size()), targs);
        }

        public JClassType parameterize(JClassType owner, String internalName, List<JTypeMirror> targs) {
            return owner.selectInner(makeClassSymbol(internalName, targs.size()), targs);
        }

    }

}
