package com.rodemtree.project.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DocumentDto(
        @JsonProperty("place_name")
        String placeName,
        @JsonProperty("address_name")
        String addressName,
        @JsonProperty("x")
        double longitude,
        @JsonProperty("y")
        double latitude,
        @JsonProperty("distance")
        double distance
) {
}
