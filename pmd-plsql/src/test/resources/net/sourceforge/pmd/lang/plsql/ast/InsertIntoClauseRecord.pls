--
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/INSERT-statement-extension.html#GUID-D81224C4-06DE-4635-A850-41D29D4A8E1B
-- https://blogs.oracle.com/oraclemagazine/working-with-records
--

DECLARE
   l_employee   omag_employees%ROWTYPE;
BEGIN
   l_employee.employee_id := 500;
   l_employee.last_name := 'Mondrian';
   l_employee.salary := 2000;

   INSERT
     INTO omag_employees 
   VALUES l_employee;
END;
/
