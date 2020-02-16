/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

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
 *
 * @param <D>
 *            The type of the data input
 * @param <R>
 *            The type of the returned data
 */
public class ScalaParserVisitorAdapter<D, R> implements ScalaParserVisitor<D, R> {

    /** Initial value when combining values returned by children. */
    protected R zero() {
        return null;
    }

    /** Merge two values of type R, used to combine values returned by children. */
    protected R combine(R acc, R r) {
        return r;
    }

    @Override
    public R visit(ScalaNode<?> node, D data) {
        R returnValue = zero();
        for (int i = 0; i < node.getNumChildren(); ++i) {
            returnValue = combine(returnValue, node.getChild(i).accept(this, data));
        }
        return returnValue;
    }

    @Override
    public R visit(ASTSource node, D data) {
        return visit((ScalaNode<Source>) node, data);
    }

    @Override
    public R visit(ASTCase node, D data) {
        return visit((ScalaNode<Case>) node, data);
    }

    @Override
    public R visit(ASTCtorPrimary node, D data) {
        return visit((ScalaNode<Ctor.Primary>) node, data);
    }

    @Override
    public R visit(ASTCtorSecondary node, D data) {
        return visit((ScalaNode<Ctor.Secondary>) node, data);
    }

    @Override
    public R visit(ASTDeclDef node, D data) {
        return visit((ScalaNode<Decl.Def>) node, data);
    }

    @Override
    public R visit(ASTDeclType node, D data) {
        return visit((ScalaNode<Decl.Type>) node, data);
    }

    @Override
    public R visit(ASTDeclVal node, D data) {
        return visit((ScalaNode<Decl.Val>) node, data);
    }

    @Override
    public R visit(ASTDeclVar node, D data) {
        return visit((ScalaNode<Decl.Var>) node, data);
    }

    @Override
    public R visit(ASTDefnClass node, D data) {
        return visit((ScalaNode<Defn.Class>) node, data);
    }

    @Override
    public R visit(ASTDefnDef node, D data) {
        return visit((ScalaNode<Defn.Def>) node, data);
    }

    @Override
    public R visit(ASTDefnMacro node, D data) {
        return visit((ScalaNode<Defn.Macro>) node, data);
    }

    @Override
    public R visit(ASTDefnObject node, D data) {
        return visit((ScalaNode<Defn.Object>) node, data);
    }

    @Override
    public R visit(ASTDefnTrait node, D data) {
        return visit((ScalaNode<Defn.Trait>) node, data);
    }

    @Override
    public R visit(ASTDefnType node, D data) {
        return visit((ScalaNode<Defn.Type>) node, data);
    }

    @Override
    public R visit(ASTDefnVal node, D data) {
        return visit((ScalaNode<Defn.Val>) node, data);
    }

    @Override
    public R visit(ASTDefnVar node, D data) {
        return visit((ScalaNode<Defn.Var>) node, data);
    }

    @Override
    public R visit(ASTEnumeratorGenerator node, D data) {
        return visit((ScalaNode<Enumerator.Generator>) node, data);
    }

    @Override
    public R visit(ASTEnumeratorGuard node, D data) {
        return visit((ScalaNode<Enumerator.Guard>) node, data);
    }

    @Override
    public R visit(ASTEnumeratorVal node, D data) {
        return visit((ScalaNode<Enumerator.Val>) node, data);
    }

    @Override
    public R visit(ASTImport node, D data) {
        return visit((ScalaNode<Import>) node, data);
    }

    @Override
    public R visit(ASTImporteeName node, D data) {
        return visit((ScalaNode<Importee.Name>) node, data);
    }

    @Override
    public R visit(ASTImporteeRename node, D data) {
        return visit((ScalaNode<Importee.Rename>) node, data);
    }

    @Override
    public R visit(ASTImporteeUnimport node, D data) {
        return visit((ScalaNode<Importee.Unimport>) node, data);
    }

    @Override
    public R visit(ASTImporteeWildcard node, D data) {
        return visit((ScalaNode<Importee.Wildcard>) node, data);
    }

    @Override
    public R visit(ASTImporter node, D data) {
        return visit((ScalaNode<Importer>) node, data);
    }

    @Override
    public R visit(ASTInit node, D data) {
        return visit((ScalaNode<Init>) node, data);
    }

    @Override
    public R visit(ASTLitBoolean node, D data) {
        return visit((ScalaNode<Lit.Boolean>) node, data);
    }

    @Override
    public R visit(ASTLitByte node, D data) {
        return visit((ScalaNode<Lit.Byte>) node, data);
    }

