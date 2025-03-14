--
-- Using the GROUP BY Clause: Examples
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6__I2066419
--
BEGIN
SELECT department_id, MIN(salary), MAX (salary) INTO some_record
     FROM employees
     GROUP BY department_id
   ORDER BY department_id;
END;
/
