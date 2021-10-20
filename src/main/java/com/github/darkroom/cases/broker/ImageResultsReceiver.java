package com.github.darkroom.cases.broker;

import com.github.darkroom.cases.CaseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO: Type hint is required to work with @RabbitHandler. Example __TypeId__:	com.github.darkroom.cases.broker.ComparisonResults
@Component
public class ImageResultsReceiver {

    private final CaseService caseService;

    @Autowired
    public ImageResultsReceiver(CaseService caseService) {
        this.caseService = caseService;
    }

    @RabbitListener(ackMode = "AUTO", queues = "#{brokerConfig.queue().getName()}")
    public void receiveResults(ComparisonResults results) {
        caseService.saveComparisonResults(results.id(), results.image(), results.delta());
    }
}
