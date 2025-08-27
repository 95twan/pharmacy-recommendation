package com.rodemtree.project.pharmacy.service;

import com.rodemtree.project.api.dto.DocumentDto;
import com.rodemtree.project.api.dto.KakaoApiResponseDto;
import com.rodemtree.project.api.service.KakaoAddressSearchService;
import com.rodemtree.project.direction.dto.OutputDto;
import com.rodemtree.project.direction.entity.Direction;
import com.rodemtree.project.direction.service.Base62Service;
import com.rodemtree.project.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyRecommendationService {

    private final DirectionService directionService;
    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final Base62Service base62Service;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    @Value("${pharmacy.recommendation.base.url}")
    private String baseUrl;

    public List<OutputDto> recommendPharmacyList(String address) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.documentList())) {
            log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input adress: {}", address);
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.documentList().get(0);

//        List<Direction> directionList = directionService.buildDirectionList(documentDto);
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);

        return directionService.saveAll(directionList).stream()
                .map(this::convertToOutputDto)
                .toList();

    }

    private OutputDto convertToOutputDto(Direction direction) {

        return OutputDto.builder()
                .pharmacyName(direction.getTargetPharmacyName())
                .pharmacyAddress(direction.getTargetAddress())
                .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId()))
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}
