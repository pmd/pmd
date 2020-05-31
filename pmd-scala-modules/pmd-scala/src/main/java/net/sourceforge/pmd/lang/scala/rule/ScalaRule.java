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
import net.sourceforge.pmd.lang.scala.ast.ASTCase;
import net.sourceforge.pmd.lang.scala.ast.ASTCtorPrimary;
import net.sourceforge.pmd.lang.scala.ast.ASTCtorSecondary;
import net.sourceforge.pmd.lang.scala.ast.ASTDeclDef;
import net.sourceforge.pmd.lang.scala.ast.ASTDeclType;
import net.sourceforge.pmd.lang.scala.ast.ASTDeclVal;
import net.sourceforge.pmd.lang.scala.ast.ASTDeclVar;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnClass;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnDef;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnMacro;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnObject;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnTrait;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnType;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnVal;
import net.sourceforge.pmd.lang.scala.ast.ASTDefnVar;
import net.sourceforge.pmd.lang.scala.ast.ASTEnumeratorGenerator;
import net.sourceforge.pmd.lang.scala.ast.ASTEnumeratorGuard;
import net.sourceforge.pmd.lang.scala.ast.ASTEnumeratorVal;
import net.sourceforge.pmd.lang.scala.ast.ASTImport;
import net.sourceforge.pmd.lang.scala.ast.ASTImporteeName;
import net.sourceforge.pmd.lang.scala.ast.ASTImporteeRename;
import net.sourceforge.pmd.lang.scala.ast.ASTImporteeUnimport;
import net.sourceforge.pmd.lang.scala.ast.ASTImporteeWildcard;
import net.sourceforge.pmd.lang.scala.ast.ASTImporter;
import net.sourceforge.pmd.lang.scala.ast.ASTInit;
import net.sourceforge.pmd.lang.scala.ast.ASTLitBoolean;
import net.sourceforge.pmd.lang.scala.ast.ASTLitByte;
import net.sourceforge.pmd.lang.scala.ast.ASTLitChar;
import net.sourceforge.pmd.lang.scala.ast.ASTLitDouble;
import net.sourceforge.pmd.lang.scala.ast.ASTLitFloat;
import net.sourceforge.pmd.lang.scala.ast.ASTLitInt;
import net.sourceforge.pmd.lang.scala.ast.ASTLitLong;
import net.sourceforge.pmd.lang.scala.ast.ASTLitNull;
import net.sourceforge.pmd.lang.scala.ast.ASTLitShort;
import net.sourceforge.pmd.lang.scala.ast.ASTLitString;
import net.sourceforge.pmd.lang.scala.ast.ASTLitSymbol;
import net.sourceforge.pmd.lang.scala.ast.ASTLitUnit;
import net.sourceforge.pmd.lang.scala.ast.ASTModAbstract;
import net.sourceforge.pmd.lang.scala.ast.ASTModAnnot;
import net.sourceforge.pmd.lang.scala.ast.ASTModCase;
import net.sourceforge.pmd.lang.scala.ast.ASTModContravariant;
import net.sourceforge.pmd.lang.scala.ast.ASTModCovariant;
import net.sourceforge.pmd.lang.scala.ast.ASTModFinal;
import net.sourceforge.pmd.lang.scala.ast.ASTModImplicit;
import net.sourceforge.pmd.lang.scala.ast.ASTModInline;
import net.sourceforge.pmd.lang.scala.ast.ASTModLazy;
import net.sourceforge.pmd.lang.scala.ast.ASTModOverride;
import net.sourceforge.pmd.lang.scala.ast.ASTModPrivate;
import net.sourceforge.pmd.lang.scala.ast.ASTModProtected;
import net.sourceforge.pmd.lang.scala.ast.ASTModSealed;
import net.sourceforge.pmd.lang.scala.ast.ASTModValParam;
import net.sourceforge.pmd.lang.scala.ast.ASTModVarParam;
import net.sourceforge.pmd.lang.scala.ast.ASTNameAnonymous;
import net.sourceforge.pmd.lang.scala.ast.ASTNameIndeterminate;
import net.sourceforge.pmd.lang.scala.ast.ASTPatAlternative;
import net.sourceforge.pmd.lang.scala.ast.ASTPatBind;
import net.sourceforge.pmd.lang.scala.ast.ASTPatExtract;
import net.sourceforge.pmd.lang.scala.ast.ASTPatExtractInfix;
import net.sourceforge.pmd.lang.scala.ast.ASTPatInterpolate;
import net.sourceforge.pmd.lang.scala.ast.ASTPatSeqWildcard;
import net.sourceforge.pmd.lang.scala.ast.ASTPatTuple;
import net.sourceforge.pmd.lang.scala.ast.ASTPatTyped;
import net.sourceforge.pmd.lang.scala.ast.ASTPatVar;
import net.sourceforge.pmd.lang.scala.ast.ASTPatWildcard;
import net.sourceforge.pmd.lang.scala.ast.ASTPatXml;
import net.sourceforge.pmd.lang.scala.ast.ASTPkg;
import net.sourceforge.pmd.lang.scala.ast.ASTPkgObject;
import net.sourceforge.pmd.lang.scala.ast.ASTQuasi;
import net.sourceforge.pmd.lang.scala.ast.ASTSelf;
import net.sourceforge.pmd.lang.scala.ast.ASTSource;
import net.sourceforge.pmd.lang.scala.ast.ASTTemplate;
import net.sourceforge.pmd.lang.scala.ast.ASTTermAnnotate;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApply;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApplyInfix;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApplyType;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApplyUnary;
import net.sourceforge.pmd.lang.scala.ast.ASTTermAscribe;
import net.sourceforge.pmd.lang.scala.ast.ASTTermAssign;
import net.sourceforge.pmd.lang.scala.ast.ASTTermBlock;
import net.sourceforge.pmd.lang.scala.ast.ASTTermDo;
import net.sourceforge.pmd.lang.scala.ast.ASTTermEta;
import net.sourceforge.pmd.lang.scala.ast.ASTTermFor;
import net.sourceforge.pmd.lang.scala.ast.ASTTermForYield;
import net.sourceforge.pmd.lang.scala.ast.ASTTermFunction;
import net.sourceforge.pmd.lang.scala.ast.ASTTermIf;
import net.sourceforge.pmd.lang.scala.ast.ASTTermInterpolate;
import net.sourceforge.pmd.lang.scala.ast.ASTTermMatch;
import net.sourceforge.pmd.lang.scala.ast.ASTTermName;
import net.sourceforge.pmd.lang.scala.ast.ASTTermNew;
import net.sourceforge.pmd.lang.scala.ast.ASTTermNewAnonymous;
import net.sourceforge.pmd.lang.scala.ast.ASTTermParam;
import net.sourceforge.pmd.lang.scala.ast.ASTTermPartialFunction;
import net.sourceforge.pmd.lang.scala.ast.ASTTermPlaceholder;
import net.sourceforge.pmd.lang.scala.ast.ASTTermRepeated;
import net.sourceforge.pmd.lang.scala.ast.ASTTermReturn;
import net.sourceforge.pmd.lang.scala.ast.ASTTermSelect;
import net.sourceforge.pmd.lang.scala.ast.ASTTermSuper;
import net.sourceforge.pmd.lang.scala.ast.ASTTermThis;
import net.sourceforge.pmd.lang.scala.ast.ASTTermThrow;
import net.sourceforge.pmd.lang.scala.ast.ASTTermTry;
import net.sourceforge.pmd.lang.scala.ast.ASTTermTryWithHandler;
import net.sourceforge.pmd.lang.scala.ast.ASTTermTuple;
import net.sourceforge.pmd.lang.scala.ast.ASTTermWhile;
import net.sourceforge.pmd.lang.scala.ast.ASTTermXml;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeAnd;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeAnnotate;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeApply;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeApplyInfix;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeBounds;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeByName;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeExistential;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeFunction;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeImplicitFunction;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeLambda;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeName;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeOr;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeParam;
import net.sourceforge.pmd.lang.scala.ast.ASTTypePlaceholder;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeProject;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeRefine;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeRepeated;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeSelect;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeSingleton;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeTuple;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeVar;
import net.sourceforge.pmd.lang.scala.ast.ASTTypeWith;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;

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
        for (ScalaNode<?> child : node.children()) {
            child.accept(this, data);
        }
        return data;
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
