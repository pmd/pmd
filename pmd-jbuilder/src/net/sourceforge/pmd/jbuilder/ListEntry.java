package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.properties.GlobalProperty;

/**
 * Wraps the entries that are places in the ListBox objects so that they can
 * track the GlobalProperty that's associated with each entry.
 */
public class ListEntry {
    GlobalProperty prop;
    String displayName;

    /**
     * Constructor
     *
     * @param name name as it is to appear in the list box
     * @param prop the GlobalProperty associated with this name
     */
    public ListEntry(String name, GlobalProperty prop) {
        this.displayName = name;
        this.prop = prop;
    }

    /**
     * get the GlobalProperty
     *
     * @return GlobalProperty object
     */
    public GlobalProperty getProp() {
        return prop;
    }

    /**
     * Get the display name
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Use the display name as the string representation
     *
     * @return display name
     */
    public String toString() {
        return displayName;
    }
}

