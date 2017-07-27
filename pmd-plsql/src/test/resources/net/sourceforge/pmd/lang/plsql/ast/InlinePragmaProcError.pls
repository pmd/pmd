create or replace package inline_praqma_error is

end;
/

create or replace package body inline_praqma_error is
  procedure do_transaction(p_input_token        in varchar(200)) is
  
  begin
    PRAGMA AUTONOMOUS_TRANSACTION;
    bno74.log_hentglass_request(p_hentglass_request
                               ,v_logging_req_seq_no);
    COMMIT;
   end do_transaction;

end inline_praqma_error;
/
