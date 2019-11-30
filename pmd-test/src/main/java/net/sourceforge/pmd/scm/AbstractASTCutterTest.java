/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractASTCutterTest {
    private final Path tempFile;
    private final Parser parser;
    private final Charset charset;

    private Node originalRoot;
    private ASTCutter cutter;

    protected AbstractASTCutterTest(Parser parser, Charset charset, String extension) throws IOException {
        tempFile = Files.createTempFile("pmd-test-", "." + extension);
        this.parser = parser;
        this.charset = charset;
    }

    private void assertEqualsAfterRemoval(Node expected, Set<Node> nodesToRemove, Node actual) {
        // current nodes equals
        Assert.assertEquals(expected.getXPathNodeName(), actual.getXPathNodeName());
        Assert.assertEquals(expected.getImage(), actual.getImage());

        // calculate expected list of children
        List<Node> filteredReferenceChildren = new ArrayList<>();
        for (int i = 0; i < expected.jjtGetNumChildren(); ++i) {
            if (!nodesToRemove.contains(expected.jjtGetChild(i))) {
                filteredReferenceChildren.add(expected.jjtGetChild(i));
            }
        }

        // recurse into child nodes
        Assert.assertEquals(filteredReferenceChildren.size(), actual.jjtGetNumChildren());
        for (int i = 0; i < filteredReferenceChildren.size(); ++i) {
            assertEqualsAfterRemoval(filteredReferenceChildren.get(i), nodesToRemove, actual.jjtGetChild(i));
        }
    }

    protected void assertResultedSourceEquals(Charset charset, URL expected) throws IOException {
        byte[] data = new byte[65536];
        int length = expected.openStream().read(data);
        String expectedContents = new String(data, 0, length, charset);
        String actualContents = new String(Files.readAllBytes(tempFile), charset);
        Assert.assertEquals(expectedContents, actualContents);
    }

    private Node load(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            return parser.parse(file.toString(), reader);
        }
    }

    protected Node initializeFor(URL resourceUrl) throws IOException {
        Files.copy(resourceUrl.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        cutter = new ASTCutter(parser, charset, tempFile);
        originalRoot = cutter.commitChange();
        return originalRoot;
    }

    protected void testExactRemoval(List<Node> nodesToRemove) throws IOException {
        cutter.writeTrimmedSource(nodesToRemove);
        Node trimmedRoot = load(tempFile);
        assertEqualsAfterRemoval(originalRoot, new HashSet<Node>(nodesToRemove), trimmedRoot);
    }
}
