/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.util.Collections;
import java.util.List;


/**
 * Marker interface for settings owners. Settings owners form a
 * tree-like hierarchy, which is explored recursively to build
 * a model of the settings to persist, under the form of a
 * {@link SimpleBeanModelNode}.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public interface SettingsOwner {


    /** Gets the children of this node in order. */
    default List<SettingsOwner> getChildrenSettingsNodes() {
        return Collections.emptyList();
    }

}
