/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorField.LEGAL_PACKAGES;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Property which restricts the type of its values to some packages. If
 * the legalPackageNames value is set to null then no restrictions are made.
 *
 * @param <T> The type of the values
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
/* default */ abstract class AbstractPackagedProperty<T> extends AbstractSingleValueProperty<T> {

    /** Required keys in the map. */
    protected static final Map<PropertyDescriptorField, Boolean> PACKAGED_FIELD_TYPES_BY_KEY
        = BasicPropertyDescriptorFactory.expectedFieldTypesWith(new PropertyDescriptorField[] {LEGAL_PACKAGES},
                                                                new Boolean[] {false});
    private static final char PACKAGE_NAME_DELIMITER = ' ';
    private static Pattern packageNamePattern = Pattern.compile("(\\w+)(\\.\\w+)*");

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
        if (item == null) {
            return;
        }

        String name = packageNameOf(item);

        for (String legalNamePrefixe : legalNamePrefixes) {
            if (name.startsWith(legalNamePrefixe)) {
                return;
            }
        }

        throw new IllegalArgumentException("Invalid item: " + item);
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


    /**
     * Returns the legal package names.
     *
     * @return The legal package names
     */
    public String[] legalPackageNames() {
        return Arrays.copyOf(legalPackageNames, legalPackageNames.length); // defensive copy
    }


    protected static String[] packageNamesIn(Map<PropertyDescriptorField, String> params) {
        String[] packageNames = StringUtil.substringsOf(params.get(LEGAL_PACKAGES),
                                                        PACKAGE_NAME_DELIMITER);

        for (String name : packageNames) {
            if (!packageNamePattern.matcher(name).matches()) {
                throw new IllegalArgumentException("One name is not a package: '" + name + "'");
            }
        }

        return packageNames;
    }

}
