package com.ice.bond.practiee.portfolio_risk_analyzer.service;

import com.ice.bond.practiee.portfolio_risk_analyzer.model.AvailableBonds;
import com.ice.bond.practiee.portfolio_risk_analyzer.model.Bond;
import com.ice.bond.practiee.portfolio_risk_analyzer.utils.BondUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
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
