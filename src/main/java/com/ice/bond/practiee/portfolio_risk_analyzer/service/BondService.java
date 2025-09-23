package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ice.bond.practiee.portfolio_risk_analyzer.exception.PortfolioNotFoundException;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.AvailableBonds;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.Bond;
import com.ice.bond.practiee.portfolio_risk_analyzer.utils.BondUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
public class BondService {

    private AvailableBonds availableBonds;
    private final Cache<String, Bond> bondCache;


    @Autowired
    public BondService(AvailableBonds availableBonds) {
        this.availableBonds = availableBonds;

        // Initialize a Caffeine cache for Bond lookups
        this.bondCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) // expire after 30 min
                .maximumSize(1000) // max 1000 bonds cached
                .build();
    }
    @PostConstruct
    public void loadBondsFromCSV() {


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/bonds.csv")))) {

            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                try {


                    if (values.length == 7) {
                        availableBonds.add(Bond.builder()
                                .isin(values[0].trim())
                                .issuer(values[1].trim())
                                .maturityDate(LocalDate.parse(values[2].trim(), formatter))
                                .couponRate(Double.parseDouble(values[3].trim()))
                                .paymentFrequency(Integer.parseInt(values[4].trim()))
                                .marketPrice(Double.parseDouble(values[5].trim()))
                                .faceValue(Double.parseDouble(values[6].trim()))
                                .yearsToMaturity(BondUtils.calculateYearsToMaturity(LocalDate.parse(values[2].trim(), formatter)))
                                .build());
                    } else {
                        System.err.println("Skipping malformed CSV row: " + line);
                    }
                } catch (Exception ex) {
                    System.err.println("Error parsing row: " + line + " - " + ex.getMessage());
                }
            }
            System.out.println("########## Loaded bonds: " + availableBonds.getAvailableBonds().size());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load bonds.csv", e);
        }
    }

    public Bond looupBondByIsin(String isin) {
        return bondCache.get(isin, key ->
                availableBonds.getAvailableBonds().stream()
                        .filter(bond -> bond.getIsin().equals(isin))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid ISIN: " + isin))
        );
    }
}
