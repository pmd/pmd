/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;

public final class ASTExpression extends AbstractVfNode {
    private static final Logger LOGGER = Logger.getLogger(ASTExpression.class.getName());

    ASTExpression(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVfVisitor(VfVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    private void logWarning(String warning, Node node) {
        LOGGER.warning(warning
                + ". nodeClass=" + node.getClass().getSimpleName()
                // + ", fileName=" + AbstractTokenManager.getFileName()
                + ", beginLine=" + node.getBeginLine()
                + ", image=" + node.getImage());
    }

    /**
     * <p>
     * An Expression can contain one or more strings that map to a piece of data. This method maps the string
     * from the Visualforce page to terminal AST node that the string represents. The terminal node will be either an
     * ASTIdentifier or ASTLiteral. It is the terminal node that is most important since it represents the type of data
     * that will be displayed in the page.
     * </p>
     * <p>
     * The string representation can be reconstructed by starting at the {@code Identifier} node and traversing its
     * siblings until a node other than a {@code DotExpression} is encountered. Some more advanced situations aren't
     * currently handled by this method. The method will throw an exception in such cases.
     * </p>
     * <pre>{@code
     * <apex:outputText value="{!MyValue}" /> results in AST
     * <Identifier Image='MyValue'/>
     * The method would return key=ASTIdentifier(Image='MyValue'), value="MyValue"
     * }</pre>
     * <pre>{@code
     * <apex:outputText value="{!MyObject__c.Text__c}" /> results in AST (It's important to notice that DotExpression is
     * a sibling of Identifier.
     * <Identifier Image='MyObject__c'/>
     * <DotExpression Image=''>
     *     <Identifier Image='Text__c'/>
     * </DotExpression>
     * This method would return key=ASTIdentifier(Image='Text__c'), value="MyObject__c.Text__c"
     * }</pre>
     *
     * THE FOLLOWING SITUATIONS ARE NOT HANDLED AND WILL THROW AN EXCEPTION.
     * This syntax causes ambiguities with Apex Controller methods that return Maps versus accessing a CustomObject's
     * field via array notation. This may be addressed in a future release.
     *
     * <pre>{@code
     * <apex:outputText value="{!MyObject__c['Text__c']}" /> results in AST
     * <Identifier Image='MyObject__c'/>
     * <Expression Image=''>
     *     <Literal Image='&apos;Text__c&apos;'>
     * </Expression>

     * <apex:outputText value="{!MyObject__c[AnotherObject__c.Id]}" /> results in AST
     * <Identifier Image='MyObject__c'/>
     * <Expression Image=''>
     *     <Identifier Image='AnotherObject__c'/>
     *         <DotExpression Image=''>
     *             <Identifier Image='Id'/>
     *         </DotExpression>
     *     </Identifier>
     * </Expression>
     * }</pre>
     *
     * @throws DataNodeStateException if the results of this method could have been incorrect. Callers should typically
     * not rethrow this exception, as it will happen often and doesn't represent a terminal exception.
     */
    public Map<VfTypedNode, String> getDataNodes() throws DataNodeStateException {
        Map<VfTypedNode, String> result = new IdentityHashMap<>();

        int numChildren = getNumChildren();
        List<ASTIdentifier> identifiers = findChildrenOfType(ASTIdentifier.class);
        for (ASTIdentifier identifier : identifiers) {
            LinkedList<VfTypedNode> identifierNodes = new LinkedList<>();

            // The Identifier is the first item that makes up the string
            identifierNodes.add(identifier);
            int index = identifier.getIndexInParent();

            // Iterate through the rest of the children looking for ASTDotExpression nodes.
            // The Image value of these nodes will be used to reconstruct the string. Any other node encountered will
            // cause the while loop to break. The content of identifierNodes is used to construct the string and map
            // it to the last element in identifierNodes.
            index++;
            while (index < numChildren) {
                final Node node = getChild(index);
                if (node instanceof ASTDotExpression) {
                    // The next part of the identifier will constructed from dot or array notation
                    if (node.getNumChildren() == 1) {
                        final Node expressionChild = node.getChild(0);
                        if (expressionChild instanceof ASTIdentifier || expressionChild instanceof ASTLiteral) {
                            identifierNodes.add((VfTypedNode) expressionChild);
                        } else {
                            // This should never happen
                            logWarning("Node expected to be Identifier or Literal", node);
                            throw new DataNodeStateException();
                        }
                    } else {
                        // This should never happen
                        logWarning("More than one child found for ASTDotExpression", node);
                        throw new DataNodeStateException();
                    }
                } else if (node instanceof ASTExpression) {
                    // Not currently supported. This can occur in a couple of cases that may be supported in the future.
                    // 1. Custom Field using array notation. MyObject__c['Text__c']
                    // 2. An Apex method that returns a map. ControllerMethod['KeyForMap']
                    throw new DataNodeStateException();
                } else {
                    // Any other node type is not considered part of the identifier and breaks out of the loop
                    break;
                }
                index++;
            }

            // Convert the list of nodes to a string representation, store the last node in the list as the map's key
            String idString = String.join(".", identifierNodes.stream()
                    .map(i -> i.getImage())
                    .collect(Collectors.toList()));
            result.put(identifierNodes.getLast(), idString);
        }
        return result;
    }


    /**
     * Thrown in cases where the the Identifiers in this node aren't ALL successfully parsed in a call to
     * {@link #getDataNodes()}
     */
    public static final class DataNodeStateException extends Exception {
    }

}
