BEGIN

-- see https://docs.oracle.com/en//database/oracle/oracle-database/23/sqlrf/TRIM.html

-- If you specify LEADING, then Oracle Database removes any leading characters equal to trim_character.
SELECT employee_id,
      TO_CHAR(TRIM(LEADING 0 FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

-- If you specify TRAILING, then Oracle removes any trailing characters equal to trim_character.
SELECT employee_id,
      TO_CHAR(TRIM(TRAILING 0 FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

-- If you specify BOTH or none of the three, then Oracle removes leading and trailing characters equal to trim_character.
SELECT employee_id,
      TO_CHAR(TRIM(BOTH 0 FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

-- If you specify BOTH or none of the three, then Oracle removes leading and trailing characters equal to trim_character.
SELECT employee_id,
      TO_CHAR(TRIM(0 FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

-- If you do not specify trim_character, then the default value is a blank space.
SELECT employee_id,
      TO_CHAR(TRIM(LEADING FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

-- If you specify only trim_source, then Oracle removes leading and trailing blank spaces.
SELECT employee_id,
      TO_CHAR(TRIM(hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

-- If either trim_source or trim_character is null, then the TRIM function returns null.
SELECT employee_id,
      TO_CHAR(TRIM(NULL))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;
SELECT employee_id,
      TO_CHAR(TRIM(NULL FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

select max(cmp_id)
        into v_cmp_id
        from companies
        where trim(leading '0' from sap_number) = trim(leading '0' from v_sap_nr);

select max(cmp_id)
        into v_cmp_id
        from companies
        where trim(sap_number) = trim(v_sap_nr);


-- outputs "3" in sqlplus
select trim(1+2) from dual;

-- outputs col5="3" in sqlplus
select 'X' || trim(leading 1 from '  test  ') || 'X' as col1,
       'X' || trim(leading ' ' from '  test  ') || 'X' as col2,
       'X' || trim(both ' ' from '  test  ') || 'X' as col3,
       'X' || trim(leading from '  test  ') || 'X' as col4,
       trim(1+2) as col5
       from dual;

END;

create or replace package p_utils is
    function distinguishXML(x in number, y in number) return varchar;
end;
/
create or replace package body p_utils is
    function distinguishXML(x in number, y in number) return varchar is
    begin
      return trim(x + y);
    end;
end;
/
SHOW ERRORS
SET SERVEROUTPUT ON
DECLARE
    BEGIN
        DBMS_OUTPUT.PUT_LINE(p_utils.distinguishXML(1, 2));
    END;
/
