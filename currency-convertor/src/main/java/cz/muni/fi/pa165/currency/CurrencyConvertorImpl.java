package cz.muni.fi.pa165.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    private final static Logger log = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        log.trace("convert({},{},{})",sourceCurrency, targetCurrency, sourceAmount);
        //
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("sourceCurrency null");
        }
        if (targetCurrency == null) {
            throw new IllegalArgumentException("targetCurrency null");
        }
        if (sourceAmount == null) {
            throw new IllegalArgumentException("sourceAmount null");
        }
        
        
        try {
            BigDecimal exchangeRate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
            if (exchangeRate == null) {
                throw new UnknownExchangeRateException("ExchangeRate unknown");
            }
            //TODO: result of ccy convertor
            return exchangeRate.multiply(sourceAmount).setScale(2, RoundingMode.HALF_EVEN);
        } catch (ExternalServiceFailureException ex) {
            throw new UnknownExchangeRateException("Error when fetching exchange rate", ex);
        }
    }

}
