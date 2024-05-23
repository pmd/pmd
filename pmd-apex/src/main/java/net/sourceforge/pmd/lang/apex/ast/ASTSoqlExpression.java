/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.summit.ast.expression.SoqlExpression;

public final class ASTSoqlExpression extends AbstractApexNode.Single<SoqlExpression> {
    private final String canoncialQuery;

    ASTSoqlExpression(SoqlExpression soqlExpression) {
        super(soqlExpression);
        canoncialQuery = convertToCanonicalQuery(soqlExpression.getQuery());
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the raw query as it appears in the source code.
     */
    public String getQuery() {
        return node.getQuery();
    }

    /**
     * Returns the query with the SOQL keywords normalized as uppercase.
     */
    public String getCanonicalQuery() {
        return canoncialQuery;
    }

    static String convertToCanonicalQuery(String rawQuery) {
        // note: this is a very crude way. At some point, the parsed query should be
        // provided by Summit AST. The Apex Parser already parses the SOQL/SOSL queries.
        String query = rawQuery;
        query = query.replaceAll("(?i)\\bselect\\b", "SELECT");
        query = query.replaceAll("(?i)\\bfrom\\b", "FROM");
        query = query.replaceAll("(?i)\\bupdate\\b", "UPDATE");
        query = query.replaceAll("(?i)\\bwhere\\b", "WHERE");
        query = query.replaceAll("(?i)\\bgroup\\b\\s+\\bby\\b", "GROUP BY");
        query = query.replaceAll("(?i)\\band\\b", "AND");
        query = query.replaceAll("(?i)\\bor\\b", "OR");
        query = query.replaceAll("(?i)\\bnot\\b", "NOT");

        // soql functions
        query = query.replaceAll("(?i)\\bavg\\(", "AVG(");
        query = query.replaceAll("(?i)\\bcount\\(", "COUNT(");
        query = query.replaceAll("(?i)\\bcount_distinct\\(", "COUNT_DISTINCT(");
        query = query.replaceAll("(?i)\\bmin\\(", "MIN(");
        query = query.replaceAll("(?i)\\bmax\\(", "MAX(");
        query = query.replaceAll("(?i)\\bsum\\(", "SUM(");
        query = query.replaceAll("(?i)\\btolabel\\(", "TOLABEL(");
        query = query.replaceAll("(?i)\\bformat\\(", "FORMAT(");
        query = query.replaceAll("(?i)\\bcalendar_month\\(", "FORMAT(");
        query = query.replaceAll("(?i)\\bcalendar_quarter\\(", "FORMAT(");
        query = query.replaceAll("(?i)\\bcalendar_year\\(", "FORMAT(");
        query = query.replaceAll("(?i)\\bDAY_IN_MONTH\\(", "DAY_IN_MONTH(");
        query = query.replaceAll("(?i)\\bDAY_IN_WEEK\\(", "DAY_IN_WEEK(");
        query = query.replaceAll("(?i)\\bDAY_IN_YEAR\\(", "DAY_IN_YEAR(");
        query = query.replaceAll("(?i)\\bDAY_ONLY\\(", "DAY_ONLY(");
        query = query.replaceAll("(?i)\\bFISCAL_MONTH\\(", "FISCAL_MONTH(");
        query = query.replaceAll("(?i)\\bFISCAL_QUARTER\\(", "FISCAL_QUART(");
        query = query.replaceAll("(?i)\\bFISCAL_YEAR\\(", "FISCAL_YEAR(");
        query = query.replaceAll("(?i)\\bHOUR_IN_DAY\\(", "HOUR_IN_DAY(");
        query = query.replaceAll("(?i)\\bWEEK_IN_MONTH\\(", "WEEK_IN_MONT(");
        query = query.replaceAll("(?i)\\bWEEK_IN_YEAR\\(", "WEEK_IN_YEAR(");
        query = query.replaceAll("(?i)\\bFIELDS\\(all\\)", "FIELDS(ALL)");
        query = query.replaceAll("(?i)\\bFIELDS\\(custom\\)", "FIELDS(CUSTOM)");
        query = query.replaceAll("(?i)\\bFIELDS\\(standard\\)", "FIELDS(STANDARD)");
        query = query.replaceAll("(?i)\\bDISTANCE\\(", "DISTANCE(");
        query = query.replaceAll("(?i)\\bconverttimezone\\(", "CONVERTTIMEZONE(");

        // sosl keywords
        query = query.replaceAll("(?i)\\bfind\\b", "FIND");
        query = query.replaceAll("(?i)\\bin\\b", "IN");
        query = query.replaceAll("(?i)\\breturning\\b", "RETURNING");
        query = query.replaceAll("(?i)\\bassign\\b", "ASSIGN");
        query = query.replaceAll("(?i)\\btarget_length\\b", "TARGET_LENGTH");
        query = query.replaceAll("(?i)\\bmetadata\\b", "METADATA");
        query = query.replaceAll("(?i)\\blimit\\b", "LIMIT");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\bdivision\\b", "WITH DIVISION");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\bdata\\b\\s+\\bcategory\\b", "WITH DATA CATEGORY");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\bsnippet\\b", "WITH SNIPPET");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\bnetwork\\b", "WITH NETWORK");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\bpricebookid\\b", "WITH PRICEBOOKID");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\buser_mode\\b", "WITH USER_MODE");
        query = query.replaceAll("(?i)\\bwith\\b\\s+\\bsystem_mode\\b", "WITH SYSTEM_MODE");

        // replace all binding variables with :tmpVar1 :tmpVar2...
        Pattern bindingVarPattern = Pattern.compile(":[^ :]+\\b");
        int tmpVarCounter = 1;
        StringBuffer normalizedVars = new StringBuffer();
        Matcher matcher = bindingVarPattern.matcher(query);
        while (matcher.find()) {
            matcher.appendReplacement(normalizedVars, ":tmpVar" + tmpVarCounter);
            tmpVarCounter++;
        }
        matcher.appendTail(normalizedVars);

        return normalizedVars.toString().trim();
    }
}
