package com.mrsisa.pharmacy;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.*;
import com.mrsisa.pharmacy.domain.valueobjects.*;
import com.mrsisa.pharmacy.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
public class DataLoader implements ApplicationRunner {
    private final IAuthorityRepository authorityRepository;
    private final IUserRepository userRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IEmploymentContractRepository employmentContractRepository;
    private final IPatientCategoryRepository patientCategoryRepository;
    private final IMedicineRepository medicineRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IAppointmentPriceRepository appointmentPriceRepository;
    private final IOrderRepository orderRepository;
    private final IOfferRepository offerRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IMissingMedicineLogRepository missingMedicineLogRepository;
    private final ILeaveDaysRequestRepository leaveDaysRequestRepository;
    private final IReviewRepository reviewRepository;
    private final IMedicinePurchaseRepository medicinePurchaseRepository;
    private final IRecipeRepository recipeRepository;
    private final IPromotionRepository promotionRepository;
    private final ISystemSettingsRepository systemSettingsRepository;

    private final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    public DataLoader(IAuthorityRepository authorityRepository, IUserRepository userRepository, IPharmacyRepository pharmacyRepository, IEmploymentContractRepository employmentContractRepository, IPatientCategoryRepository patientCategoryRepository, IMedicineRepository medicineRepository, IAppointmentRepository appointmentRepository, IAppointmentPriceRepository appointmentPriceRepository, IOrderRepository orderRepository, IOfferRepository offerRepository, IMedicineReservationRepository medicineReservationRepository, IMedicineStockRepository medicineStockRepository, IMissingMedicineLogRepository missingMedicineLogRepository, ILeaveDaysRequestRepository leaveDaysRequestRepository, IReviewRepository reviewRepository, IMedicinePurchaseRepository medicinePurchaseRepository, IRecipeRepository recipeRepository, IPromotionRepository promotionRepository, ISystemSettingsRepository systemSettingsRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.employmentContractRepository = employmentContractRepository;
        this.patientCategoryRepository = patientCategoryRepository;
        this.medicineRepository = medicineRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentPriceRepository = appointmentPriceRepository;
        this.orderRepository = orderRepository;
        this.offerRepository = offerRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.missingMedicineLogRepository = missingMedicineLogRepository;
        this.leaveDaysRequestRepository = leaveDaysRequestRepository;
        this.reviewRepository = reviewRepository;
        this.medicinePurchaseRepository = medicinePurchaseRepository;
        this.recipeRepository = recipeRepository;
        this.promotionRepository = promotionRepository;
        this.systemSettingsRepository = systemSettingsRepository;
    }

    @Override
    @Transactional
    @SuppressWarnings("unused")
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting database initialization...");

        // Create authorities
        Authority systemAdminAuthority = createAuthority("ROLE_SYSTEM_ADMIN");
        Authority pharmacyAdminAuthority = createAuthority("ROLE_PHARMACY_ADMIN");
        Authority patientAuthority = createAuthority("ROLE_PATIENT");
        Authority pharmacistAuthority = createAuthority("ROLE_PHARMACIST");
        Authority dermatologistAuthority = createAuthority("ROLE_DERMATOLOGIST");
        Authority supplierAuthority = createAuthority("ROLE_SUPPLIER");

        // Create system admins
        SystemAdmin systemAdmin1 = new SystemAdmin("Stanko", "Antic", "stankoantic", "test123", "stankoantic@gmail.com", true, true);
        systemAdmin1.getAuthorities().add(systemAdminAuthority);
        SystemAdmin systemAdmin2 = new SystemAdmin("Pera", "Zivanovic", "perazivanovic", "test123", "perazivanovic@gmail.com", true, true);
        systemAdmin2.getAuthorities().add(systemAdminAuthority);
        SystemAdmin systemAdmin3 = new SystemAdmin("admin", "admin", "admin", "admin", "admin@gmail.com", true, true);
        systemAdmin3.getAuthorities().add(systemAdminAuthority);
        userRepository.save(systemAdmin1);
        userRepository.save(systemAdmin2);
        userRepository.save(systemAdmin3);

        // Create patient categories
        PatientCategory defaultCategory = createPatientCategory("Default category", 0, 0, "#ffffff");
        PatientCategory bronzeCategory = createPatientCategory("Bronze", 1000, 3, "#632201");
        PatientCategory silverCategory = createPatientCategory("Silver", 2000, 6, "#bfb9b6");
        PatientCategory goldCategory = createPatientCategory("Gold", 3000, 9, "#eba502");
        PatientCategory platinumCategory = createPatientCategory("Platinum", 4000, 15, "#14ffd4");


        // Create patients
        Patient p6 = createPatient("pera", "", 1650, 2, bronzeCategory, "0601133327", getNoviSadAddress("Gogoljeva", "14"), patientAuthority);
        Patient p1 = createPatient("Dejan", "Djordjevic", 1650, 1, bronzeCategory, "0601133327", getNoviSadAddress("Gogoljeva", "14"), patientAuthority);
        Patient p2 = createPatient("Ljiljana", "Petrovic", 2200, 1, silverCategory, "456", getNoviSadAddress("Radnicka", "88A"), patientAuthority);
        Patient p3 = createPatient("Pera", "Tanackovic", 3780, 1, goldCategory, "789", getNoviSadAddress("Sumadijska", "22"), patientAuthority);
        Patient p4 = createPatient("Ivana", "Mandic", 9000, 0, platinumCategory, "199", getNoviSadAddress("Resavska", "60"), patientAuthority);
        Patient p5 = createPatient("Pera", "Pera", 3600, 3, goldCategory, "199333111", getNoviSadAddress("Resavska", "62"), patientAuthority);

        Complaint c1 = createComplaint("losa usluga", ComplaintType.PHARMACY, p1, "Benu Apoteka");
        Complaint c2 = createComplaint("dugo sam cekao", ComplaintType.EMPLOYEE, p1, "Andrea Todorovic");
        ComplaintReply reply = new ComplaintReply("zao nam je", LocalDateTime.now(), systemAdmin1, c1);
        c1.setReply(reply);

