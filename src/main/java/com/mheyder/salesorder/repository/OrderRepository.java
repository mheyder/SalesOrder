package com.mheyder.salesorder.repository;

import com.mheyder.salesorder.domain.Order;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("select order from Order order where order.user.login = ?#{principal.username}")
    List<Order> findByUserIsCurrentUser();

}
