package com.mheyder.salesorder.repository;

import com.mheyder.salesorder.domain.Order;
import com.mheyder.salesorder.domain.enumeration.OrderStatus;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Order entity.
 */
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("select order from Order order where order.user.login = ?#{principal.username}")
    List<Order> findByUserIsCurrentUser();

    @Query("select order from Order order where order.status = ?1 and order.user.login = ?#{principal.username}")
	List<Order> findByStatusAndUserIsCurrentUser(OrderStatus status);

}
