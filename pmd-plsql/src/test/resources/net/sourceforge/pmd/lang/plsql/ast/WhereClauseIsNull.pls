--
-- Where Clause With Null Condition
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/Null-Conditions.html#GUID-657F2BA6-5687-4A00-8C2F-57515FD2DAEB
--

BEGIN

    SELECT last_name
      INTO rec
      FROM employees
      WHERE commission_pct
      IS NULL
      ORDER BY last_name;

END;
/
