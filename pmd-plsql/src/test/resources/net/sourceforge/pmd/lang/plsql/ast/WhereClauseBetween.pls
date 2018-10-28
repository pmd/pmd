--
-- Where Clause With Between Condition
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/BETWEEN-Condition.html#GUID-868A7C9D-EDF9-44E7-91B5-C3F69E503CCB
--

BEGIN

    SELECT *
      INTO rec
      FROM employees
      WHERE salary
      BETWEEN 2000 AND 3000
      ORDER BY employee_id;

END;
/
