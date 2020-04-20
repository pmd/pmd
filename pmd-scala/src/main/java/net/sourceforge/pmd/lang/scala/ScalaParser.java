/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.scala.ast.ASTSource;

import scala.meta.Dialect;
import scala.meta.Source;
import scala.meta.inputs.Input;
import scala.meta.internal.parsers.ScalametaParser;

/**
 * Scala's Parser implementation. Defers parsing to the scala compiler via
 * Scalameta. This parser then wraps all of ScalaMeta's Nodes in Java versions
 * for compatibility.
 */
public class ScalaParser extends AbstractParser {
    private final Dialect dialect;

    /**
     * Create a parser using the given Scala Dialect and set of parser options.
     *
     * @param scalaDialect
     *            the Scala Dialect for this parser
     * @param parserOptions
     *            any additional options for this parser
     */
    public ScalaParser(Dialect scalaDialect, ParserOptions parserOptions) {
        super(parserOptions);
        this.dialect = scalaDialect;
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public ASTSource parse(String fileName, Reader source) throws ParseException {
        Input.VirtualFile virtualFile;
        try {
            String sourceString = IOUtils.toString(source);
            virtualFile = new Input.VirtualFile(fileName, sourceString);
        } catch (IOException e) {
            throw new ParseException(e);
        }
        Source src = new ScalametaParser(virtualFile, dialect).parseSource();
        return (ASTSource) new ScalaTreeBuilder().build(src);
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>(); // FIXME;
    }

    @Override
    protected TokenManager createTokenManager(Reader source) {
        return null;
    }

}