    @Override
    public R visit(ASTLitChar node, D data) {
        return visit((ScalaNode<Lit.Char>) node, data);
    }

    @Override
    public R visit(ASTLitDouble node, D data) {
        return visit((ScalaNode<Lit.Double>) node, data);
    }

    @Override
    public R visit(ASTLitFloat node, D data) {
        return visit((ScalaNode<Lit.Float>) node, data);
    }

    @Override
    public R visit(ASTLitInt node, D data) {
        return visit((ScalaNode<Lit.Int>) node, data);
    }

    @Override
    public R visit(ASTLitLong node, D data) {
        return visit((ScalaNode<Lit.Long>) node, data);
    }

    @Override
    public R visit(ASTLitNull node, D data) {
        return visit((ScalaNode<Lit.Null>) node, data);
    }

    @Override
    public R visit(ASTLitShort node, D data) {
        return visit((ScalaNode<Lit.Short>) node, data);
    }

    @Override
    public R visit(ASTLitString node, D data) {
        return visit((ScalaNode<Lit.String>) node, data);
    }

    @Override
    public R visit(ASTLitSymbol node, D data) {
        return visit((ScalaNode<Lit.Symbol>) node, data);
    }

    @Override
    public R visit(ASTLitUnit node, D data) {
        return visit((ScalaNode<Lit.Unit>) node, data);
    }

    @Override
    public R visit(ASTModAbstract node, D data) {
        return visit((ScalaNode<Mod.Abstract>) node, data);
    }

    @Override
    public R visit(ASTModAnnot node, D data) {
        return visit((ScalaNode<Mod.Annot>) node, data);
    }

    @Override
    public R visit(ASTModCase node, D data) {
        return visit((ScalaNode<Mod.Case>) node, data);
    }

    @Override
    public R visit(ASTModContravariant node, D data) {
        return visit((ScalaNode<Mod.Contravariant>) node, data);
    }

    @Override
    public R visit(ASTModCovariant node, D data) {
        return visit((ScalaNode<Mod.Covariant>) node, data);
    }

    @Override
    public R visit(ASTModFinal node, D data) {
        return visit((ScalaNode<Mod.Final>) node, data);
    }

    @Override
    public R visit(ASTModImplicit node, D data) {
        return visit((ScalaNode<Mod.Implicit>) node, data);
    }

    @Override
    public R visit(ASTModInline node, D data) {
        return visit((ScalaNode<Mod.Inline>) node, data);
    }

    @Override
    public R visit(ASTModLazy node, D data) {
        return visit((ScalaNode<Mod.Lazy>) node, data);
    }

    @Override
    public R visit(ASTModOverride node, D data) {
        return visit((ScalaNode<Mod.Override>) node, data);
    }

    @Override
    public R visit(ASTModPrivate node, D data) {
        return visit((ScalaNode<Mod.Private>) node, data);
    }

    @Override
    public R visit(ASTModProtected node, D data) {
        return visit((ScalaNode<Mod.Protected>) node, data);
    }

    @Override
    public R visit(ASTModSealed node, D data) {
        return visit((ScalaNode<Mod.Sealed>) node, data);
    }

    @Override
    public R visit(ASTModValParam node, D data) {
        return visit((ScalaNode<Mod.ValParam>) node, data);
    }

    @Override
    public R visit(ASTModVarParam node, D data) {
        return visit((ScalaNode<Mod.VarParam>) node, data);
    }

    @Override
    public R visit(ASTNameAnonymous node, D data) {
        return visit((ScalaNode<Name.Anonymous>) node, data);
    }

    @Override
    public R visit(ASTNameIndeterminate node, D data) {
        return visit((ScalaNode<Name.Indeterminate>) node, data);
    }

    @Override
    public R visit(ASTPatAlternative node, D data) {
        return visit((ScalaNode<Pat.Alternative>) node, data);
    }

    @Override
    public R visit(ASTPatBind node, D data) {
        return visit((ScalaNode<Pat.Bind>) node, data);
    }

    @Override
    public R visit(ASTPatExtract node, D data) {
        return visit((ScalaNode<Pat.Extract>) node, data);
    }

    @Override
    public R visit(ASTPatExtractInfix node, D data) {
        return visit((ScalaNode<Pat.ExtractInfix>) node, data);
    }

    @Override
    public R visit(ASTPatInterpolate node, D data) {
        return visit((ScalaNode<Pat.Interpolate>) node, data);
    }

    @Override
    public R visit(ASTPatSeqWildcard node, D data) {
        return visit((ScalaNode<Pat.SeqWildcard>) node, data);
    }

