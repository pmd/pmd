/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.metrics;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.metrics.internal.NcssVisitor;

/**
 * Built-in PL/SQL metrics. See {@link Metric} and {@link MetricsUtil}
 * for usage doc.
 */
public final class PlsqlMetrics {
    private PlsqlMetrics() {
        // utility class
    }

    /**
     * NCSS (Non-commenting source statements) counts the
     * number of statements in a source file, oracle object or operation.
     * That’s roughly equivalent to counting the number of semicolons in
     * the program.
     * Comments and blank lines are ignored, and statements
     * spread on multiple lines count as only one.
     *
     *
     * <pre>{@code
     * DECLARE                                     -- total Ncss: 14
     *     PROCEDURE bigMethod IS
     *         x NUMBER;
     *         y NUMBER := 2;                      -- +1
     *         a BOOLEAN := FALSE;                 -- +1
     *         b BOOLEAN := TRUE;                  -- +1
     *     BEGIN
     *         IF (a OR b) THEN                    -- +2
     *             LOOP                            -- +1
     *                 x := x + 2;                 -- +1
     *                 EXIT WHEN x >= 12;          -- +2
     *             END LOOP;
     *
     *             DBMS_OUTPUT.PUT_LINE('done');   -- +1
     *         ELSE
     *             DBMS_OUTPUT.PUT_LINE('false');  -- +1
     *         END IF;
     *     EXCEPTION
     *         WHEN PROGRAM_ERROR THEN DBMS_OUTPUT.PUT_LINE('Error Occurred'); -- +2
     *     END bigMethod;
     * BEGIN
     *     bigMethod();                            -- +1
     * END;
     * }</pre>
     * @since 7.19.0
     */
    public static final Metric<PLSQLNode, Integer> NCSS =
            Metric.of(PlsqlMetrics::computeNcss, isPlsqlNode(),
                    "Non-commenting source statements", "NCSS");

    private static Function<Node, PLSQLNode> isPlsqlNode() {
        return n -> n instanceof PLSQLNode ? (PLSQLNode) n : null;
    }

    private static int computeNcss(PLSQLNode node, MetricOptions ignored) {
        MutableInt result = new MutableInt(0);
        node.acceptVisitor(new NcssVisitor(), result);
        return result.intValue();
    }
}
