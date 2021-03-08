/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static java.util.Arrays.asList;

import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


public class AvoidUsingHardCodedIPRule extends AbstractJavaRulechainRule {

    private enum AddressKinds {
        IPV4("IPv4"),
        IPV6("IPv6"),
        IPV4_MAPPED_IPV6("IPv4 mapped IPv6");

        private final String label;

        AddressKinds(String label) {
            this.label = label;
        }
    }


    private static final PropertyDescriptor<List<AddressKinds>> CHECK_ADDRESS_TYPES_DESCRIPTOR =
        PropertyFactory.enumListProperty("checkAddressTypes", AddressKinds.class, k -> k.label)
                       .desc("Check for IP address types.")
                       .defaultValue(asList(AddressKinds.values()))
                       .build();

    // Provides 4 capture groups that can be used for additional validation
    private static final String IPV4_REGEXP = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";

    // Uses IPv4 pattern, but changes the groups to be non-capture
    private static final String IPV6_REGEXP = "(?:(?:[0-9a-fA-F]{1,4})?\\:)+(?:[0-9a-fA-F]{1,4}|"
        + IPV4_REGEXP.replace("(", "(?:") + ")?";

    private static final Pattern IPV4_PATTERN = Pattern.compile("^" + IPV4_REGEXP + "$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^" + IPV6_REGEXP + "$");

    private final EnumSet<AddressKinds> kindsToCheck = EnumSet.noneOf(AddressKinds.class);

    public AvoidUsingHardCodedIPRule() {
        super(ASTStringLiteral.class);
        definePropertyDescriptor(CHECK_ADDRESS_TYPES_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        kindsToCheck.clear();
        kindsToCheck.addAll(getProperty(CHECK_ADDRESS_TYPES_DESCRIPTOR));
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        final String image = node.getConstValue();

        // Note: We used to check the addresses using
        // InetAddress.getByName(String), but that's extremely slow,
        // so we created more robust checking methods.
        if (image.length() > 0) {
            final char firstChar = Character.toUpperCase(image.charAt(0));

            boolean checkIPv4 = kindsToCheck.contains(AddressKinds.IPV4);
            boolean checkIPv6 = kindsToCheck.contains(AddressKinds.IPV6);
            boolean checkIPv4MappedIPv6 = kindsToCheck.contains(AddressKinds.IPV4_MAPPED_IPV6);

            if (checkIPv4 && isIPv4(firstChar, image) || isIPv6(firstChar, image, checkIPv6, checkIPv4MappedIPv6)) {
                addViolation(data, node);
            }
        }
        return data;
    }

    private boolean isLatinDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private boolean isHexCharacter(char c) {
        return isLatinDigit(c) || 'A' <= c && c <= 'F' || 'a' <= c && c <= 'f';
    }

    private boolean isIPv4(final char firstChar, final String s) {
        // Quick check before using Regular Expression
        // 1) At least 7 characters
        // 2) 1st character must be a digit from '0' - '9'
        // 3) Must contain at least 1 . (period)
        if (s.length() < 7 || !isLatinDigit(firstChar) || s.indexOf('.') < 0) {
            return false;
        }

        Matcher matcher = IPV4_PATTERN.matcher(s);
        if (matcher.matches()) {
            // All octets in range [0, 255]
            for (int i = 1; i <= matcher.groupCount(); i++) {
                int octet = Integer.parseInt(matcher.group(i));
                if (octet < 0 || octet > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isIPv6(final char firstChar, String s, final boolean checkIPv6,
            final boolean checkIPv4MappedIPv6) {
        // Quick check before using Regular Expression
        // 1) At least 3 characters
        // 2) 1st must be a Hex number or a : (colon)
        // 3) Must contain at least 2 colons (:)
        if (s.length() < 3 || !(isHexCharacter(firstChar) || firstChar == ':')
                || StringUtils.countMatches(s, ':') < 2) {
            return false;
        }

        Matcher matcher = IPV6_PATTERN.matcher(s);
        if (matcher.matches()) {
            // Account for leading or trailing :: before splitting on :
            boolean zeroSubstitution = false;
            if (s.startsWith("::")) {
                s = s.substring(2);
                zeroSubstitution = true;
            } else if (s.endsWith("::")) {
                s = s.substring(0, s.length() - 2);
                zeroSubstitution = true;
            }

            // String.split() doesn't produce an empty String in the trailing
            // case, but it does in the leading.
            if (s.endsWith(":")) {
                return false;
            }

            // All the intermediate parts must be hexadecimal, or
            int count = 0;
            boolean ipv4Mapped = false;
            String[] parts = s.split(":");
            for (int i = 0; i < parts.length; i++) {
                final String part = parts[i];
                // An empty part indicates :: was encountered. There can only be
                // 1 such instance.
                if (part.length() == 0) {
                    if (zeroSubstitution) {
                        return false;
                    } else {
                        zeroSubstitution = true;
                    }
                    continue;
                } else {
                    count++;
                }
                // Should be a hexadecimal number in range [0, 65535]
                try {
                    int value = Integer.parseInt(part, 16);
                    if (value < 0 || value > 65535) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    // The last part can be a standard IPv4 address.
                    if (i != parts.length - 1 || !isIPv4(part.charAt(0), part)) {
                        return false;
                    }
                    ipv4Mapped = true;
                }
            }

            // IPv6 addresses are 128 bit, are we that long?
            if (zeroSubstitution) {
                if (ipv4Mapped) {
                    return checkIPv4MappedIPv6 && 1 <= count && count <= 6;
                } else {
                    return checkIPv6 && 1 <= count && count <= 7;
                }
            } else {
                if (ipv4Mapped) {
                    return checkIPv4MappedIPv6 && count == 7;
                } else {
                    return checkIPv6 && count == 8;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public String dysfunctionReason() {
        return !getProperty(CHECK_ADDRESS_TYPES_DESCRIPTOR).isEmpty() ? null : "No address types specified";
    }
}
