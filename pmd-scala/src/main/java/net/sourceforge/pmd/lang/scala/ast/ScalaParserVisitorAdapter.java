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

import scala.meta.Case;
import scala.meta.Ctor;
import scala.meta.Decl;
import scala.meta.Defn;
import scala.meta.Enumerator;
import scala.meta.Import;
import scala.meta.Importee;
import scala.meta.Importer;
import scala.meta.Init;
import scala.meta.Lit;
import scala.meta.Mod;
import scala.meta.Name;
import scala.meta.Pat;
import scala.meta.Pkg;
import scala.meta.Self;
import scala.meta.Source;
import scala.meta.Template;
import scala.meta.Term;
import scala.meta.Tree.Quasi;
import scala.meta.Type;

/**
 * An Adapter for the Scala Parser that implements the Visitor Pattern.
 */
public class ScalaParserVisitorAdapter implements ScalaParserVisitor {

    @Override
    public Object visit(ScalaNode<?> node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTSource node, Object data) {
        return visit((ScalaNode<Source>) node, data);
    }

    @Override
    public Object visit(ASTCase node, Object data) {
        return visit((ScalaNode<Case>) node, data);
    }

    @Override
    public Object visit(ASTCtorPrimary node, Object data) {
        return visit((ScalaNode<Ctor.Primary>) node, data);
    }

    @Override
    public Object visit(ASTCtorSecondary node, Object data) {
        return visit((ScalaNode<Ctor.Secondary>) node, data);
    }

    @Override
    public Object visit(ASTDeclDef node, Object data) {
        return visit((ScalaNode<Decl.Def>) node, data);
    }

    @Override
    public Object visit(ASTDeclType node, Object data) {
        return visit((ScalaNode<Decl.Type>) node, data);
    }

    @Override
    public Object visit(ASTDeclVal node, Object data) {
        return visit((ScalaNode<Decl.Val>) node, data);
    }

    @Override
    public Object visit(ASTDeclVar node, Object data) {
        return visit((ScalaNode<Decl.Var>) node, data);
    }

    @Override
    public Object visit(ASTDefnClass node, Object data) {
        return visit((ScalaNode<Defn.Class>) node, data);
    }

    @Override
    public Object visit(ASTDefnDef node, Object data) {
        return visit((ScalaNode<Defn.Def>) node, data);
    }

    @Override
    public Object visit(ASTDefnMacro node, Object data) {
        return visit((ScalaNode<Defn.Macro>) node, data);
    }

    @Override
    public Object visit(ASTDefnObject node, Object data) {
        return visit((ScalaNode<Defn.Object>) node, data);
    }

    @Override
    public Object visit(ASTDefnTrait node, Object data) {
        return visit((ScalaNode<Defn.Trait>) node, data);
    }

    @Override
    public Object visit(ASTDefnType node, Object data) {
        return visit((ScalaNode<Defn.Type>) node, data);
    }

    @Override
    public Object visit(ASTDefnVal node, Object data) {
        return visit((ScalaNode<Defn.Val>) node, data);
    }

    @Override
    public Object visit(ASTDefnVar node, Object data) {
        return visit((ScalaNode<Defn.Var>) node, data);
    }

    @Override
    public Object visit(ASTEnumeratorGenerator node, Object data) {
        return visit((ScalaNode<Enumerator.Generator>) node, data);
    }

    @Override
    public Object visit(ASTEnumeratorGuard node, Object data) {
        return visit((ScalaNode<Enumerator.Guard>) node, data);
    }

    @Override
    public Object visit(ASTEnumeratorVal node, Object data) {
        return visit((ScalaNode<Enumerator.Val>) node, data);
    }

    @Override
    public Object visit(ASTImport node, Object data) {
        return visit((ScalaNode<Import>) node, data);
    }

    @Override
    public Object visit(ASTImporteeName node, Object data) {
        return visit((ScalaNode<Importee.Name>) node, data);
    }

    @Override
    public Object visit(ASTImporteeRename node, Object data) {
        return visit((ScalaNode<Importee.Rename>) node, data);
    }

    @Override
    public Object visit(ASTImporteeUnimport node, Object data) {
        return visit((ScalaNode<Importee.Unimport>) node, data);
    }

    @Override
    public Object visit(ASTImporteeWildcard node, Object data) {
        return visit((ScalaNode<Importee.Wildcard>) node, data);
    }

    @Override
    public Object visit(ASTImporter node, Object data) {
        return visit((ScalaNode<Importer>) node, data);
    }

    @Override
    public Object visit(ASTInit node, Object data) {
        return visit((ScalaNode<Init>) node, data);
    }

    @Override
    public Object visit(ASTLitBoolean node, Object data) {
        return visit((ScalaNode<Lit.Boolean>) node, data);
    }

