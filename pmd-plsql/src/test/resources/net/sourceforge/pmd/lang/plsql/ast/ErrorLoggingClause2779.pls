--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

create or replace procedure test as
begin

    -- https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/INSERT.html#GUID-903F8043-0254-4EE9-ACC1-CB8AC0AF3423__BCEGDJDJ
    INSERT INTO raises
       SELECT employee_id, salary*1.1 FROM employees
       WHERE commission_pct > .2
       LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10;

    -- https://docs.oracle.com/en/database/oracle/oracle-database/23/sqlrf/UPDATE.html#GUID-027A462D-379D-4E35-8611-410F3AC8FDA5__I2135485
    UPDATE people_demo1 p SET VALUE(p) =
       (SELECT VALUE(q) FROM people_demo2 q
        WHERE p.department_id = q.department_id)
       WHERE p.department_id = 10
       LOG ERRORS INTO errlog ('my_bad') REJECT LIMIT 10;

    -- https://github.com/pmd/pmd/issues/2779
    delete from test_table talias
      log errors into err$_test_table reject limit unlimited;

    -- without a table alias
    delete from test_table
      log errors into err$_test_table reject limit unlimited;
end;
/
