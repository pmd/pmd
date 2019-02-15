--
-- Expressions in the SelectList
--

BEGIN

SELECT AVG(sal)*2 INTO foo FROM bar;


SELECT
    AVG(salary) * 12 "Average Sal"
  INTO some_record
  FROM some_table;

SELECT
    TO_CHAR(sum(amount_sold) , '9,999,999,999') SALES$
  INTO some_record
  FROM some_table;

-- Example from: https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/AVG.html
SELECT manager_id, last_name, hire_date, salary,
       AVG(salary) OVER (PARTITION BY manager_id ORDER BY hire_date
  ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS c_mavg
  INTO some_record
  FROM employees
  ORDER BY manager_id, hire_date, salary;

-- Example from: https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/LISTAGG.html
SELECT LISTAGG(last_name, '; ')
         WITHIN GROUP (ORDER BY hire_date, last_name) "Emp_list",
       MIN(hire_date) "Earliest"
  INTO some_record
  FROM employees
  WHERE department_id = 30;

SELECT department_id "Dept.",
       LISTAGG(last_name, '; ') WITHIN GROUP (ORDER BY hire_date) "Employees"
  INTO some_record
  FROM employees
  GROUP BY department_id
  ORDER BY department_id;

SELECT department_id "Dept.",
       LISTAGG(last_name, '; ' ON OVERFLOW TRUNCATE '...')
               WITHIN GROUP (ORDER BY hire_date) "Employees"
  INTO some_record
  FROM employees
  GROUP BY department_id
  ORDER BY department_id;

SELECT department_id "Dept", hire_date "Date", last_name "Name",
       LISTAGG(last_name, '; ') WITHIN GROUP (ORDER BY hire_date, last_name)
         OVER (PARTITION BY department_id) as "Emp_list"
  INTO some_record
  FROM employees
  WHERE hire_date < '01-SEP-2003'
  ORDER BY "Dept", "Date", "Name";

SELECT listagg(e.email,',') within group (order by e.email )INTO
                v_task_resp
            FROM sso_auth_employees e;

select listagg(asap_func_loc_number,'; ') within group (order by 1)
    INTO my_record
    FROM company_asap_func_locs
    WHERE cmp_id = cmp_id_in;

SELECT CASE
            WHEN priv != 'Y' AND my_package.my_function(param1, TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy'))) >= 100 THEN
                 'Y'
               ELSE
                 'N'
             END
        INTO my_result
        FROM DUAL;

END;
/