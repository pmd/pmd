--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE bar
IS
    v_link varchar2(10) := 'xxx';
BEGIN
    -- EXECUTE IMMEDIATE with an expression instead of just a literal or variable.
    EXECUTE IMMEDIATE 'drop database link ' || v_link;
END bar;
