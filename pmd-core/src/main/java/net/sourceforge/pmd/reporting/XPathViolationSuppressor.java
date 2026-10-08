/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

final class XPathViolationSuppressor implements ViolationSuppressor {

    private static final SimpleDataKey<Map<Rule, SaxonXPathRuleQuery>> QUERY_CACHE =
        DataMap.simpleDataKey("pmd.core.xpath.suppressor");

    @Override
    public String getId() {
        return "XPath";
    }

    @Override
    public @Nullable SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
        Rule rule = rv.getRule();
        Optional<String> xpath = rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
        if (!xpath.isPresent()) {
            return null;
        }

        SaxonXPathRuleQuery query = getQuery(rule, node, xpath.get());
        if (!query.evaluate(node).isEmpty()) {
            return new SuppressedViolation(rv, this, xpath.get());
        }
        return null;
    }

    // test only
    SaxonXPathRuleQuery getQuery(Rule rule, Node node, String xpath) {
        Map<Rule, SaxonXPathRuleQuery> queries = node.getAstInfo().getUserMap()
            .computeIfAbsent(QUERY_CACHE, IdentityHashMap::new);
        return queries.computeIfAbsent(rule, ignored -> new SaxonXPathRuleQuery(
            xpath,
            XPathVersion.DEFAULT,
            rule.getPropertiesByPropertyDescriptor(),
            node.getAstInfo().getLanguageProcessor().services().getXPathHandler(),
            DeprecatedAttrLogger.createForSuppression(rule)
        ));
    }
}
