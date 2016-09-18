package com.mheyder.salesorder.repository;

import com.mheyder.salesorder.domain.OrderItem;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OrderItem entity.
 */
@SuppressWarnings("unused")
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
