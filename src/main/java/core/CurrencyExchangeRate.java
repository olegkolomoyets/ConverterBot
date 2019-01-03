package core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by OlegK on 7/30/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyExchangeRate {
    @JsonProperty("StartDate")
    public String startDate;
    @JsonProperty("CurrencyCodeL")
    public String instrument;
    @JsonProperty("Amount")
    public Double amount;

    public CurrencyExchangeRate() {
    }

    public CurrencyExchangeRate(String startDate, String currencyCodeL, Double amount) {
        this.startDate = startDate;
        this.instrument = currencyCodeL;
        this.amount = amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
       return instrument + " " + startDate + " " + amount;
    }
}