    @Override
    public Object visit(ASTLitByte node, Object data) {
        return visit((ScalaNode<Lit.Byte>) node, data);
    }

    @Override
    public Object visit(ASTLitChar node, Object data) {
        return visit((ScalaNode<Lit.Char>) node, data);
    }

    @Override
    public Object visit(ASTLitDouble node, Object data) {
        return visit((ScalaNode<Lit.Double>) node, data);
    }

    @Override
    public Object visit(ASTLitFloat node, Object data) {
        return visit((ScalaNode<Lit.Float>) node, data);
    }

    @Override
    public Object visit(ASTLitInt node, Object data) {
        return visit((ScalaNode<Lit.Int>) node, data);
    }

    @Override
    public Object visit(ASTLitLong node, Object data) {
        return visit((ScalaNode<Lit.Long>) node, data);
    }

    @Override
    public Object visit(ASTLitNull node, Object data) {
        return visit((ScalaNode<Lit.Null>) node, data);
    }

    @Override
    public Object visit(ASTLitShort node, Object data) {
        return visit((ScalaNode<Lit.Short>) node, data);
    }

    @Override
    public Object visit(ASTLitString node, Object data) {
        return visit((ScalaNode<Lit.String>) node, data);
    }

    @Override
    public Object visit(ASTLitSymbol node, Object data) {
        return visit((ScalaNode<Lit.Symbol>) node, data);
    }

    @Override
    public Object visit(ASTLitUnit node, Object data) {
        return visit((ScalaNode<Lit.Unit>) node, data);
    }

    @Override
    public Object visit(ASTModAbstract node, Object data) {
        return visit((ScalaNode<Mod.Abstract>) node, data);
    }

    @Override
    public Object visit(ASTModAnnot node, Object data) {
        return visit((ScalaNode<Mod.Annot>) node, data);
    }

    @Override
    public Object visit(ASTModCase node, Object data) {
        return visit((ScalaNode<Mod.Case>) node, data);
    }

    @Override
    public Object visit(ASTModContravariant node, Object data) {
        return visit((ScalaNode<Mod.Contravariant>) node, data);
    }

    @Override
    public Object visit(ASTModCovariant node, Object data) {
        return visit((ScalaNode<Mod.Covariant>) node, data);
    }

    @Override
    public Object visit(ASTModFinal node, Object data) {
        return visit((ScalaNode<Mod.Final>) node, data);
    }

    @Override
    public Object visit(ASTModImplicit node, Object data) {
        return visit((ScalaNode<Mod.Implicit>) node, data);
    }

    @Override
    public Object visit(ASTModInline node, Object data) {
        return visit((ScalaNode<Mod.Inline>) node, data);
    }

    @Override
    public Object visit(ASTModLazy node, Object data) {
        return visit((ScalaNode<Mod.Lazy>) node, data);
    }

    @Override
    public Object visit(ASTModOverride node, Object data) {
        return visit((ScalaNode<Mod.Override>) node, data);
    }

    @Override
    public Object visit(ASTModPrivate node, Object data) {
        return visit((ScalaNode<Mod.Private>) node, data);
    }

    @Override
    public Object visit(ASTModProtected node, Object data) {
        return visit((ScalaNode<Mod.Protected>) node, data);
    }

    @Override
    public Object visit(ASTModSealed node, Object data) {
        return visit((ScalaNode<Mod.Sealed>) node, data);
    }

    @Override
    public Object visit(ASTModValParam node, Object data) {
        return visit((ScalaNode<Mod.ValParam>) node, data);
    }

    @Override
    public Object visit(ASTModVarParam node, Object data) {
        return visit((ScalaNode<Mod.VarParam>) node, data);
    }

    @Override
    public Object visit(ASTNameAnonymous node, Object data) {
        return visit((ScalaNode<Name.Anonymous>) node, data);
    }

    @Override
    public Object visit(ASTNameIndeterminate node, Object data) {
        return visit((ScalaNode<Name.Indeterminate>) node, data);
    }

    @Override
    public Object visit(ASTPatAlternative node, Object data) {
        return visit((ScalaNode<Pat.Alternative>) node, data);
    }

    @Override
    public Object visit(ASTPatBind node, Object data) {
        return visit((ScalaNode<Pat.Bind>) node, data);
    }

    @Override
    public Object visit(ASTPatExtract node, Object data) {
        return visit((ScalaNode<Pat.Extract>) node, data);
    }

    @Override
    public Object visit(ASTPatExtractInfix node, Object data) {
        return visit((ScalaNode<Pat.ExtractInfix>) node, data);
    }

    @Override
    public Object visit(ASTPatInterpolate node, Object data) {
        return visit((ScalaNode<Pat.Interpolate>) node, data);
    }

