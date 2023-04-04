/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;

/**
 * A Visitor Pattern Interface for the Scala AST.
 *
 * @param <D> The type of the data input to each visit method
 * @param <R> the type of the returned data from each visit method
 */
public interface ScalaParserVisitor<D, R> extends AstVisitor<D, R> {

    /**
     * Visit an arbitrary Scala Node (any node in the tree).
     *
     * @param node the node of the tree
     * @param data context-specific data
     *
     * @return context-specific data
     */
    default R visit(ScalaNode<?> node, D data) {
        return visitNode(node, data);
    }


    /**
     * Visit the Source Node (the root node of the tree).
     *
     * @param node the root node of the tree
     * @param data context-specific data
     *
     * @return context-specific data
     */
    default R visit(ASTSource node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTCase node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTCtorPrimary node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTCtorSecondary node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDeclDef node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDeclType node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDeclVal node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDeclVar node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnClass node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnDef node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnMacro node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnObject node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnTrait node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnType node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnVal node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTDefnVar node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTEnumeratorGenerator node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTEnumeratorGuard node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTEnumeratorVal node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTImport node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTImporteeName node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTImporteeRename node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTImporteeUnimport node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTImporteeWildcard node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTImporter node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTInit node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitBoolean node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitByte node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitChar node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitDouble node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitFloat node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitInt node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitLong node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitNull node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitShort node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitString node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitSymbol node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTLitUnit node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModAbstract node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModAnnot node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModCase node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModContravariant node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModCovariant node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModFinal node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModImplicit node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModInline node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModLazy node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModOverride node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModPrivate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModProtected node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModSealed node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModValParam node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTModVarParam node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTNameAnonymous node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTNameIndeterminate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatAlternative node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatBind node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatExtract node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatExtractInfix node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatInterpolate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatSeqWildcard node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatTuple node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatTyped node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatVar node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatWildcard node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPatXml node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPkg node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTPkgObject node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTQuasi node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTSelf node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTemplate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermAnnotate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermApply node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermApplyInfix node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermApplyType node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermApplyUnary node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermAscribe node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermAssign node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermBlock node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermDo node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermEta node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermFor node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermForYield node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermFunction node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermIf node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermInterpolate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermMatch node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermName node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermNewAnonymous node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermNew node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermParam node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermPartialFunction node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermPlaceholder node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermRepeated node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermReturn node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermSelect node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermSuper node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermThis node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermThrow node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermTry node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermTryWithHandler node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermTuple node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermWhile node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTermXml node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeAnd node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeAnnotate node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeApply node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeApplyInfix node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeBounds node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeByName node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeExistential node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeFunction node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeImplicitFunction node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeLambda node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeMethod node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeName node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeOr node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeParam node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypePlaceholder node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeProject node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeRefine node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeRepeated node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeSelect node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeSingleton node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeTuple node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeVar node, D data) {
        return visit((ScalaNode<?>) node, data);
    }


    default R visit(ASTTypeWith node, D data) {
        return visit((ScalaNode<?>) node, data);
    }

}
