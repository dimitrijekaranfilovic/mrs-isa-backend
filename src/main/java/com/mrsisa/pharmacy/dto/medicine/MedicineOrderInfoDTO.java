package com.mrsisa.pharmacy.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.criteria.CriteriaBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineOrderInfoDTO {

    private Long id;
    private Long medicineId;
    private String medicineName;
    private Integer quantity;
    private Boolean isNew;
    private Double medicinePrice;

}
