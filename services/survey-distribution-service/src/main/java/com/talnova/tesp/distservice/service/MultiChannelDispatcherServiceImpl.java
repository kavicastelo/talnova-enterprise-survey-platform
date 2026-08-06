package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.adapter.NotificationChannelAdapter;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MultiChannelDispatcherServiceImpl implements MultiChannelDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(MultiChannelDispatcherServiceImpl.class);

    private final Map<DistributionChannel, NotificationChannelAdapter> adapterMap;

    public MultiChannelDispatcherServiceImpl(List<NotificationChannelAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(NotificationChannelAdapter::getSupportedChannel, Function.identity()));
    }

    @Override
    public DispatchResultDTO dispatchSingleMessage(ChannelMessageDispatchDTO message) {
        NotificationChannelAdapter adapter = adapterMap.get(message.getChannel());
        if (adapter == null) {
            log.warn("No adapter found for channel '{}', marking dispatch as FAILED", message.getChannel());
            return DispatchResultDTO.builder()
                    .dispatchId("DISP-ERR-" + UUID.randomUUID().toString().substring(0, 8))
                    .campaignId(message.getCampaignId())
                    .employeeId(message.getEmployeeId())
                    .channel(message.getChannel())
                    .status(DispatchResultDTO.DispatchStatus.FAILED)
                    .errorMessage("No registered connector adapter for channel: " + message.getChannel())
                    .dispatchedAt(Instant.now())
                    .build();
        }

        try {
            return adapter.dispatchMessage(message);
        } catch (Exception e) {
            log.error("Unhandled error during channel dispatch: {}", e.getMessage(), e);
            return DispatchResultDTO.builder()
                    .dispatchId("DISP-ERR-" + UUID.randomUUID().toString().substring(0, 8))
                    .campaignId(message.getCampaignId())
                    .employeeId(message.getEmployeeId())
                    .channel(message.getChannel())
                    .status(DispatchResultDTO.DispatchStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .dispatchedAt(Instant.now())
                    .build();
        }
    }

    @Override
    public List<DispatchResultDTO> dispatchBatchMessages(List<ChannelMessageDispatchDTO> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }

        log.info("Executing concurrent multi-channel dispatch batch of {} messages using Virtual Threads", messages.size());
        List<DispatchResultDTO> results = Collections.synchronizedList(new ArrayList<>());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (ChannelMessageDispatchDTO msg : messages) {
                futures.add(executor.submit(() -> {
                    DispatchResultDTO res = dispatchSingleMessage(msg);
                    results.add(res);
                }));
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            log.error("Virtual Threads dispatch batch execution error: {}", e.getMessage(), e);
        }

        long sentCount = results.stream().filter(r -> r.getStatus() == DispatchResultDTO.DispatchStatus.SENT).count();
        long failedCount = results.stream().filter(r -> r.getStatus() == DispatchResultDTO.DispatchStatus.FAILED).count();
        log.info("Batch dispatch completed. Total: {}, Sent: {}, Failed: {}", results.size(), sentCount, failedCount);

        return results;
    }
}
