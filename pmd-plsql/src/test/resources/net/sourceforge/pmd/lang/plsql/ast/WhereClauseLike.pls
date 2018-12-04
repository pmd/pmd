--
-- Where Clause With Like
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/Pattern-matching-Conditions.html#GUID-0779657B-06A8-441F-90C5-044B47862A0A
--

BEGIN

    SELECT salary 
    INTO rec
        FROM employees
        WHERE last_name LIKE 'R%'
        ORDER BY salary;

END;
/
