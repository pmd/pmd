/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.AbruptCompletionAnalysis.canCompleteNormally;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.PatternBindingsUtil.bindersOfExpr;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.PatternBindingsUtil.bindersOfPattern;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTCompactConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceList;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.PatternBindingsUtil.BindSet;
import net.sourceforge.pmd.lang.java.types.JClassType;


/**
 * Visitor that builds all symbol table stacks for a compilation unit.
 * It's bound to a compilation unit and cannot be reused for several ACUs.
 *
 * @since 7.0.0
 */
public final class SymbolTableResolver {

    private SymbolTableResolver() {
        // façade
    }

    public static void traverse(JavaAstProcessor processor, ASTCompilationUnit root) {
        SymTableFactory helper = new SymTableFactory(root.getPackageName(), processor);
        ReferenceCtx ctx = ReferenceCtx.root(processor, root);
        Set<DeferredNode> todo = Collections.singleton(new DeferredNode(root, ctx, SymbolTableImpl.EMPTY));
        do {
            Set<DeferredNode> newDeferred = new HashSet<>();
            for (DeferredNode deferred : todo) {
                MyVisitor visitor = new MyVisitor(helper, todo, newDeferred);
                visitor.traverse(deferred);
            }
            todo = newDeferred;
        } while (!todo.isEmpty());
    }

    private static final class DeferredNode {

        final JavaNode node;
        // this is data used to resume the traversal
        final ReferenceCtx enclosingCtx;
        final JSymbolTable localStackTop;

        private DeferredNode(JavaNode node, ReferenceCtx enclosingCtx, JSymbolTable localStackTop) {
            this.node = node;
            this.enclosingCtx = enclosingCtx;
            this.localStackTop = localStackTop;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DeferredNode that = (DeferredNode) o;
            return node.equals(that.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node);
        }
    }

    private static class MyVisitor extends JavaVisitorBase<@NonNull ReferenceCtx, Void> {

        private final SymTableFactory f;
        private final Deque<JSymbolTable> stack = new ArrayDeque<>();

        private final Deque<JClassType> enclosingType = new ArrayDeque<>();

        private final Set<DeferredNode> deferredInPrevRound;
        private final Set<DeferredNode> newDeferred;
        private final StatementVisitor stmtVisitor = new StatementVisitor();

        MyVisitor(SymTableFactory helper, Set<DeferredNode> deferredInPrevRound, Set<DeferredNode> newDeferred) {
            f = helper;
            this.deferredInPrevRound = deferredInPrevRound;
            this.newDeferred = newDeferred;
        }


        /**
         * Start the analysis.
         */
        void traverse(DeferredNode task) {
            assert stack.isEmpty()
                : "Stack should be empty when starting the traversal";

            stack.push(task.localStackTop);
            task.node.acceptVisitor(this, task.enclosingCtx);
            JSymbolTable last = stack.pop();

            assert last == task.localStackTop  // NOPMD CompareObjectsWithEquals
                : "Unbalanced stack push/pop! Started with " + task.localStackTop + ", finished on " + last;
        }

        @Override
        public Void visit(ASTClassType node, @NonNull ReferenceCtx data) {
            // all types are disambiguated in this resolver, because
            // the symbols available inside the body of an anonymous class
            // depend on the type of the superclass/superinterface (the Runnable in `new Runnable() { }`).
            // This type may be
            // 1. a diamond (`new Function<>() { ... }`),
            // 2. qualified (`expr.new Inner() { ... }`)
            // 3. both
            // For case 2, resolution of the symbol of Inner needs full
            // type resolution of the qualifying `expr`, which may depend
            // on the disambiguation of arbitrary type nodes (eg method
            // parameters, local var types).
            // Which means, as early as in this visitor, we may need the
            // symbols of all class types. For that reason we disambiguate
            // them early.
            // Todo test ambig names in expressions depended on by the qualifier
            f.disambig(NodeStream.of(node), data);
            return null;
        }

        @Override
        public Void visit(ASTAmbiguousName node, @NonNull ReferenceCtx data) {
            // see comment in visit(ClassType)
            f.disambig(NodeStream.of(node), data);
            return null;
        }

