create or replace package body test is

  function test1() return clob sql_macro is
  begin

    return 'q[select * from dual]';

  end test1;

  function test2() return clob sql_macro(TYPE => SCALAR) is
  begin

    return 'q[select * from dual]';

  end test2;

  function test3() return clob sql_macro(SCALAR) is
  begin

    return 'q[select * from dual]';

  end test3;

  function test4() return clob sql_macro(TABLE) is
  begin

    return 'q[select * from dual]';

  end test4;

  function test5() return int  is
    cursor c is select 1 from test.test1();
  begin

    return 1;
  end test5;

end test;
