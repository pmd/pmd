/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;
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
 * The default base implementation of a PMD Rule for Scala. Uses the Visitor
 * Pattern to traverse the AST.
 */
public class ScalaRule extends AbstractRule implements ScalaParserVisitor<RuleContext, RuleContext> {

    /**
     * Create a new Scala Rule.
     */
    public ScalaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ScalaLanguageModule.NAME));
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
            if (node instanceof ASTSource) {
                visit((ASTSource) node, ctx);
            }
        }
    }

    @Override
    public RuleContext visit(ScalaNode<?> node, RuleContext data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public RuleContext visit(ASTSource node, RuleContext data) {
        return visit((ScalaNode<Source>) node, data);
    }

    @Override
    public RuleContext visit(ASTCase node, RuleContext data) {
        return visit((ScalaNode<Case>) node, data);
    }

    @Override
    public RuleContext visit(ASTCtorPrimary node, RuleContext data) {
        return visit((ScalaNode<Ctor.Primary>) node, data);
    }

    @Override
    public RuleContext visit(ASTCtorSecondary node, RuleContext data) {
        return visit((ScalaNode<Ctor.Secondary>) node, data);
    }

    @Override
    public RuleContext visit(ASTDeclDef node, RuleContext data) {
        return visit((ScalaNode<Decl.Def>) node, data);
    }

    @Override
    public RuleContext visit(ASTDeclType node, RuleContext data) {
        return visit((ScalaNode<Decl.Type>) node, data);
    }

    @Override
    public RuleContext visit(ASTDeclVal node, RuleContext data) {
        return visit((ScalaNode<Decl.Val>) node, data);
    }

    @Override
    public RuleContext visit(ASTDeclVar node, RuleContext data) {
        return visit((ScalaNode<Decl.Var>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnClass node, RuleContext data) {
        return visit((ScalaNode<Defn.Class>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnDef node, RuleContext data) {
        return visit((ScalaNode<Defn.Def>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnMacro node, RuleContext data) {
        return visit((ScalaNode<Defn.Macro>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnObject node, RuleContext data) {
        return visit((ScalaNode<Defn.Object>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnTrait node, RuleContext data) {
        return visit((ScalaNode<Defn.Trait>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnType node, RuleContext data) {
        return visit((ScalaNode<Defn.Type>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnVal node, RuleContext data) {
        return visit((ScalaNode<Defn.Val>) node, data);
    }

    @Override
    public RuleContext visit(ASTDefnVar node, RuleContext data) {
        return visit((ScalaNode<Defn.Var>) node, data);
    }

    @Override
    public RuleContext visit(ASTEnumeratorGenerator node, RuleContext data) {
        return visit((ScalaNode<Enumerator.Generator>) node, data);
    }

    @Override
    public RuleContext visit(ASTEnumeratorGuard node, RuleContext data) {
        return visit((ScalaNode<Enumerator.Guard>) node, data);
    }

    @Override
    public RuleContext visit(ASTEnumeratorVal node, RuleContext data) {
        return visit((ScalaNode<Enumerator.Val>) node, data);
    }

    @Override
    public RuleContext visit(ASTImport node, RuleContext data) {
        return visit((ScalaNode<Import>) node, data);
    }

    @Override
    public RuleContext visit(ASTImporteeName node, RuleContext data) {
        return visit((ScalaNode<Importee.Name>) node, data);
    }

    @Override
    public RuleContext visit(ASTImporteeRename node, RuleContext data) {
        return visit((ScalaNode<Importee.Rename>) node, data);
    }

    @Override
    public RuleContext visit(ASTImporteeUnimport node, RuleContext data) {
        return visit((ScalaNode<Importee.Unimport>) node, data);
    }

    @Override
    public RuleContext visit(ASTImporteeWildcard node, RuleContext data) {
        return visit((ScalaNode<Importee.Wildcard>) node, data);
    }

    @Override
    public RuleContext visit(ASTImporter node, RuleContext data) {
        return visit((ScalaNode<Importer>) node, data);
    }

    @Override
    public RuleContext visit(ASTInit node, RuleContext data) {
        return visit((ScalaNode<Init>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitBoolean node, RuleContext data) {
        return visit((ScalaNode<Lit.Boolean>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitByte node, RuleContext data) {
        return visit((ScalaNode<Lit.Byte>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitChar node, RuleContext data) {
        return visit((ScalaNode<Lit.Char>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitDouble node, RuleContext data) {
        return visit((ScalaNode<Lit.Double>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitFloat node, RuleContext data) {
        return visit((ScalaNode<Lit.Float>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitInt node, RuleContext data) {
        return visit((ScalaNode<Lit.Int>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitLong node, RuleContext data) {
        return visit((ScalaNode<Lit.Long>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitNull node, RuleContext data) {
        return visit((ScalaNode<Lit.Null>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitShort node, RuleContext data) {
        return visit((ScalaNode<Lit.Short>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitString node, RuleContext data) {
        return visit((ScalaNode<Lit.String>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitSymbol node, RuleContext data) {
        return visit((ScalaNode<Lit.Symbol>) node, data);
    }

    @Override
    public RuleContext visit(ASTLitUnit node, RuleContext data) {
        return visit((ScalaNode<Lit.Unit>) node, data);
    }

    @Override
    public RuleContext visit(ASTModAbstract node, RuleContext data) {
        return visit((ScalaNode<Mod.Abstract>) node, data);
    }

    @Override
    public RuleContext visit(ASTModAnnot node, RuleContext data) {
        return visit((ScalaNode<Mod.Annot>) node, data);
    }

    @Override
    public RuleContext visit(ASTModCase node, RuleContext data) {
        return visit((ScalaNode<Mod.Case>) node, data);
    }

    @Override
    public RuleContext visit(ASTModContravariant node, RuleContext data) {
        return visit((ScalaNode<Mod.Contravariant>) node, data);
    }

    @Override
    public RuleContext visit(ASTModCovariant node, RuleContext data) {
        return visit((ScalaNode<Mod.Covariant>) node, data);
    }

    @Override
    public RuleContext visit(ASTModFinal node, RuleContext data) {
        return visit((ScalaNode<Mod.Final>) node, data);
    }

    @Override
    public RuleContext visit(ASTModImplicit node, RuleContext data) {
        return visit((ScalaNode<Mod.Implicit>) node, data);
    }

    @Override
    public RuleContext visit(ASTModInline node, RuleContext data) {
        return visit((ScalaNode<Mod.Inline>) node, data);
    }

    @Override
    public RuleContext visit(ASTModLazy node, RuleContext data) {
        return visit((ScalaNode<Mod.Lazy>) node, data);
    }

    @Override
    public RuleContext visit(ASTModOverride node, RuleContext data) {
        return visit((ScalaNode<Mod.Override>) node, data);
    }

    @Override
    public RuleContext visit(ASTModPrivate node, RuleContext data) {
        return visit((ScalaNode<Mod.Private>) node, data);
    }

    @Override
    public RuleContext visit(ASTModProtected node, RuleContext data) {
        return visit((ScalaNode<Mod.Protected>) node, data);
    }

    @Override
    public RuleContext visit(ASTModSealed node, RuleContext data) {
        return visit((ScalaNode<Mod.Sealed>) node, data);
    }

    @Override
    public RuleContext visit(ASTModValParam node, RuleContext data) {
        return visit((ScalaNode<Mod.ValParam>) node, data);
    }

    @Override
    public RuleContext visit(ASTModVarParam node, RuleContext data) {
        return visit((ScalaNode<Mod.VarParam>) node, data);
    }

    @Override
    public RuleContext visit(ASTNameAnonymous node, RuleContext data) {
        return visit((ScalaNode<Name.Anonymous>) node, data);
    }

    @Override
    public RuleContext visit(ASTNameIndeterminate node, RuleContext data) {
        return visit((ScalaNode<Name.Indeterminate>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatAlternative node, RuleContext data) {
        return visit((ScalaNode<Pat.Alternative>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatBind node, RuleContext data) {
        return visit((ScalaNode<Pat.Bind>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatExtract node, RuleContext data) {
        return visit((ScalaNode<Pat.Extract>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatExtractInfix node, RuleContext data) {
        return visit((ScalaNode<Pat.ExtractInfix>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatInterpolate node, RuleContext data) {
        return visit((ScalaNode<Pat.Interpolate>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatSeqWildcard node, RuleContext data) {
        return visit((ScalaNode<Pat.SeqWildcard>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatTuple node, RuleContext data) {
        return visit((ScalaNode<Pat.Tuple>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatTyped node, RuleContext data) {
        return visit((ScalaNode<Pat.Typed>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatVar node, RuleContext data) {
        return visit((ScalaNode<Pat.Var>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatWildcard node, RuleContext data) {
        return visit((ScalaNode<Pat.Wildcard>) node, data);
    }

    @Override
    public RuleContext visit(ASTPatXml node, RuleContext data) {
        return visit((ScalaNode<Pat.Xml>) node, data);
    }

    @Override
    public RuleContext visit(ASTPkg node, RuleContext data) {
        return visit((ScalaNode<Pkg>) node, data);
    }

    @Override
    public RuleContext visit(ASTPkgObject node, RuleContext data) {
        return visit((ScalaNode<Pkg.Object>) node, data);
    }

    @Override
    public RuleContext visit(ASTQuasi node, RuleContext data) {
        return visit((ScalaNode<Quasi>) node, data);
    }

    @Override
    public RuleContext visit(ASTSelf node, RuleContext data) {
        return visit((ScalaNode<Self>) node, data);
    }

    @Override
    public RuleContext visit(ASTTemplate node, RuleContext data) {
        return visit((ScalaNode<Template>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermAnnotate node, RuleContext data) {
        return visit((ScalaNode<Term.Annotate>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermApply node, RuleContext data) {
        return visit((ScalaNode<Term.Apply>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermApplyInfix node, RuleContext data) {
        return visit((ScalaNode<Term.ApplyInfix>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermApplyType node, RuleContext data) {
        return visit((ScalaNode<Term.ApplyType>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermApplyUnary node, RuleContext data) {
        return visit((ScalaNode<Term.ApplyUnary>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermAscribe node, RuleContext data) {
        return visit((ScalaNode<Term.Ascribe>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermAssign node, RuleContext data) {
        return visit((ScalaNode<Term.Assign>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermBlock node, RuleContext data) {
        return visit((ScalaNode<Term.Block>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermDo node, RuleContext data) {
        return visit((ScalaNode<Term.Do>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermEta node, RuleContext data) {
        return visit((ScalaNode<Term.Eta>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermFor node, RuleContext data) {
        return visit((ScalaNode<Term.For>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermForYield node, RuleContext data) {
        return visit((ScalaNode<Term.ForYield>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermFunction node, RuleContext data) {
        return visit((ScalaNode<Term.Function>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermIf node, RuleContext data) {
        return visit((ScalaNode<Term.If>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermInterpolate node, RuleContext data) {
        return visit((ScalaNode<Term.Interpolate>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermMatch node, RuleContext data) {
        return visit((ScalaNode<Term.Match>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermName node, RuleContext data) {
        return visit((ScalaNode<Term.Name>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermNewAnonymous node, RuleContext data) {
        return visit((ScalaNode<Term.NewAnonymous>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermNew node, RuleContext data) {
        return visit((ScalaNode<Term.New>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermParam node, RuleContext data) {
        return visit((ScalaNode<Term.Param>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermPartialFunction node, RuleContext data) {
        return visit((ScalaNode<Term.PartialFunction>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermPlaceholder node, RuleContext data) {
        return visit((ScalaNode<Term.Placeholder>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermRepeated node, RuleContext data) {
        return visit((ScalaNode<Term.Repeated>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermReturn node, RuleContext data) {
        return visit((ScalaNode<Term.Return>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermSelect node, RuleContext data) {
        return visit((ScalaNode<Term.Select>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermSuper node, RuleContext data) {
        return visit((ScalaNode<Term.Super>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermThis node, RuleContext data) {
        return visit((ScalaNode<Term.This>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermThrow node, RuleContext data) {
        return visit((ScalaNode<Term.Throw>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermTry node, RuleContext data) {
        return visit((ScalaNode<Term.Try>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermTryWithHandler node, RuleContext data) {
        return visit((ScalaNode<Term.TryWithHandler>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermTuple node, RuleContext data) {
        return visit((ScalaNode<Term.Tuple>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermWhile node, RuleContext data) {
        return visit((ScalaNode<Term.While>) node, data);
    }

    @Override
    public RuleContext visit(ASTTermXml node, RuleContext data) {
        return visit((ScalaNode<Term.Xml>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeAnd node, RuleContext data) {
        return visit((ScalaNode<Type.And>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeAnnotate node, RuleContext data) {
        return visit((ScalaNode<Type.Annotate>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeApply node, RuleContext data) {
        return visit((ScalaNode<Type.Apply>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeApplyInfix node, RuleContext data) {
        return visit((ScalaNode<Type.ApplyInfix>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeBounds node, RuleContext data) {
        return visit((ScalaNode<Type.Bounds>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeByName node, RuleContext data) {
        return visit((ScalaNode<Type.ByName>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeExistential node, RuleContext data) {
        return visit((ScalaNode<Type.Existential>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeFunction node, RuleContext data) {
        return visit((ScalaNode<Type.Function>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeImplicitFunction node, RuleContext data) {
        return visit((ScalaNode<Type.ImplicitFunction>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeLambda node, RuleContext data) {
        return visit((ScalaNode<Type.Lambda>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeMethod node, RuleContext data) {
        return visit((ScalaNode<Type.Method>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeName node, RuleContext data) {
        return visit((ScalaNode<Type.Name>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeOr node, RuleContext data) {
        return visit((ScalaNode<Type.Or>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeParam node, RuleContext data) {
        return visit((ScalaNode<Type.Param>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypePlaceholder node, RuleContext data) {
        return visit((ScalaNode<Type.Placeholder>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeProject node, RuleContext data) {
        return visit((ScalaNode<Type.Project>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeRefine node, RuleContext data) {
        return visit((ScalaNode<Type.Refine>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeRepeated node, RuleContext data) {
        return visit((ScalaNode<Type.Repeated>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeSelect node, RuleContext data) {
        return visit((ScalaNode<Type.Select>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeSingleton node, RuleContext data) {
        return visit((ScalaNode<Type.Singleton>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeTuple node, RuleContext data) {
        return visit((ScalaNode<Type.Tuple>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeVar node, RuleContext data) {
        return visit((ScalaNode<Type.Var>) node, data);
    }

    @Override
    public RuleContext visit(ASTTypeWith node, RuleContext data) {
        return visit((ScalaNode<Type.With>) node, data);
    }
}
