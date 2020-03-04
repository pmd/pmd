--
-- Subqueries / Expressions in the SelectList
--

SELECT EXTRACT(month FROM order_date) "Month",
  COUNT(order_date) "No. of Orders"
  FROM orders
  GROUP BY EXTRACT(month FROM order_date)
  ORDER BY "No. of Orders" DESC;

SELECT EXTRACT(YEAR FROM DATE '1998-03-07') FROM DUAL;

SELECT last_name, employee_id, hire_date
   FROM employees
   WHERE EXTRACT(YEAR FROM
   TO_DATE(hire_date, 'DD-MON-RR')) > 1998
   ORDER BY hire_date;

SELECT EXTRACT(TIMEZONE_REGION
      FROM TIMESTAMP '1999-01-01 10:00:00 -08:00')
   FROM DUAL;

declare
   v_months  NUMBER;
begin
   v_months := (extract(YEAR FROM v_rec.v_rec_rec.v_rec_rec_field) -
                    extract(YEAR FROM v_rec.v_rec_rec.v_rec_rec_field)) * 12 +
                (extract(MONTH FROM v_rec.v_rec_rec.v_rec_rec_field) -
                    extract(MONTH FROM v_rec.v_rec_rec.v_rec_rec_field));

end;