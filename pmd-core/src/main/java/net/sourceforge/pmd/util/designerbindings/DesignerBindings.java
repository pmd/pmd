/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.designerbindings;

import java.util.Collection;
import java.util.Collections;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

/**
 * Gathers some services to customise how language implementations bind
 * to the designer.
 *
 * @author Clément Fournier
 * @since 6.20.0
 */
@Experimental
public interface DesignerBindings {

    /**
     * Returns an instance of {@link RelatedNodesSelector}, or
     * null if it should be defaulted to using the old symbol table ({@link ScopedNode}).
     * That default behaviour is implemented in the designer directly.
     */
    RelatedNodesSelector getRelatedNodesSelector();


    /**
     * Returns a collection of "additional information" entries pertaining to
     * the given node. An entry may look like {@code ("Type = List<String>", 0)},
     * or show the result of an XPath function. The information is shown
     * when the node is displayed.
     *
     * <p>Order of the collection is unimportant, it's sorted using
     * {@link AdditionalInfo#getSortKey()}.
     */
    Collection<AdditionalInfo> getAdditionalInfo(Node node);


    /**
     * An entry for the "additional info" panel.
     */
    class AdditionalInfo {

        private final String sortKey;
        private final String display;


        public AdditionalInfo(String sortKey, String display) {
            this.sortKey = sortKey;
            this.display = display;
        }

        public AdditionalInfo(String display) {
            this(display, display);
        }

        /**
         * Returns the string used to sort the additional info.
         * For example, returning {@code "A"} ensures this is displayed
         * first, provided there's no other entry with an {@code "A"}.
         */
        public String getSortKey() {
            return sortKey;
        }

        /**
         * Returns the string displayed to the user.
         */
        public String getDisplayString() {
            return display;
        }
    }


    /**
     * Returns the "main" attribute of the given node.
     * The string representation of this attribute ({@link Attribute#getStringValue()})
     * will be displayed next to the node type in the treeview. For
     * example, for a numeric literal, this could return the attribute
     * {@code (@IntValue, 1)}, for a class declaration, it could return the name
     * of the class (eg {@code (@SimpleName, String)}.
     *
     * <p>If there's no obvious "main" attribute, or if the node is not
     * supported, returns null. If the returned attribute is non-null,
     * but its string value is, the return value is ignored.
     *
     * <p>Note: the attribute doesn't need to originate from
     * {@link Node#getXPathAttributesIterator()}, it can be constructed
     * ad-hoc. The name of the attribute should be a valid name for the
     * XPath attribute though.
     *
     * <p>This method is meant to break the designer's dependency on {@link Node#getImage()}.
     */
    //    @Nullable
    Attribute getMainAttribute(Node node);


    /**
     * Returns true if the children of this node should be displayed in
     * the treeview by default. Returning "true" is the safe default value.
     */
    boolean isExpandedByDefaultInTree(Node node);


    /**
     * Returns a constant describing an icon that the node should bear
     * in the treeview and other relevant places. Returns null if no icon
     * is applicable.
     */
    //    @Nullable
    TreeIconId getIcon(Node node);


    /**
     * See {@link #getIcon(Node)}.
     */
    @Experimental
    enum TreeIconId {
        CLASS,
        METHOD,
        CONSTRUCTOR,
        FIELD,
        VARIABLE
    }


    /**
     * A base implementation for {@link DesignerBindings}.
     */
    class DefaultDesignerBindings implements DesignerBindings {

        private static final DefaultDesignerBindings INSTANCE = new DefaultDesignerBindings();

        @Override
        public RelatedNodesSelector getRelatedNodesSelector() {
            return null;
        }

        @Override
        public Collection<AdditionalInfo> getAdditionalInfo(Node node) {
            return Collections.emptyList();
        }

        @Override
        public Attribute getMainAttribute(Node node) {
            String image = node.getImage();
            if (image != null) {
                return new Attribute(node, "Image", image);
            }
            return null;
        }

        @Override
        public boolean isExpandedByDefaultInTree(Node node) {
            return true;
        }

        @Override
        public TreeIconId getIcon(Node node) {
            return null;
        }

        /** Returns the default instance. */
        public static DefaultDesignerBindings getInstance() {
            return INSTANCE;
        }
    }

}
