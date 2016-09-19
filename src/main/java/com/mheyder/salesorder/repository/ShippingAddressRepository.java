package com.mheyder.salesorder.repository;

import com.mheyder.salesorder.domain.ShippingAddress;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ShippingAddress entity.
 */
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress,Long> {

    @Query("select shippingAddress from ShippingAddress shippingAddress where shippingAddress.user.login = ?#{principal.username}")
    Page<ShippingAddress> findByUserIsCurrentUser(Pageable p);

}
