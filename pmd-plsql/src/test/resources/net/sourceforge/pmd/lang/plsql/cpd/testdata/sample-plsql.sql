CREATE OR REPLACE
PACKAGE "test_schema"."BANK_DATA"
IS
/** 
* ========================================================================<br/>
* Project:         Test Project (<a href="http://pldoc.sourceforge.net">PLDoc</a>)<br/>
* Description:     Testing national character here: äöüõçï<br/>
* DB impact:       YES<br/>
* Commit inside:   NO<br/>
* Rollback inside: NO<br/>
* ------------------------------------------------------------------------<br/>
* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>
* ========================================================================<br/>
* @headcom
* @deprecated Use something <b>new</b> instead.
*/

-- constants
pi		CONSTANT NUMBER := 3.1415;
c 		CONSTANT NUMBER := 3.2e9;
d 		CONSTANT NUMBER := 3.2E9;
year_created	CONSTANT NUMBER := 2001;
author		CONSTANT VARCHAR2(100) := 'altumano ''the wolf''';
date_created 	CONSTANT DATE := '29-oct-01';

/**
* Associative array.
*/
TYPE assc_array IS TABLE OF INTEGER INDEX BY VARCHAR2(30);

/**
* Bank record type.
*/
TYPE bank_type IS RECORD (
  id                         VARCHAR2(20),
  name                       VARCHAR2(100),
  address                    VARCHAR2(105),
  location                   VARCHAR2(35),
  bic                        VARCHAR2(20),
  auth_key                   VARCHAR2(1),
  contact                    VARCHAR2(100),
  phone                      VARCHAR2(50),
  fax                        VARCHAR2(50),
  telex                      VARCHAR2(100),
  medium                     VARCHAR2(255),
  mod_time                   DATE,
  app_user                   VARCHAR2(20),
  db_user                    VARCHAR2(20),
  customer_id                VARCHAR2(20)
);
/**
* Bank table type.
*/
TYPE bank_table IS TABLE OF bank_type INDEX BY BINARY_INTEGER;
/**
* Bank ref cursor.
*/
TYPE ref_type IS REF CURSOR RETURN bank_type;

/**
* Files subtype.
*/
SUBTYPE files_record IS files%ROWTYPE;

-- package-level variables
current_pi	NUMBER := 3.1415;
current_year	NUMBER := 2002;
current_author	VARCHAR2(100) := '\altumano\ `the wolf` äöüõç';
current_date 	DATE := '24-feb-02';

--cursors
cursor cur1(a varchar2, b number, c date, d boolean) return customer%rowtype;
cursor cur2(a varchar2, b number, c date, d boolean) is select * from customer where id = '1';

/** 
* Gets bank record by ID. <br/>
* (<a href="http://pldoc.sourceforge.net">PLDoc</a>)
* @param p_id           bank ID
* @param r_bank_rec     record of type bank_type
* @param r_message      return message
* @return               return code, <b>0 is ok</b> <br/>
*                       &lt;&gt;0 means a problem: <br/>
*                       -1 unknown error <br/>
*                       -2 locking error <br/>
*/
FUNCTION Get (
  p_id              VARCHAR2,
  r_bank_rec        OUT bank_type,
  r_message         OUT VARCHAR2)
RETURN NUMBER;

/** (no comments) */
PROCEDURE Without_Parameters;

/** (no comments) */
FUNCTION Get_Without_Parameters;

/* this compiles, but pldoc cannot handle it !
PROCEDURE function (
  function10 number)
IS
  cursor function is 
    select * from dual;
BEGIN
  for function in 1..100 loop
    null;
  end loop;
END;
*/

-- DEPRECATED
PROCEDURE Get_By_ID (
  p_id              IN VARCHAR2,
  r_records         IN OUT bank_table);

/** 
* Gets bank record by BIC.
* @param p_id           bank BIC
* @param r_bank_rec     record of type bank_type
* @param r_result       return code, 0=ok
* @param r_message      return message
*/
PROCEDURE Get_by_BIC (
  p_bic             VARCHAR2,
  r_bank_rec        OUT bank_type,
  r_result          OUT NUMBER,
  r_message         OUT VARCHAR2);

