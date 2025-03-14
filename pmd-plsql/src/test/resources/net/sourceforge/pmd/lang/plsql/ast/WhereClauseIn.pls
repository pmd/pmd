--
-- Where Clause With In Condition
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/IN-Condition.html#GUID-C7961CB3-8F60-47E0-96EB-BDCF5DB1317C
--

BEGIN

    -- IN

    SELECT *
      INTO rec
      FROM employees
      WHERE job_id IN
      ('PU_CLERK','SH_CLERK')
      ORDER BY employee_id;

    SELECT *
      INTO rec
      FROM employees
      WHERE salary IN
      (SELECT salary 
       FROM employees
       WHERE department_id =30)
      ORDER BY employee_id;

    -- NOT IN

    SELECT *
      INTO rec
      FROM employees
      WHERE salary NOT IN
      (SELECT salary 
       FROM employees
      WHERE department_id = 30)
      ORDER BY employee_id;

    SELECT *
      INTO rec
      FROM employees
      WHERE job_id NOT IN
      ('PU_CLERK', 'SH_CLERK')
      ORDER BY employee_id;
END;
/
