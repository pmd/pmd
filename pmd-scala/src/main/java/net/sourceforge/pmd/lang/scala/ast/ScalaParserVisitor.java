/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.scala.ast.nodes.ASTCase;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTCtorPrimary;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTCtorSecondary;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDeclDef;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDeclType;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDeclVal;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDeclVar;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnClass;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnDef;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnMacro;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnObject;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnTrait;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnType;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnVal;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTDefnVar;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTEnumeratorGenerator;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTEnumeratorGuard;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTEnumeratorVal;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTImport;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTImporteeName;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTImporteeRename;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTImporteeUnimport;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTImporteeWildcard;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTImporter;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTInit;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitBoolean;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitByte;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitChar;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitDouble;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitFloat;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitInt;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitLong;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitNull;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitShort;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitString;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitSymbol;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTLitUnit;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModAbstract;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModAnnot;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModCase;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModContravariant;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModCovariant;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModFinal;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModImplicit;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModInline;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModLazy;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModOverride;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModPrivate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModProtected;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModSealed;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModValParam;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTModVarParam;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTNameAnonymous;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTNameIndeterminate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatAlternative;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatBind;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatExtract;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatExtractInfix;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatInterpolate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatSeqWildcard;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatTuple;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatTyped;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatVar;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatWildcard;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPatXml;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPkg;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTPkgObject;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTQuasi;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTSelf;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTSource;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTemplate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermAnnotate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermApply;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermApplyInfix;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermApplyType;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermApplyUnary;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermAscribe;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermAssign;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermBlock;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermDo;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermEta;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermFor;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermForYield;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermFunction;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermIf;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermInterpolate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermMatch;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermName;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermNew;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermNewAnonymous;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermParam;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermPartialFunction;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermPlaceholder;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermRepeated;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermReturn;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermSelect;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermSuper;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermThis;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermThrow;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermTry;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermTryWithHandler;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermTuple;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermWhile;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTermXml;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeAnd;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeAnnotate;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeApply;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeApplyInfix;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeBounds;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeByName;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeExistential;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeFunction;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeImplicitFunction;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeLambda;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeMethod;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeName;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeOr;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeParam;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypePlaceholder;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeProject;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeRefine;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeRepeated;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeSelect;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeSingleton;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeTuple;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeVar;
import net.sourceforge.pmd.lang.scala.ast.nodes.ASTTypeWith;

/**
 * A Visitor Pattern Interface for the Scala AST.
 */
public interface ScalaParserVisitor {
    /**
     * Visit an arbitrary Scala Node (any node in the tree).
     * 
     * @param node
     *            the node of the tree
     * @param data
     *            context-specific data
     * @return context-specific data
     */
    Object visit(ScalaNode<?> node, Object data);

    /**
     * Visit the Source Node (the root node of the tree).
     * 
     * @param node
     *            the root node of the tree
     * @param data
     *            context-specific data
     * @return context-specific data
     */
    Object visit(ASTSource node, Object data);

    Object visit(ASTCase node, Object data);

    Object visit(ASTCtorPrimary node, Object data);

    Object visit(ASTCtorSecondary node, Object data);

    Object visit(ASTDeclDef node, Object data);

    Object visit(ASTDeclType node, Object data);

    Object visit(ASTDeclVal node, Object data);

    Object visit(ASTDeclVar node, Object data);

    Object visit(ASTDefnClass node, Object data);

    Object visit(ASTDefnDef node, Object data);

    Object visit(ASTDefnMacro node, Object data);

    Object visit(ASTDefnObject node, Object data);

    Object visit(ASTDefnTrait node, Object data);

    Object visit(ASTDefnType node, Object data);

    Object visit(ASTDefnVal node, Object data);

    Object visit(ASTDefnVar node, Object data);

    Object visit(ASTEnumeratorGenerator node, Object data);

    Object visit(ASTEnumeratorGuard node, Object data);

    Object visit(ASTEnumeratorVal node, Object data);

    Object visit(ASTImport node, Object data);

    Object visit(ASTImporteeName node, Object data);

    Object visit(ASTImporteeRename node, Object data);

    Object visit(ASTImporteeUnimport node, Object data);

    Object visit(ASTImporteeWildcard node, Object data);

    Object visit(ASTImporter node, Object data);

    Object visit(ASTInit node, Object data);

    Object visit(ASTLitBoolean node, Object data);

    Object visit(ASTLitByte node, Object data);

    Object visit(ASTLitChar node, Object data);

    Object visit(ASTLitDouble node, Object data);

    Object visit(ASTLitFloat node, Object data);

    Object visit(ASTLitInt node, Object data);

    Object visit(ASTLitLong node, Object data);

    Object visit(ASTLitNull node, Object data);

    Object visit(ASTLitShort node, Object data);

    Object visit(ASTLitString node, Object data);

