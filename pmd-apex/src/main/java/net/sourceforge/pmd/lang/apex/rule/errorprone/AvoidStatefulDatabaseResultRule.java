/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.Locale;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Using stateful `Database.[x]Result` instance variables can cause odd
 * serialization errors between successive batch iterations.
 *
 * https://issues.salesforce.com/issue/a028c00000qPwlqAAC/stateful-batch-job-that-stores-databasesaveresult-failed-after-validation-errors-throws-error-during-deserialization
 *
 * This rule scans classes which implement the `Database.Stateful` interface. If
 * there are instance variables of type `Database.[x]Result` (ex:
 * `Database.SaveResult`), then a violation will be added to those variables.
 */
public final class AvoidStatefulDatabaseResultRule extends AbstractApexRule {

    private static final Set<String> DATABASE_RESULT_TYPES = CollectionUtil.immutableSetOf("database.leadconvertresult",
            "database.deleteresult", "database.emptyrecyclebinresult", "database.mergeresult", "database.saveresult",
            "database.undeleteresult", "database.upsertresult");

    @Override
    public Object visit(ASTUserClass theClass, Object data) {
        if (!implementsDatabaseStateful(theClass)) {
            return data;
        }
        // Note that inner classes cannot implement `Database.Stateful`
        // interface, so we only need to check the top level class
        for (ASTField theField : theClass.descendants(ASTField.class)) {
            if (isNonTransientInstanceDatabaseResultField(theField)) {
                asCtx(data).addViolation(theField);
            }
        }
        return data;
    }

    /** Determines if the class implements the `Database.Stateful` interface */
    private boolean implementsDatabaseStateful(ASTUserClass theClass) {
        for (String interfaceName : theClass.getInterfaceNames()) {
            if ("database.stateful".equalsIgnoreCase(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a variable's definition may cause issues within batch
     * iteration by being: an instance variable, not transient, and a data type
     * of `Database.[x]Result` (or collection of them).
     */
    private boolean isNonTransientInstanceDatabaseResultField(ASTField theField) {
        return !theField.getModifiers().isStatic() && !theField.getModifiers().isTransient()
                && isDatabaseResultOrCollection(theField.getType());
    }

    /**
     * Determines if any of the unsupported types contains the type. We check
     * containment even as a substring to check if the type is a collection of
     * the result types ex: `List<Database.SaveResult>`.
     */
    private boolean isDatabaseResultOrCollection(String type) {
        for (String databaseResultType : DATABASE_RESULT_TYPES) {
            if (type.toLowerCase(Locale.ROOT).contains(databaseResultType)) {
                return true;
            }
        }
        return false;
    }
}
