/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
import scala.meta.Tree;
import scala.meta.Tree.Quasi;
import scala.meta.Type;

/**
 * Translates Scala's AST to a PMD-compatible AST.
 *
 */
class ScalaTreeBuilder {

    private static final Map<Class<? extends Tree>, Constructor<? extends ScalaNode<?>>> NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();

    static {
        register(Case.class, ASTCase.class);
        register(Ctor.Primary.class, ASTCtorPrimary.class);
        register(Ctor.Secondary.class, ASTCtorSecondary.class);
        register(Decl.Def.class, ASTDeclDef.class);
        register(Decl.Type.class, ASTDeclType.class);
        register(Decl.Val.class, ASTDeclVal.class);
        register(Decl.Var.class, ASTDeclVar.class);
        register(Defn.Class.class, ASTDefnClass.class);
        register(Defn.Def.class, ASTDefnDef.class);
        register(Defn.Macro.class, ASTDefnMacro.class);
        register(Defn.Object.class, ASTDefnObject.class);
        register(Defn.Trait.class, ASTDefnTrait.class);
        register(Defn.Type.class, ASTDefnType.class);
        register(Defn.Val.class, ASTDefnVal.class);
        register(Defn.Var.class, ASTDefnVar.class);
        register(Enumerator.Generator.class, ASTEnumeratorGenerator.class);
        register(Enumerator.Guard.class, ASTEnumeratorGuard.class);
        register(Enumerator.Val.class, ASTEnumeratorVal.class);
        register(Import.class, ASTImport.class);
        register(Importee.Name.class, ASTImporteeName.class);
        register(Importee.Rename.class, ASTImporteeRename.class);
        register(Importee.Unimport.class, ASTImporteeUnimport.class);
        register(Importee.Wildcard.class, ASTImporteeWildcard.class);
        register(Importer.class, ASTImporter.class);
        register(Init.class, ASTInit.class);
        register(Lit.Boolean.class, ASTLitBoolean.class);
        register(Lit.Byte.class, ASTLitByte.class);
        register(Lit.Char.class, ASTLitChar.class);
        register(Lit.Double.class, ASTLitDouble.class);
        register(Lit.Float.class, ASTLitFloat.class);
        register(Lit.Int.class, ASTLitInt.class);
        register(Lit.Long.class, ASTLitLong.class);
        register(Lit.Null.class, ASTLitNull.class);
        register(Lit.Short.class, ASTLitShort.class);
        register(Lit.String.class, ASTLitString.class);
        register(Lit.Symbol.class, ASTLitSymbol.class);
        register(Lit.Unit.class, ASTLitUnit.class);
        register(Mod.Abstract.class, ASTModAbstract.class);
        register(Mod.Annot.class, ASTModAnnot.class);
        register(Mod.Case.class, ASTModCase.class);
        register(Mod.Contravariant.class, ASTModContravariant.class);
        register(Mod.Covariant.class, ASTModCovariant.class);
        register(Mod.Final.class, ASTModFinal.class);
        register(Mod.Implicit.class, ASTModImplicit.class);
        register(Mod.Inline.class, ASTModInline.class);
        register(Mod.Lazy.class, ASTModLazy.class);
        register(Mod.Override.class, ASTModOverride.class);
        register(Mod.Private.class, ASTModPrivate.class);
        register(Mod.Protected.class, ASTModProtected.class);
        register(Mod.Sealed.class, ASTModSealed.class);
        register(Mod.ValParam.class, ASTModValParam.class);
        register(Mod.VarParam.class, ASTModVarParam.class);
        register(Name.Anonymous.class, ASTNameAnonymous.class);
        register(Name.Indeterminate.class, ASTNameIndeterminate.class);
        register(Pat.Alternative.class, ASTPatAlternative.class);
        register(Pat.Bind.class, ASTPatBind.class);
        register(Pat.Extract.class, ASTPatExtract.class);
        register(Pat.ExtractInfix.class, ASTPatExtractInfix.class);
        register(Pat.Interpolate.class, ASTPatInterpolate.class);
        register(Pat.SeqWildcard.class, ASTPatSeqWildcard.class);
        register(Pat.Tuple.class, ASTPatTuple.class);
        register(Pat.Typed.class, ASTPatTyped.class);
        register(Pat.Var.class, ASTPatVar.class);
        register(Pat.Wildcard.class, ASTPatWildcard.class);
        register(Pat.Xml.class, ASTPatXml.class);
        register(Pkg.class, ASTPkg.class);
        register(Pkg.Object.class, ASTPkgObject.class);
        register(Quasi.class, ASTQuasi.class);
        register(Self.class, ASTSelf.class);
        register(Source.class, ASTSource.class);
        register(Template.class, ASTTemplate.class);
        register(Term.Annotate.class, ASTTermAnnotate.class);
        register(Term.Apply.class, ASTTermApply.class);
        register(Term.ApplyInfix.class, ASTTermApplyInfix.class);
        register(Term.ApplyType.class, ASTTermApplyType.class);
        register(Term.ApplyUnary.class, ASTTermApplyUnary.class);
        register(Term.Ascribe.class, ASTTermAscribe.class);
        register(Term.Assign.class, ASTTermAssign.class);
        register(Term.Block.class, ASTTermBlock.class);
        register(Term.Do.class, ASTTermDo.class);
        register(Term.Eta.class, ASTTermEta.class);
        register(Term.For.class, ASTTermFor.class);
        register(Term.ForYield.class, ASTTermForYield.class);
        register(Term.Function.class, ASTTermFunction.class);
        register(Term.If.class, ASTTermIf.class);
        register(Term.Interpolate.class, ASTTermInterpolate.class);
        register(Term.Match.class, ASTTermMatch.class);
        register(Term.Name.class, ASTTermName.class);
        register(Term.NewAnonymous.class, ASTTermNewAnonymous.class);
        register(Term.New.class, ASTTermNew.class);
        register(Term.Param.class, ASTTermParam.class);
        register(Term.PartialFunction.class, ASTTermPartialFunction.class);
        register(Term.Placeholder.class, ASTTermPlaceholder.class);
        register(Term.Repeated.class, ASTTermRepeated.class);
        register(Term.Return.class, ASTTermReturn.class);
        register(Term.Select.class, ASTTermSelect.class);
        register(Term.Super.class, ASTTermSuper.class);
        register(Term.This.class, ASTTermThis.class);
        register(Term.Throw.class, ASTTermThrow.class);
        register(Term.Try.class, ASTTermTry.class);
        register(Term.TryWithHandler.class, ASTTermTryWithHandler.class);
        register(Term.Tuple.class, ASTTermTuple.class);
        register(Term.While.class, ASTTermWhile.class);
        register(Term.Xml.class, ASTTermXml.class);
        register(Type.And.class, ASTTypeAnd.class);
        register(Type.Annotate.class, ASTTypeAnnotate.class);
        register(Type.Apply.class, ASTTypeApply.class);
        register(Type.ApplyInfix.class, ASTTypeApplyInfix.class);
        register(Type.Bounds.class, ASTTypeBounds.class);
        register(Type.ByName.class, ASTTypeByName.class);
        register(Type.Existential.class, ASTTypeExistential.class);
        register(Type.Function.class, ASTTypeFunction.class);
        register(Type.ImplicitFunction.class, ASTTypeImplicitFunction.class);
        register(Type.Lambda.class, ASTTypeLambda.class);
        register(Type.Method.class, ASTTypeMethod.class);
        register(Type.Name.class, ASTTypeName.class);
        register(Type.Or.class, ASTTypeOr.class);
        register(Type.Param.class, ASTTypeParam.class);
        register(Type.Placeholder.class, ASTTypePlaceholder.class);
        register(Type.Project.class, ASTTypeProject.class);
        register(Type.Refine.class, ASTTypeRefine.class);
        register(Type.Repeated.class, ASTTypeRepeated.class);
        register(Type.Select.class, ASTTypeSelect.class);
        register(Type.Singleton.class, ASTTypeSingleton.class);
        register(Type.Tuple.class, ASTTypeTuple.class);
        register(Type.Var.class, ASTTypeVar.class);
        register(Type.With.class, ASTTypeWith.class);
    }

