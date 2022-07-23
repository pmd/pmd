/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

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

    @Override
    public ASTSource parse(ParserTask task) throws ParseException {
        Input.VirtualFile virtualFile = new Input.VirtualFile(task.getFileDisplayName(), task.getSourceText());
        Dialect dialect = ScalaLanguageModule.dialectOf(task.getLanguageVersion());
        Source src = new ScalametaParser(virtualFile, dialect).parseSource();
        ASTSource root = (ASTSource) new ScalaTreeBuilder().build(src);
        root.addTaskInfo(task);
        return root;
    }

}
