/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.compilation.Compilation;

@InternalApi
public final class ApexParser implements Parser {

    public ApexParser() {
        ApexJorjeLogging.disableLogging();
        Locations.useIndexFactory();
    }

    @Override
    public RootNode parse(final ParserTask task) {
        try {
            String sourceCode = task.getSourceText();
            final Compilation astRoot = CompilerService.INSTANCE.parseApex(task.getFileDisplayName(), sourceCode);

            if (astRoot == null) {
                throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
            }

            SourceCodePositioner positioner = new SourceCodePositioner(sourceCode);
            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(sourceCode, task.getCommentMarker(), positioner);
            AbstractApexNode<Compilation> treeRoot = treeBuilder.build(astRoot);
            ASTApexFile fileNode = new ASTApexFile(positioner, task, treeRoot);
            fileNode.setNoPmdComments(treeBuilder.getSuppressMap());
            return fileNode;
        } catch (apex.jorje.services.exception.ParseException e) {
            throw new ParseException(e);
        }
    }
}
