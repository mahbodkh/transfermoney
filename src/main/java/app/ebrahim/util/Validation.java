package app.ebrahim.util;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


public class Validation {
    static Logger log = Logger.getLogger(Validation.class);

    public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);

    public static boolean currencyCode(String inputCurrencyCode) {
        try {
            Currency instance = Currency.getInstance(inputCurrencyCode);
            if (log.isDebugEnabled()) {
                log.debug("Validate Currency Code: " + instance.getSymbol());
            }
            return instance.getCurrencyCode().equals(inputCurrencyCode);
        } catch (Exception e) {
            log.warn("Cannot parse the input Currency Code, Validation Failed: ", e);
        }
        return false;
    }

}
