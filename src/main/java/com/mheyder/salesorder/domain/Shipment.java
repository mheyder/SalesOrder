package com.mheyder.salesorder.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import com.mheyder.salesorder.domain.enumeration.ShipmentStatus;

/**
 * A Shipment.
 */
@Entity
@Table(name = "shipment")
public class Shipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 5, max = 20)
    @Column(name = "code", length = 20, nullable = false)
    private String code;

    @Column(name = "note")
    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @OneToOne(mappedBy = "shipment")
    @JsonIgnore
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Shipment code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public Shipment note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public Shipment status(ShipmentStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public Shipment order(Order order) {
        this.order = order;
        return this;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Shipment shipment = (Shipment) o;
        if(shipment.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, shipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Shipment{" +
            "id=" + id +
            ", code='" + code + "'" +
            ", note='" + note + "'" +
            ", status='" + status + "'" +
            '}';
    }
}
