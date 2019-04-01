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