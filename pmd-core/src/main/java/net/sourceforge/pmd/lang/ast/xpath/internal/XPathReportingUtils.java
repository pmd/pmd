/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.annotation.internal.DeprecationInfo;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

public class XPathReportingUtils {

    private static final Logger LOG = Logger.getLogger(Attribute.class.getName());
    private static final ConcurrentMap<String, Boolean> DETECTED_DEPRECATED_ATTRIBUTES = new ConcurrentHashMap<>();

    private static final String MESSAGE_PREFIX = "Use of deprecated attribute";

    private static final Pattern DEPRECATED_ATTR_PATTERN =
        Pattern.compile(Pattern.quote(MESSAGE_PREFIX) + " '(\\w+/@\\w+)'");

    private static String getLoggableAttributeName(Attribute attr) {
        return attr.getParent().getXPathNodeName() + "/@" + attr.getName();
    }

    public static List<String> deprecatedAttrNames(String log) {
        Matcher m = DEPRECATED_ATTR_PATTERN.matcher(log);
        List<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group(1));
        }

        return result;
    }

    public static void logIfDeprecated(Attribute attr, Method method) {
        if (method.isAnnotationPresent(Deprecated.class) && LOG.isLoggable(Level.WARNING)
            && DETECTED_DEPRECATED_ATTRIBUTES.putIfAbsent(getLoggableAttributeName(attr), Boolean.TRUE) == null) {

            LOG.warning(deprecationMsg(attr, method.getAnnotation(DeprecationInfo.class)));
        }
    }

    private static String deprecationMsg(Attribute attr, DeprecationInfo info) {
        // this message needs to be kept in sync with PMDCoverageTest
        String msg = MESSAGE_PREFIX + " '" + getLoggableAttributeName(attr) + "' in XPath query";
        if (info != null) {
            msg += ", " + StringUtils.uncapitalize(info.xpathReplacement());
        }
        return msg;
    }

}
