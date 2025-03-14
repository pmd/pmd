BEGIN

UPDATE references REF
      SET REF.INST_CITY = 'Munich';

UPDATE references REF
      SET REF.INST_CITY = DEFAULT;

UPDATE references REF
      SET (REF.INST_CITY) = (SELECT 'Munich' FROM DUAL);

END;
