--
-- Example 5-50 SELECT INTO Assigns Values to Record Variable
-- From: https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/plsql-collections-and-records.html#GUID-CC1DA893-4087-4DA4-8B13-052AB76DAC4F
--
DECLARE
  TYPE RecordTyp IS RECORD (
    last employees.last_name%TYPE,
    id   employees.employee_id%TYPE
  );
  rec1 RecordTyp;
BEGIN
  SELECT last_name, employee_id INTO rec1
  FROM employees
  WHERE job_id = 'AD_PRES';

  DBMS_OUTPUT.PUT_LINE ('Employee #' || rec1.id || ' = ' || rec1.last);
END;
/
