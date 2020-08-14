/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.stream.Collectors;
import java.util.List;

/**
 * @see <a href="https://openjdk.java.net/jeps/384">JEP 384: Records (Second Preview)</a>
 */
public class LocalRecords {
    public interface Merchant {}
    public static double computeSales(Merchant merchant, int month) {
        return month;
    }
    List<Merchant> findTopMerchants(List<Merchant> merchants, int month) {
        // Local record
        record MerchantSales(Merchant merchant, double sales) {}

        return merchants.stream()
            .map(merchant -> new MerchantSales(merchant, computeSales(merchant, month)))
            .sorted((m1, m2) -> Double.compare(m2.sales(), m1.sales()))
            .map(MerchantSales::merchant)
            .collect(Collectors.toList());
    }
}