    @Override
    public R visit(ASTPatTuple node, D data) {
        return visit((ScalaNode<Pat.Tuple>) node, data);
    }

    @Override
    public R visit(ASTPatTyped node, D data) {
        return visit((ScalaNode<Pat.Typed>) node, data);
    }

    @Override
    public R visit(ASTPatVar node, D data) {
        return visit((ScalaNode<Pat.Var>) node, data);
    }

    @Override
    public R visit(ASTPatWildcard node, D data) {
        return visit((ScalaNode<Pat.Wildcard>) node, data);
    }

    @Override
    public R visit(ASTPatXml node, D data) {
        return visit((ScalaNode<Pat.Xml>) node, data);
    }

    @Override
    public R visit(ASTPkg node, D data) {
        return visit((ScalaNode<Pkg>) node, data);
    }

    @Override
    public R visit(ASTPkgObject node, D data) {
        return visit((ScalaNode<Pkg.Object>) node, data);
    }

    @Override
    public R visit(ASTQuasi node, D data) {
        return visit((ScalaNode<Quasi>) node, data);
    }

    @Override
    public R visit(ASTSelf node, D data) {
        return visit((ScalaNode<Self>) node, data);
    }

    @Override
    public R visit(ASTTemplate node, D data) {
        return visit((ScalaNode<Template>) node, data);
    }

    @Override
    public R visit(ASTTermAnnotate node, D data) {
        return visit((ScalaNode<Term.Annotate>) node, data);
    }

    @Override
    public R visit(ASTTermApply node, D data) {
        return visit((ScalaNode<Term.Apply>) node, data);
    }

    @Override
    public R visit(ASTTermApplyInfix node, D data) {
        return visit((ScalaNode<Term.ApplyInfix>) node, data);
    }

    @Override
    public R visit(ASTTermApplyType node, D data) {
        return visit((ScalaNode<Term.ApplyType>) node, data);
    }

    @Override
    public R visit(ASTTermApplyUnary node, D data) {
        return visit((ScalaNode<Term.ApplyUnary>) node, data);
    }

    @Override
    public R visit(ASTTermAscribe node, D data) {
        return visit((ScalaNode<Term.Ascribe>) node, data);
    }

    @Override
    public R visit(ASTTermAssign node, D data) {
        return visit((ScalaNode<Term.Assign>) node, data);
    }

    @Override
    public R visit(ASTTermBlock node, D data) {
        return visit((ScalaNode<Term.Block>) node, data);
    }

    @Override
    public R visit(ASTTermDo node, D data) {
        return visit((ScalaNode<Term.Do>) node, data);
    }

    @Override
    public R visit(ASTTermEta node, D data) {
        return visit((ScalaNode<Term.Eta>) node, data);
    }

    @Override
    public R visit(ASTTermFor node, D data) {
        return visit((ScalaNode<Term.For>) node, data);
    }

    @Override
    public R visit(ASTTermForYield node, D data) {
        return visit((ScalaNode<Term.ForYield>) node, data);
    }

    @Override
    public R visit(ASTTermFunction node, D data) {
        return visit((ScalaNode<Term.Function>) node, data);
    }

    @Override
    public R visit(ASTTermIf node, D data) {
        return visit((ScalaNode<Term.If>) node, data);
    }

    @Override
    public R visit(ASTTermInterpolate node, D data) {
        return visit((ScalaNode<Term.Interpolate>) node, data);
    }

    @Override
    public R visit(ASTTermMatch node, D data) {
        return visit((ScalaNode<Term.Match>) node, data);
    }

    @Override
    public R visit(ASTTermName node, D data) {
        return visit((ScalaNode<Term.Name>) node, data);
    }

    @Override
    public R visit(ASTTermNewAnonymous node, D data) {
        return visit((ScalaNode<Term.NewAnonymous>) node, data);
    }

    @Override
    public R visit(ASTTermNew node, D data) {
        return visit((ScalaNode<Term.New>) node, data);
    }

    @Override
    public R visit(ASTTermParam node, D data) {
        return visit((ScalaNode<Term.Param>) node, data);
    }

    @Override
    public R visit(ASTTermPartialFunction node, D data) {
        return visit((ScalaNode<Term.PartialFunction>) node, data);
    }

    @Override
    public R visit(ASTTermPlaceholder node, D data) {
        return visit((ScalaNode<Term.Placeholder>) node, data);
    }

    @Override
    public R visit(ASTTermRepeated node, D data) {
        return visit((ScalaNode<Term.Repeated>) node, data);
    }

