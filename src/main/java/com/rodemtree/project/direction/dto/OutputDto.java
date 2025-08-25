package com.rodemtree.project.direction.dto;

import lombok.Builder;

@Builder
public record OutputDto(
        String pharmacyName,
        String pharmacyAddress,
        String directionUrl,
        String roadViewUrl,
        String distance
) {
}
