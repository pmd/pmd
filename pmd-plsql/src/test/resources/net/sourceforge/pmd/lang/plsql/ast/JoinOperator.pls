--
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/Joins.html
--

BEGIN

SELECT e1.employee_id, e1.manager_id, e2.employee_id
   FROM employees e1, employees e2
   WHERE e1.manager_id(+) = e2.employee_id
   ORDER BY e1.employee_id, e1.manager_id, e2.employee_id;

SELECT * FROM A, B, D
  WHERE A.c1 = B.c2(+) and D.c3 = B.c4(+);

-- From https://github.com/pmd/pmd/issues/1902
SELECT DISTINCT
                      o.op_id
                  FROM
                      opportunities o,
                      v_opp_bundles b,
                      jopportunities h
                  WHERE
                      o.opp_id = b.opp_id
                      AND b.bun_id = p_bun_id
                      AND o.opp_id = h.opp_id (+);

END;
/