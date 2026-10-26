package com.projects.chronicle.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EodReconciliationHandler implements JobHandler{

    private static final Logger log = LoggerFactory.getLogger(EodReconciliationHandler.class);

    @Override
    public String getJobType(){
        return "EOD_RECONCILIATION";
    }

    @Override
    public void execute(JsonNode payload) throws Exception {
        String accountId = payload.path("account_id").asText();
        String date = payload.path("target_date").asText();

        log.info("Executing reconciliation for account {} on {}", accountId, date);

        // Simulate external API call to broker
        Thread.sleep(1500);

        if (Math.random() < 0.1) {
            throw new IllegalStateException("Broker API timeout during reconciliation");
        }
    }
}
