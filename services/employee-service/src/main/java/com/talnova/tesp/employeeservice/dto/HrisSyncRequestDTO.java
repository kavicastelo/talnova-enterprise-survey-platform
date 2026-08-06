package com.talnova.tesp.employeeservice.dto;

import com.talnova.tesp.employeeservice.service.hris.HrisProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "HRIS Automated Roster Sync Request Payload")
public class HrisSyncRequestDTO {

    @NotBlank(message = "projectId is required")
    private String projectId;

    @NotNull(message = "provider is required (WORKDAY, SUCCESSFACTORS)")
    private HrisProvider provider;

    @NotBlank(message = "endpointUrl is required")
    private String endpointUrl;

    @NotBlank(message = "apiToken is required")
    private String apiToken;

    private boolean autoTerminateMissing = true;

    public HrisSyncRequestDTO() {
    }

    public HrisSyncRequestDTO(String projectId, HrisProvider provider, String endpointUrl, String apiToken, boolean autoTerminateMissing) {
        this.projectId = projectId;
        this.provider = provider;
        this.endpointUrl = endpointUrl;
        this.apiToken = apiToken;
        this.autoTerminateMissing = autoTerminateMissing;
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public HrisProvider getProvider() { return provider; }
    public void setProvider(HrisProvider provider) { this.provider = provider; }

    public String getEndpointUrl() { return endpointUrl; }
    public void setEndpointUrl(String endpointUrl) { this.endpointUrl = endpointUrl; }

    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public boolean isAutoTerminateMissing() { return autoTerminateMissing; }
    public void setAutoTerminateMissing(boolean autoTerminateMissing) { this.autoTerminateMissing = autoTerminateMissing; }
}
