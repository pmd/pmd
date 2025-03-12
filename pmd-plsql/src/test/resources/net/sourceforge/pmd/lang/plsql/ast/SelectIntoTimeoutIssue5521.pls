--
-- https://github.com/Qualtagh/OracleDBUtils/blob/master/p_utils_tests.sql
--

create or replace procedure assert_eq( tActual in varchar2, tExpected in varchar2 ) is
  tExpectedReplaced varchar2( 4000 ) := replace( tExpected, '<USER>', user );
begin
  if tActual = tExpectedReplaced or tActual is null and tExpectedReplaced is null then
    return;
  end if;
  raise_application_error( -20001, 'assertion failed: ' || chr( 10 ) || tActual || chr( 10 ) || tExpectedReplaced );
end;
/
declare
  tDepartments str_table;
  tNames str_table;
begin
  with EMPLOYEES as (
    select 'Sales' as DEPARTMENT, 'John' as NAME, 'Butler' as SURNAME from dual union all
    select 'Sales', 'John', 'Kelly' from dual union all
    select 'Sales', 'Jane', 'Kelly' from dual union all
    select 'Devs', 'Ruth', 'Ostin' from dual union all
    select 'Devs', 'Gareth', 'Pink' from dual union all
    select 'Devs', 'Cli''igan', 'Moorney' from dual union all
    select 'Devs', 'Ruth', 'Zack' from dual
  )
  select DEPARTMENT,
         dbms_xmlgen.convert(
           substr(
             replace(
               replace(
                 p_utils.distinguishXML(
                   XMLAgg(
                     XMLElement( "elem", NAME )
                     order by SURNAME
                   )
                 ).getStringVal(), '</elem>'
               ), '<elem>', ', '
             ), 3
           ), 1
         ) as NAMES
  bulk collect into tDepartments, tNames
  from EMPLOYEES
  group by DEPARTMENT
  order by DEPARTMENT;
  assert_eq( tDepartments.count, 2 );
  assert_eq( tDepartments( 1 ), 'Devs' );
  assert_eq( tDepartments( 2 ), 'Sales' );
  assert_eq( tNames( 1 ), 'Cli''igan, Ruth, Gareth' );
  assert_eq( tNames( 2 ), 'John, Jane' );
end;
/
drop procedure assert_eq;
