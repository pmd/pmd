/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.util.fxdesigner.util.beans.converters.LanguageVersionConverter;
import net.sourceforge.pmd.util.fxdesigner.util.beans.converters.PropertyTypeIdConverter;
import net.sourceforge.pmd.util.fxdesigner.util.beans.converters.RulePriorityConverter;


/**
 * Utility methods to persist settings of the application.
 *
 * @author Clément Fournier
 * @see SimpleBeanModelNode
 * @see SettingsOwner
 * @since 6.1.0
 */
public final class SettingsPersistenceUtil {

    static {
        // register converters for custom types
        ConvertUtils.register(new RulePriorityConverter(), RulePriority.class);
        ConvertUtils.register(new PropertyTypeIdConverter(), PropertyTypeId.class);
        ConvertUtils.register(new LanguageVersionConverter(), LanguageVersion.class);
    }


    private SettingsPersistenceUtil() {
    }


    /**
     * Restores properties contained in the file into the given object.
     *
     * @param root Root of the hierarchy
     * @param file Properties file
     */
    public static void restoreProperties(SettingsOwner root, File file) {
        Optional<Document> odoc = getDocument(file);

        odoc.flatMap(XmlFormatRevision::getSuitableReader)
            .map(rev -> rev.xmlInterface)
            .flatMap(xmlInterface -> odoc.flatMap(xmlInterface::parseXml))
            .ifPresent(n -> restoreSettings(root, n));
    }


    /**
     * Save properties of this object and descendants into the given file.
     *
     * @param root Root of the hierarchy
     * @param file Properties file
     */
    public static void persistProperties(SettingsOwner root, File file) throws IOException {
        SimpleBeanModelNode node = SettingsPersistenceUtil.buildSettingsModel(root);
        XmlFormatRevision.getLatest().xmlInterface.writeModelToXml(file, node);
    }


    /**
     * Returns an XML document for the given file if it exists and can be parsed.
     *
     * @param file File to parse
     */
    private static Optional<Document> getDocument(File file) {
        if (file.exists()) {
            try (InputStream stream = Files.newInputStream(file.toPath())) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.parse(stream);
                return Optional.of(document);
            } catch (SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }


    /**
     * Builds a settings model recursively for the given settings owner.
     * The properties which have a getter tagged with {@link PersistentProperty}
     * are retrieved for later serialisation.
     *
     * @param root The root of the settings owner hierarchy.
     *
     * @return The built model
     */
    // test only
    static SimpleBeanModelNode buildSettingsModel(SettingsOwner root) {
        SimpleBeanModelNode node = new SimpleBeanModelNode(root.getClass());

        for (PropertyDescriptor d : PropertyUtils.getPropertyDescriptors(root)) {
            if (d.getReadMethod() == null) {
                continue;
            }

            try {
                if (d.getReadMethod().isAnnotationPresent(PersistentSequence.class)) {

                    Object val = d.getReadMethod().invoke(root);
                    if (!Collection.class.isAssignableFrom(val.getClass())) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    Collection<SettingsOwner> values = (Collection<SettingsOwner>) val;

                    BeanModelNodeSeq<SimpleBeanModelNode> seq = new BeanModelNodeSeq<>(d.getName());

                    for (SettingsOwner item : values) {
                        seq.addChild(buildSettingsModel(item));
                    }

                    node.addChild(seq);
                } else if (d.getReadMethod().isAnnotationPresent(PersistentProperty.class)) {
                    node.addProperty(d.getName(), d.getReadMethod().invoke(root), d.getPropertyType());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        for (SettingsOwner child : root.getChildrenSettingsNodes()) {
            node.addChild(buildSettingsModel(child));
        }

        return node;
    }


    /**
     * Restores the settings from the model into the target. Dual of
     * {@link #buildSettingsModel(SettingsOwner)}. Traverses all the
     * tree.
     *
     * @param target Object in which to restore the properties
     * @param model  The model
     */
    // test only
    static void restoreSettings(SettingsOwner target, BeanModelNode model) {
        if (model == null) {
            return; // possibly it wasn't saved during the previous save cycle
        }

        if (target == null) {
            throw new IllegalArgumentException();
        }

        model.accept(new RestorePropertyVisitor(), target);
    }


    /** Enumerates different formats for compatibility. */
    private enum XmlFormatRevision implements Comparable<XmlFormatRevision> {
        V1(new XmlInterfaceVersion1(1));

        private final XmlInterface xmlInterface;


        XmlFormatRevision(XmlInterface xmlI) {
            this.xmlInterface = xmlI;
        }


        public static XmlFormatRevision getLatest() {
            return Arrays.stream(values()).max(Comparator.comparingInt(x -> x.xmlInterface.getRevisionNumber())).get();
        }


        /**
         * Gets a handler capable of reading the given document.
         *
         * @param doc The revision number
         *
         * @return A handler, if it can be found
         */
        public static Optional<XmlFormatRevision> getSuitableReader(Document doc) {
            return Arrays.stream(values())
                         .filter(rev -> rev.xmlInterface.canParse(doc))
                         .findAny();
        }
    }


    /**
     * Tags the *getter* of a property as suitable for persistence.
     * The property will be serialized and restored on startup, so
     * it must have a setter.
     *
     * <p>Properties setters and getters must respect JavaBeans
     * conventions.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PersistentProperty {
    }


    /**
     * Tags the getter of a collection for persistence. The collection
     * elements must implement {@link SettingsOwner} and have a noargs
     * constructor. This is a solution to the problem of serializing
     * collections of items of arbitrary complexity.
     *
     * <p>When restoring such a property, we assume that the property
     * already has a value, and we either update existing items with
     * the properties or we instantiate new items if not enough are
     * already available.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PersistentSequence {
    }

}
