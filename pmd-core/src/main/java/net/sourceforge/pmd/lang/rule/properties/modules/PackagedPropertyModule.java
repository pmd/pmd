/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties.modules;

import static net.sourceforge.pmd.PackagedPropertyDescriptor.PACKAGE_NAME_DELIMITER;
import static net.sourceforge.pmd.PropertyDescriptorField.LEGAL_PACKAGES;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.StringUtil;

/**
 * @author Clément Fournier
 */
public abstract class PackagedPropertyModule<T> {

    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("(\\w+)(\\.\\w+)*");

    private final String[] legalPackageNames;


    public PackagedPropertyModule(String[] legalPackageNames, List<T> defaults) {

        checkValidPackages(defaults, legalPackageNames);

        this.legalPackageNames = legalPackageNames;
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

        if (legalNamePrefixes == null) {
            return;
        }

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
        String[] packageNames = StringUtil.substringsOf(params.get(LEGAL_PACKAGES), PACKAGE_NAME_DELIMITER);

        for (String name : packageNames) {
            if (!PACKAGE_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("One name is not a package: '" + name + "'");
            }
        }

        return packageNames;
    }

}