        // Create pharmacies and their admins
        Location benuLocation = new Location(45.25407418051719, 19.84837710688678, getNoviSadAddress("Bulevar Mihajla Pupina", "9"));
        Pharmacy benu = new Pharmacy("Benu Apoteka", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer magna tortor, commodo elementum augue hendrerit, aliquet porttitor odio. Etiam efficitur pellentesque velit sit amet malesuada.", benuLocation);
        benu.setAverageGrade(4.86);
        benu.getComplaints().add(c1);
        Location drMaxLocation = new Location(45.248661135597416, 19.839300607106516, getNoviSadAddress("Bulevar oslobodjenja", "105"));
        Pharmacy drMax = new Pharmacy("Dr Max", "Mauris et velit vitae justo aliquet aliquam tristique et risus. Nunc luctus elit at malesuada luctus. Aliquam tincidunt felis ac sodales bibendum.", drMaxLocation);
        Location jankovicLocation = new Location(45.25681728629487, 19.81379914341574, getNoviSadAddress("Hadzi Ruvimovia", "48"));
        Pharmacy jankovic = new Pharmacy("Jankovic", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", jankovicLocation);
        Location lillyLocation = new Location(45.26128371780874, 19.81573069750041, new Address("Serbia", "Novi Sad", "Janka Veselinovica", "20", "21137"));
        Pharmacy lilly = new Pharmacy("Lilly", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", lillyLocation);

        Location dmLocation = new Location(45.26129717114014, 19.812867840128376, new Address("Serbia", "Novi Sad", "Trg Majke Jevrosime", "21", "21137"));
        Pharmacy dm = new Pharmacy("DM", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", dmLocation);

        Location ibisLocation = new Location(45.2630180312307, 19.83046478671768, new Address("Serbia", "Novi Sad", "Bulevar oslobodjenja", "4a", "21101"));
        Pharmacy ibis = new Pharmacy("Ibis", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", ibisLocation);

        Location mediGrupLocation = new Location(44.82198829616635, 20.462140149011613, new Address("Serbia", "Stari Grad Urban Municipality", "Cara Dusana", "58", "11158"));
        Pharmacy mediGrup = new Pharmacy("MediGrup", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", mediGrupLocation);

        Location apotekaBgLocation = new Location(44.82288751241458, 20.45877372595868, new Address("Serbia", "Stari Grad Urban Municipality", "Kralja Petra", "85", "11158"));
        Pharmacy apotekaBg = new Pharmacy("Apoteka Beograd", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", apotekaBgLocation);

        Location tiliaLocation = new Location(45.254689574828035, 19.8350150917808, new Address("Serbia", "Novi Sad", "Bulevar oslobodjenja", "66", "21101"));
        Pharmacy tilia = new Pharmacy("Tilia", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", tiliaLocation);

        Location livsaneLocation = new Location(45.25001916978874, 19.848136592714777, new Address("Serbia", "Novi Sad", "Strazilovska", "19a", "21101"));
        Pharmacy livsane = new Pharmacy("Livsane", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", livsaneLocation);

        Location treccaLocation = new Location(41.890558171850785, 12.506332260870309, new Address("Italy", "Rome", "Via Emanuele Filiberto", "155", "00185"));
        Pharmacy trecca = new Pharmacy("Trecca Mastrangelli", "Duis augue quam, pulvinar in condimentum id, aliquet tristique nibh. Pellentesque in facilisis velit. Aliquam eu aliquam ante. Donec a lacinia tortor.", treccaLocation);

        PharmacyAdmin benuAdmin = new PharmacyAdmin("Vidoje", "Gavrilovic", "vidojegavrilovic", "test123", "vidojegavrilovic@gmail.com", true, true, benu);
        benuAdmin.getAuthorities().add(pharmacyAdminAuthority);
        benu.getPharmacyAdmins().add(benuAdmin);
        PharmacyAdmin drMaxAdmin = new PharmacyAdmin("Mladen", "Gojkovic", "mladengojkovic", "test123", "mladengojkovic@gmail.com", true, true, drMax);
        drMaxAdmin.getAuthorities().add(pharmacyAdminAuthority);
        drMax.getPharmacyAdmins().add(drMaxAdmin);
        PharmacyAdmin jankovicAdmin = new PharmacyAdmin("Milovan", "Todorovic", "milovantodorovic", "test123", "milovantodorovic@gmail.com", true, true, jankovic);
        jankovicAdmin.getAuthorities().add(pharmacyAdminAuthority);
        jankovic.getPharmacyAdmins().add(jankovicAdmin);
        PharmacyAdmin lillyAdmin = new PharmacyAdmin("root", "root", "root", "root", "root@gmail.com", true, true, lilly);
        lillyAdmin.getAuthorities().add(pharmacyAdminAuthority);
        lilly.getPharmacyAdmins().add(lillyAdmin);
        pharmacyRepository.save(benu);
        pharmacyRepository.save(drMax);
        pharmacyRepository.save(jankovic);
        pharmacyRepository.save(lilly);
        pharmacyRepository.save(dm);
        pharmacyRepository.save(ibis);
        pharmacyRepository.save(mediGrup);
        pharmacyRepository.save(apotekaBg);
        pharmacyRepository.save(tilia);
        pharmacyRepository.save(livsane);
        pharmacyRepository.save(trecca);

        // Create pharmacists
        PharmacyEmployee ph1 = createPharmacyEmployee("Rakita", "Moldovan", EmployeeType.PHARMACIST, pharmacistAuthority, 4.2);
        PharmacyEmployee ph2 = createPharmacyEmployee("Vesna", "Janketic", EmployeeType.PHARMACIST, pharmacistAuthority, 3.7);
        PharmacyEmployee ph3 = createPharmacyEmployee("Ljubinka", "Pap", EmployeeType.PHARMACIST, pharmacistAuthority, 5.0);
        PharmacyEmployee ph4 = createPharmacyEmployee("Sara", "Velimirovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.8);
        PharmacyEmployee ph5 = createPharmacyEmployee("Neda", "Pejic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.1);
        PharmacyEmployee ph6 = createPharmacyEmployee("Mira", "Vasic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.7);

        // Only for testing
        PharmacyEmployee tempPharmacist1 = createPharmacyEmployee("Slavica", "Krstic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.7);
        PharmacyEmployee tempPharmacist2 = createPharmacyEmployee("Dragana", "Aleksic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.8);
        PharmacyEmployee tempPharmacist3 = createPharmacyEmployee("Vesna", "Nedeljkovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.95);
        PharmacyEmployee tempPharmacist4 = createPharmacyEmployee("Gorana", "Andric", EmployeeType.PHARMACIST, pharmacistAuthority, 4.2);
        PharmacyEmployee tempPharmacist5 = createPharmacyEmployee("Zorka", "Bojanic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.1);
        PharmacyEmployee tempPharmacist6 = createPharmacyEmployee("Elena", "Borisavljevic", EmployeeType.PHARMACIST, pharmacistAuthority, 3.7);
        PharmacyEmployee tempPharmacist7 = createPharmacyEmployee("Andrea", "Jovanovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.6);
        PharmacyEmployee tempPharmacist8 = createPharmacyEmployee("Bogdana", "Markovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.6);
        PharmacyEmployee tempPharmacist9 = createPharmacyEmployee("Bogdana", "Darkovic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.5);
        PharmacyEmployee tempPharmacist10 = createPharmacyEmployee("Renja", "Miljatovic", EmployeeType.PHARMACIST, pharmacistAuthority, 2.7);
        PharmacyEmployee tempPharmacist11 = createPharmacyEmployee("Sofija", "Brkic", EmployeeType.PHARMACIST, pharmacistAuthority, 3.7);
        PharmacyEmployee tempPharmacist12 = createPharmacyEmployee("Milena", "Vujic", EmployeeType.PHARMACIST, pharmacistAuthority, 4.8);

        PharmacyEmployee tempDermatologist1 = createPharmacyEmployee("Andrea", "Todorovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.7, c2);
        PharmacyEmployee tempDermatologist2 = createPharmacyEmployee("Andrea", "Novakovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.8);
        PharmacyEmployee tempDermatologist3 = createPharmacyEmployee("Snezana", "Brdjanin", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.95);
        PharmacyEmployee tempDermatologist4 = createPharmacyEmployee("Mina", "Savicevic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.2);
        PharmacyEmployee tempDermatologist5 = createPharmacyEmployee("Milana", "Lazic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.1);
        PharmacyEmployee tempDermatologist6 = createPharmacyEmployee("Jelena", "Aleksic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 3.7);
        PharmacyEmployee tempDermatologist7 = createPharmacyEmployee("Radina", "Vladimirovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.6);
        PharmacyEmployee tempDermatologist8 = createPharmacyEmployee("Djurica", "Pesic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.6);
        PharmacyEmployee tempDermatologist9 = createPharmacyEmployee("Stojanka", "Carapic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.5);
        PharmacyEmployee tempDermatologist10 = createPharmacyEmployee("Emilija", "Nikolic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 2.7);
        PharmacyEmployee tempDermatologist11 = createPharmacyEmployee("Jasna", "Pajic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 3.7);
        PharmacyEmployee tempDermatologist12 = createPharmacyEmployee("Vesna", "Evic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.8);

        // Create dermatologists
        PharmacyEmployee dm1 = createPharmacyEmployee("Divna", "Bojanic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 3.6);
        PharmacyEmployee dm2 = createPharmacyEmployee("Mirjana", "Filipovic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 5.0);
        PharmacyEmployee dm3 = createPharmacyEmployee("Anastasija", "Bojevic", EmployeeType.DERMATOLOGIST, dermatologistAuthority, 4.4);

        // Create employment contracts
        EmploymentContract ec1 = createEmploymentContract(dm3, benu, "22.03.2021.", getWorkingHours("09:00", "12:00"));
        EmploymentContract ec2 = createEmploymentContract(ph1, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));

        // ############## Testing contract ######################################
        EmploymentContract ec2a = createEmploymentContract(tempPharmacist1, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2b = createEmploymentContract(tempPharmacist2, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2c = createEmploymentContract(tempPharmacist3, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2d = createEmploymentContract(tempPharmacist4, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2e = createEmploymentContract(tempPharmacist5, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2f = createEmploymentContract(tempPharmacist6, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2g = createEmploymentContract(tempPharmacist7, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2h = createEmploymentContract(tempPharmacist8, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2i = createEmploymentContract(tempPharmacist9, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2j = createEmploymentContract(tempPharmacist10, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2k = createEmploymentContract(tempPharmacist11, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2l = createEmploymentContract(tempPharmacist12, benu, "21.03.2021.", getWorkingHours("09:00", "17:00"));

        EmploymentContract tempC = createEmploymentContract(tempDermatologist1, benu, "21.03.2021.", getWorkingHours("18:00", "20:00"));
        EmploymentContract ec2aa = createEmploymentContract(tempDermatologist1, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2bb = createEmploymentContract(tempDermatologist2, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2cc = createEmploymentContract(tempDermatologist3, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2dd = createEmploymentContract(tempDermatologist4, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2ee = createEmploymentContract(tempDermatologist5, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2ff = createEmploymentContract(tempDermatologist6, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2gg = createEmploymentContract(tempDermatologist7, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2hh = createEmploymentContract(tempDermatologist8, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2ii = createEmploymentContract(tempDermatologist9, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2jj = createEmploymentContract(tempDermatologist10, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2kk = createEmploymentContract(tempDermatologist11, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec2ll = createEmploymentContract(tempDermatologist12, drMax, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        // ##################################################################################################################
        EmploymentContract ec3 = createEmploymentContract(ph2, benu, "22.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec4 = createEmploymentContract(dm1, benu, "21.03.2021.", getWorkingHours("09:00", "14:00"));
        EmploymentContract ec5 = createEmploymentContract(dm1, drMax, "22.03.2021.", getWorkingHours("15:00", "17:00"));
        EmploymentContract ec6 = createEmploymentContract(ph3, drMax, "23.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec7 = createEmploymentContract(ph4, drMax, "22.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec8 = createEmploymentContract(dm2, drMax, "21.03.2021.", getWorkingHours("09:00", "11:00"));
        EmploymentContract ec9 = createEmploymentContract(dm2, jankovic, "24.03.2021.", getWorkingHours("11:30", "17:00"));
        EmploymentContract ec10 = createEmploymentContract(ph5, jankovic, "22.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec11 = createEmploymentContract(ph6, jankovic, "21.03.2021.", getWorkingHours("09:00", "17:00"));
        EmploymentContract ec12 = createEmploymentContract(dm3, jankovic, "23.03.2021.", getWorkingHours("14:00", "17:00"));

        // Create suppliers
        Supplier testSupplier = new Supplier("Dusan", "Erdeljan", "dusanerdeljan", "test123", "dusanerdeljan99@gmail.com", true, true, "Moja kompanija");
        Supplier testSupplier1 = new Supplier("Dusan", "Erdeljan", "dusanerdeljan1", "test123", "dusanerdeljan858@gmail.com", true, true, "Moja kompanija");
        testSupplier.getAuthorities().add(supplierAuthority);
        testSupplier1.getAuthorities().add(supplierAuthority);
        userRepository.save(testSupplier);
        userRepository.save(testSupplier1);
        Supplier s1 = createSupplier("Đurađ", "Nedeljković", "GALENIKA AD", supplierAuthority);
        Supplier s2 = createSupplier("Miša", "Jelić", "HEMOFARM AD", supplierAuthority);
        Supplier s3 = createSupplier("Milić", "Zebić", "PHARMANOVA D.O.O.", supplierAuthority);
        Supplier s4 = createSupplier("Jakov", "Matic", "FIRMA D.O.O.", supplierAuthority);



        // Create medicines
        Medicine aspirin = createMedicine("MED_1", "Aspirin", MedicineShape.TABLET, MedicineType.ANTIHISTAMINE, "acetilsalicilna kiselina", "BAYER BITTERFELD GMBH", false, 1);
        Medicine brufen = createMedicine("MED_2", "Brufen", MedicineShape.SYRUP, MedicineType.ANESTHETIC, "ibuprofen", "ABBVIE S.R.L.", false, 2);
        Medicine hepalpan = createMedicine("MED_3", "Hepalpan", MedicineShape.GEL, MedicineType.ANESTHETIC, "heparin-natrijum", "GALENIKA AD", false, 2);
        Medicine galitifen = createMedicine("MED_4", "Galitifen", MedicineShape.SYRUP, MedicineType.ANESTHETIC, "ketotifen", "GALENIKA AD", true, 2);
        Medicine itanem = createMedicine("MED_5", "Itanem", MedicineShape.SOLUTION, MedicineType.ANESTHETIC, "meropenem", "GALENIKA AD", false, 2);
        Medicine paravano = createMedicine("MED_6", "Paravano", MedicineShape.TABLET, MedicineType.ANESTHETIC, "rosuvastatin", "HEMOFARM AD", true, 2);
        Medicine soliphar = createMedicine("MED_7", "SoliPhar", MedicineShape.TABLET, MedicineType.ANESTHETIC, "solifenacin", "PHARMAS D.O.O.", true, 2);
        Medicine gabana = createMedicine("MED_8", "Gabana", MedicineShape.CAPSULE, MedicineType.ANTIBIOTIC, "pregabalin", "PHARMACEUTICALBALKANS DOO", true, 2);
        Medicine nebispes = createMedicine("MED_9", "Nebispes", MedicineShape.TABLET, MedicineType.ANTIHISTAMINE, "nebivolol", "PHARMANOVA D.O.O.", true, 2);
        Medicine tragal = createMedicine("MED_10", "Tragal", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sertralin", "GALENIKA AD", true, 2);

        Medicine m1 = createMedicine("MED_11", "Lijek1", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", false, 2);
        Medicine m2 = createMedicine("MED_12", "Lijek2", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", false, 2);
        Medicine m3 = createMedicine("MED_13", "Lijek3", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m4 = createMedicine("MED_14", "Lijek4", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m5 = createMedicine("MED_15", "Lijek5", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m6 = createMedicine("MED_16", "Lijek6", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m7 = createMedicine("MED_17", "Lijek7", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m8 = createMedicine("MED_18", "Lijek8", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m9 = createMedicine("MED_19", "Lijek9", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m10 = createMedicine("MED_20", "Lijek10", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);
        Medicine m11 = createMedicine("MED_21", "Lijek11", MedicineShape.TABLET, MedicineType.ANTIBIOTIC, "sastav", "proizvodjac", true, 2);

        // Create medicine replacements
        configureMedicineReplacements(hepalpan, nebispes, tragal);
        configureMedicineReplacements(paravano, itanem, soliphar);
        configureMedicineReplacements(galitifen, brufen, gabana);
        configureMedicineReplacements(gabana, tragal, nebispes, itanem, aspirin);
        configureMedicineReplacements(tragal, hepalpan, gabana, soliphar, itanem);

        // Add patient allergies
        configurePatientAllergies(p1, hepalpan, galitifen);
        configurePatientAllergies(p2, tragal);
        configurePatientAllergies(p3, soliphar, paravano);
        configurePatientAllergies(p4, nebispes);

        // Add medicine stock info
        addToStock(benu, aspirin, 300, 500);
        addToStock(benu, brufen, 240, 400);
        addToStock(benu, hepalpan, 400, 29);
        addToStock(benu, galitifen, 130, 50);
        addToStock(benu, gabana, 500, 0);
        addToStock(benu, itanem, 500, 0);
        addToStock(benu, nebispes, 100, 100);

        addToStock(drMax, aspirin, 310, 500);
        addToStock(drMax, brufen, 220, 400);
        addToStock(drMax, itanem, 500, 10);
        addToStock(drMax, paravano, 200, 56);

        addToStock(jankovic, aspirin, 290, 500);
        addToStock(jankovic, brufen, 260, 400);
        addToStock(jankovic, soliphar, 400, 60);
        addToStock(jankovic, gabana, 370, 100);

        // Configure pharmacy appointment prices
        configurePharmacyAppointmentPrices(benu, 3000, 3000);
        configurePharmacyAppointmentPrices(drMax, 2800, 3200);
        configurePharmacyAppointmentPrices(jankovic, 3500, 2500);

        // Create available appointments
        Appointment a1 = createAvailableAppointment("09.04.2021. 09:00", "09.04.2021. 11:30", 2800, tempC);
        Appointment a2 = createAvailableAppointment("10.04.2021. 10:30", "10.04.2021. 11:15", 3000, tempC);

        Appointment a3 = createAvailableAppointment("14.04.2021. 22:00", "14.04.2021. 23:30", 2900, tempC);
        Appointment a4 = createAvailableAppointment("17.04.2021. 15:45", "17.04.2021. 16:20", 3100, tempC);

        Appointment a5 = createAvailableAppointment("28.05.2021. 10:00", "28.05.2021. 10:30", 2950, ec2a);
        Appointment a6 = createAvailableAppointment("29.03.2021. 12:10", "29.03.2021. 12:30", 3050, ec9);


        // Create leave days request
        LeaveDaysRequest request1 = new LeaveDaysRequest(LocalDate.of(2021, 6, 10), LocalDate.of(2021, 6, 24), ec1.getPharmacyEmployee(), LeaveDaysRequestStatus.APPROVED);
        LeaveDaysRequest request2 = new LeaveDaysRequest(LocalDate.of(2021, 7, 10), LocalDate.of(2021, 7, 24), ec1.getPharmacyEmployee(), LeaveDaysRequestStatus.PENDING);
        LeaveDaysRequest request3 = new LeaveDaysRequest(LocalDate.of(2021, 10, 10), LocalDate.of(2021, 10, 24), ec1.getPharmacyEmployee(), LeaveDaysRequestStatus.REJECTED, new Rejection("Some rejection reason."));
        leaveDaysRequestRepository.save(request1);
        leaveDaysRequestRepository.save(request2);
        leaveDaysRequestRepository.save(request3);

        // overlapping appointments with appointment 1
        Appointment a16 = createAvailableAppointment("28.03.2022. 09:15", "28.03.2022. 09:45", 2800, ec1);
        Appointment a17 = createAvailableAppointment("28.03.2022. 08:15", "28.03.2022. 09:15", 2800, ec1);
        Appointment a18 = createAvailableAppointment("28.03.2022. 08:15", "28.03.2022. 09:45", 2800, ec1);


        // Create appointments which already took place
        Appointment a7 = createAppointmentWhichTookPlace("01.02.2021. 12:00", "01.02.2021. 12:30", 3000.0, tempC, p1, new Report("dijagnostika"));
        Appointment a8 = createAppointmentWhichTookPlace("01.02.2021. 16:00", "01.02.2021. 16:25", 2000.0, tempC, p2, new Report("dijagnostika"));
        Appointment a9 = createAppointmentWhichTookPlace("01.02.2021. 09:00", "01.02.2021. 09:50", 2500.0, ec2aa, p3, new Report("dijagnostika"));
        Appointment a15 = createAppointmentWhichTookPlace("01.02.2021. 10:00", "01.02.2021. 10:45", 3000.0, ec2aa, p1, new Report("dijagnostika"));
        Appointment a19 = createAppointmentWhichTookPlace("01.02.2020. 10:00", "01.02.2020. 10:45", 2000.0, ec2aa, p1, new Report("dijagnostika"));
        Appointment a20 = createAppointmentWhichTookPlace("04.03.2019. 11:00", "04.03.2019. 12:45", 1000.0, ec4, p1, new Report("dijagnostika"));

        // Add missing medicine logs (this is not valid data and is only used for testing)
        ArrayList<Medicine> logMedicines = new ArrayList<>(List.of(aspirin, brufen, galitifen));
        IntStream.range(0, 500).forEach(i -> {
            Collections.shuffle(logMedicines);
            missingMedicineLogRepository.save(new MissingMedicineLog(LocalDateTime.now(), logMedicines.get(0), a7));
            missingMedicineLogRepository.save(new MissingMedicineLog(LocalDateTime.now(), logMedicines.get(1), a7));
            missingMedicineLogRepository.save(new MissingMedicineLog(LocalDateTime.now(), logMedicines.get(2), a7));
        });
        
        Appointment a10 = createBookedAppointment("25.06.2021. 12:00", "25.06.2021. 12:30", 3000, tempC, p1);
        Appointment a11 = createBookedAppointment("23.06.2021. 18:00", "23.06.2021. 18:30", 3000, tempC, p1);
        Appointment a12 = createBookedAppointment("01.02.2022. 12:00", "01.02.2022. 12:30", 3000, tempC, p1);
        Appointment a21 = createBookedAppointment("02.05.2021. 08:00", "02.05.2021. 23:30", 1999, tempC, p1);
        Appointment a22 = createBookedAppointment("19.05.2021. 01:00", "19.05.2021. 22:30", 3500, ec2aa, p1);
        Appointment a55 = createBookedAppointment("17.05.2021. 01:00", "17.05.2021. 22:30", 3500, ec2aa, p2);
        Appointment a23 = createBookedAppointment("21.04.2021. 09:40", "21.04.2021. 12:30", 2500, ec2a, p1);
        Appointment a24 = createBookedAppointment("21.04.2021. 09:43", "21.04.2021. 12:45", 4500, ec2aa, p1);
        Appointment a13 = createBookedAppointment("01.02.2021. 12:00", "01.02.2021. 12:30", 3000, ec2, p1);

        // Create pharmacist appointments 21.06.2021 5 per day overlapping 3 pharmacies
        Appointment a25 = createAvailableAppointment("21.06.2021. 09:40", "21.06.2021. 12:30",3000,ec1);
        Appointment a26 = createAvailableAppointment("21.06.2021. 12:30", "21.06.2021. 15:30",3000,ec1);
        Appointment a27 = createAvailableAppointment("21.06.2021. 09:40", "21.06.2021. 12:30",3000,ec3);
        Appointment a28 = createAvailableAppointment("21.06.2021. 12:30", "21.06.2021. 15:30",3000,ec3);
        Appointment a29 = createAvailableAppointment("21.06.2021. 09:40", "21.06.2021. 12:30",2800,ec6);
        Appointment a30 = createAvailableAppointment("21.06.2021. 12:30", "21.06.2021. 15:30",2800,ec6);
        Appointment a31 = createAvailableAppointment("08.05.2021. 09:40", "08.05.2021. 12:30",2800,ec2aa);
        Appointment a32 = createAvailableAppointment("21.06.2021. 12:30", "21.06.2021. 15:30",2800,ec7);
        Appointment a33 = createAvailableAppointment("21.06.2021. 09:40", "21.06.2021. 12:30",3500,ec10);
        Appointment a34 = createAvailableAppointment("21.06.2021. 12:30", "21.06.2021. 15:30",3500,ec10);

        // Create pharmacist appointments 22.06.2021 2 per day overlapping 1 pharmacy
        Appointment a35 = createAvailableAppointment("22.06.2021. 09:40", "22.06.2021. 12:30",3000,ec1);
        Appointment a37 = createAvailableAppointment("22.06.2021. 09:40", "22.06.2021. 12:30",3000,ec3);
        Appointment a38 = createAvailableAppointment("22.06.2021. 12:30", "22.06.2021. 15:30",3000,ec3);

        // Create pharmacist appointments 22.06.2021 3 per day overlapping 2 pharmacies
        Appointment a39 = createAvailableAppointment("23.06.2021. 09:40", "23.06.2021. 12:30",2800,ec2aa);
        Appointment a40 = createAvailableAppointment("23.06.2021. 12:30", "23.06.2021. 15:30",2800,ec6);
        Appointment a41 = createAvailableAppointment("23.06.2021. 09:40", "23.06.2021. 12:30",2800,ec7);
        Appointment a42 = createAvailableAppointment("23.06.2021. 12:30", "23.06.2021. 15:30",2800,ec7);
        Appointment a43 = createAvailableAppointment("23.06.2021. 09:40", "23.06.2021. 12:30",3500,ec10);
        Appointment a44 = createAvailableAppointment("23.06.2021. 12:30", "23.06.2021. 15:30",3500,ec10);

        // Create pharmacist appointments 22.06.2021 3 per day overlapping 2 pharmacies that are already booked
        Appointment a49 = createBookedAppointment("23.06.2021. 09:40", "23.06.2021. 12:30",2800,ec2a, p1);
        Appointment a50 = createBookedAppointment("08.05.2021. 02:30", "08.05.2021. 22:30",2800,ec2aa, p1);
        Appointment a51 = createBookedAppointment("23.06.2021. 09:40", "23.06.2021. 12:30",2800,ec2c, p3);
        Appointment a52 = createBookedAppointment("23.06.2021. 12:30", "23.06.2021. 15:30",2800,ec2d, p4);
        Appointment a53 = createBookedAppointment("23.06.2021. 09:40", "23.06.2021. 12:30",3500,ec2e, p5);
        Appointment a54 = createBookedAppointment("23.06.2021. 13:30", "23.06.2021. 15:30",3500,ec2f, p1);

        // Test data for charts
        Appointment a71 = createAppointmentWhichTookPlace("01.04.2021. 12:00", "01.04.2021. 12:30", 3000.0, tempC, p1, new Report("dijagnostika"));
        Appointment a82 = createAppointmentWhichTookPlace("01.03.2021. 16:00", "01.03.2021. 16:25", 2000.0, tempC, p2, new Report("dijagnostika"));
        Appointment a73 = createAppointmentWhichTookPlace("01.04.2021. 12:00", "01.04.2021. 12:30", 3000.0, tempC, p1, new Report("dijagnostika"));
        Appointment a84 = createAppointmentWhichTookPlace("01.06.2021. 16:00", "01.06.2021. 16:25", 2000.0, tempC, p2, new Report("dijagnostika"));
        Appointment a75 = createAppointmentWhichTookPlace("01.07.2021. 12:00", "01.07.2021. 12:30", 3000.0, tempC, p1, new Report("dijagnostika"));
        Appointment a86 = createAppointmentWhichTookPlace("01.08.2021. 16:00", "01.08.2021. 16:25", 2000.0, tempC, p2, new Report("dijagnostika"));

        // Create medicine reservations
        MedicineReservation medicineReservation1 = createMedicineReservation(450.0, "28.03.2021. 10:00", "05.08.2021. 10:00", benu, p1, new MedicineReservationItem(2, aspirin, 300.0));
        MedicineReservation medicineReservation2 = createMedicineReservation(550.0, "28.03.2021. 10:15", "05.06.2021. 10:00", drMax, p2, new MedicineReservationItem(3, brufen, 220.0));
        MedicineReservation medicineReservation3 = createMedicineReservation(650.0, "28.03.2021. 10:20", "05.06.2021. 10:00", jankovic, p3, new MedicineReservationItem(3, aspirin, 290.0));
        MedicineReservation medicineReservation4 = createMedicineReservation(650.0, "28.03.2020. 10:20", "05.06.2020. 10:00", jankovic, p1, new MedicineReservationItem(3, aspirin, 290.0));

        // Create order
        Order order1 = createOrder("05.06.2021. 18:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen), new MedicineOrderInfo(10, aspirin), new MedicineOrderInfo(30, brufen));
        Order order2 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(50, hepalpan), new MedicineOrderInfo(100, galitifen));
        Order order21 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(55, hepalpan), new MedicineOrderInfo(101, galitifen));
        Order order22 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(51, hepalpan), new MedicineOrderInfo(99, galitifen));
        Order order23 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(52, hepalpan), new MedicineOrderInfo(98, galitifen));
        Order order24 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(53, hepalpan), new MedicineOrderInfo(97, galitifen));
        Order order25 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(54, hepalpan), new MedicineOrderInfo(96, galitifen));
        Order order31 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(55, hepalpan), new MedicineOrderInfo(101, galitifen));
        Order order32 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(51, hepalpan), new MedicineOrderInfo(99, galitifen));
        Order order33 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(52, hepalpan), new MedicineOrderInfo(98, galitifen));
        Order order34 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(53, hepalpan), new MedicineOrderInfo(97, galitifen));
        Order order35 = createOrder("06.06.2021. 19:00", benuAdmin, benu, OrderStatus.PROCESSED, new MedicineOrderInfo(54, hepalpan), new MedicineOrderInfo(96, galitifen));

        Order order3 = createOrder("07.06.2021. 17:30", drMaxAdmin, drMax, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(45, aspirin), new MedicineOrderInfo(66, brufen));
        Order order4 = createOrder("08.06.2021. 16:50", drMaxAdmin, drMax, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(71, itanem), new MedicineOrderInfo(17, paravano));

        Order order5 = createOrder("09.06.2021. 23:00", jankovicAdmin, jankovic, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(11, aspirin), new MedicineOrderInfo(54, brufen));
        Order order6 = createOrder("10.06.2021. 15:00", jankovicAdmin, jankovic, OrderStatus.WAITING_FOR_OFFERS, new MedicineOrderInfo(41, soliphar), new MedicineOrderInfo(64, gabana));

        // Create offers
        Offer offer1 = createOffer(10000.0, "02.06.2021. 17:00", s4, order1, OfferStatus.ACCEPTED);
        Offer offer11 = createOffer(12000.0, "01.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer12 = createOffer(14000.0, "03.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer13 = createOffer(8000.0, "05.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer14 = createOffer(12500.0, "04.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer15 = createOffer(9600.0, "01.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer16 = createOffer(8000.0, "12.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer17 = createOffer(11000.0, "22.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer18 = createOffer(12000.0, "04.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer19 = createOffer(11000.0, "09.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer110 = createOffer(10500.0, "08.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);
        Offer offer111 = createOffer(12400.0, "04.06.2021. 17:00", s1, order1, OfferStatus.REJECTED);

        //Offer offer2 = createOffer(12000.0, "03.06.2021. 19:00", s4, order2, OfferStatus.PENDING);

        Offer offer3 = createOffer(13000.0, "04.06.2021. 16:00", s4, order3, OfferStatus.REJECTED);

        Offer offer4 = createOffer(9000.0, "02.06.2021. 12:00", s1, order3, OfferStatus.PENDING);
        Offer offer5 = createOffer(8600.0, "04.06.2021. 11:00", s2, order4, OfferStatus.PENDING);
        Offer offer6 = createOffer(8000.0, "03.06.2021. 15:00", s3, order5, OfferStatus.PENDING);

        Order testOrder = createOrder("16.04.2021. 14:00", benuAdmin, benu, OrderStatus.WAITING_FOR_OFFERS,
                new MedicineOrderInfo(54, hepalpan),
                new MedicineOrderInfo(22, gabana),
                new MedicineOrderInfo(96, paravano, true, 390.0));
        Offer testOffer1 = createOffer(10000.0, "20.04.2021. 16:00", testSupplier, testOrder, OfferStatus.PENDING);
        Offer testOffer2 = createOffer(11000.0, "21.04.2021. 16:00", testSupplier1, testOrder, OfferStatus.PENDING);
        Offer testOffer3 = createOffer(12000.0, "22.04.2021. 16:00", testSupplier1, testOrder, OfferStatus.PENDING);

        // Test leave days requests
        createPendingLeaveDaysRequest(ec2aa, LocalDate.of(2021, 6, 10), LocalDate.of(2021, 6, 22));
        createAvailableAppointment("18.06.2021. 10:00", "18.06.2021. 10:30", 500, ec2a);
        createAvailableAppointment("19.06.2021. 10:00", "19.06.2021. 10:30", 500, ec2a);
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 5, 10), LocalDate.of(2021, 5, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 5, 23), LocalDate.of(2021, 6, 20));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 8, 10), LocalDate.of(2021, 8, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 9, 10), LocalDate.of(2021, 9, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 10, 10), LocalDate.of(2021, 10, 22));
        createPendingLeaveDaysRequest(ec2a, LocalDate.of(2021, 11, 10), LocalDate.of(2021, 11, 22));

        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 4, 10), LocalDate.of(2021, 5, 22));
        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 7, 10), LocalDate.of(2021, 7, 22));
        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 8, 10), LocalDate.of(2021, 8, 22));
        createPendingLeaveDaysRequest(ec2bb, LocalDate.of(2021, 9, 10), LocalDate.of(2021, 9, 22));
        createPendingLeaveDaysRequest(ec2ff, LocalDate.of(2021, 10, 10), LocalDate.of(2021, 10, 22));
        createPendingLeaveDaysRequest(ec2gg, LocalDate.of(2021, 11, 10), LocalDate.of(2021, 11, 22));

        // Create test medicine purchases
        IntStream.rangeClosed(1, 30).forEach(day ->  medicinePurchaseRepository.save(new MedicinePurchase(ThreadLocalRandom.current().nextInt(0, 30 + 1), 200.0, benu, LocalDate.of(2021, 4, day), aspirin)));
        IntStream.rangeClosed(1, 30).forEach(day ->  medicinePurchaseRepository.save(new MedicinePurchase(ThreadLocalRandom.current().nextInt(0, 20 + 1), 200.0, benu, LocalDate.of(2021, 4, day), brufen)));
        IntStream.rangeClosed(1, 30).forEach(day ->  medicinePurchaseRepository.save(new MedicinePurchase(ThreadLocalRandom.current().nextInt(0, 10 + 1), 200.0, benu, LocalDate.of(2021, 4, day), gabana)));

        // TODO: Add everything else according to your needs here
        // Add promotions
        Promotion promotion = new Promotion(benu, "Hajmo na nog hop, ublazite bol uz ibutop...Samo mu recite stop uz ibutop, I B U T O P :grimmacing:", LocalDate.of(2021, 5, 2), LocalDate.of(2021, 5, 14), PromotionStatus.ACTIVE);
        PromotionItem item1 = new PromotionItem(promotion, aspirin, 50);
        item1.setPriceReduction(300.0);
        PromotionItem item2 = new PromotionItem(promotion, brufen, 50);
        item2.setPriceReduction(240.0);
        promotion.getPromotionItems().addAll(List.of(item1, item2));
        promotionRepository.save(promotion);

        // Employee reviews
        Review r1 = createEmployeeReview(dm1, p3, 4);
        Review r2 = createEmployeeReview(dm1, p2, 3);

        // Configure subscriptions
        subscribe(benu, p1, p2, p3);
        subscribe(drMax, p2, p3, p4);
        subscribe(jankovic, p1, p2, p4);

        Recipe recipe1 = createRecipe(LocalDateTime.of(2021, 6, 1, 20, 4), p1, benu, new RecipeMedicineInfo(2,2,aspirin,200.0), new RecipeMedicineInfo(6, 4, brufen, 160.0));
        Recipe recipe2 = createRecipe(LocalDateTime.of(2021, 7, 14, 18, 30), p1, benu, new RecipeMedicineInfo(14, 1, aspirin, 160.0));


        SystemSettings systemSettings = new SystemSettings(3, 2);
        this.systemSettingsRepository.save(systemSettings);

        logger.info("Database initialized.");
    }

    public Recipe createRecipe(LocalDateTime time, Patient patient, Pharmacy pharmacy, RecipeMedicineInfo... recipeMedicineInfos){
        Recipe recipe = new Recipe(time, false, patient, pharmacy);
        Arrays.stream(recipeMedicineInfos).forEach(recipeMedicineInfo -> {
            recipe.getReservedMedicines().add(recipeMedicineInfo);
            recipe.setPrice(recipe.getPrice() + recipeMedicineInfo.getPrice() * recipeMedicineInfo.getQuantity());
            recipeMedicineInfo.setRecipe(recipe);
        });
        this.recipeRepository.save(recipe);
        return recipe;

    }

    public Review createEmployeeReview(PharmacyEmployee employee, Patient patient, Integer rating){
        Review review = new Review(rating, LocalDate.now(), ReviewType.EMPLOYEE, patient);
        employee.getReviews().add(review);

        employee.setAverageGrade(employee.getReviews().parallelStream()
                .reduce(
                        0d, (accumRating, rev) -> accumRating + rev.getGrade(),
                        Double::sum) / employee.getReviews().size());

        this.userRepository.save(employee);
        return this.reviewRepository.save(review);
    }
    public Complaint createComplaint(String title, ComplaintType type, Patient patient, String entity){
        return new Complaint(title, LocalDateTime.now(), type, patient,entity);

    }

    public void createPendingLeaveDaysRequest(EmploymentContract employee, LocalDate from, LocalDate to) {
        LeaveDaysRequest request = new LeaveDaysRequest(from, to, employee.getPharmacyEmployee(), LeaveDaysRequestStatus.PENDING);
        leaveDaysRequestRepository.save(request);
    }

    private MedicineReservation createMedicineReservation(Double price, String reservedAt, String reservationDeadline, Pharmacy pharmacy, Patient patient, MedicineReservationItem... medicineReservationItems) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        MedicineReservation medicineReservation = new MedicineReservation(price, LocalDateTime.parse(reservedAt, formatter), LocalDateTime.parse(reservationDeadline, formatter), ReservationStatus.RESERVED, pharmacy, patient);
        Arrays.stream(medicineReservationItems).forEach(medicineReservationItem -> {
            medicineReservationItem.setReservation(medicineReservation);
            medicineReservation.getReservedMedicines().add(medicineReservationItem);
        });
        patient.getMedicineReservations().add(medicineReservation);
        medicineReservationRepository.save(medicineReservation);
        return medicineReservation;
    }

    private Offer createOffer(Double totalPrice, String deliveryDueDate, Supplier supplier, Order order, OfferStatus status) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        Offer offer = new Offer(totalPrice, LocalDateTime.parse(deliveryDueDate, formatter), status, supplier, order);
        order.getAvailableOffers().add(offer);
        supplier.getMyOffers().add(offer);
        offerRepository.save(offer);
        return offer;
    }

    private Order createOrder(String dueDate, PharmacyAdmin admin, Pharmacy pharmacy, OrderStatus status, MedicineOrderInfo... medicineOrderInfos) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        Order order = new Order(LocalDateTime.parse(dueDate, formatter), status, admin, pharmacy);
        Arrays.stream(medicineOrderInfos).forEach(medicineOrderInfo -> {
            medicineOrderInfo.setOrder(order);
            order.getOrderItems().add(medicineOrderInfo);
        });
        admin.getPharmacy().getOrders().add(order);
        admin.getMyOrders().add(order);
        orderRepository.save(order);
        return order;
    }

    private Supplier createSupplier(String firstName, String lastName, String company, Authority authority) {
        String username = String.format("%s%s", firstName.toLowerCase(), lastName.toLowerCase());
        Supplier supplier = new Supplier(firstName, lastName, username, "test123", generateMail(username), true, true, company);
        supplier.getAuthorities().add(authority);
        userRepository.save(supplier);
        return supplier;
    }

    private void configurePharmacyAppointmentPrices(Pharmacy pharmacy, double pharmacistAppointmentPrice, double dermatologistAppointmentPrice) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        LocalDate fromDate = LocalDate.parse("21.03.2021.", formatter);
        AppointmentPrice pharmacistPrice = new AppointmentPrice(pharmacistAppointmentPrice, fromDate, null, false, pharmacy, EmployeeType.PHARMACIST);
        AppointmentPrice dermatologistPrice = new AppointmentPrice(dermatologistAppointmentPrice, fromDate, null, false, pharmacy, EmployeeType.DERMATOLOGIST);
        pharmacy.addPharmacistAppointmentPrice(pharmacistPrice);
        pharmacy.addDermatologistAppointmentPrice(dermatologistPrice);
        appointmentPriceRepository.save(pharmacistPrice);
        appointmentPriceRepository.save(dermatologistPrice);
    }

    private Appointment createBookedAppointment(String from, String to, double price, EmploymentContract contract, Patient patient) {
        return createAppointment(from, to, price, contract, patient, AppointmentStatus.BOOKED, null);
    }

    private Appointment createAvailableAppointment(String from, String to, double price, EmploymentContract contract) {
        return createAppointment(from, to, price, contract, null, AppointmentStatus.AVAILABLE, null);
    }

    private Appointment createAppointmentWhichTookPlace(String from, String to, double price, EmploymentContract contract, Patient patient, Report report) {
        return createAppointment(from, to, price, contract, patient, AppointmentStatus.TOOK_PLACE, report);
    }

    private Appointment createAppointment(String from, String to, double price, EmploymentContract contract, Patient patient, AppointmentStatus status, Report report) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        LocalDateTime dateFrom = LocalDateTime.parse(from, formatter);
        LocalDateTime dateTo = LocalDateTime.parse(to, formatter);
        Appointment appointment = new Appointment(dateFrom, dateTo, price, status, patient, contract, report);
        appointmentRepository.save(appointment);
        return appointment;
    }

    private void addToStock(Pharmacy pharmacy, Medicine medicine, double price, int quantity) {
        MedicineStock stock = new MedicineStock(quantity, pharmacy, medicine);
        StockPrice priceTag = new StockPrice(price, false, stock);
        stock.addPriceTag(priceTag);
        pharmacy.getMedicineStocks().add(stock);
        medicineStockRepository.save(stock);
    }

    private void configurePatientAllergies(Patient patient, Medicine... allergicTo) {
        Arrays.stream(allergicTo).forEach(medicine -> patient.getAllergicTo().add(medicine));
        userRepository.save(patient);
    }

    private Medicine createMedicine(String code, String name, MedicineShape shape, MedicineType type, String composition, String manufacturer, boolean issueOnRecipe, Integer points) {
        Medicine medicine = new Medicine(code, name, shape, type, composition, manufacturer, issueOnRecipe, "No additional notes.", points);
        medicineRepository.save(medicine);
        return medicine;
    }

    private void configureMedicineReplacements(Medicine originalMedicine, Medicine... medicineReplacements) {
        Arrays.stream(medicineReplacements).forEach(replacement -> originalMedicine.getReplacements().add(replacement));
        medicineRepository.save(originalMedicine);
    }

    private void subscribe(Pharmacy pharmacy, Patient... subscribers) {
        Arrays.stream(subscribers).forEach(subscriber -> {
            pharmacy.getPromotionSubscribers().add(subscriber);
            subscriber.getSubscribedTo().add(pharmacy);
        });
        pharmacyRepository.save(pharmacy);
    }

    private EmploymentContract createEmploymentContract(PharmacyEmployee employee, Pharmacy pharmacy, String from, Collection<WorkingDay> workingHours) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        EmploymentContract contract = new EmploymentContract(LocalDate.parse(from, formatter), null, employee, pharmacy);
        employee.getContracts().add(contract);
        pharmacy.getEmployees().add(contract);
        workingHours.forEach(workingDay -> {
            workingDay.setEmployee(contract);
            contract.getWorkingHours().add(workingDay);
        });
        employmentContractRepository.save(contract);
        return contract;
    }

    private Patient createPatient(String firstName, String lastName, int numPoints, int numPenalties, PatientCategory category, String phoneNumber, Address address, Authority authority) {
        String username = String.format("%s%s", firstName.toLowerCase(), lastName.toLowerCase());
        Patient patient = new Patient(firstName, lastName, username, "test123", generateMail(username), true, true, numPoints, numPenalties, phoneNumber, category, address);
        patient.getAuthorities().add(authority);
        userRepository.save(patient);
        return patient;
    }

    private PharmacyEmployee createPharmacyEmployee(String firstName, String lastName, EmployeeType employeeType, Authority authority, Double averageGrade, Complaint... complaints) {
        String username = String.format("%s%s", firstName.toLowerCase(), lastName.toLowerCase());
        PharmacyEmployee employee = new PharmacyEmployee(firstName, lastName, username, "test123", generateMail(username), true, true, employeeType);
        employee.getAuthorities().add(authority);
        employee.setAverageGrade(averageGrade);
        Arrays.stream(complaints).forEach(complaint -> {
            employee.getComplaints().add(complaint);
        });

        userRepository.save(employee);
        return employee;
    }

    private Address getNoviSadAddress(String street, String streetNumber) {
        return new Address("Srbija", "Novi Sad", street, streetNumber, "21000");
    }

    private Collection<WorkingDay> getWorkingHours(String fromTime, String toTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime beginTimestamp = LocalTime.parse(fromTime, formatter);
        LocalTime endTimestamp = LocalTime.parse(toTime, formatter);
        return List.of(
                new WorkingDay(DayOfWeek.MONDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.TUESDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.WEDNESDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.THURSDAY, beginTimestamp, endTimestamp),
                new WorkingDay(DayOfWeek.FRIDAY, beginTimestamp, endTimestamp)
        );
    }

    private PatientCategory createPatientCategory(String categoryName, int numPoints, int discount, String color) {
        PatientCategory patientCategory = new PatientCategory(categoryName, numPoints, discount, color);
        patientCategoryRepository.save(patientCategory);
        return patientCategory;
    }

    private Authority createAuthority(String roleName) {
        Authority authority = new Authority(roleName);
        authorityRepository.save(authority);
        return authority;
    }

    private String generateMail(String username) {
        return String.format("%s@gmail.com", username);
    }
}