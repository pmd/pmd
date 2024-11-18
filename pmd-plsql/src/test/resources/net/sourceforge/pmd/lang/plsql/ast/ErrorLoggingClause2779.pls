--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

create or replace procedure test as
begin
    -- https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/INSERT.html#GUID-903F8043-0254-4EE9-ACC1-CB8AC0AF3423__BCEGDJDJ
    INSERT INTO raises
       SELECT employee_id, salary*1.1 FROM employees
       WHERE commission_pct > .2
       LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10;

    -- https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/UPDATE.html#GUID-027A462D-379D-4E35-8611-410F3AC8FDA5__I2135485
    UPDATE people_demo1 p SET VALUE(p) =
       (SELECT VALUE(q) FROM people_demo2 q
        WHERE p.department_id = q.department_id)
       WHERE p.department_id = 10
       LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10;

    -- https://github.com/pmd/pmd/issues/2779
    delete from test_table talias
      log errors into err$_test_table reject limit unlimited;

    -- without a table alias
    delete from test_table
      log errors into err$_test_table reject limit unlimited;

    -- https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/INSERT.html#GUID-903F8043-0254-4EE9-ACC1-CB8AC0AF3423__BCEGDJDJ
    -- multi-insert with conditional and optional error logging
    INSERT ALL
       WHEN order_total <= 100000 THEN
          INTO small_orders LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10
       WHEN order_total > 1000000 AND order_total <= 200000 THEN
          INTO medium_orders LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10
       WHEN order_total > 200000 THEN
          INTO large_orders
       ELSE
          INTO other_orders LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10
       SELECT order_id, order_total, sales_rep_id, customer_id
          FROM orders;
    -- multi-insert with optional error logging
    INSERT ALL
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date, sales_sun)
          LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date+1, sales_mon)
          LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date+2, sales_tue)
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date+3, sales_wed)
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date+4, sales_thu)
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date+5, sales_fri)
          INTO sales (prod_id, cust_id, time_id, amount)
          VALUES (product_id, customer_id, weekly_start_date+6, sales_sat)
       SELECT product_id, customer_id, weekly_start_date, sales_sun,
          sales_mon, sales_tue, sales_wed, sales_thu, sales_fri, sales_sat
          FROM sales_input_table;
end;
/
