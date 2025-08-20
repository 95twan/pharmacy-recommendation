package com.rodemtree.project.pharmacy.dto;

import lombok.Builder;

@Builder
public record PharmacyDto(
        Long id,
        String pharmacyName,
        String pharmacyAddress,
        double latitude,
        double longitude
) {
}
