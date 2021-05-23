package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class PriceTag extends BaseEntity {

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "from_date", nullable = false)
    private LocalDate from;

    @Column(name = "to_date")
    private LocalDate to;

    @Column(name = "is_promotion", nullable = false)
    private Boolean isPromotion;

    public PriceTag() {
        super();
    }

    public PriceTag(Double price, LocalDate from, LocalDate to, Boolean isPromotion) {
        this();
        this.setPrice(price);
        this.setFrom(from);
        this.setTo(to);
        this.setIsPromotion(isPromotion);
    }

    public PriceTag(Double price, Boolean isPromotion) {
        this(price, LocalDate.now(), null, isPromotion);
    }

    public void deprecate() {
        this.setTo(LocalDate.now());
    }
}
