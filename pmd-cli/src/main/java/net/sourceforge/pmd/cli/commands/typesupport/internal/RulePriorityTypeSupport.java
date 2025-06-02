/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.rule.RulePriority;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public class RulePriorityTypeSupport implements ITypeConverter<RulePriority>, Iterable<String> {
    @Override
    public RulePriority convert(String value) {
        for (RulePriority rulePriority : RulePriority.values()) {
            String descriptiveName = rulePriority.getName();
            String name = rulePriority.name();
            String priority = String.valueOf(rulePriority.getPriority());
            if (descriptiveName.equalsIgnoreCase(value) || name.equalsIgnoreCase(value) || priority.equalsIgnoreCase(value)) {
                return rulePriority;
            }
        }
        throw new TypeConversionException("Invalid priority: " + value);
    }

    @Override
    public Iterator<String> iterator() {
        List<String> completionValues = new ArrayList<>();
        for (RulePriority rulePriority : RulePriority.values()) {
            completionValues.add(rulePriority.name());
        }
        return completionValues.iterator();
    }
}
