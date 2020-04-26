/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;

/**
 * Acts as a bridge between outer parts (e.g. symbol table) and the restricted
 * access internal API of this package.
 *
 * <p>Note: This is internal API.
 */
@InternalApi
public final class InternalApiBridge {

    private InternalApiBridge() {

    }

    @Deprecated
    public static ASTVariableDeclaratorId newVarId(String image) {
        ASTVariableDeclaratorId varid = new ASTVariableDeclaratorId(JavaParserImplTreeConstants.JJTVARIABLEDECLARATORID);
        varid.setImage(image);
        return varid;
    }

    @Deprecated
    public static ASTPrimaryPrefix newThisSuperPrefix(String image, boolean isThis) {
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(JavaParserImplTreeConstants.JJTPRIMARYPREFIX);
        if (isThis) {
            prefix.setUsesThisModifier();
        } else {
            prefix.setUsesSuperModifier();
        }
        ASTName name = new ASTName(JavaParserImplTreeConstants.JJTNAME);
        name.setImage(image);
        prefix.addChild(name, 0);
        return prefix;
    }

    public static JavaccTokenDocument javaTokenDoc(String fullText) {
        return new JavaTokenDocument(fullText);
    }

}
