--
-- from https://github.com/pmd/pmd/issues/5133
--

CREATE or REPLACE PACKAGE x as
    type last_run_duration is interval day(9) to second(6);
end x;
/
