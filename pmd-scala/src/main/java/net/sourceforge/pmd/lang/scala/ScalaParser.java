/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.scala.ast.ASTSourceNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaWrapperNode;

import scala.meta.Dialect;
import scala.meta.Source;
import scala.meta.Tree;
import scala.meta.inputs.Input;
import scala.meta.internal.parsers.ScalametaParser;

/**
 * Scala's Parser implementation. Defers parsing to the scala compiler via
 * Scalameta. This parser then wraps all of ScalaMeta's Nodes in Java versions
 * for compatibility.
 */
public class ScalaParser extends AbstractParser {
    private final Dialect dialect;

    private Map<Tree, ScalaWrapperNode> nodeCache = new HashMap<>();

    /**
     * Create a parser using the given Scala Dialect and set of parser options.
     * 
     * @param scalaDialect
     *            the Scala Dialect for this parser
     * @param parserOptions
     *            any additional options for this parser
     */
    public ScalaParser(Dialect scalaDialect, ParserOptions parserOptions) {
        super(parserOptions);
        this.dialect = scalaDialect;
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        nodeCache.clear();
        Input.VirtualFile virtualFile;
        try {
            String sourceString = IOUtils.toString(source);
            virtualFile = new Input.VirtualFile(fileName, sourceString);
        } catch (IOException e) {
            throw new ParseException(e);
        }
        Source src = new ScalametaParser(virtualFile, dialect).parseSource();
        ASTSourceNode srcNode = new ASTSourceNode(this, src);
        nodeCache.put(src, srcNode);
        return srcNode;
    }

    /**
     * Creates a wrapper around the given node so that we can interact with PMD
     * systems using the underlying scala node.
     * 
     * @param scalaNode
     *            a node from Scala's parsing
     * @return A Java-wrapped version of the given node, using a cache if this
     *         has been previously wrapped, or null, if the given node is null
     */
    public ScalaWrapperNode wrapNode(Tree scalaNode) {
        if (scalaNode == null) {
            return null;
        }
        ScalaWrapperNode node = nodeCache.get(scalaNode);
        if (node == null) {
            node = new ScalaWrapperNode(this, scalaNode);
            nodeCache.put(scalaNode, node);
        }
        return node;
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>(); // FIXME;
    }

    @Override
    protected TokenManager createTokenManager(Reader source) {
        return null;
    }

}