-- DEPRECATED
PROCEDURE Get_By_BIC (
  p_bic             VARCHAR2,
  r_record          IN OUT bank_type);

/** 
* Gets bank data table (0 or 1 record) by bank ID.
* @param p_id           bank ID,
* second line is also allowed,
* and third, too
* @param r_bank_tab     table of type bank_table
* @param r_result       return code, 0=ok,
-1 error, -2 severe error
* @param r_message      return message
* @return some value
*/
FUNCTION Get_Table (
  p_id              VARCHAR2,
  r_bank_tab        IN OUT bank_table,
  r_result          OUT NUMBER,
  r_message         OUT VARCHAR2)
RETURN varchar2;

/** Search data by given criteria.
* @param p_id           bank ID
* @param p_bic          bank BIC
* @param p_name         name 
* @param p_address      address
* @param p_location     location
* @param r_bank_tab     table of type bank_table
* @param r_result       return code, 0=ok
* @param r_message      return message
*/
PROCEDURE Search (
  p_id              VARCHAR2,
  p_bic             VARCHAR2,
  p_name            VARCHAR2,
  p_address         VARCHAR2,
  p_location        VARCHAR2,
  r_bank_tab        IN OUT bank_table,
  r_result          OUT NUMBER,
  r_message         OUT VARCHAR2);

/** Gets table of banks.
*
* @deprecated Use Get_By_Criteria instead.
*/
PROCEDURE Get (
  p_id              VARCHAR2,
  p_bic             VARCHAR2,
  p_name            VARCHAR2,
  p_address         VARCHAR2,
  p_location        VARCHAR2,
  r_bank_tab        IN OUT bank_table);

/** Search data by given criteria.
* @param p_criteria     record of criteria
* @param r_bank_tab     table of type bank_table
* @param r_result       return code, 0=ok
* @param r_message      return message
*/
PROCEDURE Get_By_Criteria (
  p_criteria        bank_type,
  r_bank_tab        IN OUT bank_table,
  r_result          OUT NUMBER,
  r_message         OUT VARCHAR2);

-- Insert a record
PROCEDURE Ins (
  p_data            IN bank_type);

-- Insert a table
PROCEDURE Ins_Table (
  p_data            IN bank_table);

-- Update a record
PROCEDURE Upd (
  p_data            IN bank_type);

/** Update a table
* @param p_data     new data 
* @throws ORA-20001 if some error was recognized
* @throws ORA-xxxxx if some unexpected error occured
*/
PROCEDURE Upd_Table (
  p_data            IN bank_table);

-- Delete a record
PROCEDURE Del (
  p_data            IN bank_type);

-- Delete a table
PROCEDURE Del_Table (
  p_data            IN bank_table);

-- Lock a record
PROCEDURE Lck (
  p_data            IN bank_type);

-- Lock a table
PROCEDURE Lck_Table (
  p_data            IN bank_table);

-- Get our bank record
PROCEDURE Get_Our (
  r_ourbank         OUT bank_data.bank_type,  -- our bank data
  r_result          OUT NUMBER,               -- operation result (0=OK, <>0 means problem)
  r_message         OUT VARCHAR2);            -- error message:
					      -- 0: ok
					      -- <>0: error

END;
/

CREATE OR REPLACE
PACKAGE Advice_Data
IS
/** 
* ========================================================================<br/>
* Project:         Test Project (<a href="http://pldoc.sourceforge.net">PLDoc</a>)<br/>
* Description:     Advices<br/>
* DB impact:       YES<br/>
* Commit inside:   YES<br/>
* Rollback inside: YES<br/>
* ========================================================================<br/>
* @headcom
*/

SUBTYPE advice_type_record IS advice_type%ROWTYPE;
TYPE advice_type_table IS TABLE OF advice_type_record INDEX BY BINARY_INTEGER;

SUBTYPE advice_medium_record IS advice_medium%ROWTYPE;
TYPE advice_medium_table IS TABLE OF advice_medium_record INDEX BY BINARY_INTEGER;

SUBTYPE advice_record IS advices%ROWTYPE;
TYPE advice_table IS TABLE OF advice_record INDEX BY BINARY_INTEGER;

