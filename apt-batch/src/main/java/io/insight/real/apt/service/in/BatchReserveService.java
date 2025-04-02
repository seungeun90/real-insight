package io.insight.real.apt.service.in;

import io.insight.real.apt.dto.BatchJobRequest;

public interface BatchReserveService {
    void enqueueAptTradeJob(BatchJobRequest request);
    void enqueueAptInfoJob(BatchJobRequest request);

}
