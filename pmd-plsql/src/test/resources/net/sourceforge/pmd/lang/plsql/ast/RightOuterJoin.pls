--
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6__I2107296
--

BEGIN
SELECT times.time_id, product, quantity
INTO r_record
FROM inventory 
   RIGHT OUTER JOIN times ON (times.time_id = inventory.time_id) 
   ORDER BY  2,1;
END;

SELECT d.department_id, e.last_name
   FROM departments d RIGHT OUTER JOIN employees e
   ON d.department_id = e.department_id
   ORDER BY d.department_id, e.last_name;

/