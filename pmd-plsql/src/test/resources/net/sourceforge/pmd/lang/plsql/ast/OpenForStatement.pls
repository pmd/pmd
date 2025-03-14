--
-- See https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/OPEN-FOR-statement.html#GUID-EB7AF439-FDD3-4461-9E3F-B621E8ABFB96
-- https://github.com/pmd/pmd/issues/3487
--

CREATE OR REPLACE PROCEDURE EXAMPLE_PROCEDURE IS
   --
   TYPE t_ref_cursor IS REF CURSOR;
   --
   l_ref_cursor t_ref_cursor;
   --
BEGIN
   --
   OPEN l_ref_cursor FOR
      SELECT *
        FROM DUAL;
   --
END EXAMPLE_PROCEDURE;

--
-- Example 6-26 Fetching Data with Cursor Variables
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/static-sql.html#GUID-AA5A2016-1B76-4961-9AFB-EB052F0D0FB2
--
DECLARE
  cv SYS_REFCURSOR;  -- cursor variable
 
  v_lastname  employees.last_name%TYPE;  -- variable for last_name
  v_jobid     employees.job_id%TYPE;     -- variable for job_id
 
  query_2 VARCHAR2(200) :=
    'SELECT * FROM employees
    WHERE REGEXP_LIKE (job_id, ''[ACADFIMKSA]_M[ANGR]'')
    ORDER BY job_id';
 
  v_employees employees%ROWTYPE;  -- record variable row of table
 
BEGIN
  OPEN cv FOR
    SELECT last_name, job_id FROM employees
    WHERE REGEXP_LIKE (job_id, 'S[HT]_CLERK')
    ORDER BY last_name;
 
  LOOP  -- Fetches 2 columns into variables
    FETCH cv INTO v_lastname, v_jobid;
    EXIT WHEN cv%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE( RPAD(v_lastname, 25, ' ') || v_jobid );
  END LOOP;
 
  DBMS_OUTPUT.PUT_LINE( '-------------------------------------' );
 
  OPEN cv FOR query_2;
 
  LOOP  -- Fetches entire row into the v_employees record
    FETCH cv INTO v_employees;
    EXIT WHEN cv%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE( RPAD(v_employees.last_name, 25, ' ') ||
                               v_employees.job_id );
  END LOOP;
 
  CLOSE cv;
END;
/
