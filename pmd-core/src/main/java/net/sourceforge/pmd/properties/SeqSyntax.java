/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.XmlMapper.StableXmlMapper;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.internal.xml.PmdXmlReporter;
import net.sourceforge.pmd.util.internal.xml.XmlUtil;

import com.github.oowekyala.ooxml.DomUtils;

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
    public C fromXml(Element element, PmdXmlReporter err) {
        return collectFromXml(element, err, collector);
    }

    // capture the A type var.
    private <A> C collectFromXml(Element element, PmdXmlReporter err, Collector<? super T, A, ? extends C> collector) {
        RuntimeException error = null;
        A acc = collector.supplier().get();
        for (Element child : DomUtils.children(element)) {
            try {
                T elt = XmlUtil.expectElement(err, child, itemSyntax);
                collector.accumulator().accept(acc, elt);
            } catch (RuntimeException e) {
                if (error == null) {
                    error = err.at(child).error(e);
                } else {
                    error.addSuppressed(e);
                }
            }
        }

        if (error != null) {
            throw error;
        }
        return CollectionUtil.finish(collector, acc);
    }

    @Override
    public List<PropertyConstraint<? super C>> getConstraints() {
        return itemSyntax.getConstraints().stream()
                         .map(PropertyConstraint::toCollectionConstraint)
                         .collect(Collectors.toList());
    }

    @Override
    protected List<String> examplesImpl(String curIndent, String baseIndent) {
        String newIndent = curIndent + baseIndent;
        return Collections.singletonList(
            curIndent + "<seq>\n"
                + newIndent + String.join("\n", itemSyntax.examplesImpl(newIndent, baseIndent)) + "\n"
                + newIndent + "..."
                + curIndent + "</seq>"
        );
    }
}
