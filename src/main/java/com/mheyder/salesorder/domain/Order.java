package com.mheyder.salesorder.domain;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.mheyder.salesorder.domain.enumeration.OrderStatus;

/**
 * A Order.
 */
@Entity
@Table(name = "sales_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "note")
    private String note;

    @Column(name = "total_price")
    private Long totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "payment_info")
    private String paymentInfo;

    @ManyToOne
    @NotNull
    private User user;

    @ManyToOne
    private Coupon coupon;

    @OneToOne
    @JoinColumn(unique = true)
    private Shipment shipment;

    @ManyToOne
    private ShippingAddress shippingAddress;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Order date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public Order note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public Order totalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Order status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public Order paymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
        return this;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public User getUser() {
        return user;
    }

    public Order user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public Order coupon(Coupon coupon) {
        this.coupon = coupon;
        return this;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public Order shipment(Shipment shipment) {
        this.shipment = shipment;
        return this;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public Order shippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Order orderItems(Set<OrderItem> orderItems) {
        setOrderItems(orderItems);
        return this;
    }

    public Order addOrderItem(OrderItem orderItem) { // add or update an OrderItem
        if (orderItem.getId() == null) { // new item
            boolean isNewItem = true;
            for (OrderItem item : orderItems) {
                if (item.getProduct().getId() == orderItem.getProduct().getId()) { // add to existing item
                    orderItem = item.merge(orderItem);
                    isNewItem = false;
                    break;
                }    			
            }
            if (isNewItem) {
                orderItems.add(orderItem);
                orderItem.setOrder(this);
            }
        } else {
            for (OrderItem item : orderItems) { //find existing item
                if (item.getId() == orderItem.getId()) {
                    orderItem = item.merge(orderItem);
                    break;
                }
            }
        }
        calculateTotalPrice();
        return this;
    }

    public Order removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        return this;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        Set<OrderItem> deleteItems = new HashSet<>();
        boolean isFound;
        for (OrderItem item : this.orderItems) {
            isFound = false;
            for (OrderItem newItem : orderItems) {
                if (item.getId() == newItem.getId()) {
                    isFound = true;
                    if (newItem.getQuantity() == 0) deleteItems.add(newItem);
                    break;
                }
            }
            if (!isFound) return; //new orderItems not valid
        }
        if (deleteItems.size() > 0) {
            for (OrderItem item : deleteItems) {
                orderItems.remove(item);
                item.setOrder(null);
            }
        }

        this.orderItems = orderItems;
    }
    
    public boolean approveOrder() {
        if (shippingAddress != null && paymentInfo != null && status == OrderStatus.PENDING) 
            status = OrderStatus.PAID;
        return status == OrderStatus.PAID;
    }
    
    public void rejectOrder() {
        if (status == OrderStatus.PENDING) status = OrderStatus.CANCELLED;
    }

    private void calculateTotalPrice() {
        totalPrice = 0L;
        for (OrderItem item : orderItems) {
            item.setPrice(item.getProduct().getPrice()); // always use latest price
            totalPrice = totalPrice + (item.getPrice() * item.getQuantity());
        }
        if (coupon != null) {
            if (coupon.isValidToday() && totalPrice >= coupon.getMinimumPrice()) {
                totalPrice -= coupon.isIsPercentage() ? (totalPrice * coupon.getAmount() / 100) : coupon.getAmount();
            } else {
                coupon = null; // not valid anymore
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        if(order.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date='" + date + "'" +
                ", note='" + note + "'" +
                ", totalPrice='" + totalPrice + "'" +
                ", status='" + status + "'" +
                ", paymentInfo='" + paymentInfo + "'" +
                '}';
    }


    public void submitted() {
        // TODO Auto-generated method stub
        date = LocalDate.now();
    }
}
