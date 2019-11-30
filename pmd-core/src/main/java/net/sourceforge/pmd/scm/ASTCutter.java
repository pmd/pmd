/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.document.DeleteDocumentOperation;
import net.sourceforge.pmd.document.DocumentFile;
import net.sourceforge.pmd.document.DocumentOperationsApplierForNonOverlappingRegions;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A class for generating source files (as a plain text) from the <b>subset</b> of the given AST.
 *
 * The <b>expected</b> invariant is that the following trees should be equal:
 * <ul>
 *     <li>the original tree with every node marked for removal being deleted with its descending nodes</li>
 *     <li>the result of parsing of the plain-text file obtained from the corresponding subset</li>
 * </ul>
 *
 * In other words, for original source <code>TEXT</code> and <code>NODES</code> being a subset of all its AST nodes,
 * <code>parse(cut(TEXT, NODES)) == drop-recursively(parse(TEXT), NODES)</code>.
 *
 * This requirement can be slightly relaxed (such as not requiring presence of nodes that became empty).
 *
 * Please note, that this operation is <b>not required</b> to somehow retain formatting or create
 * nicely formatted files.
 */
public class ASTCutter {
    private final Path tempInputCopy = Files.createTempFile("pmd-", ".tmp");
    private final Parser parser;
    private final Charset charset;

    private final Path file;
    private Node currentRoot;

    /**
     * Create ASTCutter instance
     * @param parser  parser for the original and intermediate source files
     * @param charset charset of source to be cut
     * @param file    file to be modified in-place
     */
    public ASTCutter(Parser parser, Charset charset, Path file) throws IOException {
        this.parser = parser;
        this.charset = charset;
        this.file = file;
    }

    /**
     * Converts list of AST {@link Node}s to be cut off into List of {@link DeleteDocumentOperation}s dealing with
     * the plain text file representation.
     *
     * @param treeRoot     the root of AST corresponding to the file being processed
     * @param deletedNodes the nodes marked for removal (all elements are expected to be accessible from the <code>treeRoot</code>)
     * @return a list of non-overlapping operations that, being applied on the file parsed as <code>treeRoot</code>,
     *         would generate a file that is parsed to <code>treeRoot</code> with all marked codes being cut off recursively.
     */
    private List<DeleteDocumentOperation> calculateTreeCutting(Node treeRoot, Collection<Node> deletedNodes) {
        ArrayList<DeleteDocumentOperation> result = new ArrayList<>();
        calculateTreeCutting(result, treeRoot, new HashSet<>(deletedNodes));
        return result;
    }

    private void calculateTreeCutting(List<DeleteDocumentOperation> result, Node node, Set<Node> deletedNodes) {
        if (deletedNodes.contains(node)) {
            // not recursing, deleting the whole range
            result.add(new DeleteDocumentOperation(
                    node.getBeginLine() - 1, node.getEndLine() - 1,
                    node.getBeginColumn() - 1, node.getEndColumn()));
        } else {
            for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
                calculateTreeCutting(result, node.jjtGetChild(i), deletedNodes);
            }
        }
    }

    /**
     * Accepts the last written file state as a new intermediate state.
     *
     * Please note, this does not anyhow relate to committing files under version control, if any.
     *
     * @return The root node of the "new current" source state
     */
    public Node commitChange() throws IOException {
        Files.copy(file, tempInputCopy, StandardCopyOption.REPLACE_EXISTING);

        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            currentRoot = parser.parse(file.toString(), reader);
        }

        return currentRoot;
    }

    /**
     * Rolls back intermediate file to the last <i>committed</i> state.
     */
    public void rollbackChange() throws IOException {
        Files.copy(tempInputCopy, file, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Rolls back intermediate file, then tries to trim it once again.
     *
     * @param nodesToRemove nodes that have to be dropped from the resulting file together with their descendants.
     *                      They should be accessible from the root returned by the last <code>commitChange</code> call!
     */
    public void writeTrimmedSource(Collection<Node> nodesToRemove) throws IOException {
        rollbackChange();
        try (DocumentFile document = new DocumentFile(file.toFile(), charset)) {
            DocumentOperationsApplierForNonOverlappingRegions applier = new DocumentOperationsApplierForNonOverlappingRegions(document);
            List<DeleteDocumentOperation> operations = calculateTreeCutting(currentRoot, nodesToRemove);
            for (DeleteDocumentOperation operation : operations) {
                applier.addDocumentOperation(operation);
            }
            applier.apply();
        }
    }
}
