/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.Parser;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.util.document.TextDocument;

import scala.meta.Dialect;
import scala.meta.Source;
import scala.meta.inputs.Input;
import scala.meta.internal.parsers.ScalametaParser;

/**
 * Scala's Parser implementation. Defers parsing to the scala compiler via
 * Scalameta. This parser then wraps all of ScalaMeta's Nodes in Java versions
 * for compatibility.
 */
public final class ScalaParser implements Parser {
    private final Dialect dialect;

    /**
     * Create a parser using the given Scala Dialect and set of parser options.
     *
     * @param scalaDialect
     *            the Scala Dialect for this parser
     */
    public ScalaParser(Dialect scalaDialect) {
        this.dialect = scalaDialect;
    }

    @Override
    public ASTSource parse(ParserTask task) throws ParseException {
        Input.VirtualFile virtualFile = new Input.VirtualFile(task.getFileDisplayName(), task.getSourceText());
        Source src = new ScalametaParser(virtualFile, dialect).parseSource();
        ASTSource root = (ASTSource) new ScalaTreeBuilder().build(src);
        root.setTextDocument(task.getTextDocument());
        return root;
    }

}
