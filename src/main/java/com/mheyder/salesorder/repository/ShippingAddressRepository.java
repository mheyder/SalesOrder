package com.mheyder.salesorder.repository;

import com.mheyder.salesorder.domain.ShippingAddress;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ShippingAddress entity.
 */
@SuppressWarnings("unused")
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress,Long> {

    @Query("select shippingAddress from ShippingAddress shippingAddress where shippingAddress.user.login = ?#{principal.username}")
    List<ShippingAddress> findByUserIsCurrentUser();

}
