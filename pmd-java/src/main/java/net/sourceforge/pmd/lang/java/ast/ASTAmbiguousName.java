/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An ambiguous name occurring in any context. Without a disambiguation
 * pass that taking care of obscuring rules and the current declarations
 * in scope, this node could be a type, package, or variable name -we
 * can't know for sure. The node is a placeholder for that unknown entity.
 * It implements both {@link ASTType} and {@link ASTPrimaryExpression} to
 * be able to be inserted in their hierarchy (maybe that should be changed
 * though).
 *
 * <p>This node corresponds simultaneously to the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-6.html#jls-AmbiguousName">AmbiguousName</a>
 * and PackageOrTypeName productions of the JLS.
 *
 * <pre class="grammar">
 *
 * AmbiguousNameExpr ::= &lt;IDENTIFIER&gt; ( "." &lt;IDENTIFIER&gt;)*
 *
 * </pre>
 *
 * @implNote <h3>Disambiguation</h3>
 *
 * <p>Some ambiguous names are pushed by the expression parser because
 * we don't want to look too far ahead (in primary prefix). But it can
 * happen that the next segment (primary suffix) constrains the name to
 * be e.g. a type name or an expression name. E.g. From the JLS:
 *
 * <blockquote>
 * A name is syntactically classified as an ExpressionName in these contexts:
 *   ...
 * - As the qualifying expression in a qualified class instance creation
 *  expression (§15.9)
 * </blockquote>
 *
 * We don't know at the moment the name is parsed that it will be
 * followed by "." "new" and a constructor call. But as soon as the
 * {@link ASTConstructorCall} is pushed, we know that the LHS must be an
 * expression. In that case, the name can be reclassified, and e.g. if
 * it's a simple name be promoted to {@link ASTVariableAccess}. This
 * type of immediate disambiguation is carried out by the {@link AbstractJavaNode#jjtClose()}
 * method of those nodes that do force a specific context on their
 * left-hand side. See also {@link LeftRecursiveNode}.
 *
 * <p>Another mechanism is {@link #forceExprContext()} and {@link #forceTypeContext()},
 * which are called by the parser to promote an ambiguous name to an
 * expression or a type when exiting from the {@link JavaParserImpl#PrimaryExpression()}
 * production or {@link JavaParserImpl#ClassOrInterfaceType()}.
 *
 * <p>Those two mechanisms perform the first classification step, the
 * one that only depends on the syntactic context and not on semantic
 * information. A second pass on the AST after building the symbol tables
 * would allow us to remove all the remaining ambiguous names.
 */
public final class ASTAmbiguousName extends AbstractJavaExpr implements ASTReferenceType, ASTPrimaryExpression {

    ASTAmbiguousName(int id) {
        super(id);
    }


    ASTAmbiguousName(String id) {
        super(JavaParserImplTreeConstants.JJTAMBIGUOUSNAME);
        setImage(id);
    }


    public String getName() {
        return getImage();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public String getTypeImage() {
        return getImage();
    }

    // Package-private construction methods:


    /**
     * Called by the parser if this ambiguous name was a full expression.
     * Then, since the node was in an expression syntactic context, we
     * can do some preliminary reclassification:
     * <ul>
     *     <li>If the name is a single identifier, then this can be
     *       reclassified as an {@link ASTVariableAccess}
     *     <li>If the name is a sequence of identifiers, then the last
     *       segment can be reclassified as an {@link ASTFieldAccess},
     *       and the rest of the sequence (to the left) is left ambiguous.
     * </ul>
     *
     * @return the node which will replace this node in the tree
     */
    ASTExpression forceExprContext() {
        // by the time this is called, this node is on top of the stack,
        // meaning, it has no parent
        return shrinkOneSegment(ASTVariableAccess::new, ASTFieldAccess::new);
    }


    /**
     * Called by the parser if this ambiguous name was expected to be
     * a type name. Then we simply promote it to an {@link ASTClassOrInterfaceType}
     * with the appropriate LHS.
     *
     * @return the node which will replace this node in the tree
     */
    ASTClassOrInterfaceType forceTypeContext() {
        // same, there's no parent here
        return shrinkOneSegment(ASTClassOrInterfaceType::new, ASTClassOrInterfaceType::new);
    }


    /**
     * Low level method to reclassify this ambiguous name. Basically
     * the name is split in two: the part before the last dot, and the
     * part after it.
     *
     * @param simpleNameHandler Called with this name as parameter if
     *                          this ambiguous name is a simple name.
     *                          No resizing of the node is performed.
     *
     * @param splitNameConsumer Called with this node as first parameter,
     *                          and the last name segment as second
     *                          parameter. After the handler is executed,
     *                          the text bounds of this node are shrunk
     *                          to fit to only the left part. The handler
     *                          may e.g. move the node to another parent.
     * @param <T>               Result type
     *
     * @return The node that will replace this one.
     */
    private <T extends AbstractJavaNode> T shrinkOneSegment(Function<ASTAmbiguousName, T> simpleNameHandler,
                                                            BiFunction<ASTAmbiguousName, String, T> splitNameConsumer) {

        String image = getImage();

        int lastDotIdx = image.lastIndexOf('.');

        if (lastDotIdx < 0) {
            T res = simpleNameHandler.apply(this);
            if (res != null) {
                res.copyTextCoordinates(this);
            }
            return res;
        }

        String lastSegment = image.substring(lastDotIdx + 1);
        String remainingAmbiguous = image.substring(0, lastDotIdx);

        T res = splitNameConsumer.apply(this, lastSegment);
        // copy coordinates before shrinking
        if (res != null) {
            res.copyTextCoordinates(this);
        }

        // shift the ident + the dot
        this.shiftTokens(0, -2);
        setImage(remainingAmbiguous);
        return res;
    }

    /**
     * Delete this name from the children of the parent. The image of
     * this name is prepended to the image of the parent.
     */
    void deleteInParentPrependImage(char delim) {
        AbstractJavaNode parent = (AbstractJavaNode) getParent();
        String image = parent.getImage();
        parent.setImage(getName() + delim + image);

        parent.removeChildAtIndex(this.getIndexInParent());
    }

    /**
     * A specialized version of {@link #shrinkOneSegment(Function, BiFunction)}
     * for nodes that carry the unambiguous part as their own image.
     * Basically the last segment is set as the image of the parent
     * node, and no node corresponds to it.
     */
    void shrinkOrDeleteInParentSetImage() {
        // the params of the lambdas here are this object,
        // but if we use them instead of this, we avoid capturing the
        // this reference and the lambdas can be optimised to a singleton
        shrinkOneSegment(
            simpleName -> {
                AbstractJavaNode parent = (AbstractJavaNode) simpleName.getParent();
                parent.setImage(simpleName.getImage());
                parent.removeChildAtIndex(simpleName.getIndexInParent());
                return null;
            },
            (ambig, simpleName) -> {
                AbstractJavaNode parent = (AbstractJavaNode) ambig.getParent();
                parent.setImage(simpleName);
                return null;
            }
        );
    }
}
