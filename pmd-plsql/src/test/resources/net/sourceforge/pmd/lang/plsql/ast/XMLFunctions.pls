SELECT xmlroot(xmlelement("BODY",
                           xmlagg(xmlconcat(extract(v_1, '/BODY/child::node()'),
                                            extract(v_2, '/BODY/child::node()')))),
               version '1.0')
 FROM (SELECT dummy FROM dual CONNECT BY LEVEL <= 2);

SELECT warehouse_name, EXTRACT(warehouse_spec, '/Warehouse/Docks')
   "Number of Docks"
   FROM warehouses
   WHERE warehouse_spec IS NOT NULL;
   
SELECT XMLELEMENT("Emp", 
   XMLFOREST(e.employee_id, e.last_name, e.salary))
   "Emp Element"
   FROM employees e WHERE employee_id = 204;