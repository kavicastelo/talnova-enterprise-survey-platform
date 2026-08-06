package com.talnova.tesp.employeeservice.service;

import com.talnova.tesp.employeeservice.dto.CompileSnapshotRequestDTO;
import com.talnova.tesp.employeeservice.dto.DemographicSnapshotDTO;

import java.util.List;

public interface DemographicSnapshotService {

    List<DemographicSnapshotDTO> compileSurveySnapshot(CompileSnapshotRequestDTO request);

    DemographicSnapshotDTO getSnapshot(String id);
}
