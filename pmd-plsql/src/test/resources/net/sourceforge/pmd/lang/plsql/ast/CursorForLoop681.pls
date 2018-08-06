--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

BEGIN
  FOR sum_res IN (SELECT SUM(loss_reserve) loss_reserve,
                               SUM(expense_reserve) exp_reserve
                          FROM gicl_clm_reserve a, gicl_item_peril b
                         WHERE a.claim_id = b.claim_id
                           AND a.item_no  = b.item_no
                           AND a.peril_cd = b.peril_cd
                           AND a.claim_id = p_claim_id
                           AND (NVL(b.close_flag, 'AP') IN ('AP','CC','CP') OR 
                                NVL(b.close_flag2, 'AP') IN ('AP','CC','CP')))
        LOOP
           v_loss_res_amt  := sum_res.loss_reserve;
           v_exp_res_amt   := sum_res.exp_reserve;
        END LOOP;
END;
/