        @Override
        public Void visit(ASTModifierList node, @NonNull ReferenceCtx ctx) {
            // do nothing
            return null;
        }

        @Override
        public Void visit(ASTCompilationUnit node, @NonNull ReferenceCtx ctx) {
            Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.children(ASTImportDeclaration.class)
                                                                            .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

            int pushed = 0;
            pushed += pushOnStack(f.importsOnDemand(top(), isImportOnDemand.get(true)));
            pushed += pushOnStack(f.javaLangSymTable(top()));
            pushed += pushOnStack(f.samePackageSymTable(top()));
            pushed += pushOnStack(f.singleImportsSymbolTable(top(), isImportOnDemand.get(false)));

            // TODO Java 23 implicitly imports "import static java.io.IO.*" for implicitly declared classes
            boolean implicitlyDeclaredClass = node.isImplicitlyDeclaredClass();

            NodeStream<ASTTypeDeclaration> typeDecls = node.getTypeDeclarations();

            // types declared inside the compilation unit
            pushed += pushOnStack(f.typesInFile(top(), typeDecls));

            if (implicitlyDeclaredClass) {
                // TODO the implicitly declared class is a subclass of Object, but not exactly Object...
                enclosingType.push(node.getTypeSystem().OBJECT);
            }

            setTopSymbolTable(node);

            for (ASTTypeDeclaration td : typeDecls) {
                // preprocess all sibling types
                processTypeHeader(td, ctx);
            }

            // All of the header symbol tables belong to the CompilationUnit
            visitChildren(node, ctx);

            if (implicitlyDeclaredClass) {
                enclosingType.pop();
            }

            popStack(pushed);

            return null;
        }


        private void processTypeHeader(ASTTypeDeclaration node, ReferenceCtx ctx) {
            setTopSymbolTable(node.getModifiers());

            int pushed = pushOnStack(f.selfType(top(), node.getTypeMirror()));
            pushed += pushOnStack(f.typeHeader(top(), node.getSymbol()));

            NodeStream<? extends JavaNode> notBody = node.children().drop(1).dropLast(1);
            for (JavaNode it : notBody) {
                setTopSymbolTable(it);
            }

            popStack(pushed - 1);

            // resolve the supertypes, necessary for TypeMemberSymTable
            f.disambig(notBody, ctx); // extends/implements

            setTopSymbolTable(node);
            popStack();
        }

        @Override
        public Void visitTypeDecl(ASTTypeDeclaration node, @NonNull ReferenceCtx ctx) {
            int pushed = 0;

            enclosingType.push(node.getTypeMirror());
            ReferenceCtx bodyCtx = ctx.scopeDownToNested(node.getSymbol());

            // the following is just for the body
            pushed += pushOnStack(f.typeBody(top(), node.getTypeMirror()));

            setTopSymbolTable(node.getBody());

            // preprocess siblings
            node.getDeclarations(ASTTypeDeclaration.class)
                .forEach(d -> processTypeHeader(d, bodyCtx));


            // process fields first, their type is needed for JSymbolTable#resolveValue
            f.disambig(node.getDeclarations(ASTFieldDeclaration.class)
                           .map(ASTFieldDeclaration::getTypeNode),
                       bodyCtx);
            visitChildren(node.getBody(), bodyCtx);

            enclosingType.pop();

            popStack(pushed);

            return null;
        }

        @Override
        public Void visit(ASTAnonymousClassDeclaration node, @NonNull ReferenceCtx ctx) {
            if (node.getParent() instanceof ASTConstructorCall) {
                // deferred to later, the symbol table for its body depends
                // on typeres of the ctor which may be qualified, and refer
                // to stuff that are declared later in the compilation unit,
                // and not yet disambiged.
                DeferredNode deferredSpec = new DeferredNode(node, ctx, top());
                if (!deferredInPrevRound.contains(deferredSpec)) {
                    newDeferred.add(deferredSpec);
                    return null;
                }
                // otherwise fallthrough
            }
            return visitTypeDecl(node, ctx);
        }

