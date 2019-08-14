CREATE OR REPLACE PACKAGE BODY trim_test_package IS

  PROCEDURE trim_test_procedure  IS
  BEGIN
    IF TRIM(var_package.wer.wer) = 'TEST' THEN
      NULL;
    END IF;
  END;

END trim_test_package;
/
