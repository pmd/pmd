BEGIN
SELECT department_id AS d_e_dept_id, e.last_name
    INTO r_record
    FROM departments NATURAL JOIN employees;
END;

SELECT department_id AS d_e_dept_id, e.last_name
    INTO r_record
    FROM departments NATURAL INNER JOIN employees;
END;

/
