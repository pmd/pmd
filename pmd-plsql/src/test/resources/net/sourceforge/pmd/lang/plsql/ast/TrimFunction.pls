BEGIN

SELECT employee_id,
      TO_CHAR(TRIM(LEADING 0 FROM hire_date))
      FROM employees
      WHERE department_id = 60
      ORDER BY employee_id;

select max(cmp_id)
        into v_cmp_id
        from companies
        where trim(leading '0' from sap_number) = trim(leading '0' from v_sap_nr);

select max(cmp_id)
        into v_cmp_id
        from companies
        where trim(sap_number) = trim(v_sap_nr);

END;
