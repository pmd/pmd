/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorFields.LEGAL_PACKAGES;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * @param <T>
 *
 * @author Brian Remedios
 */
public abstract class AbstractMultiPackagedProperty<T> extends AbstractMultiValueProperty<T> {

    protected static final char DELIMITER = '|';
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
    protected AbstractMultiPackagedProperty(String theName, String theDescription, List<T> theDefault,
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
