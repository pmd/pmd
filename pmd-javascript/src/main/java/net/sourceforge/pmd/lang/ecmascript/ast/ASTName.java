/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Name;

public class ASTName extends AbstractEcmascriptNode<Name> {
    public ASTName(Name name) {
        super(name);
        super.setImage(name.getIdentifier());
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getIdentifier() {
        return node.getIdentifier();
    }

    public boolean isLocalName() {
        return node.isLocalName();
    }

    public boolean isGlobalName() {
        return !node.isLocalName();
    }

    /**
     * Returns whether this name node is the name of a function declaration.
     *
     * @return <code>true</code> if name of a function declaration,
     *         <code>false</code> otherwise.
     */
    public boolean isFunctionNodeName() {
        return getParent() instanceof ASTFunctionNode
                && ((ASTFunctionNode) getParent()).getFunctionName() == this;
    }

    /**
     * Returns whether this name node is the name of a function declaration
     * parameter.
     *
     * @return <code>true</code> if name of a function declaration parameter,
     *         <code>false</code> otherwise.
     */
    public boolean isFunctionNodeParameter() {
        if (getParent() instanceof ASTFunctionNode) {
            ASTFunctionNode functionNode = (ASTFunctionNode) getParent();
            for (int i = 0; i < functionNode.getNumParams(); i++) {
                if (functionNode.getParam(i) == this) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether this name node is the name of a function call.
     *
     * @return <code>true</code> if name of a function call, <code>false</code>
     *         otherwise.
     */
    public boolean isFunctionCallName() {
        return getParent() instanceof ASTFunctionCall && ((ASTFunctionCall) getParent()).getTarget() == this;
    }

    /**
     * Returns whether this name node is the name of a variable declaration.
     *
     * @return <code>true</code> if name of a variable declaration,
     *         <code>false</code> otherwise.
     */
    public boolean isVariableDeclaration() {
        return getParent() instanceof ASTVariableInitializer
                && ((ASTVariableInitializer) getParent()).getTarget() == this;
    }
}
