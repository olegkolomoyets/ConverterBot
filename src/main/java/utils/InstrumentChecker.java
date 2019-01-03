package utils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by OlegK on 8/16/2018.
 */
public class InstrumentChecker {

    public static boolean isCurrencyAvailable(String currencyToCheck) {
        Map availableCurrencies = new HashMap();
        String[] currencies = new String[]{"AUD", "AZN", "BYN", "BGN", "KRW", "HKD", "DKK", "USD", "PLN", "EUR", "EGP",
                "JPY", "INR", "IRR", "CAD", "HRK", "MXN", "MDL", "ILS", "NZD", "NOK", "ZAR", "RUB", "RON", "IDR", "SAR",
                "SGD", "KZT", "TRY", "HUF", "GBP", "CZK", "SEK", "CHF", "CNY", "XDR", "XAU", "XAG", "XPT", "XPD"};
        for (String currency : currencies) {
            availableCurrencies.put(currency, currency);
        }
        return availableCurrencies.containsKey(currencyToCheck);
    }
}
