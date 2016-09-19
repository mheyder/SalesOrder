package com.mheyder.salesorder.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Coupon.
 */
@Entity
@Table(name = "coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 4, max = 10)
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Min(value = 1)
    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotNull
    @Column(name = "is_percentage", nullable = false)
    private Boolean isPercentage;

    @NotNull
    @Min(value = 0)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Min(value = 0)
    @Column(name = "minimum_price", nullable = false)
    private Long minimumPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Coupon code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Coupon description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Coupon startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Coupon endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getAmount() {
        return amount;
    }

    public Coupon amount(Long amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean isIsPercentage() {
        return isPercentage;
    }

    public Coupon isPercentage(Boolean isPercentage) {
        this.isPercentage = isPercentage;
        return this;
    }

    public void setIsPercentage(Boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Coupon quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean isIsActive() {
        return isActive;
    }

    public Coupon isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getMinimumPrice() {
        return minimumPrice;
    }

    public Coupon minimumPrice(Long minimumPrice) {
        this.minimumPrice = minimumPrice;
        return this;
    }

    public void setMinimumPrice(Long minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coupon coupon = (Coupon) o;
        if(coupon.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, coupon.id);
    }
    
    public void useCoupon() {
        if (quantity > 0) quantity--;
    }

    public boolean isValidToday() {
        LocalDate today = LocalDate.now();
        return quantity > 0 && today.isAfter(startDate) && today.isBefore(endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Coupon{" +
            "id=" + id +
            ", code='" + code + "'" +
            ", description='" + description + "'" +
            ", startDate='" + startDate + "'" +
            ", endDate='" + endDate + "'" +
            ", amount='" + amount + "'" +
            ", isPercentage='" + isPercentage + "'" +
            ", quantity='" + quantity + "'" +
            ", isActive='" + isActive + "'" +
            ", minimumPrice='" + minimumPrice + "'" +
            '}';
    }
}
