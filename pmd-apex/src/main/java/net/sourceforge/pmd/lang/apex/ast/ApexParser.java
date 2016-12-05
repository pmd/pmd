/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;

import apex.jorje.semantic.ast.compilation.Compilation;
import apex.jorje.semantic.ast.compilation.UserClass;
import apex.jorje.semantic.ast.compilation.UserEnum;
import apex.jorje.semantic.ast.compilation.UserInterface;
import apex.jorje.semantic.ast.compilation.UserTrigger;
import apex.jorje.semantic.ast.visitor.AdditionalPassScope;
import apex.jorje.semantic.ast.visitor.AstVisitor;

public class ApexParser {
    protected final ApexParserOptions parserOptions;

    private Map<Integer, String> suppressMap;
    private String suppressMarker = "NOPMD";

    public ApexParser(ApexParserOptions parserOptions) {
        this.parserOptions = parserOptions;

        if (parserOptions.getSuppressMarker() != null) {
            suppressMarker = parserOptions.getSuppressMarker();
        }
    }

    public Compilation parseApex(final String sourceCode) throws ParseException {

        TopLevelVisitor visitor = new TopLevelVisitor();
        CompilerService.INSTANCE.visitAstFromString(sourceCode, visitor);

        Compilation astRoot = visitor.getTopLevel();
        return astRoot;
    }

    public ApexNode<Compilation> parse(final Reader reader) {
        try {
            final String sourceCode = IOUtils.toString(reader);
            final Compilation astRoot = parseApex(sourceCode);
            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(sourceCode);
            suppressMap = new HashMap<>();

            if (astRoot == null) {
                throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
            }

            ApexNode<Compilation> tree = treeBuilder.build(astRoot);
            return tree;
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public Map<Integer, String> getSuppressMap() {
        return suppressMap;
    }

    private class TopLevelVisitor extends AstVisitor<AdditionalPassScope> {
        Compilation topLevel;

        public Compilation getTopLevel() {
            return topLevel;
        }

        @Override
        public void visitEnd(UserClass node, AdditionalPassScope scope) {
            topLevel = node;
        }

        @Override
        public void visitEnd(UserEnum node, AdditionalPassScope scope) {
            topLevel = node;
        }

        @Override
        public void visitEnd(UserInterface node, AdditionalPassScope scope) {
            topLevel = node;
        }

        @Override
        public void visitEnd(UserTrigger node, AdditionalPassScope scope) {
            topLevel = node;
        }
    }
}