SUBTYPE sw_advice_record IS sw_advice%ROWTYPE;
TYPE sw_advice_table IS TABLE OF sw_advice_record INDEX BY BINARY_INTEGER;

SUBTYPE files_record IS files%ROWTYPE;
TYPE files_table IS TABLE OF files_record INDEX BY BINARY_INTEGER;

-- Get list of advice types
FUNCTION Get_Advice_Types (
  r_list            OUT advice_type_table)      -- list of advice types
RETURN NUMBER;                                  -- return code, 0=ok, <>0 error

/**
* Gets defaults of an advice type (for given sector).
* @param p_sector           business sector
* @param p_dir              direction (IN/OUT)
* @param p_type             advice type code
* @param def_medium         default medium
* @param def_medium_option  default medium option
* @param def_party          default party
* @param party_fixed        party is fixed (TRUE/FALSE)
* @param r_result           return code, 0=ok
* @param r_message          return message
*/
PROCEDURE Get_Advice_Defaults (
  p_sector          VARCHAR2,
  p_dir             VARCHAR2,
  p_type            VARCHAR2,
  def_medium        OUT VARCHAR2,
  def_medium_option OUT VARCHAR2,
  def_party         OUT VARCHAR2,
  party_fixed       OUT VARCHAR2,
  r_result          OUT NUMBER,
  r_message         OUT VARCHAR2);

-- Get name of an advice type.
-- <Second line of comment>
FUNCTION Get_Advice_Type_Name (
  p_type            VARCHAR2)                   -- advice type code
RETURN VARCHAR2;                                -- advice type name

/** Gets record of an advice medium
* @param p_adv_type         advice type
* @param p_medium           medium
* @param p_medium_option    medium option
* @param r_rec              advice medium record
* @param r_result           return code, 0=ok
* @param r_message          return message
*/
PROCEDURE Get_Advice_Medium (
  p_adv_type        VARCHAR2,
  p_medium          VARCHAR2,
  p_medium_option   VARCHAR2,
  r_rec             IN OUT advice_medium_record,
  r_result          IN OUT NUMBER,
  r_message         IN OUT VARCHAR2);

-- Get advice record
PROCEDURE Get (
  p_contract_id     VARCHAR2,                   -- contract ID
  p_step_seq        NUMBER,                     -- step number
  p_seq             NUMBER,                     -- advice number
  r_rec             IN OUT advice_record,       -- advice record
  r_result          IN OUT NUMBER,              -- return code, 0=ok
  r_message         IN OUT VARCHAR2);           -- return message

-- Get list of advices on contract
PROCEDURE List_Advices (
  p_contract_id     VARCHAR2,                   -- contract ID
  p_step_seq        NUMBER,                     -- step number
  p_in_out          VARCHAR2,                   -- direction
  r_result          IN OUT NUMBER,              -- return code, 0=ok
  r_message         IN OUT VARCHAR2,            -- return message
  r_list            IN OUT advice_table);       -- list of advices

-- Register advice
PROCEDURE Ins (
  p                   IN OUT advice_record,  -- advice data
  r_result            OUT NUMBER,
  r_message           OUT VARCHAR2);

-- Remove advice
PROCEDURE Del (
  p                   IN OUT advice_record,  -- advice data
  r_result            OUT NUMBER,
  r_message           OUT VARCHAR2);

-- Register swift (sub)message
PROCEDURE Ins_SW_Advice (
  p                   IN OUT sw_advice_record,    -- SWIFT advice record
  r_result            OUT NUMBER,
  r_message           OUT VARCHAR2);

-- Get list of SWIFT messages under SWIFT advice
PROCEDURE Get_SW_Advice (
  p_id              VARCHAR2,                   -- SWIFT advice ID
  r_list            IN OUT sw_advice_table,     -- list of messages
  r_result          IN OUT NUMBER,              -- return code, 0=ok
  r_message         IN OUT VARCHAR2);           -- return message

-- Register file (sub)documents
PROCEDURE Ins_File (
  p                   IN OUT files_record,  -- file document record
  r_result            OUT NUMBER,
  r_message           OUT VARCHAR2);

