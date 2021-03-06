package com.mheyder.salesorder.service;

import java.time.LocalDate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mheyder.salesorder.domain.Coupon;
import com.mheyder.salesorder.domain.Order;
import com.mheyder.salesorder.domain.OrderItem;
import com.mheyder.salesorder.domain.Product;
import com.mheyder.salesorder.domain.enumeration.OrderStatus;
import com.mheyder.salesorder.repository.CouponRepository;
import com.mheyder.salesorder.repository.OrderRepository;
import com.mheyder.salesorder.repository.ProductRepository;

@Service
@Transactional
public class OrderService {
	
	@Inject
	OrderRepository orderRepository;
	
	@Inject
	CouponRepository couponRepository;
	
	@Inject
	ProductRepository productRepository;

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    public Order submitOrder(Order order) {
    	log.debug("User submit Order{}", order);
    	for (OrderItem item : order.getOrderItems()) {
    		Product product = productRepository.getOne(item.getProduct().getId());
    		if (product.getQuantity() < item.getQuantity()) return null;
    		item.setProduct(product.quantity(product.getQuantity() - item.getQuantity()));
    		order.setTotalPrice(order.getTotalPrice() + (product.getPrice() * item.getQuantity()));
    		productRepository.save(product);
    	}
    	
    	LocalDate currentDate = LocalDate.now();
    	if (order.getCoupon() != null) {
    		Coupon coupon = couponRepository.findOne(order.getCoupon().getId());
    		if (!coupon.isValidToday(order.getTotalPrice())) return null;
    		coupon.useCoupon();
    		order.setCoupon(couponRepository.save(coupon));
    		long normalPrice = order.getTotalPrice();
    		long totalPrice = coupon.isIsPercentage() ? normalPrice - (normalPrice * coupon.getAmount() / 100) : normalPrice - coupon.getAmount();
    		order.setTotalPrice(totalPrice);
    	}
    	
    	return orderRepository.save(order.date(currentDate).status(OrderStatus.PENDING));
    	
    }
    
    public Order rejectOrder(Order order) {
        log.debug("User reject Order{}", order);
        // return product qty
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findOne(item.getProduct().getId());
            product.addQuantity(item.getQuantity());
            item.setProduct(productRepository.save(product));
        }
        if (order.getCoupon() != null) {
            Coupon coupon = couponRepository.getOne(order.getCoupon().getId());
            coupon.addQuantity();
            order.setCoupon(couponRepository.save(coupon));
        }
        
        return orderRepository.save(order.status(OrderStatus.CANCELLED));
    }

}
