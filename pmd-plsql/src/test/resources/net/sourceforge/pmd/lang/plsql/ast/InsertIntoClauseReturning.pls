--
-- See https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/INSERT.html#GUID-903F8043-0254-4EE9-ACC1-CB8AC0AF3423
--
BEGIN

INSERT INTO employees 
      (employee_id, last_name, email, hire_date, job_id, salary)
   VALUES 
   (employees_seq.nextval, 'Doe', 'john.doe@example.com', 
       SYSDATE, 'SH_CLERK', 2400) 
   RETURNING salary*12, job_id INTO :bnd1, :bnd2;

END;
/
