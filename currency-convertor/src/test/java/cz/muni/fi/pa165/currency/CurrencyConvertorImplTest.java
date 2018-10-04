package cz.muni.fi.pa165.currency;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CurrencyConvertorImplTest {

    private static Currency CZK = Currency.getInstance("CZK");
    private static Currency USD = Currency.getInstance("USD");

    @Mock
    private ExchangeRateTable RateTable;

    private CurrencyConvertor ccyConvertor;

    @Before
    public void init() {
        ccyConvertor = new CurrencyConvertorImpl(RateTable);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        when(RateTable.getExchangeRate(USD, CZK))
                .thenReturn(new BigDecimal("0.1"));

        assertEquals(new BigDecimal("1.00"), ccyConvertor.convert(USD, CZK, new BigDecimal("10.050")));
        assertEquals(new BigDecimal("1.01"), ccyConvertor.convert(USD, CZK, new BigDecimal("10.051")));
        assertEquals(new BigDecimal("1.01"), ccyConvertor.convert(USD, CZK, new BigDecimal("10.149")));
        assertEquals(new BigDecimal("1.02"), ccyConvertor.convert(USD, CZK, new BigDecimal("10.150")));
    }
    
     @Test
    public void testConvertWithNullSourceCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        ccyConvertor.convert(null, CZK, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        ccyConvertor.convert(USD, null, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        expectedException.expect(IllegalArgumentException.class);
        ccyConvertor.convert(USD, CZK, null);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(RateTable.getExchangeRate(USD, CZK))
                .thenReturn(null);
        expectedException.expect(UnknownExchangeRateException.class);
        ccyConvertor.convert(USD, CZK, BigDecimal.ONE);

    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(RateTable.getExchangeRate(USD, CZK))
                .thenThrow(UnknownExchangeRateException.class);
        expectedException.expect(UnknownExchangeRateException.class);
        ccyConvertor.convert(USD, CZK, BigDecimal.ONE);
    }

}