    @Override
    public R visit(ASTTermReturn node, D data) {
        return visit((ScalaNode<Term.Return>) node, data);
    }

    @Override
    public R visit(ASTTermSelect node, D data) {
        return visit((ScalaNode<Term.Select>) node, data);
    }

    @Override
    public R visit(ASTTermSuper node, D data) {
        return visit((ScalaNode<Term.Super>) node, data);
    }

    @Override
    public R visit(ASTTermThis node, D data) {
        return visit((ScalaNode<Term.This>) node, data);
    }

    @Override
    public R visit(ASTTermThrow node, D data) {
        return visit((ScalaNode<Term.Throw>) node, data);
    }

    @Override
    public R visit(ASTTermTry node, D data) {
        return visit((ScalaNode<Term.Try>) node, data);
    }

    @Override
    public R visit(ASTTermTryWithHandler node, D data) {
        return visit((ScalaNode<Term.TryWithHandler>) node, data);
    }

    @Override
    public R visit(ASTTermTuple node, D data) {
        return visit((ScalaNode<Term.Tuple>) node, data);
    }

    @Override
    public R visit(ASTTermWhile node, D data) {
        return visit((ScalaNode<Term.While>) node, data);
    }

    @Override
    public R visit(ASTTermXml node, D data) {
        return visit((ScalaNode<Term.Xml>) node, data);
    }

    @Override
    public R visit(ASTTypeAnd node, D data) {
        return visit((ScalaNode<Type.And>) node, data);
    }

    @Override
    public R visit(ASTTypeAnnotate node, D data) {
        return visit((ScalaNode<Type.Annotate>) node, data);
    }

    @Override
    public R visit(ASTTypeApply node, D data) {
        return visit((ScalaNode<Type.Apply>) node, data);
    }

    @Override
    public R visit(ASTTypeApplyInfix node, D data) {
        return visit((ScalaNode<Type.ApplyInfix>) node, data);
    }

    @Override
    public R visit(ASTTypeBounds node, D data) {
        return visit((ScalaNode<Type.Bounds>) node, data);
    }

    @Override
    public R visit(ASTTypeByName node, D data) {
        return visit((ScalaNode<Type.ByName>) node, data);
    }

    @Override
    public R visit(ASTTypeExistential node, D data) {
        return visit((ScalaNode<Type.Existential>) node, data);
    }

    @Override
    public R visit(ASTTypeFunction node, D data) {
        return visit((ScalaNode<Type.Function>) node, data);
    }

    @Override
    public R visit(ASTTypeImplicitFunction node, D data) {
        return visit((ScalaNode<Type.ImplicitFunction>) node, data);
    }

    @Override
    public R visit(ASTTypeLambda node, D data) {
        return visit((ScalaNode<Type.Lambda>) node, data);
    }

    @Override
    public R visit(ASTTypeMethod node, D data) {
        return visit((ScalaNode<Type.Method>) node, data);
    }

    @Override
    public R visit(ASTTypeName node, D data) {
        return visit((ScalaNode<Type.Name>) node, data);
    }

    @Override
    public R visit(ASTTypeOr node, D data) {
        return visit((ScalaNode<Type.Or>) node, data);
    }

    @Override
    public R visit(ASTTypeParam node, D data) {
        return visit((ScalaNode<Type.Param>) node, data);
    }

    @Override
    public R visit(ASTTypePlaceholder node, D data) {
        return visit((ScalaNode<Type.Placeholder>) node, data);
    }

    @Override
    public R visit(ASTTypeProject node, D data) {
        return visit((ScalaNode<Type.Project>) node, data);
    }

    @Override
    public R visit(ASTTypeRefine node, D data) {
        return visit((ScalaNode<Type.Refine>) node, data);
    }

    @Override
    public R visit(ASTTypeRepeated node, D data) {
        return visit((ScalaNode<Type.Repeated>) node, data);
    }

    @Override
    public R visit(ASTTypeSelect node, D data) {
        return visit((ScalaNode<Type.Select>) node, data);
    }

    @Override
    public R visit(ASTTypeSingleton node, D data) {
        return visit((ScalaNode<Type.Singleton>) node, data);
    }

    @Override
    public R visit(ASTTypeTuple node, D data) {
        return visit((ScalaNode<Type.Tuple>) node, data);
    }

    @Override
    public R visit(ASTTypeVar node, D data) {
        return visit((ScalaNode<Type.Var>) node, data);
    }

    @Override
    public R visit(ASTTypeWith node, D data) {
        return visit((ScalaNode<Type.With>) node, data);
    }

}
