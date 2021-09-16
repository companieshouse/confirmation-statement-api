package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradingStatusDataJson {

    @JsonProperty("trading_status_answer")
    private Boolean tradingStatusAnswer;

    public Boolean getTradingStatusAnswer() {
        return tradingStatusAnswer;
    }

    public void setTradingStatusAnswer(Boolean tradingStatusAnswer) {
        this.tradingStatusAnswer = tradingStatusAnswer;
    }

}
