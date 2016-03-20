/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Parameter;

public class ASTParameter extends AbstractApexNode<Parameter> {
    public ASTParameter(Parameter parameter) {
        super(parameter);
    }
}
