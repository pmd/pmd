package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.PLSQLTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

import org.junit.Before;
import org.junit.Test;



public class PLSQLTokenizerTest extends AbstractTokenizerTest {

	@Before
	@Override
	public void buildTokenizer() {
		this.tokenizer = new PLSQLTokenizer();
		this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), "server.rb"));
	}

	@Override
	public String getSampleCode() {
		 return 
	"CREATE OR REPLACE" + PMD.EOL +
	"PACKAGE \"test_schema\".\"BANK_DATA\"" + PMD.EOL +
	"IS" + PMD.EOL +
	"/** " + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* Project:         Test Project (<a href=\"http://pldoc.sourceforge.net\">PLDoc</a>)<br/>" + PMD.EOL +
	"* Description:     Testing national character here: äöüõçï<br/>" + PMD.EOL +
	"* DB impact:       YES<br/>" + PMD.EOL +
	"* Commit inside:   NO<br/>" + PMD.EOL +
	"* Rollback inside: NO<br/>" + PMD.EOL +
	"* ------------------------------------------------------------------------<br/>" + PMD.EOL +
	"* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>" + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* @headcom" + PMD.EOL +
	"* @deprecated Use something <b>new</b> instead." + PMD.EOL +
	"*/" + PMD.EOL +
	"" + PMD.EOL +
	"-- constants" + PMD.EOL +
	"pi		CONSTANT NUMBER := 3.1415;" + PMD.EOL +
	"c 		CONSTANT NUMBER := 3.2e9;" + PMD.EOL +
	"d 		CONSTANT NUMBER := 3.2E9;" + PMD.EOL +
	"year_created	CONSTANT NUMBER := 2001;" + PMD.EOL +
	"author		CONSTANT VARCHAR2(100) := 'altumano ''the wolf''';" + PMD.EOL +
	"date_created 	CONSTANT DATE := '29-oct-01';" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Associative array." + PMD.EOL +
	"*/" + PMD.EOL +
	"TYPE assc_array IS TABLE OF INTEGER INDEX BY VARCHAR2(30);" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Bank record type." + PMD.EOL +
	"*/" + PMD.EOL +
	"TYPE bank_type IS RECORD (" + PMD.EOL +
	"  id                         VARCHAR2(20)," + PMD.EOL +
	"  name                       VARCHAR2(100)," + PMD.EOL +
	"  address                    VARCHAR2(105)," + PMD.EOL +
	"  location                   VARCHAR2(35)," + PMD.EOL +
	"  bic                        VARCHAR2(20)," + PMD.EOL +
	"  auth_key                   VARCHAR2(1)," + PMD.EOL +
	"  contact                    VARCHAR2(100)," + PMD.EOL +
	"  phone                      VARCHAR2(50)," + PMD.EOL +
	"  fax                        VARCHAR2(50)," + PMD.EOL +
	"  telex                      VARCHAR2(100)," + PMD.EOL +
	"  medium                     VARCHAR2(255)," + PMD.EOL +
	"  mod_time                   DATE," + PMD.EOL +
	"  app_user                   VARCHAR2(20)," + PMD.EOL +
	"  db_user                    VARCHAR2(20)," + PMD.EOL +
	"  customer_id                VARCHAR2(20)" + PMD.EOL +
	");" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Bank table type." + PMD.EOL +
	"*/" + PMD.EOL +
	"TYPE bank_table IS TABLE OF bank_type INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Bank ref cursor." + PMD.EOL +
	"*/" + PMD.EOL +
	"TYPE ref_type IS REF CURSOR RETURN bank_type;" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Files subtype." + PMD.EOL +
	"*/" + PMD.EOL +
	"SUBTYPE files_record IS files%ROWTYPE;" + PMD.EOL +
	"" + PMD.EOL +
	"-- package-level variables" + PMD.EOL +
	"current_pi	NUMBER := 3.1415;" + PMD.EOL +
	"current_year	NUMBER := 2002;" + PMD.EOL +
	"current_author	VARCHAR2(100) := '\\altumano\\ `the wolf` äöüõç';" + PMD.EOL +
	"current_date 	DATE := '24-feb-02';" + PMD.EOL +
	"" + PMD.EOL +
	"--cursors" + PMD.EOL +
	"cursor cur1(a varchar2, b number, c date, d boolean) return customer%rowtype;" + PMD.EOL +
	"cursor cur2(a varchar2, b number, c date, d boolean) is select * from customer where id = '1';" + PMD.EOL +
	"" + PMD.EOL +
	"/** " + PMD.EOL +
	"* Gets bank record by ID. <br/>" + PMD.EOL +
	"* (<a href=\"http://pldoc.sourceforge.net\">PLDoc</a>)" + PMD.EOL +
	"* @param p_id           bank ID" + PMD.EOL +
	"* @param r_bank_rec     record of type bank_type" + PMD.EOL +
	"* @param r_message      return message" + PMD.EOL +
	"* @return               return code, <b>0 is ok</b> <br/>" + PMD.EOL +
	"*                       &lt;&gt;0 means a problem: <br/>" + PMD.EOL +
	"*                       -1 unknown error <br/>" + PMD.EOL +
	"*                       -2 locking error <br/>" + PMD.EOL +
	"*/" + PMD.EOL +
	"FUNCTION Get (" + PMD.EOL +
	"  p_id              VARCHAR2," + PMD.EOL +
	"  r_bank_rec        OUT bank_type," + PMD.EOL +
	"  r_message         OUT VARCHAR2)" + PMD.EOL +
	"RETURN NUMBER;" + PMD.EOL +
	"" + PMD.EOL +
	"/** (no comments) */" + PMD.EOL +
	"PROCEDURE Without_Parameters;" + PMD.EOL +
	"" + PMD.EOL +
	"/** (no comments) */" + PMD.EOL +
	"FUNCTION Get_Without_Parameters;" + PMD.EOL +
	"" + PMD.EOL +
	"/* this compiles, but pldoc cannot handle it !" + PMD.EOL +
	"PROCEDURE function (" + PMD.EOL +
	"  function10 number)" + PMD.EOL +
	"IS" + PMD.EOL +
	"  cursor function is " + PMD.EOL +
	"    select * from dual;" + PMD.EOL +
	"BEGIN" + PMD.EOL +
	"  for function in 1..100 loop" + PMD.EOL +
	"    null;" + PMD.EOL +
	"  end loop;" + PMD.EOL +
	"END;" + PMD.EOL +
	"*/" + PMD.EOL +
	"" + PMD.EOL +
	"-- DEPRECATED" + PMD.EOL +
	"PROCEDURE Get_By_ID (" + PMD.EOL +
	"  p_id              IN VARCHAR2," + PMD.EOL +
	"  r_records         IN OUT bank_table);" + PMD.EOL +
	"" + PMD.EOL +
	"/** " + PMD.EOL +
	"* Gets bank record by BIC." + PMD.EOL +
	"* @param p_id           bank BIC" + PMD.EOL +
	"* @param r_bank_rec     record of type bank_type" + PMD.EOL +
	"* @param r_result       return code, 0=ok" + PMD.EOL +
	"* @param r_message      return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Get_by_BIC (" + PMD.EOL +
	"  p_bic             VARCHAR2," + PMD.EOL +
	"  r_bank_rec        OUT bank_type," + PMD.EOL +
	"  r_result          OUT NUMBER," + PMD.EOL +
	"  r_message         OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- DEPRECATED" + PMD.EOL +
	"PROCEDURE Get_By_BIC (" + PMD.EOL +
	"  p_bic             VARCHAR2," + PMD.EOL +
	"  r_record          IN OUT bank_type);" + PMD.EOL +
	"" + PMD.EOL +
	"/** " + PMD.EOL +
	"* Gets bank data table (0 or 1 record) by bank ID." + PMD.EOL +
	"* @param p_id           bank ID," + PMD.EOL +
	"* second line is also allowed," + PMD.EOL +
	"* and third, too" + PMD.EOL +
	"* @param r_bank_tab     table of type bank_table" + PMD.EOL +
	"* @param r_result       return code, 0=ok," + PMD.EOL +
	"-1 error, -2 severe error" + PMD.EOL +
	"* @param r_message      return message" + PMD.EOL +
	"* @return some value" + PMD.EOL +
	"*/" + PMD.EOL +
	"FUNCTION Get_Table (" + PMD.EOL +
	"  p_id              VARCHAR2," + PMD.EOL +
	"  r_bank_tab        IN OUT bank_table," + PMD.EOL +
	"  r_result          OUT NUMBER," + PMD.EOL +
	"  r_message         OUT VARCHAR2)" + PMD.EOL +
	"RETURN varchar2;" + PMD.EOL +
	"" + PMD.EOL +
	"/** Search data by given criteria." + PMD.EOL +
	"* @param p_id           bank ID" + PMD.EOL +
	"* @param p_bic          bank BIC" + PMD.EOL +
	"* @param p_name         name " + PMD.EOL +
	"* @param p_address      address" + PMD.EOL +
	"* @param p_location     location" + PMD.EOL +
	"* @param r_bank_tab     table of type bank_table" + PMD.EOL +
	"* @param r_result       return code, 0=ok" + PMD.EOL +
	"* @param r_message      return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Search (" + PMD.EOL +
	"  p_id              VARCHAR2," + PMD.EOL +
	"  p_bic             VARCHAR2," + PMD.EOL +
	"  p_name            VARCHAR2," + PMD.EOL +
	"  p_address         VARCHAR2," + PMD.EOL +
	"  p_location        VARCHAR2," + PMD.EOL +
	"  r_bank_tab        IN OUT bank_table," + PMD.EOL +
	"  r_result          OUT NUMBER," + PMD.EOL +
	"  r_message         OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"/** Gets table of banks." + PMD.EOL +
	"*" + PMD.EOL +
	"* @deprecated Use Get_By_Criteria instead." + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Get (" + PMD.EOL +
	"  p_id              VARCHAR2," + PMD.EOL +
	"  p_bic             VARCHAR2," + PMD.EOL +
	"  p_name            VARCHAR2," + PMD.EOL +
	"  p_address         VARCHAR2," + PMD.EOL +
	"  p_location        VARCHAR2," + PMD.EOL +
	"  r_bank_tab        IN OUT bank_table);" + PMD.EOL +
	"" + PMD.EOL +
	"/** Search data by given criteria." + PMD.EOL +
	"* @param p_criteria     record of criteria" + PMD.EOL +
	"* @param r_bank_tab     table of type bank_table" + PMD.EOL +
	"* @param r_result       return code, 0=ok" + PMD.EOL +
	"* @param r_message      return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Get_By_Criteria (" + PMD.EOL +
	"  p_criteria        bank_type," + PMD.EOL +
	"  r_bank_tab        IN OUT bank_table," + PMD.EOL +
	"  r_result          OUT NUMBER," + PMD.EOL +
	"  r_message         OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Insert a record" + PMD.EOL +
	"PROCEDURE Ins (" + PMD.EOL +
	"  p_data            IN bank_type);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Insert a table" + PMD.EOL +
	"PROCEDURE Ins_Table (" + PMD.EOL +
	"  p_data            IN bank_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Update a record" + PMD.EOL +
	"PROCEDURE Upd (" + PMD.EOL +
	"  p_data            IN bank_type);" + PMD.EOL +
	"" + PMD.EOL +
	"/** Update a table" + PMD.EOL +
	"* @param p_data     new data " + PMD.EOL +
	"* @throws ORA-20001 if some error was recognized" + PMD.EOL +
	"* @throws ORA-xxxxx if some unexpected error occured" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Upd_Table (" + PMD.EOL +
	"  p_data            IN bank_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Delete a record" + PMD.EOL +
	"PROCEDURE Del (" + PMD.EOL +
	"  p_data            IN bank_type);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Delete a table" + PMD.EOL +
	"PROCEDURE Del_Table (" + PMD.EOL +
	"  p_data            IN bank_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Lock a record" + PMD.EOL +
	"PROCEDURE Lck (" + PMD.EOL +
	"  p_data            IN bank_type);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Lock a table" + PMD.EOL +
	"PROCEDURE Lck_Table (" + PMD.EOL +
	"  p_data            IN bank_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get our bank record" + PMD.EOL +
	"PROCEDURE Get_Our (" + PMD.EOL +
	"  r_ourbank         OUT bank_data.bank_type,  -- our bank data" + PMD.EOL +
	"  r_result          OUT NUMBER,               -- operation result (0=OK, <>0 means problem)" + PMD.EOL +
	"  r_message         OUT VARCHAR2);            -- error message:" + PMD.EOL +
	"					      -- 0: ok" + PMD.EOL +
	"					      -- <>0: error" + PMD.EOL +
	"" + PMD.EOL +
	"END;" + PMD.EOL +
	"/" + PMD.EOL +
	"" + PMD.EOL +
	"CREATE OR REPLACE" + PMD.EOL +
	"PACKAGE Advice_Data" + PMD.EOL +
	"IS" + PMD.EOL +
	"/** " + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* Project:         Test Project (<a href=\"http://pldoc.sourceforge.net\">PLDoc</a>)<br/>" + PMD.EOL +
	"* Description:     Advices<br/>" + PMD.EOL +
	"* DB impact:       YES<br/>" + PMD.EOL +
	"* Commit inside:   YES<br/>" + PMD.EOL +
	"* Rollback inside: YES<br/>" + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* @headcom" + PMD.EOL +
	"*/" + PMD.EOL +
	"" + PMD.EOL +
	"SUBTYPE advice_type_record IS advice_type%ROWTYPE;" + PMD.EOL +
	"TYPE advice_type_table IS TABLE OF advice_type_record INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"SUBTYPE advice_medium_record IS advice_medium%ROWTYPE;" + PMD.EOL +
	"TYPE advice_medium_table IS TABLE OF advice_medium_record INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"SUBTYPE advice_record IS advices%ROWTYPE;" + PMD.EOL +
	"TYPE advice_table IS TABLE OF advice_record INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"SUBTYPE sw_advice_record IS sw_advice%ROWTYPE;" + PMD.EOL +
	"TYPE sw_advice_table IS TABLE OF sw_advice_record INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"SUBTYPE files_record IS files%ROWTYPE;" + PMD.EOL +
	"TYPE files_table IS TABLE OF files_record INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get list of advice types" + PMD.EOL +
	"FUNCTION Get_Advice_Types (" + PMD.EOL +
	"  r_list            OUT advice_type_table)      -- list of advice types" + PMD.EOL +
	"RETURN NUMBER;                                  -- return code, 0=ok, <>0 error" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Gets defaults of an advice type (for given sector)." + PMD.EOL +
	"* @param p_sector           business sector" + PMD.EOL +
	"* @param p_dir              direction (IN/OUT)" + PMD.EOL +
	"* @param p_type             advice type code" + PMD.EOL +
	"* @param def_medium         default medium" + PMD.EOL +
	"* @param def_medium_option  default medium option" + PMD.EOL +
	"* @param def_party          default party" + PMD.EOL +
	"* @param party_fixed        party is fixed (TRUE/FALSE)" + PMD.EOL +
	"* @param r_result           return code, 0=ok" + PMD.EOL +
	"* @param r_message          return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Get_Advice_Defaults (" + PMD.EOL +
	"  p_sector          VARCHAR2," + PMD.EOL +
	"  p_dir             VARCHAR2," + PMD.EOL +
	"  p_type            VARCHAR2," + PMD.EOL +
	"  def_medium        OUT VARCHAR2," + PMD.EOL +
	"  def_medium_option OUT VARCHAR2," + PMD.EOL +
	"  def_party         OUT VARCHAR2," + PMD.EOL +
	"  party_fixed       OUT VARCHAR2," + PMD.EOL +
	"  r_result          OUT NUMBER," + PMD.EOL +
	"  r_message         OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get name of an advice type." + PMD.EOL +
	"-- <Second line of comment>" + PMD.EOL +
	"FUNCTION Get_Advice_Type_Name (" + PMD.EOL +
	"  p_type            VARCHAR2)                   -- advice type code" + PMD.EOL +
	"RETURN VARCHAR2;                                -- advice type name" + PMD.EOL +
	"" + PMD.EOL +
	"/** Gets record of an advice medium" + PMD.EOL +
	"* @param p_adv_type         advice type" + PMD.EOL +
	"* @param p_medium           medium" + PMD.EOL +
	"* @param p_medium_option    medium option" + PMD.EOL +
	"* @param r_rec              advice medium record" + PMD.EOL +
	"* @param r_result           return code, 0=ok" + PMD.EOL +
	"* @param r_message          return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Get_Advice_Medium (" + PMD.EOL +
	"  p_adv_type        VARCHAR2," + PMD.EOL +
	"  p_medium          VARCHAR2," + PMD.EOL +
	"  p_medium_option   VARCHAR2," + PMD.EOL +
	"  r_rec             IN OUT advice_medium_record," + PMD.EOL +
	"  r_result          IN OUT NUMBER," + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get advice record" + PMD.EOL +
	"PROCEDURE Get (" + PMD.EOL +
	"  p_contract_id     VARCHAR2,                   -- contract ID" + PMD.EOL +
	"  p_step_seq        NUMBER,                     -- step number" + PMD.EOL +
	"  p_seq             NUMBER,                     -- advice number" + PMD.EOL +
	"  r_rec             IN OUT advice_record,       -- advice record" + PMD.EOL +
	"  r_result          IN OUT NUMBER,              -- return code, 0=ok" + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);           -- return message" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get list of advices on contract" + PMD.EOL +
	"PROCEDURE List_Advices (" + PMD.EOL +
	"  p_contract_id     VARCHAR2,                   -- contract ID" + PMD.EOL +
	"  p_step_seq        NUMBER,                     -- step number" + PMD.EOL +
	"  p_in_out          VARCHAR2,                   -- direction" + PMD.EOL +
	"  r_result          IN OUT NUMBER,              -- return code, 0=ok" + PMD.EOL +
	"  r_message         IN OUT VARCHAR2,            -- return message" + PMD.EOL +
	"  r_list            IN OUT advice_table);       -- list of advices" + PMD.EOL +
	"" + PMD.EOL +
	"-- Register advice" + PMD.EOL +
	"PROCEDURE Ins (" + PMD.EOL +
	"  p                   IN OUT advice_record,  -- advice data" + PMD.EOL +
	"  r_result            OUT NUMBER," + PMD.EOL +
	"  r_message           OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Remove advice" + PMD.EOL +
	"PROCEDURE Del (" + PMD.EOL +
	"  p                   IN OUT advice_record,  -- advice data" + PMD.EOL +
	"  r_result            OUT NUMBER," + PMD.EOL +
	"  r_message           OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Register swift (sub)message" + PMD.EOL +
	"PROCEDURE Ins_SW_Advice (" + PMD.EOL +
	"  p                   IN OUT sw_advice_record,    -- SWIFT advice record" + PMD.EOL +
	"  r_result            OUT NUMBER," + PMD.EOL +
	"  r_message           OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get list of SWIFT messages under SWIFT advice" + PMD.EOL +
	"PROCEDURE Get_SW_Advice (" + PMD.EOL +
	"  p_id              VARCHAR2,                   -- SWIFT advice ID" + PMD.EOL +
	"  r_list            IN OUT sw_advice_table,     -- list of messages" + PMD.EOL +
	"  r_result          IN OUT NUMBER,              -- return code, 0=ok" + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);           -- return message" + PMD.EOL +
	"" + PMD.EOL +
	"-- Register file (sub)documents" + PMD.EOL +
	"PROCEDURE Ins_File (" + PMD.EOL +
	"  p                   IN OUT files_record,  -- file document record" + PMD.EOL +
	"  r_result            OUT NUMBER," + PMD.EOL +
	"  r_message           OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get list of files under the document" + PMD.EOL +
	"PROCEDURE Get_Files (" + PMD.EOL +
	"  p_doc_id          VARCHAR2,                   -- Document ID" + PMD.EOL +
	"  r_list            IN OUT files_table,         -- list of files" + PMD.EOL +
	"  r_result          IN OUT NUMBER,              -- return code, 0=ok" + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);           -- return message" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Clearance for release of outgoing advices." + PMD.EOL +
	"* @param p_contract_id      contract ID" + PMD.EOL +
	"* @param p_step             step number" + PMD.EOL +
	"* @param p_app_user         application user" + PMD.EOL +
	"* @param r_result           return code, 0=ok" + PMD.EOL +
	"* @param r_message          return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE CFR_Advice_Out (" + PMD.EOL +
	"  p_contract_id     VARCHAR2," + PMD.EOL +
	"  p_step            NUMBER," + PMD.EOL +
	"  p_app_user        VARCHAR2," + PMD.EOL +
	"  r_result          IN OUT NUMBER," + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Clearance for release of incoming advices." + PMD.EOL +
	"* @param p_contract_id      contract ID" + PMD.EOL +
	"* @param p_step             step number" + PMD.EOL +
	"* @param p_app_user         application user" + PMD.EOL +
	"* @param r_result           return code, 0=ok" + PMD.EOL +
	"* @param r_message          return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE CFR_Advice_In (" + PMD.EOL +
	"  p_contract_id     VARCHAR2," + PMD.EOL +
	"  p_step            NUMBER," + PMD.EOL +
	"  p_app_user        VARCHAR2," + PMD.EOL +
	"  r_result          IN OUT NUMBER," + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);" + PMD.EOL +
	"  " + PMD.EOL +
	"/**" + PMD.EOL +
	"* Step release of outgoing advices." + PMD.EOL +
	"* @param p_contract_id      contract ID" + PMD.EOL +
	"* @param p_step             step number" + PMD.EOL +
	"* @param p_app_user         application user" + PMD.EOL +
	"* @param r_result           return code, 0=ok" + PMD.EOL +
	"* @param r_message          return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Release_Advice_Out (" + PMD.EOL +
	"  p_contract_id     VARCHAR2," + PMD.EOL +
	"  p_step            NUMBER," + PMD.EOL +
	"  p_app_user        VARCHAR2," + PMD.EOL +
	"  r_result          IN OUT NUMBER," + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Step release of incoming advices." + PMD.EOL +
	"* @param p_contract_id      contract ID" + PMD.EOL +
	"* @param p_step             step number" + PMD.EOL +
	"* @param p_app_user         application user" + PMD.EOL +
	"* @param r_result           return code, 0=ok" + PMD.EOL +
	"* @param r_message          return message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Release_Advice_In (" + PMD.EOL +
	"  p_contract_id     VARCHAR2," + PMD.EOL +
	"  p_step            NUMBER," + PMD.EOL +
	"  p_app_user        VARCHAR2," + PMD.EOL +
	"  r_result          IN OUT NUMBER," + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"END;" + PMD.EOL +
	"/" + PMD.EOL +
	"" + PMD.EOL +
	"" + PMD.EOL +
	"CREATE OR REPLACE" + PMD.EOL +
	"PACKAGE CUSTOMER_DATA" + PMD.EOL +
	"IS" + PMD.EOL +
	"/** " + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* Project:         Test Project (<a href=\"http://pldoc.sourceforge.net\">PLDoc</a>)<br/>" + PMD.EOL +
	"* Description:     Customer Data Management<br/>" + PMD.EOL +
	"* DB impact:       YES<br/>" + PMD.EOL +
	"* Commit inside:   NO<br/>" + PMD.EOL +
	"* Rollback inside: NO<br/>" + PMD.EOL +
	"* ------------------------------------------------------------------------<br/>" + PMD.EOL +
	"* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>" + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* @headcom" + PMD.EOL +
	"*/" + PMD.EOL +
	"" + PMD.EOL +
	"record_locked EXCEPTION;" + PMD.EOL +
	"" + PMD.EOL +
	"TYPE customer_type IS RECORD (" + PMD.EOL +
	"  id                        VARCHAR2(20)," + PMD.EOL +
	"  name                      VARCHAR2(100)," + PMD.EOL +
	"  short_name                VARCHAR2(35)," + PMD.EOL +
	"  db_id                     VARCHAR2(20)," + PMD.EOL +
	"  sub_cust_code             VARCHAR2(20)," + PMD.EOL +
	"  sub_account               VARCHAR2(30)," + PMD.EOL +
	"  regno                     VARCHAR2(50)," + PMD.EOL +
	"  residence                 VARCHAR2(10)," + PMD.EOL +
	"  ct_type                   VARCHAR2(10)," + PMD.EOL +
	"  ct_entity                 VARCHAR2(10)," + PMD.EOL +
	"  language                  VARCHAR2(10)," + PMD.EOL +
	"  business_type             VARCHAR2(10)," + PMD.EOL +
	"  tax_code                  VARCHAR2(10)" + PMD.EOL +
	");" + PMD.EOL +
	"TYPE customer_table IS TABLE OF customer_type INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"SUBTYPE loan_customer_type IS loan_customers%ROWTYPE;" + PMD.EOL +
	"TYPE loan_customer_table IS TABLE OF loan_customer_type INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get data by id" + PMD.EOL +
	"PROCEDURE Get_Record (" + PMD.EOL +
	"  p_id              VARCHAR2," + PMD.EOL +
	"  r                 OUT customer_type," + PMD.EOL +
	"  r_result          IN OUT NUMBER," + PMD.EOL +
	"  r_message         IN OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Get data by id" + PMD.EOL +
	"PROCEDURE Get_By_Id (" + PMD.EOL +
	"  p_id              IN VARCHAR2," + PMD.EOL +
	"  r_records         IN OUT customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Search data" + PMD.EOL +
	"PROCEDURE Get_By_Criteria (" + PMD.EOL +
	"  p_criteria        IN customer_type," + PMD.EOL +
	"  r_records         IN OUT customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Search data" + PMD.EOL +
	"PROCEDURE Get (" + PMD.EOL +
	"  p_id              IN VARCHAR2," + PMD.EOL +
	"  p_name            IN VARCHAR2," + PMD.EOL +
	"  p_short_name      IN VARCHAR2," + PMD.EOL +
	"  p_cust_code       IN VARCHAR2," + PMD.EOL +
	"  p_account         IN VARCHAR2," + PMD.EOL +
	"  p_regno           IN VARCHAR2," + PMD.EOL +
	"  r_records         IN OUT customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Search customer by account number" + PMD.EOL +
	"PROCEDURE Search_By_Account (" + PMD.EOL +
	"  p_account         VARCHAR2,             -- account number" + PMD.EOL +
	"  r_record          IN OUT customer_type, -- found customer record" + PMD.EOL +
	"  r_result          OUT NUMBER,           -- result code (0=OK)" + PMD.EOL +
	"  r_message         OUT VARCHAR2);        -- error  message" + PMD.EOL +
	"" + PMD.EOL +
	"-- Insert a record" + PMD.EOL +
	"PROCEDURE Ins (" + PMD.EOL +
	"  p_data            IN customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Update a record" + PMD.EOL +
	"PROCEDURE Upd (" + PMD.EOL +
	"  p_data            IN customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Delete a record" + PMD.EOL +
	"PROCEDURE Del (" + PMD.EOL +
	"  p_data            IN customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"-- Lock a record" + PMD.EOL +
	"PROCEDURE Lck (" + PMD.EOL +
	"  p_data            IN customer_table);" + PMD.EOL +
	"" + PMD.EOL +
	"/**" + PMD.EOL +
	"* Get loan-related customer data by customer ID." + PMD.EOL +
	"* @param p_id               customer ID" + PMD.EOL +
	"* @param r                  loan-related customer data" + PMD.EOL +
	"* @param r_result           result (0=ok)" + PMD.EOL +
	"* @param r_message          error message" + PMD.EOL +
	"*/" + PMD.EOL +
	"PROCEDURE Get_Loan_Customer (" + PMD.EOL +
	"  p_id              VARCHAR2," + PMD.EOL +
	"  r                 OUT loan_customer_type," + PMD.EOL +
	"  r_result          OUT NUMBER," + PMD.EOL +
	"  r_message         OUT VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"END;" + PMD.EOL +
	"/" + PMD.EOL +
	"" + PMD.EOL +
	"CREATE OR REPLACE" + PMD.EOL +
	"Package    EXEC_SP " + PMD.EOL +
	"IS" + PMD.EOL +
	"/** " + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* Project:         Test<br/>" + PMD.EOL +
	"* Description:     Executes stored procedure<br/>" + PMD.EOL +
	"* DB impact:       NO<br/>" + PMD.EOL +
	"* Commit inside:   NO<br/>" + PMD.EOL +
	"* Rollback inside: NO<br/>" + PMD.EOL +
	"* ------------------------------------------------------------------------<br/>" + PMD.EOL +
	"* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>" + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* @headcom" + PMD.EOL +
	"*/" + PMD.EOL +
	"" + PMD.EOL +
	"DATEFORMAT constant VARCHAR2(100) := 'dd.mm.yyyy hh24:mi:ss';" + PMD.EOL +
	"TYPE string_array IS TABLE OF VARCHAR2(32000) INDEX BY BINARY_INTEGER;" + PMD.EOL +
	"" + PMD.EOL +
	"PROCEDURE Exec_SP (" + PMD.EOL +
	"  sp_name     VARCHAR2," + PMD.EOL +
	"  sp_package  VARCHAR2," + PMD.EOL +
	"  sp_schema   VARCHAR2," + PMD.EOL +
	"  sp_type     VARCHAR2,             -- 'PROCEDURE' of 'FUNCTION'" + PMD.EOL +
	"  arg_names   IN OUT string_array,  -- use RETURN for function return value" + PMD.EOL +
	"  arg_types   IN OUT string_array,  -- 'VARCHAR2', 'NUMBER', 'DATE' ('dd.mm.yyyy hh24:mi:ss')" + PMD.EOL +
	"  arg_pass    IN OUT string_array,  -- 'IN', 'OUT', 'IN OUT'" + PMD.EOL +
	"  arg_values  IN OUT string_array," + PMD.EOL +
	"  error_code  OUT NUMBER,           -- 0 ok" + PMD.EOL +
	"  error_msg   OUT VARCHAR2);" + PMD.EOL +
	"  " + PMD.EOL +
	"END;" + PMD.EOL +
	"/" + PMD.EOL +
	"" + PMD.EOL +
	"CREATE OR REPLACE" + PMD.EOL +
	"PACKAGE LOBS_DATA" + PMD.EOL +
	"IS" + PMD.EOL +
	"/** " + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* Project:         Test Project (<a href=\"http://pldoc.sourceforge.net\">PLDoc</a>)<br/><br/>" + PMD.EOL +
	"* Description:     Large Objects<br/>" + PMD.EOL +
	"* DB impact:       NO<br/>" + PMD.EOL +
	"* Commit inside:   NO<br/>" + PMD.EOL +
	"* Rollback inside: NO<br/>" + PMD.EOL +
	"* ------------------------------------------------------------------------<br/>" + PMD.EOL +
	"* $Header: /cvsroot/pldoc/sources/samples/sample1.sql,v 1.17 2003/08/30 07:52:44 altumano Exp $<br/>" + PMD.EOL +
	"* ========================================================================<br/>" + PMD.EOL +
	"* @headcom" + PMD.EOL +
	"*/" + PMD.EOL +
	"" + PMD.EOL +
	"-- Storing a large object; returns ID" + PMD.EOL +
	"FUNCTION put(" + PMD.EOL +
	"  p_value       IN VARCHAR2)" + PMD.EOL +
	"RETURN NUMBER;" + PMD.EOL +
	"" + PMD.EOL +
	"-- Loading a large object by ID" + PMD.EOL +
	"FUNCTION get(" + PMD.EOL +
	"  p_id          IN VARCHAR2)" + PMD.EOL +
	"RETURN VARCHAR2;" + PMD.EOL +
	"" + PMD.EOL +
	"-- Remove a stored object" + PMD.EOL +
	"PROCEDURE remove(" + PMD.EOL +
	"  p_id          IN VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"FUNCTION HH_get_info " + PMD.EOL +
	"( erty_id_in IN HH_t.a_id%TYPE 	DEFAULT NULL" + PMD.EOL +
	", df_id_in   IN HH_t.b_id%TYPE  DEFAULT NULL" + PMD.EOL +
	", fghj_id_in IN HH_t.c_id%TYPE 	DEFAULT vk_asdgfh_pa.some_function() " + PMD.EOL +
	", cascade_in IN NUMBER          DEFAULT vk_asdgfh_pa.some_constant " + PMD.EOL +
	")" + PMD.EOL +
	"RETURN vk_types_pa.type_rg_info_rec;" + PMD.EOL +
	"" + PMD.EOL +
	"PROCEDURE start_batch_job (p_interval IN dba_jobs.interval%TYPE);" + PMD.EOL +
	"" + PMD.EOL +
	"procedure out(cursor VARCHAR2);" + PMD.EOL +
	"" + PMD.EOL +
	"PROCEDURE refresh_all(kehtib date default last_day(add_months(trunc(sysdate),1))+1);" + PMD.EOL +
	"" + PMD.EOL +
	"END;--the end" + PMD.EOL ;
				 
	 }

	@Test
	public void tokenizeTest() throws IOException {
		this.expectedTokenCount = 1422;
		super.tokenizeTest();
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PLSQLTokenizerTest.class);
    }
}