    Object visit(ASTLitSymbol node, Object data);

    Object visit(ASTLitUnit node, Object data);

    Object visit(ASTModAbstract node, Object data);

    Object visit(ASTModAnnot node, Object data);

    Object visit(ASTModCase node, Object data);

    Object visit(ASTModContravariant node, Object data);

    Object visit(ASTModCovariant node, Object data);

    Object visit(ASTModFinal node, Object data);

    Object visit(ASTModImplicit node, Object data);

    Object visit(ASTModInline node, Object data);

    Object visit(ASTModLazy node, Object data);

    Object visit(ASTModOverride node, Object data);

    Object visit(ASTModPrivate node, Object data);

    Object visit(ASTModProtected node, Object data);

    Object visit(ASTModSealed node, Object data);

    Object visit(ASTModValParam node, Object data);

    Object visit(ASTModVarParam node, Object data);

    Object visit(ASTNameAnonymous node, Object data);

    Object visit(ASTNameIndeterminate node, Object data);

    Object visit(ASTPatAlternative node, Object data);

    Object visit(ASTPatBind node, Object data);

    Object visit(ASTPatExtract node, Object data);

    Object visit(ASTPatExtractInfix node, Object data);

    Object visit(ASTPatInterpolate node, Object data);

    Object visit(ASTPatSeqWildcard node, Object data);

    Object visit(ASTPatTuple node, Object data);

    Object visit(ASTPatTyped node, Object data);

    Object visit(ASTPatVar node, Object data);

    Object visit(ASTPatWildcard node, Object data);

    Object visit(ASTPatXml node, Object data);

    Object visit(ASTPkg node, Object data);

    Object visit(ASTPkgObject node, Object data);

    Object visit(ASTQuasi node, Object data);

    Object visit(ASTSelf node, Object data);

    Object visit(ASTTemplate node, Object data);

    Object visit(ASTTermAnnotate node, Object data);

    Object visit(ASTTermApply node, Object data);

    Object visit(ASTTermApplyInfix node, Object data);

    Object visit(ASTTermApplyType node, Object data);

    Object visit(ASTTermApplyUnary node, Object data);

    Object visit(ASTTermAscribe node, Object data);

    Object visit(ASTTermAssign node, Object data);

    Object visit(ASTTermBlock node, Object data);

    Object visit(ASTTermDo node, Object data);

    Object visit(ASTTermEta node, Object data);

    Object visit(ASTTermFor node, Object data);

    Object visit(ASTTermForYield node, Object data);

    Object visit(ASTTermFunction node, Object data);

    Object visit(ASTTermIf node, Object data);

    Object visit(ASTTermInterpolate node, Object data);

    Object visit(ASTTermMatch node, Object data);

    Object visit(ASTTermName node, Object data);

    Object visit(ASTTermNewAnonymous node, Object data);

    Object visit(ASTTermNew node, Object data);

    Object visit(ASTTermParam node, Object data);

    Object visit(ASTTermPartialFunction node, Object data);

    Object visit(ASTTermPlaceholder node, Object data);

    Object visit(ASTTermRepeated node, Object data);

    Object visit(ASTTermReturn node, Object data);

    Object visit(ASTTermSelect node, Object data);

    Object visit(ASTTermSuper node, Object data);

    Object visit(ASTTermThis node, Object data);

    Object visit(ASTTermThrow node, Object data);

    Object visit(ASTTermTry node, Object data);

    Object visit(ASTTermTryWithHandler node, Object data);

    Object visit(ASTTermTuple node, Object data);

    Object visit(ASTTermWhile node, Object data);

    Object visit(ASTTermXml node, Object data);

    Object visit(ASTTypeAnd node, Object data);

    Object visit(ASTTypeAnnotate node, Object data);

    Object visit(ASTTypeApply node, Object data);

    Object visit(ASTTypeApplyInfix node, Object data);

    Object visit(ASTTypeBounds node, Object data);

    Object visit(ASTTypeByName node, Object data);

    Object visit(ASTTypeExistential node, Object data);

    Object visit(ASTTypeFunction node, Object data);

    Object visit(ASTTypeImplicitFunction node, Object data);

    Object visit(ASTTypeLambda node, Object data);

    Object visit(ASTTypeMethod node, Object data);

    Object visit(ASTTypeName node, Object data);

    Object visit(ASTTypeOr node, Object data);

    Object visit(ASTTypeParam node, Object data);

    Object visit(ASTTypePlaceholder node, Object data);

    Object visit(ASTTypeProject node, Object data);

    Object visit(ASTTypeRefine node, Object data);

    Object visit(ASTTypeRepeated node, Object data);

    Object visit(ASTTypeSelect node, Object data);

    Object visit(ASTTypeSingleton node, Object data);

    Object visit(ASTTypeTuple node, Object data);

    Object visit(ASTTypeVar node, Object data);

    Object visit(ASTTypeWith node, Object data);

}
