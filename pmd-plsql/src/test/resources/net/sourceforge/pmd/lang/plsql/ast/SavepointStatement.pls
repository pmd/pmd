--
-- Example 6-38 SAVEPOINT and ROLLBACK Statements
-- from: https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/static-sql.html#GUID-68924CF6-130E-497B-9DA8-7B3E4D510FA6
--

DROP TABLE emp_name;
CREATE TABLE emp_name AS
  SELECT employee_id, last_name, salary
  FROM employees;
 
CREATE UNIQUE INDEX empname_ix
ON emp_name (employee_id);
 
DECLARE
  emp_id        employees.employee_id%TYPE;
  emp_lastname  employees.last_name%TYPE;
  emp_salary    employees.salary%TYPE;
 
BEGIN
  SELECT employee_id, last_name, salary
  INTO emp_id, emp_lastname, emp_salary 
  FROM employees
  WHERE employee_id = 120;
 
  UPDATE emp_name
  SET salary = salary * 1.1
  WHERE employee_id = emp_id;
 
  DELETE FROM emp_name
  WHERE employee_id = 130;
 
  SAVEPOINT do_insert;
 
  INSERT INTO emp_name (employee_id, last_name, salary)
  VALUES (emp_id, emp_lastname, emp_salary);
 
EXCEPTION
  WHEN DUP_VAL_ON_INDEX THEN
    ROLLBACK TO do_insert;
  DBMS_OUTPUT.PUT_LINE('Insert was rolled back');
END;
/
