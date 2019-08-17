BEGIN
SELECT department_id AS d_e_dept_id, e.last_name
INTO r_record
   FROM departments d JOIN employees e
   USING (department_id);

SELECT department_id AS d_e_dept_id, e.last_name
INTO r_record
   FROM departments d INNER JOIN employees e
   USING (department_id);

--
-- https://github.com/pmd/pmd/issues/1878
--

SELECT COUNT(qsec_id)
      FROM quots_sections  qsec
           INNER JOIN quots_sections_lang USING (qsec_id)
     WHERE qsec.wsh_id = 11
       AND qsec.revision = 1
       AND lang_code = 'en';

END;
/
