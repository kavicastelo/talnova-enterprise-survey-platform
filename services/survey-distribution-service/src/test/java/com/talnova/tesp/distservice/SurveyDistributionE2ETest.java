package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.adapter.AwsSesEmailAdapter;
import com.talnova.tesp.distservice.adapter.KioskPinAdapter;
import com.talnova.tesp.distservice.adapter.MsTeamsAdapter;
import com.talnova.tesp.distservice.adapter.SlackAdapter;
import com.talnova.tesp.distservice.adapter.TwilioSmsAdapter;
import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DeliveryWebhookPayloadDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionRequestDTO;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionResponseDTO;
import com.talnova.tesp.distservice.dto.ReminderNudgeTargetDTO;
import com.talnova.tesp.distservice.dto.TargetAudienceDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationRequestDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationResponseDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.mapper.CampaignMapper;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import com.talnova.tesp.distservice.repository.VaultRepository;
import com.talnova.tesp.distservice.service.AiOptimalDispatchPredictorServiceImpl;
import com.talnova.tesp.distservice.service.CampaignServiceImpl;
import com.talnova.tesp.distservice.service.CryptographicTokenGeneratorImpl;
import com.talnova.tesp.distservice.service.DistributionChannelRouterImpl;
import com.talnova.tesp.distservice.service.MultiChannelDispatcherServiceImpl;
import com.talnova.tesp.distservice.service.RedisTokenCacheServiceImpl;
import com.talnova.tesp.distservice.service.ReminderTargetPollerServiceImpl;
import com.talnova.tesp.distservice.service.VaultServiceImpl;
import com.talnova.tesp.distservice.service.WebhookIngestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyDistributionE2ETest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private VaultRepository vaultRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private CampaignMapper campaignMapper;
    private CampaignServiceImpl campaignService;
    private CryptographicTokenGeneratorImpl tokenGenerator;
    private VaultServiceImpl vaultService;
    private RedisTokenCacheServiceImpl redisCacheService;
    private DistributionChannelRouterImpl channelRouter;
    private MultiChannelDispatcherServiceImpl dispatcherService;
    private ReminderTargetPollerServiceImpl reminderPollerService;
    private WebhookIngestionServiceImpl webhookService;
    private AiOptimalDispatchPredictorServiceImpl aiPredictorService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        campaignMapper = new CampaignMapper();
        campaignService = new CampaignServiceImpl(campaignRepository, campaignMapper);
        tokenGenerator = new CryptographicTokenGeneratorImpl();
        vaultService = new VaultServiceImpl(vaultRepository);
        redisCacheService = new RedisTokenCacheServiceImpl(redisTemplate, objectMapper);
        channelRouter = new DistributionChannelRouterImpl();
        dispatcherService = new MultiChannelDispatcherServiceImpl(List.of(
                new AwsSesEmailAdapter(),
                new TwilioSmsAdapter(),
                new MsTeamsAdapter(),
                new SlackAdapter(),
                new KioskPinAdapter()
        ));
        reminderPollerService = new ReminderTargetPollerServiceImpl(campaignRepository, vaultRepository);
        webhookService = new WebhookIngestionServiceImpl(campaignRepository);
        aiPredictorService = new AiOptimalDispatchPredictorServiceImpl();
    }

    @Test
    @DisplayName("TC-DST-901-01: High-Performance SLA - Generate 100,000 Cryptographic Tokens using Java 21 Virtual Threads")
    void test100kTokenGenerationSLAAndVaultIsolation() {
        List<String> employeeIds = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            employeeIds.add("EMP-VAL-" + i);
        }

        TokenBatchGenerationRequestDTO request = TokenBatchGenerationRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-100k-SLA")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .employeeIds(employeeIds)
                .campaignSalt("SALT-100K-SECURITY")
                .build();

        long startTime = System.currentTimeMillis();
        TokenBatchGenerationResponseDTO response = tokenGenerator.generateTokensForCampaign(request);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(response);
        assertEquals(100000, response.getTotalGenerated());
        assertTrue(duration < 15000, "100,000 token generation must execute within high-performance SLA < 15s (Actual: " + duration + " ms)");

        // Mock Vault bulk persistence
        when(vaultRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        vaultService.persistTokenBatchToVault("PRJ-99201", "CMP-100k-SLA", response.getTokens());

        verify(vaultRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("TC-DST-901-02: Comprehensive End-to-End Multi-Channel Survey Distribution Engine Integration Workflow")
    void testEndToEndCampaignDistributionLifecycle() throws Exception {
        String projectId = "PRJ-99201";
        String campaignId = "CMP-E2E-999";
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(86400 * 14);

        // 1. Campaign Launch
        CampaignCreateDTO createDTO = CampaignCreateDTO.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .title("Annual Employee Satisfaction Survey 2026")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .channels(List.of(DistributionChannel.EMAIL, DistributionChannel.TEAMS))
                .targetAudience(TargetAudienceDTO.builder().nodeIds(List.of("N-301", "N-302")).build())
                .startDate(now)
                .expirationDate(expiry)
                .build();

        when(campaignRepository.existsByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)).thenReturn(false);
        when(campaignRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CampaignResponseDTO createdCampaign = campaignService.createCampaign(createDTO);
        assertNotNull(createdCampaign);
        assertEquals(CampaignStatus.ACTIVE, createdCampaign.getStatus());
        assertEquals(4, createdCampaign.getMetrics().getTotalTargeted());

        // 2. Token Generation
        TokenBatchGenerationRequestDTO tokenReq = TokenBatchGenerationRequestDTO.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .employeeIds(List.of("EMP-N-301-101", "EMP-N-301-102", "EMP-N-302-101", "EMP-N-302-102"))
                .campaignSalt("SALT-E2E-TEST")
                .build();

        TokenBatchGenerationResponseDTO generatedTokens = tokenGenerator.generateTokensForCampaign(tokenReq);
        assertEquals(4, generatedTokens.getTotalGenerated());

        // 3. Vault Persistence
        vaultService.persistTokenBatchToVault(projectId, campaignId, generatedTokens.getTokens());

        // 4. Redis Token Caching
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisCacheService.cacheTokenBatch(projectId, campaignId, "SRV-5001", 1, generatedTokens.getTokens(), expiry);
        verify(valueOperations, times(4)).set(anyString(), anyString(), any(Duration.class));

        // 5. Multi-Channel Hydration & Dispatch
        List<ChannelMessageDispatchDTO> dispatchPayloads = channelRouter.prepareBatchDispatch(
                projectId, campaignId, createdCampaign.getTitle(), generatedTokens.getTokens(), createdCampaign.getChannels(), "en-US"
        );
        assertEquals(8, dispatchPayloads.size(), "4 recipients x 2 channels = 8 payloads");

        List<DispatchResultDTO> dispatchResults = dispatcherService.dispatchBatchMessages(dispatchPayloads);
        assertEquals(8, dispatchResults.size());
        assertTrue(dispatchResults.stream().allMatch(r -> r.getStatus() == DispatchResultDTO.DispatchStatus.SENT));

        // 6. Reminder Target Polling
        SurveyCampaignDocument activeDoc = SurveyCampaignDocument.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .status(CampaignStatus.ACTIVE)
                .channels(List.of(DistributionChannel.EMAIL))
                .build();

        IdentityTokenVaultDocument unburnedDoc = IdentityTokenVaultDocument.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .employeeId("EMP-N-301-101")
                .token(generatedTokens.getTokens().get(0).getToken())
                .isBurned(false)
                .build();

        when(campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)).thenReturn(Optional.of(activeDoc));
        when(vaultRepository.findByCampaignIdAndIsBurnedFalse(campaignId)).thenReturn(List.of(unburnedDoc));

        List<ReminderNudgeTargetDTO> nudgeTargets = reminderPollerService.pollUncompletedReminderTargets(projectId, campaignId);
        assertEquals(1, nudgeTargets.size());

        // 7. Webhook Ingestion & Metric Aggregation
        DeliveryWebhookPayloadDTO webhook = DeliveryWebhookPayloadDTO.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .provider(DeliveryWebhookPayloadDTO.Provider.AWS_SES)
                .eventType(DeliveryWebhookPayloadDTO.EventType.DELIVERED)
                .build();

        webhookService.processDeliveryWebhook(webhook);
        assertEquals(1, activeDoc.getMetrics().getDelivered());

        // 8. AI Optimal Dispatch Hour Prediction
        OptimalDispatchPredictionRequestDTO aiReq = OptimalDispatchPredictionRequestDTO.builder()
                .projectId(projectId)
                .employeeId("EMP-N-301-101")
                .department("Plant Operations")
                .build();

        OptimalDispatchPredictionResponseDTO aiRes = aiPredictorService.predictOptimalDispatchHour(aiReq);
        assertEquals(14, aiRes.getRecommendedHour());
        assertTrue(aiRes.getConfidenceScore() >= 0.85);
    }
}
