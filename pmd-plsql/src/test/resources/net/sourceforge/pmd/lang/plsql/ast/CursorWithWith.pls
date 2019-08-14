create or replace procedure test is

  cursor c_tariff_price is
    with risk_set as
     (select 2
      from   dual)
    select 1
    from   dual;

begin
  null;
end;
/
