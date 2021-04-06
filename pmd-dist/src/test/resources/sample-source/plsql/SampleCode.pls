--
-- Example 2-25 Assigning Value to Variable with SELECT INTO Statement
-- From: https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/plsql-language-fundamentals.html#GUID-664BFFEA-063A-48B6-A65B-95225EDDED59
-- Note that the original code example from Oracle is invalid:
-- The call to dbms_output.put_line MUST be inside begin ... end.
DECLARE
  bonus   NUMBER(8,2);
BEGIN
  SELECT salary * 0.10 INTO bonus
  FROM employees
  WHERE employee_id = 100;
  DBMS_OUTPUT.PUT_LINE('bonus = ' || TO_CHAR(bonus));
END;
/