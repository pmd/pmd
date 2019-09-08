create or replace package body trim_test_package is

  procedure trim_test_procedure is
  begin
    if trim(var_package.record.field) = 'TEST' then
      null;
    end if;
  end;

end trim_test_package;
/
