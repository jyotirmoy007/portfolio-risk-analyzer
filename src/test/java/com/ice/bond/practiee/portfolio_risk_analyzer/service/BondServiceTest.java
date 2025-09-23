package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.AvailableBonds;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.Bond;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BondServiceTest {
    private AvailableBonds availableBonds;
    private BondService bondService;

    @BeforeEach
    void setUp() {
        availableBonds = mock(AvailableBonds.class);
        bondService = new BondService(availableBonds);
    }

    @Test
    void testLookupBondByIsin_CacheHit() {
        // given
        Bond bond = Bond.builder()
                .isin("US1234567890")
                .issuer("US Treasury")
                .maturityDate(LocalDate.of(2030, 12, 31))
                .couponRate(5.0)
                .paymentFrequency(2)
                .marketPrice(980.0)
                .faceValue(1000.0)
                .yearsToMaturity(7)
                .build();

        when(availableBonds.getAvailableBonds()).thenReturn(java.util.List.of(bond));

        // first call -> should fetch from availableBonds and cache it
        Bond firstLookup = bondService.looupBondByIsin("US1234567890");
        assertNotNull(firstLookup);
        assertEquals("US1234567890", firstLookup.getIsin());

        // second call -> should be served from cache (no new call to availableBonds)
        Bond secondLookup = bondService.looupBondByIsin("US1234567890");
        assertSame(firstLookup, secondLookup); // same object due to cache
    }

    @Test
    void testLookupBondByIsin_InvalidIsinThrowsException() {
        when(availableBonds.getAvailableBonds()).thenReturn(java.util.List.of());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bondService.looupBondByIsin("INVALID123"));

        assertEquals("Invalid ISIN: INVALID123", exception.getMessage());
    }
}