--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE TEST_PROCEDURE IS
    --
    CURSOR c_test IS
        SELECT si.sid, sn.name, sa.age, ss.score, sp.parent
        FROM ((((STUDENT_INFO si) INNER JOIN (STUDENT_AGE sa) on si.sid = sa.sid)
            INNER JOIN
            (STUDENT_SCORE ss) on si.sid = sp.sid)
            INNER JOIN
            (STUDENT_PARENT sp) on si.sid = sp.sid)
        WHERE si.sid = '114514';
    --
BEGIN
    --
    --
END EXAMPLE_PROCEDURE;
