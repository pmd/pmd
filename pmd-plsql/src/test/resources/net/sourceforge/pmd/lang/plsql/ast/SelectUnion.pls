--
BEGIN

--
-- Example from: https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/The-UNION-ALL-INTERSECT-MINUS-Operators.html
--
SELECT location_id, department_name "Department",
   TO_CHAR(NULL) "Warehouse"
   INTO my_record
   FROM departments
   UNION
   SELECT location_id, TO_CHAR(NULL) "Department", warehouse_name
   FROM warehouses;

SELECT location_id, department_name "Department",
   TO_CHAR(NULL) "Warehouse"
   FROM departments
   UNION
   SELECT location_id, TO_CHAR(NULL) "Department", warehouse_name
   FROM warehouses;

SELECT dep.dep_id
  FROM employees e
  INNER JOIN departments dep ON (dep.code = e.dep_code AND e.dep_id = dep.dep_id)
  UNION
  SELECT adep.dep_id
  FROM departments adep
  WHERE adep.dep_id = id;

END;
