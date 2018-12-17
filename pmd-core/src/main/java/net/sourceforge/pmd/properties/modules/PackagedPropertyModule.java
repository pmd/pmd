/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.modules;

import static net.sourceforge.pmd.properties.PackagedPropertyDescriptor.PACKAGE_NAME_DELIMITER;
import static net.sourceforge.pmd.properties.PropertyDescriptorField.LEGAL_PACKAGES;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.properties.PropertyDescriptorField;


/**
 * Factorises common functionality for packaged properties.
 *
 * @author Clément Fournier
 */
@Deprecated
public abstract class PackagedPropertyModule<T> {

    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("(\\w+)(\\.\\w+)*");

    private final String[] legalPackageNames;


    public PackagedPropertyModule(String[] legalPackageNames, List<T> defaults) {

        checkValidPackages(legalPackageNames);
        checkValidDefaults(defaults, legalPackageNames);

        this.legalPackageNames = legalPackageNames;
    }


    /**
     * Checks that the legal packages are okay.
     *
     * @param legalNamePrefixes Prefixes to check. Can be null, but not contain null
     *
     * @throws IllegalArgumentException If the prefixes contain null
     * @throws IllegalArgumentException If one name that does not look like a package name
     */
    private void checkValidPackages(String[] legalNamePrefixes) throws IllegalArgumentException {
        if (legalNamePrefixes == null) {
            return;
        }

        for (String name : legalNamePrefixes) {
            if (name == null) {
                throw new IllegalArgumentException("Null is not allowed in the legal package names:"
                                                   + Arrays.toString(legalNamePrefixes));
            } else if (!PACKAGE_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("One name is not a package: '" + name + "'");

            }
        }
    }


    /**
     * Evaluates the names of the items against the allowable name prefixes. If one or more do not have valid prefixes
     * then an exception will be thrown.
     *
     * @param items             Items to check
     * @param legalNamePrefixes Legal name prefixes
     *
     * @throws IllegalArgumentException if some items are not allowed
     */
    private void checkValidDefaults(List<T> items, String[] legalNamePrefixes) {

        if (legalNamePrefixes == null) { // valid value, matches everything
            return;
        }

        Set<String> nameSet = new HashSet<>();

        for (T item : items) {
            if (item == null) {
                continue;
            }
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
     * @param item Item (not null)
     *
     * @return Package name of the item
     */
    protected abstract String packageNameOf(T item);


    public String valueErrorFor(T value) {

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


    public String[] legalPackageNames() {
        return Arrays.copyOf(legalPackageNames, legalPackageNames.length);
    }


    public void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
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


    public String[] packageNamesIn(Map<PropertyDescriptorField, String> params) {
        return StringUtils.split(params.get(LEGAL_PACKAGES), PACKAGE_NAME_DELIMITER);
    }

}
