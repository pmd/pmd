/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.IteratorUtil;

import com.google.summit.ast.statement.DmlStatement;

public abstract class AbstractDmlStatement extends AbstractApexNode.Single<DmlStatement> {
    protected AbstractDmlStatement(DmlStatement node) {
        super(node);
    }

    public Optional<String> getRunAsMode() {
        DmlStatement.AccessLevel accessLevel = node.getAccess();
        if (accessLevel != null) {
            return Optional.of(accessLevel.name());
        }
        return Optional.empty();
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        Iterator<Attribute> attributes = super.getXPathAttributesIterator();
        return getRunAsMode()
                .map(mode -> IteratorUtil.concat(attributes, Collections.singleton(new Attribute(this, "RunAsMode", mode)).iterator()))
                .orElse(attributes);
    }
}
