--
-- Examples from SQL Reference
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/UPDATE.html
--

BEGIN

UPDATE employees
   SET commission_pct = NULL
   WHERE job_id = 'SH_CLERK';

UPDATE employees SET
    job_id = 'SA_MAN', salary = salary + 1000, department_id = 120
    WHERE first_name||' '||last_name = 'Douglas Grant';

UPDATE employees@remote
   SET salary = salary*1.1
   WHERE last_name = 'Baer';

UPDATE employees a
    SET department_id =
        (SELECT department_id
            FROM departments
            WHERE location_id = '2100'),
        (salary, commission_pct) =
        (SELECT 1.1*AVG(salary), 1.5*AVG(commission_pct)
          FROM employees b
          WHERE a.department_id = b.department_id)
    WHERE department_id IN
        (SELECT department_id
          FROM departments
          WHERE location_id = 2900
              OR location_id = 2700);

UPDATE sales PARTITION (sales_q1_1999) s
   SET s.promo_id = 494
   WHERE amount_sold > 1000;

UPDATE people_demo1 p SET VALUE(p) =
   (SELECT VALUE(q) FROM people_demo2 q
    WHERE p.department_id = q.department_id)
   WHERE p.department_id = 10;

UPDATE employees
  SET job_id ='SA_MAN', salary = salary + 1000, department_id = 140
  WHERE last_name = 'Jones'
  RETURNING salary*0.25, last_name, department_id
    INTO :bnd1, :bnd2, :bnd3;

UPDATE employees
   SET salary = salary * 1.1
   WHERE department_id = 100
   RETURNING SUM(salary) INTO :bnd1;

update xsearch_wsh_active
     set line_items_cnt = p_cnt
     where wsh_id = p_wid
       and revision = p_rev
     returning opp_id into v_opp_id;

update employees
     set salary = salary + sal_raise,
         salary = (salary - discounts) * sal_raise
     where employee_id = emp_id;

END;
