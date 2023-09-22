/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import java.util.Objects;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.xml.ast.internal.XmlParserImpl.RootXmlNode;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * XPath rule that executes an expression on the DOM directly, and not
 * on the PMD AST wrapper. The XPath expressions adheres to the XPath
 * (2.0) spec, so they can be tested in any existing XPath testing tools
 * instead of just the PMD designer (google "xpath test"). Usage of this
 * class is strongly recommended over the standard {@link XPathRule}, which
 * is mostly useful in other PMD languages.
 *
 * <h3>Differences with {@link XPathRule}</h3>
 *
 * This rule and {@link XPathRule} do not accept exactly the same queries,
 * because {@link XPathRule} implements the XPath spec in an ad-hoc way.
 * The main differences are:
 * <ul>
 * <li>{@link XPathRule} uses <i>elements</i> to represent text nodes.
 * This is contrary to the XPath spec, in which element and text nodes
 * are different kinds of nodes. To replace the query {@code //elt/text[@Image="abc"]},
 * use the XPath function {@code text()}, eg {@code //elt[text()="abc"]}.
 * <li>{@link XPathRule} adds additional attributes to each element
 * (eg {@code @BeginLine} and {@code @Image}). These attributes are not
 * XML attributes, so they are not accessible using DomXPathRule rule.
 * Instead, use the XPath functions {@code pmd:startLine(node)}, {@code pmd:endLine(node)} and related.
 * For instance, replace {@code //elt[@EndLine - @BeginLine > 10]} with
 * {@code elt[pmd:endLine(.) - pmd:startLine(.) > 10]}.
 * <li>{@link XPathRule} uses an element called {@code "document"} as the
 * root node of every XML AST. This node does not have the correct node kind,
 * as it's an element, not a document. To replace {@code /document/RootNode},
 * use just {@code /RootNode}.
 * <li>{@link XPathRule} ignores comments and processing instructions
 * (eg FXML's {@code <?import javafx.Node ?>}).
 * This rule makes them accessible with the regular XPath syntax.
 * The following finds all comments in the file:
 * <pre>{@code
 *  //comment()
 * }</pre>
 * The following finds only top-level comments starting with "prefix":
 * <pre>{@code
 *  /comment()[fn:starts-with(fn:string(.), "prefix")]
 * }</pre>
 * Note the use of {@code fn:string}.
 *
 * As an example of matching processing instructions, the following
 * fetches all {@code <?import ... ?>} processing instructions.
 * <pre>{@code
 *  /processing-instruction('import')
 * }</pre>
 * The string value of the instruction can be found with {@code fn:string}.
 * </li>
 * </ul>
 *
 * <p>Additionally, this rule only supports XPath 2.0, with no option
 * for configuration. This will be bumped to XPath 3.1 in PMD 7.
 *
 * <h4>Namespace-sensitivity</h4>
 *
 * <p>Another important difference is that this rule is namespace-sensitive.
 * If the tested XML documents use a schema ({@code xmlns} attribute on the root),
 * you should set the property {@code defaultNsUri} on the rule with
 * the value of the {@code xmlns} attribute. Otherwise node tests won't
 * match unless you use a wildcard URI prefix ({@code *:nodeName}).
 *
 * <p>For instance for the document
 * <pre>{@code
 * <foo xmlns="http://company.com/aschema">
 * </foo>
 * }</pre>
 * the XPath query {@code //foo} will not match anything, while {@code //*:foo}
 * will. If you set the property {@code defaultNsUri} to {@code "http://company.com/aschema"},
 * then {@code //foo} will be expanded to {@code //Q{http://company.com/aschema}foo},
 * and match the {@code foo} node. The behaviour is equivalent in the following
 * document:
 * <pre>{@code
 * <my:foo xmlns:my='http://company.com/aschema'>
 * </my:foo>
 * }</pre>
 *
 * <p>However, for the document
 * <pre>{@code
 * <foo>
 * </foo>
 * }</pre>
 * the XPath queries {@code //foo} and {@code //*:foo} both match, because
 * {@code //foo} is expanded to {@code //Q{}foo} (local name foo, empty URI),
 * and the document has no default namespace (= the empty default namespace).
 *
 * <p>Note that explicitly specifying URIs with {@code Q{...}localName}
 * as in this documentation is XPath 3.1 syntax and will only be available
 * in PMD 7.
 *
 * @since PMD 6.44.0
 * @author Clément Fournier
 */
public class DomXPathRule extends AbstractRule {

    SaxonDomXPathQuery query;

    private static final PropertyDescriptor<String> XPATH_EXPR
        = PropertyFactory.stringProperty("xpath")
                         .desc("An XPath 2.0 expression that will be evaluated against the root DOM")
                         .defaultValue("") // no default value
                         .build();

    private static final PropertyDescriptor<String> DEFAULT_NS_URI
        = PropertyFactory.stringProperty("defaultNsUri")
                         .desc("A URI for the default namespace of node tests in the XPath expression."
                               + "This is provided to match documents based on their declared schema.")
                         .defaultValue("")
                         .build();


    public DomXPathRule() {
        definePropertyDescriptor(XPATH_EXPR);
        definePropertyDescriptor(DEFAULT_NS_URI);
        // for compatibility, but is ignored.
        definePropertyDescriptor(XPathRule.VERSION_DESCRIPTOR);
    }


    public DomXPathRule(String xpath) {
        this(xpath, "");
    }

    public DomXPathRule(String xpath, String defaultNsUri) {
        this();
        setProperty(XPATH_EXPR, xpath);
        setProperty(DEFAULT_NS_URI, defaultNsUri);
    }

    @Override
    public void apply(Node node, RuleContext ctx) {
        RootXmlNode root = (RootXmlNode) node;
        SaxonDomXPathQuery query = getXPathQuery();
        for (Node foundNode : query.evaluate(root, this)) {
            ctx.addViolation(foundNode);
        }
    }

    @Override
    public void initialize(LanguageProcessor languageProcessor) {
        query = new SaxonDomXPathQuery(getProperty(XPATH_EXPR),
                                       getProperty(DEFAULT_NS_URI),
                                       getPropertyDescriptors(),
                                       languageProcessor.services().getXPathHandler());

    }

    private SaxonDomXPathQuery getXPathQuery() {
        return Objects.requireNonNull(query, "rule not initialized");
    }

}
