/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;

/**
 * Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@InternalApi
public final class InternalApiBridge {

    private InternalApiBridge() {

    }

    public static ASTCompilationUnit parseInternal(String fileName, Reader source, int jdkVersion, boolean preview, ParserOptions options) {
        JavaParser parser = new JavaParser(new JavaCharStream(source));
        String suppressMarker = options.getSuppressMarker();
        if (suppressMarker != null) {
            parser.setSuppressMarker(suppressMarker);
        }
        parser.setJdkVersion(jdkVersion);
        parser.setPreview(preview);

        AbstractTokenManager.setFileName(fileName);
        ASTCompilationUnit acu = parser.CompilationUnit();
        acu.setNoPmdComments(parser.getSuppressMap());
        return acu;
    }

}
