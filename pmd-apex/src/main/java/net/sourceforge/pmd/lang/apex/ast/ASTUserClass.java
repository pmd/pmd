/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserClass;

public class ASTUserClass extends AbstractApexNode<UserClass> {
    public ASTUserClass(UserClass userClass) {
        super(userClass);
    }
}
