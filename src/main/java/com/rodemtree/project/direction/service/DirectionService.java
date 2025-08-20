package com.rodemtree.project.direction.service;

import com.rodemtree.project.api.dto.DocumentDto;
import com.rodemtree.project.api.service.KakaoCategorySearchService;
import com.rodemtree.project.direction.entity.Direction;
import com.rodemtree.project.direction.repository.DirectionRepository;
import com.rodemtree.project.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 10;
    private static final double RADIUS_KM = 10.0;

    private final PharmacySearchService pharmacySearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final DirectionRepository directionRepository;

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if(CollectionUtils.isEmpty(directionList)) return Collections.emptyList();
        return directionRepository.saveAll(directionList);
    }

    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        if(Objects.isNull(documentDto)) return Collections.emptyList();

        return pharmacySearchService.searchPharmacyDtoList().stream()
                .map(pharmacyDto ->
                        Direction.builder()
                                .inputAddress(documentDto.addressName())
                                .inputLatitude(documentDto.latitude())
                                .inputLongitude(documentDto.longitude())
                                .targetPharmacyName(pharmacyDto.pharmacyName())
                                .targetAddress(pharmacyDto.pharmacyAddress())
                                .targetLatitude(pharmacyDto.latitude())
                                .targetLongitude(pharmacyDto.longitude())
                                .distance(calculateDistance(documentDto.latitude(), documentDto.longitude(), pharmacyDto.latitude(), pharmacyDto.longitude()))
                                .build()
                )
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .toList();
    }

    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {
        if(Objects.isNull(inputDocumentDto)) return Collections.emptyList();

        return kakaoCategorySearchService
                .requestPharmacyCategorySearch(inputDocumentDto.latitude(), inputDocumentDto.longitude(), RADIUS_KM)
                .documentList()
                .stream().map(resultDocumentDto ->
                        Direction.builder()
                                .inputAddress(inputDocumentDto.addressName())
                                .inputLatitude(inputDocumentDto.latitude())
                                .inputLongitude(inputDocumentDto.longitude())
                                .targetPharmacyName(resultDocumentDto.placeName())
                                .targetAddress(resultDocumentDto.addressName())
                                .targetLatitude(resultDocumentDto.latitude())
                                .targetLongitude(resultDocumentDto.longitude())
                                .distance(resultDocumentDto.distance() * 0.001) // km 단위
                                .build())
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //km
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }

}
