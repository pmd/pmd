BEGIN
SELECT department_id AS d_e_dept_id, e.last_name
   INTO r_record
   FROM departments d FULL OUTER JOIN employees e
   USING (department_id);
END;
/