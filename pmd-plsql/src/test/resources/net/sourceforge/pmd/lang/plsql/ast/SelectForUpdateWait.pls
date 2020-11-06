--
-- From: https://docs.oracle.com/database/121/SQLRF/statements_10002.htm#i2126016
--

begin
  for r_emp_test in (select employee_id,
                            last_name,
                            salary,
                            job_id
                     from   employees e
                     for    update)
  loop
    null;
  end loop;
end;
/

begin
  for r_emp_test in (select employee_id,
                            last_name,
                            salary,
                            job_id
                     from   employees e
                     for    update of salary)
  loop
    null;
  end loop;
end;
/

begin
  for r_emp_test in (select employee_id,
                            last_name,
                            salary,
                            job_id
                     from   employees e
                     for    update of e.salary wait 4)
  loop
    null;
  end loop;
end;
/

begin
  for r_emp_test in (select employee_id,
                            last_name,
                            salary,
                            job_id
                     from   employees e
                     for    update of e.salary nowait)
  loop
    null;
  end loop;
end;
/

declare
  w_found number(10);
begin

  select 1
  into   w_found
  from   hr.employees e
  where  rownum < 2
  for    update of hr.employees.salary skip locked;

end;
/
