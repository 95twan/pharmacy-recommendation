package com.rodemtree.project.pharmacy.service

import com.rodemtree.project.AbstractIntegrationContainerBaseTest
import com.rodemtree.project.pharmacy.entity.Pharmacy
import com.rodemtree.project.pharmacy.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired

class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepositoryService pharmacyRepositoryService

    @Autowired
    private PharmacyRepository pharmacyRepository

    def setup() {
        pharmacyRepository.deleteAll()
    }

    def "PharmacyRepository update - dirty checking success"() {
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        def entity = pharmacyRepository.save(pharmacy)

        when:
        pharmacyRepositoryService.updateAddress(entity.getId(), modifiedAddress)

        then:
        def result = pharmacyRepository.findAll()
        result.get(0).getPharmacyAddress() == modifiedAddress
    }

    def "PharmacyRepository update - dirty checking fail"() {
        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "은혜 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        def entity = pharmacyRepository.save(pharmacy)

        when:
        pharmacyRepositoryService.updateAddressWithoutTransaction(entity.getId(), modifiedAddress)

        then:
        def result = pharmacyRepository.findAll()
        result.get(0).getPharmacyAddress() == inputAddress
    }
}