-- Get list of files under the document
PROCEDURE Get_Files (
  p_doc_id          VARCHAR2,                   -- Document ID
  r_list            IN OUT files_table,         -- list of files
  r_result          IN OUT NUMBER,              -- return code, 0=ok
  r_message         IN OUT VARCHAR2);           -- return message

/**
* Clearance for release of outgoing advices.
* @param p_contract_id      contract ID
* @param p_step             step number
* @param p_app_user         application user
* @param r_result           return code, 0=ok
* @param r_message          return message
*/
PROCEDURE CFR_Advice_Out (
  p_contract_id     VARCHAR2,
  p_step            NUMBER,
  p_app_user        VARCHAR2,
  r_result          IN OUT NUMBER,
  r_message         IN OUT VARCHAR2);

/**
* Clearance for release of incoming advices.
* @param p_contract_id      contract ID
* @param p_step             step number
* @param p_app_user         application user
* @param r_result           return code, 0=ok
* @param r_message          return message
*/
PROCEDURE CFR_Advice_In (
  p_contract_id     VARCHAR2,
  p_step            NUMBER,
  p_app_user        VARCHAR2,
  r_result          IN OUT NUMBER,
  r_message         IN OUT VARCHAR2);
  
/**
* Step release of outgoing advices.
* @param p_contract_id      contract ID
* @param p_step             step number
* @param p_app_user         application user
* @param r_result           return code, 0=ok
* @param r_message          return message
*/
PROCEDURE Release_Advice_Out (
  p_contract_id     VARCHAR2,
  p_step            NUMBER,
  p_app_user        VARCHAR2,
  r_result          IN OUT NUMBER,
  r_message         IN OUT VARCHAR2);

/**
* Step release of incoming advices.
* @param p_contract_id      contract ID
* @param p_step             step number
* @param p_app_user         application user
* @param r_result           return code, 0=ok
* @param r_message          return message
*/
PROCEDURE Release_Advice_In (
  p_contract_id     VARCHAR2,
  p_step            NUMBER,
  p_app_user        VARCHAR2,
  r_result          IN OUT NUMBER,
  r_message         IN OUT VARCHAR2);

END;
/


CREATE OR REPLACE
PACKAGE CUSTOMER_DATA
IS
/** 
* ========================================================================<br/>
* Project:         Test Project (<a href="http://pldoc.sourceforge.net">PLDoc</a>)<br/>
* Description:     Customer Data Management<br/>
* DB impact:       YES<br/>
* Commit inside:   NO<br/>
* Rollback inside: NO<br/>
* ------------------------------------------------------------------------<br/>
* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>
* ========================================================================<br/>
* @headcom
*/

record_locked EXCEPTION;

TYPE customer_type IS RECORD (
  id                        VARCHAR2(20),
  name                      VARCHAR2(100),
  short_name                VARCHAR2(35),
  db_id                     VARCHAR2(20),
  sub_cust_code             VARCHAR2(20),
  sub_account               VARCHAR2(30),
  regno                     VARCHAR2(50),
  residence                 VARCHAR2(10),
  ct_type                   VARCHAR2(10),
  ct_entity                 VARCHAR2(10),
  language                  VARCHAR2(10),
  business_type             VARCHAR2(10),
  tax_code                  VARCHAR2(10)
);
TYPE customer_table IS TABLE OF customer_type INDEX BY BINARY_INTEGER;

SUBTYPE loan_customer_type IS loan_customers%ROWTYPE;
TYPE loan_customer_table IS TABLE OF loan_customer_type INDEX BY BINARY_INTEGER;

-- Get data by id
PROCEDURE Get_Record (
  p_id              VARCHAR2,
  r                 OUT customer_type,
  r_result          IN OUT NUMBER,
  r_message         IN OUT VARCHAR2);

-- Get data by id
PROCEDURE Get_By_Id (
  p_id              IN VARCHAR2,
  r_records         IN OUT customer_table);

-- Search data
PROCEDURE Get_By_Criteria (
  p_criteria        IN customer_type,
  r_records         IN OUT customer_table);

