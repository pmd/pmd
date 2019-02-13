--
-- Expressions in the SelectList
--

BEGIN

SELECT AVG(sal)*2 INTO foo FROM bar;


SELECT
    AVG(salary) * 12 "Average Sal"
  INTO some_record
  FROM some_table;

SELECT
    TO_CHAR(sum(amount_sold) , '9,999,999,999') SALES$
  INTO some_record
  FROM some_table;

-- Example from: https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/AVG.html
SELECT manager_id, last_name, hire_date, salary,
       AVG(salary) OVER (PARTITION BY manager_id ORDER BY hire_date
  ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS c_mavg
  INTO some_record
  FROM employees
  ORDER BY manager_id, hire_date, salary;

END;
/