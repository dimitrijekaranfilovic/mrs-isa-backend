package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.MedicinePurchase;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;
import com.mrsisa.pharmacy.repository.IMedicinePurchaseRepository;
import com.mrsisa.pharmacy.repository.IPharmacyRepository;
import com.mrsisa.pharmacy.service.IMedicinePurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MedicinePurchaseService extends JPAService<MedicinePurchase> implements IMedicinePurchaseService {
    private final IMedicinePurchaseRepository medicinePurchaseRepository;
    private final IPharmacyRepository pharmacyRepository;

    @Autowired
    public MedicinePurchaseService(IMedicinePurchaseRepository medicinePurchaseRepository,
                                   IPharmacyRepository pharmacyRepository) {
        this.medicinePurchaseRepository = medicinePurchaseRepository;
        this.pharmacyRepository = pharmacyRepository;
    }

    @Override
    protected JpaRepository<MedicinePurchase, Long> getEntityRepository() {
        return medicinePurchaseRepository;
    }

    @Override
    public void createMedicinePurchaseFromMedicineReservationItem(MedicineReservationItem medicineReservationItem,
                                                                  Pharmacy pharmacy) {
        MedicinePurchase medicinePurchase = new MedicinePurchase(medicineReservationItem.getQuantity(),
                medicineReservationItem.getPrice(), pharmacy, LocalDate.now(), medicineReservationItem.getMedicine());
        medicinePurchaseRepository.save(medicinePurchase);
    }
}