        @Override
        public Void visitMethodOrCtor(ASTExecutableDeclaration node, @NonNull ReferenceCtx ctx) {
            setTopSymbolTable(node.getModifiers());
            int pushed = pushOnStack(f.bodyDeclaration(top(), enclosing(), node.getFormalParameters(), node.getTypeParameters()));
            setTopSymbolTableAndVisitAllChildren(node, ctx);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTInitializer node, @NonNull ReferenceCtx ctx) {
            int pushed = pushOnStack(f.bodyDeclaration(top(), enclosing(), null, null));
            setTopSymbolTableAndVisitAllChildren(node, ctx);
            popStack(pushed);
            return null;
        }


        @Override
        public Void visit(ASTCompactConstructorDeclaration node, @NonNull ReferenceCtx ctx) {
            setTopSymbolTable(node.getModifiers());
            int pushed = pushOnStack(f.recordCtor(top(), enclosing(), node.getSymbol()));
            setTopSymbolTableAndVisitAllChildren(node, ctx);
            popStack(pushed);
            return null;
        }


        @Override
        public Void visit(ASTLambdaExpression node, @NonNull ReferenceCtx ctx) {
            int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), formalsOf(node)));
            setTopSymbolTableAndVisitAllChildren(node, ctx);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTBlock node, @NonNull ReferenceCtx ctx) {
            int pushed = visitBlockLike(node, ctx);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTSwitchStatement node, @NonNull ReferenceCtx ctx) {
            return visitSwitch(node, ctx);
        }

        @Override
        public Void visit(ASTSwitchExpression node, @NonNull ReferenceCtx ctx) {
            return visitSwitch(node, ctx);
        }


        private Void visitSwitch(ASTSwitchLike node, @NonNull ReferenceCtx ctx) {
            setTopSymbolTable(node);
            node.getTestedExpression().acceptVisitor(this, ctx);

            int pushed = 0;

            for (ASTSwitchBranch branch : node.getBranches()) {
                ASTSwitchLabel label = branch.getLabel();
                // collect all bindings. Maybe it's illegal to use composite label with bindings, idk
                BindSet bindings =
                    label.children(ASTPattern.class)
                         .reduce(BindSet.EMPTY, (bindSet, pat) -> bindSet.union(bindersOfPattern(pat)));

                // visit guarded patterns in label
                setTopSymbolTableAndVisit(label, ctx);

                if (branch instanceof ASTSwitchArrowBranch) {
                    pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), bindings.getTrueBindings()));
                    setTopSymbolTableAndVisit(((ASTSwitchArrowBranch) branch).getRightHandSide(), ctx);
                    popStack(pushed);
                    pushed = 0;
                } else if (branch instanceof ASTSwitchFallthroughBranch) {
                    pushed += pushOnStack(f.localVarSymTable(top(), enclosing(), bindings.getTrueBindings()));
                    pushed += visitBlockLike(((ASTSwitchFallthroughBranch) branch).getStatements(), ctx);
                }
            }
            popStack(pushed);
            return null;
        }


        /**
         * Note: caller is responsible for popping.
         */
        private int visitBlockLike(Iterable<? extends JavaNode> node, @NonNull ReferenceCtx ctx) {
            /*
             * Process the statements of a block in a sequence. Each local
             * var/class declaration is only in scope for the following
             * statements (and its own initializer).
             */
            int pushed = 0;
            for (JavaNode st : node) {
                if (st instanceof ASTLocalVariableDeclaration) {
                    pushed += processLocalVarDecl((ASTLocalVariableDeclaration) st, ctx);
                    // note we don't pop here, all those variables will be popped at the end of the block
                } else if (st instanceof ASTLocalClassStatement) {
                    ASTTypeDeclaration local = ((ASTLocalClassStatement) st).getDeclaration();
                    pushed += pushOnStack(f.localTypeSymTable(top(), local.getTypeMirror()));
                    processTypeHeader(local, ctx);
                }

                if (st instanceof ASTStatement) {
                    setTopSymbolTable(st);
                    // those vars are the one produced by pattern bindings/ local var decls
                    PSet<ASTVariableId> newVars = st.acceptVisitor(this.stmtVisitor, ctx);
                    pushed += pushOnStack(f.localVarSymTable(top(), enclosing(), newVars));
                } else {
                    // concise resource initializer
                    assert st instanceof ASTExpression && st.getParent() instanceof ASTResource : st;
                    setTopSymbolTable(st.getParent());
                    st.acceptVisitor(this, ctx);
                }
            }

            return pushed;
        }

        /**
         * Note: caller is responsible for popping.
         */
        private int processLocalVarDecl(ASTLocalVariableDeclaration st, @NonNull ReferenceCtx ctx) {
            // each variable is visible in its own initializer and the ones of the following variables
            int pushed = 0;
            for (ASTVariableDeclarator declarator : st.children(ASTVariableDeclarator.class)) {
                ASTVariableId varId = declarator.getVarId();
                pushed += pushOnStack(f.localVarSymTable(top(), enclosing(), varId));
                // visit initializer
                setTopSymbolTableAndVisit(declarator.getInitializer(), ctx);
            }
            return pushed;
        }

        @Override
        public Void visit(ASTForeachStatement node, @NonNull ReferenceCtx ctx) {
            // the varId is only in scope in the body and not the iterable expr
            setTopSymbolTableAndVisit(node.getIterableExpr(), ctx);

            ASTVariableId varId = node.getVarId();
            setTopSymbolTableAndVisit(varId.getTypeNode(), ctx);

            int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), varId));
            ASTStatement body = node.getBody();
            // unless it's a block the body statement may never set a
            // symbol table that would have this table as parent,
            // so the table would be dangling
            setTopSymbolTableAndVisit(body, ctx);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTTryStatement node, @NonNull ReferenceCtx ctx) {

            ASTResourceList resources = node.getResources();
            if (resources != null) {
                NodeStream<JavaNode> union =
                    NodeStream.union(
                        stmtsOfResources(resources),
                        // use the body instead of unwrapping it so
                        // that it has the correct symbol table too
                        NodeStream.of(node.getBody())
                    );
                popStack(visitBlockLike(union, ctx));

                for (Node child : node.getBody().asStream().followingSiblings()) {
                    child.acceptVisitor(this, ctx);
                }
            } else {
                visitChildren(node, ctx);
            }

            return null;
        }

        @Override
        public Void visit(ASTCatchClause node, @NonNull ReferenceCtx ctx) {
            int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), node.getParameter().getVarId()));
            setTopSymbolTableAndVisitAllChildren(node, ctx);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTInfixExpression node, @NonNull ReferenceCtx ctx) {
            // need to account for pattern bindings.
            // visit left operand first. Maybe it introduces bindings in the rigt operand.

            node.getLeftOperand().acceptVisitor(this, ctx);

            BinaryOp op = node.getOperator();
            if (op == BinaryOp.CONDITIONAL_AND) {

                PSet<ASTVariableId> trueBindings = bindersOfExpr(node.getLeftOperand()).getTrueBindings();
                if (!trueBindings.isEmpty()) {
                    int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), trueBindings));
                    setTopSymbolTableAndVisit(node.getRightOperand(), ctx);
                    popStack(pushed);
                    return null;
                }

            } else if (op == BinaryOp.CONDITIONAL_OR) {

                PSet<ASTVariableId> falseBindings = bindersOfExpr(node.getLeftOperand()).getFalseBindings();
                if (!falseBindings.isEmpty()) {
                    int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), falseBindings));
                    setTopSymbolTableAndVisit(node.getRightOperand(), ctx);
                    popStack(pushed);
                    return null;
                }

            }

            // not a special case, finish visiting right operand
            return node.getRightOperand().acceptVisitor(this, ctx);
        }

        @Override
        public Void visit(ASTConditionalExpression node, @NonNull ReferenceCtx ctx) {
            // need to account for pattern bindings.

            ASTExpression condition = node.getCondition();
            condition.acceptVisitor(this, ctx);
            BindSet binders = bindersOfExpr(condition);
            if (binders.isEmpty()) {
                node.getThenBranch().acceptVisitor(this, ctx);
                node.getElseBranch().acceptVisitor(this, ctx);
            } else {
                int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), binders.getTrueBindings()));
                setTopSymbolTableAndVisit(node.getThenBranch(), ctx);
                popStack(pushed);
                pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), binders.getFalseBindings()));
                setTopSymbolTableAndVisit(node.getElseBranch(), ctx);
                popStack(pushed);
            }
            return null;
        }

        /**
         * Handles statements. Every visit method should
         * <ul>
         * <li>Visit the statement and its <i>entire</i> subtree according to the
         * scoping rules of the statement (eg, a for statement may declare
         * some variables in its initializers). Note that the subtree should be visited
         * with the enclosing instance of {@link MyVisitor}, not the statement visitor itself.
         * <li>Pop any new symbol tables it pushes.
         * <li>return the set of variables that are <i>introduced</i> by the statement (in following statements)
         * as defined in the JLS: https://docs.oracle.com/javase/specs/jls/se17/html/jls-6.html#jls-6.3.2
         * This is used to implement scoping of pat variables in blocks.
         * <li>
         * </ul>
         *
         * <p>{@link #visitBlockLike(Iterable, ReferenceCtx)} calls this to process a block scope.
         *
         * <p>Statements that have no special rules concerning pat bindings can
         * implement a visit method in the MyVisitor instance, this visitor will
         * default to that implementation.
         */
        class StatementVisitor extends JavaVisitorBase<ReferenceCtx, PSet<ASTVariableId>> {

            @Override
            public PSet<ASTVariableId> visitJavaNode(JavaNode node, ReferenceCtx ctx) {
                throw new IllegalStateException("I only expect statements, got " + node);
            }

            @Override
            public PSet<ASTVariableId> visitStatement(ASTStatement node, ReferenceCtx ctx) {
                // Default to calling the method on the outer class,
                // which will recurse
                node.acceptVisitor(MyVisitor.this, ctx);
                return BindSet.noBindings();
            }

            @Override
            public PSet<ASTVariableId> visit(ASTLabeledStatement node, @NonNull ReferenceCtx ctx) {
                // A pattern variable is introduced by a labeled statement
                // if and only if it is introduced by its immediately contained Statement.
                return node.getStatement().acceptVisitor(this, ctx);
            }

            @Override
            public PSet<ASTVariableId> visit(ASTIfStatement node, ReferenceCtx ctx) {
                BindSet bindSet = bindersOfExpr(node.getCondition());

                ASTStatement thenBranch = node.getThenBranch();
                ASTStatement elseBranch = node.getElseBranch();

                MyVisitor.this.setTopSymbolTableAndVisit(node.getCondition(), ctx);

                // the true bindings of the condition are in scope in the then branch
                int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), bindSet.getTrueBindings()));
                setTopSymbolTableAndVisit(thenBranch, ctx);
                popStack(pushed);

                if (elseBranch != null) {
                    // if there is an else, the false bindings are in scope in the else branch
                    pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), bindSet.getFalseBindings()));
                    setTopSymbolTableAndVisit(elseBranch, ctx);
                    popStack(pushed);
                }

                if (!bindSet.isEmpty()) {
                    // avoid computing canCompleteNormally if possible
                    boolean thenCanCompleteNormally = canCompleteNormally(thenBranch);
                    boolean elseCanCompleteNormally = elseBranch == null || canCompleteNormally(elseBranch);

                    // the bindings are visible in the statements following this if/else
                    // if one of those conditions match
                    if (thenCanCompleteNormally && !elseCanCompleteNormally) {
                        return bindSet.getTrueBindings();
                    } else if (!thenCanCompleteNormally && elseCanCompleteNormally) {
                        return bindSet.getFalseBindings();
                    }
                }
                return BindSet.noBindings();
            }

            @Override
            public PSet<ASTVariableId> visit(ASTWhileStatement node, ReferenceCtx ctx) {
                BindSet bindSet = bindersOfExpr(node.getCondition());

                MyVisitor.this.setTopSymbolTableAndVisit(node.getCondition(), ctx);

                int pushed = pushOnStack(f.localVarSymTable(top(), enclosing(), NodeStream.fromIterable(bindSet.getTrueBindings())));
                setTopSymbolTableAndVisit(node.getBody(), ctx);
                popStack(pushed);

                if (hasNoBreakContainingStmt(node)) {
                    return bindSet.getFalseBindings();
                }
                return BindSet.noBindings();
            }

            @Override
            public PSet<ASTVariableId> visit(ASTForStatement node, @NonNull ReferenceCtx ctx) {
                int pushed = 0;
                ASTStatement init = node.getInit();
                if (init instanceof ASTLocalVariableDeclaration) {
                    pushed += processLocalVarDecl((ASTLocalVariableDeclaration) init, ctx);
                } else {
                    setTopSymbolTableAndVisit(init, ctx);
                }

                ASTExpression condition = node.getCondition();
                MyVisitor.this.setTopSymbolTableAndVisit(node.getCondition(), ctx);

                BindSet bindSet = bindersOfExpr(condition);
                pushed += pushOnStack(f.localVarSymTable(top(), enclosing(), bindSet.getTrueBindings()));
                setTopSymbolTableAndVisit(node.getUpdate(), ctx);
                setTopSymbolTableAndVisit(node.getBody(), ctx);
                popStack(pushed);

                if (bindSet.getFalseBindings().isEmpty()) {
                    return BindSet.noBindings();
                } else {
                    // A pattern variable is introduced by a basic for statement iff
                    // (i) it is introduced by the condition expression when false and
                    // (ii) the contained statement, S, does not contain a reachable
                    // break statement whose break target contains S (§14.15).
                    if (hasNoBreakContainingStmt(node)) {
                        return bindSet.getFalseBindings();
                    } else {
                        return BindSet.noBindings();
                    }
                }
            }

            private boolean hasNoBreakContainingStmt(ASTLoopStatement node) {
                Set<JavaNode> containingStatements = node.ancestorsOrSelf()
                                                         .filter(JavaAstUtils::mayBeBreakTarget)
                                                         .collect(Collectors.toSet());
                return node.getBody()
                           .descendants(ASTBreakStatement.class)
                           .none(it -> containingStatements.contains(it.getTarget()));
            }

            // shadow the methods of the outer class to visit with this visitor.

            @SuppressWarnings("PMD.UnusedPrivateMethod")
            private void setTopSymbolTableAndVisitAllChildren(JavaNode node, @NonNull ReferenceCtx ctx) {
                if (node == null) {
                    return;
                }
                setTopSymbolTable(node);
                visitChildren(node, ctx);
            }

            private void setTopSymbolTableAndVisit(JavaNode node, @NonNull ReferenceCtx ctx) {
                if (node == null) {
                    return;
                }
                setTopSymbolTable(node);
                node.acceptVisitor(this, ctx);
            }
        }


        // <editor-fold defaultstate="collapsed" desc="Stack manipulation routines">

        private void setTopSymbolTable(JavaNode node) {
            InternalApiBridge.setSymbolTable(node, top());
        }

        private JClassType enclosing() {
            if (enclosingType.isEmpty()) {
                return null;
            }
            return enclosingType.getFirst();
        }

        // this does not visit the given node, only its children
        private void setTopSymbolTableAndVisitAllChildren(JavaNode node, @NonNull ReferenceCtx ctx) {
            if (node == null) {
                return;
            }
            setTopSymbolTable(node);
            visitChildren(node, ctx);
        }

        private void setTopSymbolTableAndVisit(JavaNode node, @NonNull ReferenceCtx ctx) {
            if (node == null) {
                return;
            }
            setTopSymbolTable(node);
            node.acceptVisitor(this, ctx);
        }

        private int pushOnStack(JSymbolTable table) {
            if (table == top()) { // NOPMD CompareObjectsWithEquals
                return 0; // and don't set the stack top
            }
            stack.push(table);
            return 1;
        }

        private JSymbolTable popStack() {
            return stack.pop();
        }

        private void popStack(int times) {
            assert stack.size() > times : "Stack is too small (" + times + ") " + stack;
            while (times-- > 0) {
                popStack();
            }
        }

        private JSymbolTable top() {
            return stack.getFirst();
        }


        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Convenience methods">


        static NodeStream<JavaNode> stmtsOfResources(ASTResourceList node) {
            return node.toStream().map(GenericNode::getFirstChild);
        }


        static NodeStream<ASTVariableId> formalsOf(ASTLambdaExpression node) {
            return node.getParameters().toStream().map(ASTLambdaParameter::getVarId);
        }

        // </editor-fold>


    }

}