-- Search data
PROCEDURE Get (
  p_id              IN VARCHAR2,
  p_name            IN VARCHAR2,
  p_short_name      IN VARCHAR2,
  p_cust_code       IN VARCHAR2,
  p_account         IN VARCHAR2,
  p_regno           IN VARCHAR2,
  r_records         IN OUT customer_table);

-- Search customer by account number
PROCEDURE Search_By_Account (
  p_account         VARCHAR2,             -- account number
  r_record          IN OUT customer_type, -- found customer record
  r_result          OUT NUMBER,           -- result code (0=OK)
  r_message         OUT VARCHAR2);        -- error  message

-- Insert a record
PROCEDURE Ins (
  p_data            IN customer_table);

-- Update a record
PROCEDURE Upd (
  p_data            IN customer_table);

-- Delete a record
PROCEDURE Del (
  p_data            IN customer_table);

-- Lock a record
PROCEDURE Lck (
  p_data            IN customer_table);

/**
* Get loan-related customer data by customer ID.
* @param p_id               customer ID
* @param r                  loan-related customer data
* @param r_result           result (0=ok)
* @param r_message          error message
*/
PROCEDURE Get_Loan_Customer (
  p_id              VARCHAR2,
  r                 OUT loan_customer_type,
  r_result          OUT NUMBER,
  r_message         OUT VARCHAR2);

END;
/

CREATE OR REPLACE
Package    EXEC_SP 
IS
/** 
* ========================================================================<br/>
* Project:         Test<br/>
* Description:     Executes stored procedure<br/>
* DB impact:       NO<br/>
* Commit inside:   NO<br/>
* Rollback inside: NO<br/>
* ------------------------------------------------------------------------<br/>
* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>
* ========================================================================<br/>
* @headcom
*/

DATEFORMAT constant VARCHAR2(100) := 'dd.mm.yyyy hh24:mi:ss';
TYPE string_array IS TABLE OF VARCHAR2(32000) INDEX BY BINARY_INTEGER;

PROCEDURE Exec_SP (
  sp_name     VARCHAR2,
  sp_package  VARCHAR2,
  sp_schema   VARCHAR2,
  sp_type     VARCHAR2,             -- 'PROCEDURE' of 'FUNCTION'
  arg_names   IN OUT string_array,  -- use RETURN for function return value
  arg_types   IN OUT string_array,  -- 'VARCHAR2', 'NUMBER', 'DATE' ('dd.mm.yyyy hh24:mi:ss')
  arg_pass    IN OUT string_array,  -- 'IN', 'OUT', 'IN OUT'
  arg_values  IN OUT string_array,
  error_code  OUT NUMBER,           -- 0 ok
  error_msg   OUT VARCHAR2);
  
END;
/

CREATE OR REPLACE
PACKAGE LOBS_DATA
IS
/** 
* ========================================================================<br/>
* Project:         Test Project (<a href="http://pldoc.sourceforge.net">PLDoc</a>)<br/><br/>
* Description:     Large Objects<br/>
* DB impact:       NO<br/>
* Commit inside:   NO<br/>
* Rollback inside: NO<br/>
* ------------------------------------------------------------------------<br/>
* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>
* ========================================================================<br/>
* @headcom
*/

-- Storing a large object; returns ID
FUNCTION put(
  p_value       IN VARCHAR2)
RETURN NUMBER;

-- Loading a large object by ID
FUNCTION get(
  p_id          IN VARCHAR2)
RETURN VARCHAR2;

-- Remove a stored object
PROCEDURE remove(
  p_id          IN VARCHAR2);

FUNCTION HH_get_info 
( erty_id_in IN HH_t.a_id%TYPE 	DEFAULT NULL
, df_id_in   IN HH_t.b_id%TYPE  DEFAULT NULL
, fghj_id_in IN HH_t.c_id%TYPE 	DEFAULT vk_asdgfh_pa.some_function() 
, cascade_in IN NUMBER          DEFAULT vk_asdgfh_pa.some_constant 
)
RETURN vk_types_pa.type_rg_info_rec;

PROCEDURE start_batch_job (p_interval IN dba_jobs.interval%TYPE);

procedure out(cursor VARCHAR2);

PROCEDURE refresh_all(kehtib date default last_day(add_months(trunc(sysdate),1))+1);

END;--the end

