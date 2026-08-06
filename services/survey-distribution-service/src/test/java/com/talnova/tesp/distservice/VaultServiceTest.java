package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.repository.VaultRepository;
import com.talnova.tesp.distservice.service.VaultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaultServiceTest {

    @Mock
    private VaultRepository vaultRepository;

    private VaultServiceImpl vaultService;

    @BeforeEach
    void setUp() {
        vaultService = new VaultServiceImpl(vaultRepository);
    }

    @Test
    @DisplayName("TC-DST-202-01: Persist token batch to isolated vault database")
    void testPersistTokenBatchToVault() {
        GeneratedTokenDTO token1 = GeneratedTokenDTO.builder()
                .token("TKN-101")
                .employeeId("EMP-10020")
                .campaignId("CMP-1001")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .build();

        GeneratedTokenDTO token2 = GeneratedTokenDTO.builder()
                .token("TKN-102")
                .employeeId("EMP-10021")
                .campaignId("CMP-1001")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .build();

        when(vaultRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        vaultService.persistTokenBatchToVault("PRJ-99201", "CMP-1001", List.of(token1, token2));

        verify(vaultRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("TC-DST-202-02: Mark token as burned in isolated vault")
    void testMarkTokenAsBurned() {
        IdentityTokenVaultDocument doc = IdentityTokenVaultDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .token("TKN-101")
                .employeeId("EMP-10020")
                .isBurned(false)
                .build();

        when(vaultRepository.findByProjectIdAndCampaignIdAndToken("PRJ-99201", "CMP-1001", "TKN-101"))
                .thenReturn(Optional.of(doc));
        when(vaultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        vaultService.markTokenAsBurned("PRJ-99201", "CMP-1001", "TKN-101");

        assertTrue(doc.isBurned());
        assertNotNull(doc.getBurnedAt());
        verify(vaultRepository, times(1)).save(doc);
    }

    @Test
    @DisplayName("TC-DST-202-03: Retrieve unburned tokens for reminder nudge target filtering")
    void testGetUnburnedVaultRecordsForCampaign() {
        IdentityTokenVaultDocument unburnedDoc = IdentityTokenVaultDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .token("TKN-102")
                .employeeId("EMP-10021")
                .isBurned(false)
                .build();

        when(vaultRepository.findByCampaignIdAndIsBurnedFalse("CMP-1001"))
                .thenReturn(List.of(unburnedDoc));

        List<IdentityTokenVaultDocument> unburnedList = vaultService.getUnburnedVaultRecordsForCampaign("CMP-1001");

        assertNotNull(unburnedList);
        assertEquals(1, unburnedList.size());
        assertEquals("EMP-10021", unburnedList.get(0).getEmployeeId());
    }
}
