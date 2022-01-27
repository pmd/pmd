set define on
set escape off
define schema_name=SYS
define object_name=DBMS_OUTPUT
define directory_name=not_existing
define file_name=not_existing.sql

rem Ampersand in a comment: Copyright Foo,Bar&Baz 2567
rem Backslash in a comment: See c:\doc\bla
/*
rem Ampersand in a comment: Copyright Foo,Bar& Baz 2567
rem Backslash in a comment: See c:\doc\bla
*/
-- Ampersand in a comment: Copyright Foo,Bar&Baz 2567
-- Backslash in a comment: See c:\doc\bla

prompt Backslash in a start command
start c:\not_existing\file\sql
@@c:\not_existing\file\sql
@c:\not_existing\file\sql

prompt Lexical variable in a start command
start &file_name
@&file_name
@&file_name

set escape \
prompt An ampersand directly followed by the text "schema_name": \&schema_name
prompt A windows directory name: C:\\temp - the backslash has to be doubled

prompt Double dot results in SYS.DBMS_OUTPUT
describe &schema_name..&object_name
-- Unfortunately, DESC does not work.
-- Anyway, this is usually only used in interactive sessions, not for linting.
-- desc &schema_name..&object_name

host dir c:\\temp\\*.pdf

rem Unfortunately, the $ and ! SQLPlus*Commands (shortcuts for HOST) do not work:
-- $dir c:\\temp\\*.pdf

prompt Double ampersand or single ampersand to reference a variable in SQL.
select *
from all_errors where owner = '&&schema_name' and name = '&object_name';

set serveroutput on
prompt Double ampersand or single ampersand to reference a variable in PL/SQL.
begin
  &&schema_name..&object_name..put_line('Hello from Foo,Bar\&Baz');
end;
/
set escape off
