
-- see https://github.com/pmd/pmd/issues/195
-- both define and spool are SQL*Plus commands, and they should not be ended with a semi-colon.

define patch_name = acme_module
spool &patch_name..log
