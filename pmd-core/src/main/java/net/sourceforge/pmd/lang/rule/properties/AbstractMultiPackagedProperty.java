/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorField.LEGAL_PACKAGES;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.AbstractPropertyDescriptorFactory;
import net.sourceforge.pmd.PackagedPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Multi-valued property restricting the type of its values to some packages.
 *
 * @param <T> The type of the values
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
/* default */ abstract class AbstractMultiPackagedProperty<T> extends AbstractMultiValueProperty<T>
    implements PackagedPropertyDescriptor<List<T>>{

    /** Delimiter between values. */
    protected static final char DELIMITER = '|';
    /** Required keys in the map. */
    protected static final Map<PropertyDescriptorField, Boolean> PACKAGED_FIELD_TYPES_BY_KEY
        = AbstractPropertyDescriptorFactory.expectedFieldTypesWith(new PropertyDescriptorField[] {LEGAL_PACKAGES},
                                                                   new Boolean[] {false});
    private static final char PACKAGE_NAME_DELIMITER = ' ';
    private String[] legalPackageNames;


    /**
     * Create a packaged property.
     *
     * @param theName              Name
     * @param theDescription       Description
     * @param theDefault           Default value
     * @param theLegalPackageNames Legal package names
     * @param theUIOrder           UI order
     *
     * @throws IllegalArgumentException
     */
    protected AbstractMultiPackagedProperty(String theName, String theDescription, List<T> theDefault,
                                            String[] theLegalPackageNames, float theUIOrder,
                                            boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);

        checkValidPackages(theDefault, theLegalPackageNames);

        legalPackageNames = theLegalPackageNames;
    }


    /**
     * Evaluates the names of the items against the allowable name prefixes. If
     * one or more do not have valid prefixes then an exception will be thrown.
     *
     * @param items             Items to check
     * @param legalNamePrefixes Legal name prefixes
     *
     * @throws IllegalArgumentException if some items are not allowed
     */
    private void checkValidPackages(List<T> items, String[] legalNamePrefixes) {

        Set<String> nameSet = new HashSet<>();

        for (T item : items) {
            nameSet.add(packageNameOf(item));
        }

        Set<String> notAllowed = new HashSet<>(nameSet);


        for (String name : nameSet) {
            for (String prefix : legalNamePrefixes) {
                if (name.startsWith(prefix)) {
                    notAllowed.remove(name);
                    break;
                }
            }
        }

        if (notAllowed.isEmpty()) {
            return;
        }

        throw new IllegalArgumentException("Invalid items: " + notAllowed);
    }


    /**
     * Returns the package name of the item.
     *
     * @param item Item
     *
     * @return Package name of the item
     */
    protected abstract String packageNameOf(T item);


    @Override
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        super.addAttributesTo(attributes);

        attributes.put(LEGAL_PACKAGES, delimitedPackageNames());
    }


    private String delimitedPackageNames() {

        if (legalPackageNames == null || legalPackageNames.length == 0) {
            return "";
        }
        if (legalPackageNames.length == 1) {
            return legalPackageNames[0];
        }

        StringBuilder sb = new StringBuilder();
        sb.append(legalPackageNames[0]);
        for (int i = 1; i < legalPackageNames.length; i++) {
            sb.append(PACKAGE_NAME_DELIMITER).append(legalPackageNames[i]);
        }
        return sb.toString();
    }


    @Override
    protected String valueErrorFor(T value) {

        if (value == null) {
            String err = super.valueErrorFor(null);
            if (err != null) {
                return err;
            }
        }

        if (legalPackageNames == null) {
            return null; // no restriction
        }

        String name = packageNameOf(value);

        for (int i = 0; i < legalPackageNames.length; i++) {
            if (name.startsWith(legalPackageNames[i])) {
                return null;
            }
        }

        return "Disallowed " + itemTypeName() + ": " + name;
    }


    /**
     * Returns the name of the type of item.
     *
     * @return The name of the type of item
     */
    protected abstract String itemTypeName();


    @Override
    public String[] legalPackageNames() {
        return Arrays.copyOf(legalPackageNames, legalPackageNames.length);
    }


    protected static String[] packageNamesIn(Map<PropertyDescriptorField, String> params) {
        // TODO
        return null;
    }
}
