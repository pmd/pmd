/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.xml.XmlMapper.StableXmlMapper;
import net.sourceforge.pmd.properties.xml.internal.XmlUtils;

/**
 * Serialize to and from a simple string. Examples:
 *
 * <pre>{@code
 *  <seq>1</seq>
 * }</pre>
 */
final class SeqSyntax<T, C extends Iterable<T>> extends StableXmlMapper<C> {

    private final XmlMapper<T> itemSyntax;
    private final Collector<? super T, ?, ? extends C> collector;

    SeqSyntax(XmlMapper<T> itemSyntax, Collector<? super T, ?, ? extends C> collector) {
        super("seq");
        this.itemSyntax = itemSyntax;
        this.collector = collector;
    }

    @Override
    public void toXml(Element container, C value) {
        for (T v : value) {
            Element item = container.getOwnerDocument().createElement(itemSyntax.getWriteElementName(v));
            itemSyntax.toXml(item, v);
            container.appendChild(item);
        }
    }

    @Override
    public C fromXml(Element element, XmlErrorReporter err) {
        return XmlUtils.getElementChildren(element)
                       .map(child -> XmlUtils.expectElement(err, child, itemSyntax))
                       .filter(Objects::nonNull)
                       .collect(collector);
    }

    @Override
    protected List<String> examples(String curIndent, String baseIndent) {
        String newIndent = curIndent + baseIndent;
        return Collections.singletonList(
            curIndent + "<seq>\n"
                + newIndent + String.join("\n", itemSyntax.examples(newIndent, baseIndent)) + "\n"
                + newIndent + "..."
                + curIndent + "</seq>"
        );
    }
}
