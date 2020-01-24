/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

/**
 * A Visitor Pattern Interface for the Scala AST.
 *
 * @param <D>
 *            The type of the data input to each visit method
 * @param <R>
 *            the type of the returned data from each visit method
 */
public interface ScalaParserVisitor<D, R> {
    /**
     * Visit an arbitrary Scala Node (any node in the tree).
     *
     * @param node
     *            the node of the tree
     * @param data
     *            context-specific data
     * @return context-specific data
     */
    R visit(ScalaNode<?> node, D data);

    /**
     * Visit the Source Node (the root node of the tree).
     *
     * @param node
     *            the root node of the tree
     * @param data
     *            context-specific data
     * @return context-specific data
     */
    R visit(ASTSource node, D data);

    R visit(ASTCase node, D data);

    R visit(ASTCtorPrimary node, D data);

    R visit(ASTCtorSecondary node, D data);

    R visit(ASTDeclDef node, D data);

    R visit(ASTDeclType node, D data);

    R visit(ASTDeclVal node, D data);

    R visit(ASTDeclVar node, D data);

    R visit(ASTDefnClass node, D data);

    R visit(ASTDefnDef node, D data);

    R visit(ASTDefnMacro node, D data);

    R visit(ASTDefnObject node, D data);

    R visit(ASTDefnTrait node, D data);

    R visit(ASTDefnType node, D data);

    R visit(ASTDefnVal node, D data);

    R visit(ASTDefnVar node, D data);

    R visit(ASTEnumeratorGenerator node, D data);

    R visit(ASTEnumeratorGuard node, D data);

    R visit(ASTEnumeratorVal node, D data);

    R visit(ASTImport node, D data);

    R visit(ASTImporteeName node, D data);

    R visit(ASTImporteeRename node, D data);

    R visit(ASTImporteeUnimport node, D data);

    R visit(ASTImporteeWildcard node, D data);

    R visit(ASTImporter node, D data);

    R visit(ASTInit node, D data);

    R visit(ASTLitBoolean node, D data);

    R visit(ASTLitByte node, D data);

    R visit(ASTLitChar node, D data);

    R visit(ASTLitDouble node, D data);

    R visit(ASTLitFloat node, D data);

    R visit(ASTLitInt node, D data);

    R visit(ASTLitLong node, D data);

    R visit(ASTLitNull node, D data);

    R visit(ASTLitShort node, D data);

    R visit(ASTLitString node, D data);

    R visit(ASTLitSymbol node, D data);

    R visit(ASTLitUnit node, D data);

    R visit(ASTModAbstract node, D data);

    R visit(ASTModAnnot node, D data);

    R visit(ASTModCase node, D data);

    R visit(ASTModContravariant node, D data);

    R visit(ASTModCovariant node, D data);

    R visit(ASTModFinal node, D data);

    R visit(ASTModImplicit node, D data);

    R visit(ASTModInline node, D data);

    R visit(ASTModLazy node, D data);

    R visit(ASTModOverride node, D data);

    R visit(ASTModPrivate node, D data);

    R visit(ASTModProtected node, D data);

    R visit(ASTModSealed node, D data);

    R visit(ASTModValParam node, D data);

    R visit(ASTModVarParam node, D data);

    R visit(ASTNameAnonymous node, D data);

    R visit(ASTNameIndeterminate node, D data);

    R visit(ASTPatAlternative node, D data);

    R visit(ASTPatBind node, D data);

    R visit(ASTPatExtract node, D data);

    R visit(ASTPatExtractInfix node, D data);

    R visit(ASTPatInterpolate node, D data);

    R visit(ASTPatSeqWildcard node, D data);

    R visit(ASTPatTuple node, D data);

    R visit(ASTPatTyped node, D data);

    R visit(ASTPatVar node, D data);

    R visit(ASTPatWildcard node, D data);

    R visit(ASTPatXml node, D data);

    R visit(ASTPkg node, D data);

    R visit(ASTPkgObject node, D data);

    R visit(ASTQuasi node, D data);

    R visit(ASTSelf node, D data);

    R visit(ASTTemplate node, D data);

    R visit(ASTTermAnnotate node, D data);

    R visit(ASTTermApply node, D data);

    R visit(ASTTermApplyInfix node, D data);

    R visit(ASTTermApplyType node, D data);

    R visit(ASTTermApplyUnary node, D data);

    R visit(ASTTermAscribe node, D data);

    R visit(ASTTermAssign node, D data);

    R visit(ASTTermBlock node, D data);

    R visit(ASTTermDo node, D data);

    R visit(ASTTermEta node, D data);

    R visit(ASTTermFor node, D data);

    R visit(ASTTermForYield node, D data);

    R visit(ASTTermFunction node, D data);

    R visit(ASTTermIf node, D data);

    R visit(ASTTermInterpolate node, D data);

    R visit(ASTTermMatch node, D data);

    R visit(ASTTermName node, D data);

    R visit(ASTTermNewAnonymous node, D data);

    R visit(ASTTermNew node, D data);

    R visit(ASTTermParam node, D data);

    R visit(ASTTermPartialFunction node, D data);

    R visit(ASTTermPlaceholder node, D data);

    R visit(ASTTermRepeated node, D data);

    R visit(ASTTermReturn node, D data);

    R visit(ASTTermSelect node, D data);

    R visit(ASTTermSuper node, D data);

    R visit(ASTTermThis node, D data);

    R visit(ASTTermThrow node, D data);

    R visit(ASTTermTry node, D data);

    R visit(ASTTermTryWithHandler node, D data);

    R visit(ASTTermTuple node, D data);

    R visit(ASTTermWhile node, D data);

    R visit(ASTTermXml node, D data);

    R visit(ASTTypeAnd node, D data);

    R visit(ASTTypeAnnotate node, D data);

    R visit(ASTTypeApply node, D data);

    R visit(ASTTypeApplyInfix node, D data);

    R visit(ASTTypeBounds node, D data);

    R visit(ASTTypeByName node, D data);

    R visit(ASTTypeExistential node, D data);

    R visit(ASTTypeFunction node, D data);

    R visit(ASTTypeImplicitFunction node, D data);

    R visit(ASTTypeLambda node, D data);

    R visit(ASTTypeMethod node, D data);

    R visit(ASTTypeName node, D data);

    R visit(ASTTypeOr node, D data);

    R visit(ASTTypeParam node, D data);

    R visit(ASTTypePlaceholder node, D data);

    R visit(ASTTypeProject node, D data);

    R visit(ASTTypeRefine node, D data);

    R visit(ASTTypeRepeated node, D data);

    R visit(ASTTypeSelect node, D data);

    R visit(ASTTypeSingleton node, D data);

    R visit(ASTTypeTuple node, D data);

    R visit(ASTTypeVar node, D data);

    R visit(ASTTypeWith node, D data);

}
