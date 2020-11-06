--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

-- FIXED records
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (RECORDS FIXED 20 FIELDS (first_name CHAR(7),
                                                                    last_name CHAR(8),
                                                                    year_of_birth CHAR(4)))
                         LOCATION ('info.dat'));

-- VARIABLE records
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (RECORDS VARIABLE 2 FIELDS TERMINATED BY ','
                                             (first_name CHAR(7),
                                              last_name CHAR(8),
                                              year_of_birth CHAR(4)))
                         LOCATION ('info.dat'));

-- DELIMITED BY records
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (RECORDS DELIMITED BY '|' FIELDS TERMINATED BY ','
                                              (first_name CHAR(7),
                                               last_name CHAR(8),
                                               year_of_birth CHAR(4)))
                         LOCATION ('info.dat'));

-- without any access parameters
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
 ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir LOCATION ('info.dat'));

-- External Table with Terminating Delimiters
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (FIELDS TERMINATED BY WHITESPACE)
                         LOCATION ('info.dat'));

-- External Table with Enclosure and Terminator Delimiters
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                        ACCESS PARAMETERS (FIELDS TERMINATED BY "," ENCLOSED BY "("  AND ")")
                        LOCATION ('info.dat'));

-- Example: External Table with Optional Enclosure Delimiters
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (FIELDS TERMINATED BY ','
                                            OPTIONALLY ENCLOSED BY '(' and ')'
                                            LRTRIM)
                         LOCATION ('info.dat'));

-- all data is fixed-length
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20),
year_of_birth CHAR(4))
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (FIELDS LTRIM)
                         LOCATION ('info.dat'));

-- MISSING FIELD VALUES ARE NULL
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth INT)
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (FIELDS TERMINATED BY ","
                                            MISSING FIELD VALUES ARE NULL)
                         LOCATION ('info.dat'));

-- external table with no field_list and a delim_spec
CREATE TABLE emp_load (first_name CHAR(15), last_name CHAR(20), year_of_birth INT)
  ORGANIZATION EXTERNAL (TYPE ORACLE_LOADER DEFAULT DIRECTORY ext_tab_dir
                         ACCESS PARAMETERS (FIELDS TERMINATED BY "|")
                         LOCATION ('info.dat'));

-- various ways of using pos_spec
CREATE TABLE emp_load (first_name CHAR(15),
                      last_name CHAR(20),
                      year_of_birth INT,
                      phone CHAR(12),
                      area_code CHAR(3),
                      exchange CHAR(3),
                      extension CHAR(4))
  ORGANIZATION EXTERNAL
  (TYPE ORACLE_LOADER
   DEFAULT DIRECTORY ext_tab_dir
   ACCESS PARAMETERS
     (FIELDS RTRIM
            (first_name (1:15) CHAR(15),
             last_name (*:+20),
             year_of_birth (36:39),
             phone (40:52),
             area_code (*-12: +3),
             exchange (*+1: +3),
             extension (*+1: +4)))
   LOCATION ('info.dat'));

-- Use of the CHAR clause.
CREATE TABLE emp_load
      (employee_number      CHAR(5),
       employee_dob         CHAR(20),
       employee_last_name   CHAR(20),
       employee_first_name  CHAR(15),
       employee_middle_name CHAR(15),
       employee_hire_date   DATE)
  ORGANIZATION EXTERNAL
    (TYPE ORACLE_LOADER
      DEFAULT DIRECTORY def_dir1
      ACCESS PARAMETERS
        (RECORDS DELIMITED BY NEWLINE
         FIELDS (employee_number      CHAR(2),
                 employee_dob         CHAR(20),
                 employee_last_name   CHAR(18),
                 employee_first_name  CHAR(11),
                 employee_middle_name CHAR(11),
                 employee_hire_date   CHAR(10) date_format DATE mask "mm/dd/yyyy"
                )
        )
      LOCATION ('info.dat')
    );

-- Use of a complex DATE character string and a TIMESTAMP character string
CREATE TABLE emp_load
  (employee_number      CHAR(5),
   employee_dob         CHAR(20),
   employee_last_name   CHAR(20),
   employee_first_name  CHAR(15),
   employee_middle_name CHAR(15),
   employee_hire_date   DATE,
   rec_creation_date    TIMESTAMP WITH TIME ZONE)
