package net.sourceforge.pmd.lang.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;

public class NodeFindingUtilTest {

    @Test
    public void testFindNode() {
        DummyNode.DummyRootNode root = parseLispish("(a(b)x(c))");

        assertFinds("a", 1, root);
        assertFinds("b", 2, root);
        assertFinds("b", 3, root);
        assertFinds("b", 4, root);
        assertFinds("a", 5, root);
        assertFinds("c", 6, root);
        assertFinds("c", 7, root);
        assertFinds("c", 8, root);
        assertFinds("a", 9, root);
        assertDoesNotFind(10, root);
    }

    private static void assertDoesNotFind(int offset, DummyNode.DummyRootNode root) {
        assertFalse(NodeFindingUtil.findNodeAt(root, offset).isPresent());
    }

    static void assertFinds(String nodeImage, int offset, Node root) {
        Optional<Node> found = NodeFindingUtil.findNodeAt(root, offset);

        assertTrue(found.isPresent(), "Node not found: " + nodeImage + " at offset " + offset);
        assertThat(found.get().getImage(), equalTo(nodeImage));
    }

    static DummyNode.DummyRootNode parseLispish(String source) {

        DummyLanguageModule lang = DummyLanguageModule.getInstance();
        try (LanguageProcessor processor = lang.createProcessor(lang.newPropertyBundle())) {
            Parser.ParserTask task = new Parser.ParserTask(
                TextDocument.readOnlyString(source, lang.getDefaultVersion()),
                SemanticErrorReporter.noop(),
                LanguageProcessorRegistry.singleton(processor)
            );
            RootNode root = processor.services().getParser().parse(task);

            return (DummyNode.DummyRootNode) root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
