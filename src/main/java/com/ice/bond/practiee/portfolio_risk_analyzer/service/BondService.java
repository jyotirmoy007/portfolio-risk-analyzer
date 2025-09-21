package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.AvailableBonds;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.Bond;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
@Getter
public class BondService {

    @Autowired
    AvailableBonds availableBonds;

    @PostConstruct
    public void loadBondsFromCSV() {


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/bonds.csv")))) {

            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 8) {
                    availableBonds.add(Bond.builder()
                            .isin(values[0])
                            .issuer(values[1])
                            //.maturityDate(java.time.LocalDate.parse(values[2]))
                            .couponRate(Double.parseDouble(values[3].trim()))
                            .couponFrequency(Integer.parseInt(values[4].trim()))
                            .marketPrice(Double.parseDouble(values[5].trim()))
                            .faceValue(1000) // Assuming a default face value of 1000
                            .yearsToMaturity(21)
                            .build());
                }
            }
            System.out.println("########## Loaded bonds: " + availableBonds.getAvailableBonds().size());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load bonds.csv", e);
        }
    }

    public Bond looupBondByIsin(String isin) {

        return availableBonds.getAvailableBonds().stream()
                .filter(bond -> bond.getIsin().equals(isin))
                .findFirst()
                .orElse(null);
    }
}
