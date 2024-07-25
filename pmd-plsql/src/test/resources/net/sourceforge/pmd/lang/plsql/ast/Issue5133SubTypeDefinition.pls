--
-- from https://github.com/pmd/pmd/issues/5133
--

CREATE or REPLACE PACKAGE x as
    type last_run_duration is interval day(9) to second(6);
end x;
/

CREATE TYPE phone_list_typ_demo AS VARRAY(5) OF VARCHAR2(25);
/
