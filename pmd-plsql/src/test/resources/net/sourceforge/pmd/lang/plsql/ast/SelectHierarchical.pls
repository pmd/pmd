--
-- Select statement with hierarchical queries
--
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6__I2130004
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/Hierarchical-Queries.html#GUID-0118DF1D-B9A9-41EB-8556-C6E7D6A5A84E
--

BEGIN

SELECT id INTO v_id 
  FROM (SELECT separator_in || string_in || separator_in AS token_list FROM DUAL)
  CONNECT BY col_length <= LENGTH(string_in) - LENGTH(separator_in);

SELECT last_name, employee_id, manager_id
   INTO test
   FROM employees
   CONNECT BY employee_id = manager_id
   ORDER BY last_name;

SELECT last_name, employee_id, manager_id
   INTO test
   FROM employees
   CONNECT BY PRIOR employee_id = manager_id
   AND salary > commission_pct
   ORDER BY last_name;

SELECT employee_id, last_name, manager_id
   INTO test
   FROM employees
   CONNECT BY PRIOR employee_id = manager_id;

SELECT employee_id, last_name, manager_id, LEVEL
   INTO test
   FROM employees
   CONNECT BY PRIOR employee_id = manager_id;

SELECT last_name, employee_id, manager_id, LEVEL
    INTO test
    FROM employees
    START WITH employee_id = 100
    CONNECT BY PRIOR employee_id = manager_id
    ORDER SIBLINGS BY last_name;

SELECT last_name "Employee", 
   LEVEL, SYS_CONNECT_BY_PATH(last_name, '/') "Path"
   INTO test
   FROM employees
   WHERE level <= 3 AND department_id = 80
   START WITH last_name = 'King'
   CONNECT BY PRIOR employee_id = manager_id AND LEVEL <= 4;

SELECT last_name "Employee", CONNECT_BY_ISCYCLE "Cycle",
   LEVEL, SYS_CONNECT_BY_PATH(last_name, '/') "Path"
   INTO test
   FROM employees
   WHERE level <= 3 AND department_id = 80
   START WITH last_name = 'King'
   CONNECT BY NOCYCLE PRIOR employee_id = manager_id AND LEVEL <= 4
   ORDER BY "Employee", "Cycle", LEVEL, "Path";

SELECT LTRIM(SYS_CONNECT_BY_PATH (warehouse_id,','),',')
   INTO test
   FROM
   (SELECT ROWNUM r, warehouse_id FROM warehouses)
   WHERE CONNECT_BY_ISLEAF = 1
   START WITH r = 1
   CONNECT BY r = PRIOR r + 1
   ORDER BY warehouse_id;

SELECT last_name "Employee", CONNECT_BY_ROOT last_name "Manager",
   LEVEL-1 "Pathlen", SYS_CONNECT_BY_PATH(last_name, '/') "Path"
   INTO test
   FROM employees
   WHERE LEVEL > 1 and department_id = 110
   CONNECT BY PRIOR employee_id = manager_id
   ORDER BY "Employee", "Manager", "Pathlen", "Path";

SELECT name, SUM(salary) "Total_Salary"
   INTO test
   FROM (
   SELECT CONNECT_BY_ROOT last_name as name, Salary
      FROM employees
      WHERE department_id = 110
      CONNECT BY PRIOR employee_id = manager_id)
      GROUP BY name
   ORDER BY name, "Total_Salary";

END;
/
