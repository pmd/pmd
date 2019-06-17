CREATE OR REPLACE PACKAGE lpe_test is

procedure testif;

end lpe_test;
/

CREATE OR REPLACE PACKAGE BODY lpe_test is


procedure testif
is
cursor TestSearch
is
select 1 from dual;

TestRow TestSearch%rowtype;

begin
    open TestSearch;
    fetch TestSearch into TestRow;

    if TestSearch%notfound then
        DBMS_OUTPUT.PUT_LINE('Not found');
    else
        DBMS_OUTPUT.PUT_LINE('Found');
    end if;
    close TestSearch;
end testif;

end lpe_test;
/

--
-- Implicit cursor attributes
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/implicit-cursor-attribute.html#GUID-5A938EE7-E8D2-468C-B60F-81898F110BE1
-- Example from: https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/plsql-optimization-and-tuning.html#GUID-DDB5CCDA-8060-4511-BA20-4D1F2C478412
--

DECLARE
  TYPE NumList IS TABLE OF NUMBER;
  depts NumList := NumList(30, 50, 60);
BEGIN
  FORALL j IN depts.FIRST..depts.LAST
    DELETE FROM emp_temp WHERE department_id = depts(j);

  FOR i IN depts.FIRST..depts.LAST LOOP
    DBMS_OUTPUT.PUT_LINE (
      'Statement #' || i || ' deleted ' ||
      SQL%BULK_ROWCOUNT(i) || ' rows.'
    );
  END LOOP;

  DBMS_OUTPUT.PUT_LINE('Total rows deleted: ' || SQL%ROWCOUNT);
END;
/