/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

import com.google.summit.SummitAST;
import com.google.summit.ast.CompilationUnit;

@InternalApi
public final class ApexParser implements Parser {

    public ApexParser() {
        Locations.useIndexFactory();
    }

    @Override
    public ASTApexFile parse(final ParserTask task) {
        final CompilationUnit astRoot = SummitAST.INSTANCE.parseAndTranslate(sourceCode, null);

        if (astRoot == null) {
            throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
        }

        final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(task, (ApexLanguageProcessor) task.getLanguageProcessor());
        return treeBuilder.buildTree(astRoot);
    }
}
