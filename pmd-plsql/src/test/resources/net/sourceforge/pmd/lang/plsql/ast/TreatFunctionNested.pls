--
-- From https://github.com/utPLSQL/utPLSQL/blob/develop/examples/developer_examples/RunExampleTestAnnotationsParsingTimeHugePackage.sql
-- See https://github.com/pmd/pmd/issues/5675
--

--Shows that even a very large package specification can be parsed quite quickly
--Clear Screen
set serveroutput on
set echo off
--install the example unit test packages
@@tst_pkg_huge.pks

declare
  l_suites ut_suite_items;
  l_items  ut_suite_items;
begin
  l_suites := ut_suite_manager.configure_execution_by_path(ut_varchar2_list(USER||'.TST_PKG_HUGE'));
  l_items := treat(
      treat( treat( l_suites( 1 ) as ut_logical_suite ).items( 1 ) as ut_logical_suite ).items( 1 )
      as ut_logical_suite
  ).items;
  dbms_output.put_line('Created '||l_items.count||' tests in suite');
  dbms_output.put_line('  Last test name='||l_items(l_items.last).name);
end;
/

drop package tst_pkg_huge;
