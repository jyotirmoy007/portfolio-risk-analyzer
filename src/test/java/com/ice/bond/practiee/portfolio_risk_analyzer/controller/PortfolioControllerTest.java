package com.ice.bond.practiee.portfolio_risk_analyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.*;
import com.ice.bond.practiee.portfolio_risk_analyzer.service.PortfolioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    // TODO - Fix the CreatePortfolio test
//    @Test
//    void testCreatePortfolio() throws Exception {
//        // given
//        CreatePortfolioRequest request = CreatePortfolioRequest.builder()
//                .customerName("Alice").build();
//        String portfolioId = "PORT123";
//
//        when(portfolioService.createPortfolio(request)).thenReturn(portfolioId);
//
//        // when + then
//        mockMvc.perform(post("/api/portfolio")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.portfolioId").value("PORT123"));
//    }

    @Test
    void testAddBondToPortfolio() throws Exception {
        // given
        BondAddRequest request = new BondAddRequest();
        request.setPortfolioId("PORT123");
        request.setIsin("US1234567890");
        request.setQuantity(50);

        when(portfolioService.addBondToPortfolio("PORT123", "US1234567890", 50))
                .thenReturn(true);

        // when + then
        mockMvc.perform(post("/api/portfolio/bond")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"));
    }

    @Test
    void testRetrievePortfolio() throws Exception {
        // given
        String portfolioId = "PORT123";
        PortfolioDetailsResponse response = PortfolioDetailsResponse.builder()
                .id(portfolioId)
                .customerName("Alice")
                .bonds(List.of(
                        BondDTO.builder().isin("US1234567890").quantity(50).build(),
                        BondDTO.builder().isin("US0987654321").quantity(100).build()
                ))
                .build();

        when(portfolioService.getPortfolioDetails(portfolioId)).thenReturn(response);

        // when + then
        mockMvc.perform(get("/api/portfolio/{portfolioId}", portfolioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PORT123"))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.bonds[0].isin").value("US1234567890"))
                .andExpect(jsonPath("$.bonds[0].quantity").value(50))
                .andExpect(jsonPath("$.bonds[1].isin").value("US0987654321"))
                .andExpect(jsonPath("$.bonds[1].quantity").value(100));
    }
}