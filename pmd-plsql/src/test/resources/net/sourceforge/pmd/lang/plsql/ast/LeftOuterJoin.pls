--
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6__I2107296
--

BEGIN

SELECT d.department_id, e.last_name
   FROM departments d LEFT OUTER JOIN employees e
   ON d.department_id = e.department_id
   ORDER BY d.department_id, e.last_name;

SELECT cv.hidden
      FROM (SELECT CONNECT_BY_ROOT dep_id dep_id, LEVEL as dep_level
              FROM departments
             WHERE dep_id = c.dep_id
           CONNECT BY PRIOR dep_id = parent_dep_id
             ORDER BY LEVEL) dep
      LEFT JOIN config_visibility cv ON (cv.dep_id = dep.dep_id AND cv.app_id = p_app_id)
      ORDER BY dep.dep_level;

END;
/