    @Override
    public Object visit(ASTPatSeqWildcard node, Object data) {
        return visit((ScalaNode<Pat.SeqWildcard>) node, data);
    }

    @Override
    public Object visit(ASTPatTuple node, Object data) {
        return visit((ScalaNode<Pat.Tuple>) node, data);
    }

    @Override
    public Object visit(ASTPatTyped node, Object data) {
        return visit((ScalaNode<Pat.Typed>) node, data);
    }

    @Override
    public Object visit(ASTPatVar node, Object data) {
        return visit((ScalaNode<Pat.Var>) node, data);
    }

    @Override
    public Object visit(ASTPatWildcard node, Object data) {
        return visit((ScalaNode<Pat.Wildcard>) node, data);
    }

    @Override
    public Object visit(ASTPatXml node, Object data) {
        return visit((ScalaNode<Pat.Xml>) node, data);
    }

    @Override
    public Object visit(ASTPkg node, Object data) {
        return visit((ScalaNode<Pkg>) node, data);
    }

    @Override
    public Object visit(ASTPkgObject node, Object data) {
        return visit((ScalaNode<Pkg.Object>) node, data);
    }

    @Override
    public Object visit(ASTQuasi node, Object data) {
        return visit((ScalaNode<Quasi>) node, data);
    }

    @Override
    public Object visit(ASTSelf node, Object data) {
        return visit((ScalaNode<Self>) node, data);
    }

    @Override
    public Object visit(ASTTemplate node, Object data) {
        return visit((ScalaNode<Template>) node, data);
    }

    @Override
    public Object visit(ASTTermAnnotate node, Object data) {
        return visit((ScalaNode<Term.Annotate>) node, data);
    }

    @Override
    public Object visit(ASTTermApply node, Object data) {
        return visit((ScalaNode<Term.Apply>) node, data);
    }

    @Override
    public Object visit(ASTTermApplyInfix node, Object data) {
        return visit((ScalaNode<Term.ApplyInfix>) node, data);
    }

    @Override
    public Object visit(ASTTermApplyType node, Object data) {
        return visit((ScalaNode<Term.ApplyType>) node, data);
    }

    @Override
    public Object visit(ASTTermApplyUnary node, Object data) {
        return visit((ScalaNode<Term.ApplyUnary>) node, data);
    }

    @Override
    public Object visit(ASTTermAscribe node, Object data) {
        return visit((ScalaNode<Term.Ascribe>) node, data);
    }

    @Override
    public Object visit(ASTTermAssign node, Object data) {
        return visit((ScalaNode<Term.Assign>) node, data);
    }

    @Override
    public Object visit(ASTTermBlock node, Object data) {
        return visit((ScalaNode<Term.Block>) node, data);
    }

    @Override
    public Object visit(ASTTermDo node, Object data) {
        return visit((ScalaNode<Term.Do>) node, data);
    }

    @Override
    public Object visit(ASTTermEta node, Object data) {
        return visit((ScalaNode<Term.Eta>) node, data);
    }

    @Override
    public Object visit(ASTTermFor node, Object data) {
        return visit((ScalaNode<Term.For>) node, data);
    }

    @Override
    public Object visit(ASTTermForYield node, Object data) {
        return visit((ScalaNode<Term.ForYield>) node, data);
    }

    @Override
    public Object visit(ASTTermFunction node, Object data) {
        return visit((ScalaNode<Term.Function>) node, data);
    }

    @Override
    public Object visit(ASTTermIf node, Object data) {
        return visit((ScalaNode<Term.If>) node, data);
    }

    @Override
    public Object visit(ASTTermInterpolate node, Object data) {
        return visit((ScalaNode<Term.Interpolate>) node, data);
    }

    @Override
    public Object visit(ASTTermMatch node, Object data) {
        return visit((ScalaNode<Term.Match>) node, data);
    }

    @Override
    public Object visit(ASTTermName node, Object data) {
        return visit((ScalaNode<Term.Name>) node, data);
    }

    @Override
    public Object visit(ASTTermNewAnonymous node, Object data) {
        return visit((ScalaNode<Term.NewAnonymous>) node, data);
    }

    @Override
    public Object visit(ASTTermNew node, Object data) {
        return visit((ScalaNode<Term.New>) node, data);
    }

    @Override
    public Object visit(ASTTermParam node, Object data) {
        return visit((ScalaNode<Term.Param>) node, data);
    }

    @Override
    public Object visit(ASTTermPartialFunction node, Object data) {
        return visit((ScalaNode<Term.PartialFunction>) node, data);
    }

    @Override
    public Object visit(ASTTermPlaceholder node, Object data) {
        return visit((ScalaNode<Term.Placeholder>) node, data);
    }

