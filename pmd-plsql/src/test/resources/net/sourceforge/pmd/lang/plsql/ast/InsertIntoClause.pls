--
-- See https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/INSERT.html#GUID-903F8043-0254-4EE9-ACC1-CB8AC0AF3423
--
BEGIN

INSERT INTO departments
   VALUES (280, 'Recreation', 121, 1700);

INSERT INTO departments
   VALUES (280, 'Recreation', DEFAULT, 1700);

INSERT INTO employees (employee_id, last_name, email, 
      hire_date, job_id, salary, commission_pct) 
   VALUES (207, 'Gregory', 'pgregory@example.com', 
      sysdate, 'PU_CLERK', 1.2E3, NULL);

INSERT INTO 
   (SELECT employee_id, last_name, email, hire_date, job_id, 
      salary, commission_pct FROM employees) 
   VALUES (207, 'Gregory', 'pgregory@example.com', 
      sysdate, 'PU_CLERK', 1.2E3, NULL);

INSERT INTO bonuses
   SELECT employee_id, salary*1.1 
   FROM employees
   WHERE commission_pct > 0.25;

INSERT INTO emp_job a
     (a.employee_id, a.job_id)
   VALUES (emp_id, emp_jobid);
END;
/
