package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.MessageTemplateRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DistributionChannelRouterImpl implements DistributionChannelRouter {

    private static final Logger log = LoggerFactory.getLogger(DistributionChannelRouterImpl.class);
    private static final String BASE_SURVEY_URL = "https://surveys.aitkenspence.com/p?t=";
    private static final String BASE_KIOSK_URL = "https://surveys.aitkenspence.com/kiosk?pin=";

    private static final Map<String, Map<String, String>> DICTIONARY = Map.of(
            "en-US", Map.of(
                    "emailSubject", "You're Invited: %s",
                    "emailBody", "<h2>Hello</h2><p>Please complete your survey for <strong>%s</strong> using the secure link below:</p><p><a href='%s'>Take Survey Now</a></p>",
                    "smsBody", "Survey Invitation: %s. Click here to respond: %s",
                    "kioskBody", "Survey Kiosk Invitation: %s. Your 6-digit access PIN is: %s",
                    "teamsBody", "Hello! You have a new survey invitation for **%s**. Click [here](%s) to complete."
            ),
            "si-LK", Map.of(
                    "emailSubject", "ඔබට ඇරයුම් කර ඇත: %s",
                    "emailBody", "<h2>ආයුබෝවන්</h2><p>කරුණාකර <strong>%s</strong> සමීක්ෂණය පහත සබැඳියෙන් සම්පූර්ණ කරන්න:</p><p><a href='%s'>සමීක්ෂණය ආරම්භ කරන්න</a></p>",
                    "smsBody", "සමීක්ෂණ ඇරයුම: %s. පිවිසෙන්න: %s",
                    "kioskBody", "කිඕස්ක් සමීක්ෂණ ඇරයුම: %s. ඔබගේ PIN අංකය: %s",
                    "teamsBody", "ආයුබෝවන්! %s සඳහා ඔබගේ සමීක්ෂණ ඇරයුම ලබා ගැනීමට [මෙතැනින්](%s) පිවිසෙන්න."
            )
    );

    @Override
    public ChannelMessageDispatchDTO buildAndHydrateMessage(MessageTemplateRequestDTO request) {
        String locale = request.getLocale() != null && DICTIONARY.containsKey(request.getLocale()) ? request.getLocale() : "en-US";
        Map<String, String> dict = DICTIONARY.get(locale);

        String surveyUrl = (request.getToken() != null && !request.getToken().isBlank())
                ? BASE_SURVEY_URL + request.getToken()
                : (request.getKioskPin() != null ? BASE_KIOSK_URL + request.getKioskPin() : "");

        String subject = String.format(dict.getOrDefault("emailSubject", "Survey Invitation: %s"), request.getCampaignTitle());
        String bodyHtml = "";
        String bodyText = "";

        if (request.getChannel() == DistributionChannel.EMAIL) {
            bodyHtml = String.format(dict.getOrDefault("emailBody", ""), request.getCampaignTitle(), surveyUrl);
            bodyText = String.format("Survey Invitation: %s\nLink: %s", request.getCampaignTitle(), surveyUrl);
        } else if (request.getChannel() == DistributionChannel.SMS) {
            bodyText = String.format(dict.getOrDefault("smsBody", ""), request.getCampaignTitle(), surveyUrl);
        } else if (request.getChannel() == DistributionChannel.KIOSK_PIN) {
            bodyText = String.format(dict.getOrDefault("kioskBody", ""), request.getCampaignTitle(), request.getKioskPin());
        } else if (request.getChannel() == DistributionChannel.TEAMS || request.getChannel() == DistributionChannel.SLACK) {
            bodyText = String.format(dict.getOrDefault("teamsBody", ""), request.getCampaignTitle(), surveyUrl);
        }

        return ChannelMessageDispatchDTO.builder()
                .projectId(request.getProjectId())
                .campaignId(request.getCampaignId())
                .employeeId(request.getEmployeeId())
                .recipientContact(request.getRecipientContact())
                .channel(request.getChannel())
                .subject(subject)
                .bodyHtml(bodyHtml)
                .bodyText(bodyText)
                .surveyUrl(surveyUrl)
                .kioskPin(request.getKioskPin())
                .build();
    }

    @Override
    public List<ChannelMessageDispatchDTO> prepareBatchDispatch(String projectId, String campaignId, String campaignTitle, List<GeneratedTokenDTO> tokens, List<DistributionChannel> channels, String locale) {
        log.info("Preparing multi-channel batch dispatch for campaignId: {}, tokenCount: {}, channels: {}", campaignId, tokens.size(), channels);
        List<ChannelMessageDispatchDTO> batchPayloads = new ArrayList<>();

        for (GeneratedTokenDTO tokenDTO : tokens) {
            for (DistributionChannel channel : channels) {
                MessageTemplateRequestDTO req = MessageTemplateRequestDTO.builder()
                        .projectId(projectId)
                        .campaignId(campaignId)
                        .campaignTitle(campaignTitle)
                        .employeeId(tokenDTO.getEmployeeId())
                        .recipientContact(tokenDTO.getEmployeeId() != null ? tokenDTO.getEmployeeId() + "@aitkenspence.lk" : "anonymous")
                        .channel(channel)
                        .locale(locale)
                        .token(tokenDTO.getToken())
                        .kioskPin(tokenDTO.getKioskPin())
                        .build();

                batchPayloads.add(buildAndHydrateMessage(req));
            }
        }

        log.info("Successfully prepared {} hydrated dispatch messages for campaign '{}'", batchPayloads.size(), campaignId);
        return batchPayloads;
    }
}
