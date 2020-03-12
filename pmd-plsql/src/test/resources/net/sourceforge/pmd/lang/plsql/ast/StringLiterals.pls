--
-- See https://github.com/pmd/pmd/issues/2008
-- [plsql] In StringLiteral using alternative quoting mechanism single quotes cause parsing errors
--
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/sqlrf/Literals.html#GUID-1824CBAA-6E16-4921-B2A6-112FB02248DA
--

declare

  literal1 clob := 'Hello';
  literal2 clob := 'ORACLE.dbs';
  literal3 clob := 'Jackie''s raincoat';
  literal4 clob := '09-MAR-98';
  literal5 clob := N'nchar literal';

  -- alternative quoting mechanism
  qliteral1a clob := q'[abc]';
  qliteral1b clob := q'[ab']cd]';
  qliteral1c clob := q'[ab[cd]';
  qliteral1d clob := q'[ab]cd]';
  qliteral1e clob := q'[ab
  cd]';
  qliteral1f clob := Nq'[a"b"c]';
  qliteral1g clob := nQ'[ab']cd]';

  qliteral2a clob := q'!name LIKE '%DBMS_%%'!';
  qliteral2b clob := Q'{SELECT * FROM employees WHERE last_name = 'Smith';}';
  qliteral2c clob := q'! test !';
  qliteral2d clob := q'{
    also multiple
    lines
  }';
  qliteral3a clob := q'% test abc %';
  qliteral3b clob := q'% test % abc %';


  qliteral3c clob := q'% test'test %';
  qliteral4 clob := nq'!name LIKE '%DBMS_%%'!';



begin
  null;
end;
/
