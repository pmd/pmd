/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public interface JavaNode extends ScopedNode {

    Object jjtAccept(JavaParserVisitor visitor, Object data);


    Object childrenAccept(JavaParserVisitor visitor, Object data);


    @Override
    Scope getScope();


    @Deprecated
    @InternalApi
    void setScope(Scope scope);
}