    @Override
    public Object visit(ASTTermRepeated node, Object data) {
        return visit((ScalaNode<Term.Repeated>) node, data);
    }

    @Override
    public Object visit(ASTTermReturn node, Object data) {
        return visit((ScalaNode<Term.Return>) node, data);
    }

    @Override
    public Object visit(ASTTermSelect node, Object data) {
        return visit((ScalaNode<Term.Select>) node, data);
    }

    @Override
    public Object visit(ASTTermSuper node, Object data) {
        return visit((ScalaNode<Term.Super>) node, data);
    }

    @Override
    public Object visit(ASTTermThis node, Object data) {
        return visit((ScalaNode<Term.This>) node, data);
    }

    @Override
    public Object visit(ASTTermThrow node, Object data) {
        return visit((ScalaNode<Term.Throw>) node, data);
    }

    @Override
    public Object visit(ASTTermTry node, Object data) {
        return visit((ScalaNode<Term.Try>) node, data);
    }

    @Override
    public Object visit(ASTTermTryWithHandler node, Object data) {
        return visit((ScalaNode<Term.TryWithHandler>) node, data);
    }

    @Override
    public Object visit(ASTTermTuple node, Object data) {
        return visit((ScalaNode<Term.Tuple>) node, data);
    }

    @Override
    public Object visit(ASTTermWhile node, Object data) {
        return visit((ScalaNode<Term.While>) node, data);
    }

    @Override
    public Object visit(ASTTermXml node, Object data) {
        return visit((ScalaNode<Term.Xml>) node, data);
    }

    @Override
    public Object visit(ASTTypeAnd node, Object data) {
        return visit((ScalaNode<Type.And>) node, data);
    }

    @Override
    public Object visit(ASTTypeAnnotate node, Object data) {
        return visit((ScalaNode<Type.Annotate>) node, data);
    }

    @Override
    public Object visit(ASTTypeApply node, Object data) {
        return visit((ScalaNode<Type.Apply>) node, data);
    }

    @Override
    public Object visit(ASTTypeApplyInfix node, Object data) {
        return visit((ScalaNode<Type.ApplyInfix>) node, data);
    }

    @Override
    public Object visit(ASTTypeBounds node, Object data) {
        return visit((ScalaNode<Type.Bounds>) node, data);
    }

    @Override
    public Object visit(ASTTypeByName node, Object data) {
        return visit((ScalaNode<Type.ByName>) node, data);
    }

    @Override
    public Object visit(ASTTypeExistential node, Object data) {
        return visit((ScalaNode<Type.Existential>) node, data);
    }

    @Override
    public Object visit(ASTTypeFunction node, Object data) {
        return visit((ScalaNode<Type.Function>) node, data);
    }

    @Override
    public Object visit(ASTTypeImplicitFunction node, Object data) {
        return visit((ScalaNode<Type.ImplicitFunction>) node, data);
    }

    @Override
    public Object visit(ASTTypeLambda node, Object data) {
        return visit((ScalaNode<Type.Lambda>) node, data);
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        return visit((ScalaNode<Type.Method>) node, data);
    }

    @Override
    public Object visit(ASTTypeName node, Object data) {
        return visit((ScalaNode<Type.Name>) node, data);
    }

    @Override
    public Object visit(ASTTypeOr node, Object data) {
        return visit((ScalaNode<Type.Or>) node, data);
    }

    @Override
    public Object visit(ASTTypeParam node, Object data) {
        return visit((ScalaNode<Type.Param>) node, data);
    }

    @Override
    public Object visit(ASTTypePlaceholder node, Object data) {
        return visit((ScalaNode<Type.Placeholder>) node, data);
    }

    @Override
    public Object visit(ASTTypeProject node, Object data) {
        return visit((ScalaNode<Type.Project>) node, data);
    }

    @Override
    public Object visit(ASTTypeRefine node, Object data) {
        return visit((ScalaNode<Type.Refine>) node, data);
    }

    @Override
    public Object visit(ASTTypeRepeated node, Object data) {
        return visit((ScalaNode<Type.Repeated>) node, data);
    }

    @Override
    public Object visit(ASTTypeSelect node, Object data) {
        return visit((ScalaNode<Type.Select>) node, data);
    }

    @Override
    public Object visit(ASTTypeSingleton node, Object data) {
        return visit((ScalaNode<Type.Singleton>) node, data);
    }

    @Override
    public Object visit(ASTTypeTuple node, Object data) {
        return visit((ScalaNode<Type.Tuple>) node, data);
    }

    @Override
    public Object visit(ASTTypeVar node, Object data) {
        return visit((ScalaNode<Type.Var>) node, data);
    }

    @Override
    public Object visit(ASTTypeWith node, Object data) {
        return visit((ScalaNode<Type.With>) node, data);
    }

}