    // The nodes having children built.
    private final Stack<AbstractScalaNode<?>> nodes = new Stack<>();

    private static <T extends Tree> void register(Class<T> nodeType,
            Class<? extends ScalaNode<T>> nodeAdapterType) {
        try {
            NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType, nodeAdapterType.getDeclaredConstructor(nodeType));
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Tree> AbstractScalaNode<T> createNodeAdapter(T node) {
        try {

            Constructor<? extends AbstractScalaNode<T>> constructor = null;

            // This isInstance is unfortunately necessary as Scala gives us
            // access to the Interface (Trait) of classes at compile time, but
            // at runtime only operates using a synthetic Impl class. So at
            // runtime, Case.class is really CaseImpl.class due to the
            // translation between Scala Traits and Java Classes
            for (Class<?> treeClass : NODE_TYPE_TO_NODE_ADAPTER_TYPE.keySet()) {
                if (treeClass.isInstance(node)) {
                    constructor = (Constructor<? extends AbstractScalaNode<T>>) NODE_TYPE_TO_NODE_ADAPTER_TYPE.get(treeClass);
                }
            }

            if (constructor == null) {
                throw new IllegalArgumentException(
                        "There is no Node adapter class registered for the Node class: " + node.getClass());
            }
            return constructor.newInstance(node);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }

    /**
     * Construct a matching tree that implements the PMD Node interface.
     *
     * @param <T>
     *            the scala node that extends the Tree trait
     * @param astNode
     *            the Java node that extends the PMD Node interface
     * @return a PMD compatible node representing the Scala AST node
     */
    <T extends Tree> ScalaNode<T> build(T astNode) {
        return buildInternal(astNode);
    }

    private <T extends Tree> ScalaNode<T> buildInternal(T astNode) {
        // Create a Node
        AbstractScalaNode<T> node = createNodeAdapter(astNode);
        // Append to parent
        AbstractScalaNode<?> parent = nodes.isEmpty() ? null : nodes.peek();
        if (parent != null) {
            parent.addChild(node, parent.getNumChildren());
        }

        // Build the children...
        nodes.push(node);
        int childrenNum = astNode.children().size();
        for (int i = 0; i < childrenNum; i++) {
            buildInternal(astNode.children().apply(i));
        }
        nodes.pop();

        return node;
    }
}
