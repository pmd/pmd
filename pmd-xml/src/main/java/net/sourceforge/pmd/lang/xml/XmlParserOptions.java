/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import java.io.ByteArrayInputStream;
import java.util.Objects;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @deprecated Parser options will be removed with 7.0, these options
 *             will assume their default values then.
 */
@Deprecated
public class XmlParserOptions extends ParserOptions {

    // Note: The UI order values are chosen to be larger than those built into
    // XPathRule.
    public static final PropertyDescriptor<Boolean> COALESCING_DESCRIPTOR =
        PropertyFactory.booleanProperty("coalescing")
                       .desc("deprecated!Specifies that the XML parser convert CDATA nodes to Text nodes and append it to the adjacent (if any) text node.")
                       .defaultValue(false)
                       .build();
    public static final PropertyDescriptor<Boolean> EXPAND_ENTITY_REFERENCES_DESCRIPTOR =
        PropertyFactory.booleanProperty("expandEntityReferences")
                       .desc("deprecated!Specifies that the XML parser expand entity reference nodes.")
                       .defaultValue(true)
                       .build();
    public static final PropertyDescriptor<Boolean> IGNORING_COMMENTS_DESCRIPTOR =
        PropertyFactory.booleanProperty("ignoringComments")
                       .desc("deprecated!Specifies that the XML parser ignore comments.")
                       .defaultValue(false)
                       .build();
    public static final PropertyDescriptor<Boolean> IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR =
        PropertyFactory.booleanProperty("ignoringElementContentWhitespace")
                       .desc("deprecated!Specifies that the XML parser eliminate whitespace in element content.  Setting this to 'true' will force validating.")
                       .defaultValue(false)
                       .build();
    public static final PropertyDescriptor<Boolean> NAMESPACE_AWARE_DESCRIPTOR =
        PropertyFactory.booleanProperty("namespaceAware")
                       .desc("deprecated!Specifies that the XML parser will provide support for XML namespaces.")
                       .defaultValue(true)
                       .build();
    public static final PropertyDescriptor<Boolean> VALIDATING_DESCRIPTOR =
        PropertyFactory.booleanProperty("validating")
                       .desc("deprecated!Specifies that the XML parser will validate documents as they are parsed.  This only works for DTDs.")
                       .defaultValue(false)
                       .build();
    public static final PropertyDescriptor<Boolean> XINCLUDE_AWARE_DESCRIPTOR =
        PropertyFactory.booleanProperty("xincludeAware")
                       .desc("deprecated!Specifies that the XML parser will process XInclude markup.")
                       .defaultValue(false)
                       .build();
    public static final PropertyDescriptor<Boolean> LOOKUP_DESCRIPTOR_DTD =
        PropertyFactory.booleanProperty("xincludeAware")
                       .desc("deprecated!Specifies whether XML parser will attempt to lookup the DTD.")
                       .defaultValue(false)
                       .build();

    public static final EntityResolver SILENT_ENTITY_RESOLVER = (publicId, systemId) -> new InputSource(new ByteArrayInputStream("".getBytes()));

    private boolean coalescing;
    private boolean expandEntityReferences;
    private boolean ignoringComments;
    private boolean ignoringElementContentWhitespace;
    private boolean namespaceAware;
    private boolean validating;
    private boolean xincludeAware;
    private boolean lookupDescriptorDoc;

    public XmlParserOptions() {
        this.coalescing = COALESCING_DESCRIPTOR.defaultValue();
        this.expandEntityReferences = EXPAND_ENTITY_REFERENCES_DESCRIPTOR.defaultValue();
        this.ignoringComments = IGNORING_COMMENTS_DESCRIPTOR.defaultValue();
        this.ignoringElementContentWhitespace = IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR.defaultValue();
        this.namespaceAware = NAMESPACE_AWARE_DESCRIPTOR.defaultValue();
        this.validating = VALIDATING_DESCRIPTOR.defaultValue();
        this.xincludeAware = XINCLUDE_AWARE_DESCRIPTOR.defaultValue();
        this.lookupDescriptorDoc = LOOKUP_DESCRIPTOR_DTD.defaultValue();
    }

    public XmlParserOptions(Rule rule) {
        this.coalescing = rule.getProperty(COALESCING_DESCRIPTOR);
        this.expandEntityReferences = rule.getProperty(EXPAND_ENTITY_REFERENCES_DESCRIPTOR);
        this.ignoringComments = rule.getProperty(IGNORING_COMMENTS_DESCRIPTOR);
        this.ignoringElementContentWhitespace = rule.getProperty(IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR);
        this.namespaceAware = rule.getProperty(NAMESPACE_AWARE_DESCRIPTOR);
        this.validating = rule.getProperty(VALIDATING_DESCRIPTOR);
        this.xincludeAware = rule.getProperty(XINCLUDE_AWARE_DESCRIPTOR);
        this.lookupDescriptorDoc = rule.getProperty(LOOKUP_DESCRIPTOR_DTD);
    }

    /**
     *
     * @return the configured entity resolver. If {@link #lookupDescriptorDoc}
     *         is false it would normally force the XML parser to use its own
     *         resolver
     */
    public EntityResolver getEntityResolver() {
        if (!lookupDescriptorDoc) {
            return SILENT_ENTITY_RESOLVER;
        } else {
            return null;
        }
    }

    public boolean isLookupDescriptorDoc() {
        return lookupDescriptorDoc;
    }

    public void setLookupDescriptorDoc(boolean lookupDescriptorDoc) {
        this.lookupDescriptorDoc = lookupDescriptorDoc;
    }

    public boolean isCoalescing() {
        return this.coalescing;
    }

    public void setCoalescing(boolean coalescing) {
        this.coalescing = coalescing;
    }

    public boolean isExpandEntityReferences() {
        return this.expandEntityReferences;
    }

    public void setExpandEntityReferences(boolean expandEntityReferences) {
        this.expandEntityReferences = expandEntityReferences;
    }

    public boolean isIgnoringComments() {
        return this.ignoringComments;
    }

    public void setIgnoringComments(boolean ignoringComments) {
        this.ignoringComments = ignoringComments;
    }

    public boolean isIgnoringElementContentWhitespace() {
        return this.ignoringElementContentWhitespace;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringElementContentWhitespace) {
        this.ignoringElementContentWhitespace = ignoringElementContentWhitespace;
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    public boolean isValidating() {
        return this.validating;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public boolean isXincludeAware() {
        return this.xincludeAware;
    }

    public void setXincludeAware(boolean xincludeAware) {
        this.xincludeAware = xincludeAware;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (coalescing ? 1231 : 1237);
        result = prime * result + (expandEntityReferences ? 1231 : 1237);
        result = prime * result + (ignoringComments ? 1231 : 1237);
        result = prime * result + (ignoringElementContentWhitespace ? 1231 : 1237);
        result = prime * result + (namespaceAware ? 1231 : 1237);
        result = prime * result + (validating ? 1231 : 1237);
        result = prime * result + (xincludeAware ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final XmlParserOptions that = (XmlParserOptions) obj;
        return Objects.equals(this.getSuppressMarker(), that.getSuppressMarker())
                && this.coalescing == that.coalescing && this.expandEntityReferences == that.expandEntityReferences
                && this.ignoringComments == that.ignoringComments
                && this.ignoringElementContentWhitespace == that.ignoringElementContentWhitespace
                && this.namespaceAware == that.namespaceAware && this.validating == that.validating
                && this.xincludeAware == that.xincludeAware;
    }
}
