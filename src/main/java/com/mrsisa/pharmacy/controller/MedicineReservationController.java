package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.domain.entities.MedicineReservation;
import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.dto.medicine.MedicineReservationDTO;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@RestController
@RequestMapping(value= "/api/medicine-reservations")
public class MedicineReservationController {

    private final IMedicineReservationService medicineReservationService;

    private final IPharmacyEmployeeService pharmacyEmployeeService;

    private final IMedicinePurchaseService medicinePurchaseService;

    private final IEmailService emailService;

    private final IPatientService patientService;

    private final IConverter<MedicineReservation, MedicineReservationDTO> toMedicineReservationDTO;

    @Autowired
    public MedicineReservationController(IMedicineReservationService medicineReservationService,
                                         IPharmacyEmployeeService pharmacyEmployeeService,
                                         IConverter<MedicineReservation, MedicineReservationDTO> toMedicineReservationDTO,
                                         IMedicinePurchaseService medicinePurchaseService,
                                         IEmailService emailService, IPatientService patientService)
    {
        this.medicineReservationService = medicineReservationService;
        this.pharmacyEmployeeService = pharmacyEmployeeService;
        this.toMedicineReservationDTO = toMedicineReservationDTO;
        this.medicinePurchaseService = medicinePurchaseService;
        this.emailService = emailService;
        this.patientService = patientService;
    }

    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    @GetMapping(value = "/{id}")
    public MedicineReservationDTO getMedicineReservation(@PathVariable("id") Long reservationId, Principal principal) {
        Pharmacy pharmacy = pharmacyEmployeeService.findActivePharmacyOfPharmacist(principal.getName());
        return toMedicineReservationDTO.convert(findValidMedicineReservation(reservationId, pharmacy));
    }

    @PreAuthorize("hasRole('ROLE_PHARMACIST')")
    @PutMapping(value = "/issue/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issueReservation(@PathVariable("id") Long reservationId, Principal principal) {
        pharmacyEmployeeService.findActivePharmacyOfPharmacist(principal.getName());

        MedicineReservation medicineReservation = medicineReservationService.issueReservation(reservationId);
        emailService.sendIssuedReservationMessage(medicineReservation);
    }

    private MedicineReservation findValidMedicineReservation(Long reservationId, Pharmacy pharmacy) {
        if (pharmacy == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pharmacy not found!");
        }

        MedicineReservation medicineReservation = medicineReservationService.getMedicineReservationForIssuing(reservationId,
                pharmacy.getId());
        if (medicineReservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine reservation not found!");
        }

        return medicineReservation;
    }

}