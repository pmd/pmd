/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorFields.LEGAL_PACKAGES;

import java.util.Map;

import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Concrete subclasses manage items that reside within namespaces per the design
 * of the Java language. Rule developers can limit the range of permissible
 * items by specifying portions of their package names in the constructor. If
 * the legalPackageNames value is set to null then no restrictions are made.
 *
 * @param <T>
 *
 * @author Brian Remedios
 */
public abstract class AbstractPackagedProperty<T> extends AbstractSingleValueProperty<T> {

    protected static final Map<String, Boolean> PACKAGED_FIELD_TYPES_BY_KEY = BasicPropertyDescriptorFactory
        .expectedFieldTypesWith(new String[] {LEGAL_PACKAGES}, new Boolean[] {Boolean.FALSE});
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
    protected AbstractPackagedProperty(String theName, String theDescription, T theDefault,
                                       String[] theLegalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);

        checkValidPackages(theDefault, theLegalPackageNames);

        legalPackageNames = theLegalPackageNames;
    }

    protected static String[] packageNamesIn(Map<String, String> params) {
        // TODO
        return null;
    }

    @Override
    protected void addAttributesTo(Map<String, String> attributes) {
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

    /**
     * Evaluates the names of the items against the allowable name prefixes. If
     * one or more do not have valid prefixes then an exception will be thrown.
     *
     * @param item              The item to check
     * @param legalNamePrefixes The legal name prefixes
     *
     * @throws IllegalArgumentException If the item's package is not whitelisted.
     */
    private void checkValidPackages(T item, String[] legalNamePrefixes) {
        String name = packageNameOf(item);

        for (String legalNamePrefixe : legalNamePrefixes) {
            if (name.startsWith(legalNamePrefixe)) {
                return;
            }
        }

        throw new IllegalArgumentException("Invalid item: " + item);
    }

    /**
     * Returns the name of the type of item.
     *
     * @return The name of the type of item
     */
    protected abstract String itemTypeName();


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
     * Returns the package name of the item.
     *
     * @param item Item
     *
     * @return Package name of the item
     */
    protected abstract String packageNameOf(T item);

    /**
     * Returns the legal package names.
     *
     * @return The legal package names
     */
    public String[] legalPackageNames() {
        return legalPackageNames;
    }

}
