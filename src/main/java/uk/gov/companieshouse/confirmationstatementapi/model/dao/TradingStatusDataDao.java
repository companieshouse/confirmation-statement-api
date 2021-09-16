package uk.gov.companieshouse.confirmationstatementapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class TradingStatusDataDao {

    @Field("trading_status_answer")
    private Boolean tradingStatusAnswer;

    public Boolean getTradingStatusAnswer() {
        return tradingStatusAnswer;
    }

    public void setTradingStatusAnswer(Boolean tradingStatusAnswer) {
        this.tradingStatusAnswer = tradingStatusAnswer;
    }

}