ORGANIZATION EXTERNAL
  (TYPE ORACLE_LOADER
   DEFAULT DIRECTORY def_dir1
   ACCESS PARAMETERS
     (RECORDS DELIMITED BY NEWLINE
      FIELDS (employee_number      CHAR(2),
              employee_dob         CHAR(20),
              employee_last_name   CHAR(18),
              employee_first_name  CHAR(11),
              employee_middle_name CHAR(11),
              employee_hire_date   CHAR(22) date_format DATE mask "mm/dd/yyyy hh:mi:ss AM",
              rec_creation_date    CHAR(35) date_format TIMESTAMP WITH TIMEZONE mask "DD-MON-RR HH.MI.SSXFF AM TZH:TZM"
             )
     )
   LOCATION ('infoc.dat')
  );

-- Uses of VARCHAR and VARRAW
CREATE TABLE emp_load
             (first_name CHAR(15),
              last_name CHAR(20),
              resume CHAR(2000),
              picture RAW(2000))
  ORGANIZATION EXTERNAL
  (TYPE ORACLE_LOADER
   DEFAULT DIRECTORY ext_tab_dir
   ACCESS PARAMETERS
     (RECORDS
        VARIABLE 2
        DATA IS BIG ENDIAN
        CHARACTERSET US7ASCII
      FIELDS (first_name VARCHAR(2,12),
              last_name VARCHAR(2,20),
              resume VARCHAR(4,10000),
              picture VARRAW(4,100000)))
    LOCATION ('info.dat'));

-- Uses of VARCHARC and VARRAWC
CREATE TABLE emp_load
             (first_name CHAR(15),
              last_name CHAR(20),
              resume CHAR(2000),
              picture RAW (2000))
  ORGANIZATION EXTERNAL
  (TYPE ORACLE_LOADER
    DEFAULT DIRECTORY ext_tab_dir
    ACCESS PARAMETERS
      (FIELDS (first_name VARCHARC(5,12),
               last_name VARCHARC(2,20),
               resume VARCHARC(4,10000),
               picture VARRAWC(4,100000)))
  LOCATION ('info.dat'));

CREATE TABLE lob_tab (
  colid        NUMBER(10),
  clob_content CLOB
)
ORGANIZATION EXTERNAL
(
  TYPE ORACLE_LOADER
  DEFAULT DIRECTORY temp_dir
  ACCESS PARAMETERS
  (
    RECORDS DELIMITED BY NEWLINE
    BADFILE temp_dir:'data.bad'
    LOGFILE temp_dir:'data.log'
    FIELDS TERMINATED BY ','
    MISSING FIELD VALUES ARE NULL
    (
      colid             CHAR(10),
      clob_filename     CHAR(100)
    )
    COLUMN TRANSFORMS (clob_content FROM LOBFILE (clob_filename) FROM (temp_dir) CLOB)
  )
  LOCATION ('data.txt')
)
--PARALLEL 2
REJECT LIMIT UNLIMITED;

CREATE TABLE dept_external (
   deptno     NUMBER(6),
   dname      VARCHAR2(20),
   loc        VARCHAR2(25)
)
ORGANIZATION EXTERNAL
(TYPE ORACLE_LOADER
 DEFAULT DIRECTORY admin
 ACCESS PARAMETERS
 (
  RECORDS DELIMITED BY newline
  BADFILE 'ulcase1.bad'
  DISCARDFILE 'ulcase1.dis'
  LOGFILE 'ulcase1.log'
  SKIP 20
  FIELDS TERMINATED BY ","  OPTIONALLY ENCLOSED BY '"'
  (
   deptno     INTEGER EXTERNAL(6),
   dname      CHAR(20),
   loc        CHAR(25)
  )
 )
 LOCATION ('ulcase1.ctl')
)
REJECT LIMIT UNLIMITED;

CREATE TABLE countries_demo
    ( country_id      CHAR(2)
    , country_name    VARCHAR2(40)
    , currency_name   VARCHAR2(25)
    , currency_symbol VARCHAR2(3)
    , region          VARCHAR2(15) )
    ORGANIZATION INDEX
    STORAGE
     ( INITIAL  4K )
    PCTTHRESHOLD 2
    INCLUDING   country_name
   OVERFLOW
    STORAGE
      ( INITIAL  4K );

create table toys_heap (
  toy_name varchar2(100)
) organization heap;
