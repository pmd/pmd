/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.xml.XmlMapper.StableXmlMapper;

/**
 * Serialize to and from a simple string. Examples:
 *
 * <pre>{@code
 *  <seq>1</seq>
 * }</pre>
 */
final class SeqSyntax<T, C extends Collection<T>> extends StableXmlMapper<C> {

    private final XmlMapper<T> itemSyntax;
    private final Supplier<C> emptyCollSupplier;

    SeqSyntax(XmlMapper<T> itemSyntax, Supplier<C> emptyCollSupplier) {
        super("seq");
        this.itemSyntax = itemSyntax;
        this.emptyCollSupplier = emptyCollSupplier;
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
        C result = emptyCollSupplier.get();

        XmlUtils.getElementChildren(element).forEach(child -> {
            T item = XmlUtils.expectElement(err, child, itemSyntax);
            if (item != null) {
                result.add(item);
            }
        });
        return result;
    }

    @Override
    public List<String> examples() {
        return Collections.singletonList("<seq>\n"
            + "   " + String.join("\n    ", itemSyntax.examples()) + "\n"
            + "   ..."
            + "</seq>");
    }
}
