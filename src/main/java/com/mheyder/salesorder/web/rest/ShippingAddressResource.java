package com.mheyder.salesorder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mheyder.salesorder.domain.ShippingAddress;

import com.mheyder.salesorder.repository.ShippingAddressRepository;
import com.mheyder.salesorder.web.rest.util.HeaderUtil;
import com.mheyder.salesorder.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ShippingAddress.
 */
@RestController
@RequestMapping("/api")
public class ShippingAddressResource {

    private final Logger log = LoggerFactory.getLogger(ShippingAddressResource.class);
        
    @Inject
    private ShippingAddressRepository shippingAddressRepository;

    /**
     * POST  /shipping-addresses : Create a new shippingAddress.
     *
     * @param shippingAddress the shippingAddress to create
     * @return the ResponseEntity with status 201 (Created) and with body the new shippingAddress, or with status 400 (Bad Request) if the shippingAddress has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/shipping-addresses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ShippingAddress> createShippingAddress(@Valid @RequestBody ShippingAddress shippingAddress) throws URISyntaxException {
        log.debug("REST request to save ShippingAddress : {}", shippingAddress);
        if (shippingAddress.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("shippingAddress", "idexists", "A new shippingAddress cannot already have an ID")).body(null);
        }
        ShippingAddress result = shippingAddressRepository.save(shippingAddress);
        return ResponseEntity.created(new URI("/api/shipping-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("shippingAddress", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /shipping-addresses : Updates an existing shippingAddress.
     *
     * @param shippingAddress the shippingAddress to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated shippingAddress,
     * or with status 400 (Bad Request) if the shippingAddress is not valid,
     * or with status 500 (Internal Server Error) if the shippingAddress couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/shipping-addresses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ShippingAddress> updateShippingAddress(@Valid @RequestBody ShippingAddress shippingAddress) throws URISyntaxException {
        log.debug("REST request to update ShippingAddress : {}", shippingAddress);
        if (shippingAddress.getId() == null) {
            return createShippingAddress(shippingAddress);
        }
        ShippingAddress result = shippingAddressRepository.save(shippingAddress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("shippingAddress", shippingAddress.getId().toString()))
            .body(result);
    }

    /**
     * GET  /shipping-addresses : get all the shippingAddresses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of shippingAddresses in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/shipping-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ShippingAddress>> getAllShippingAddresses(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ShippingAddresses");
        Page<ShippingAddress> page = shippingAddressRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/shipping-addresses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /shipping-addresses/:id : get the "id" shippingAddress.
     *
     * @param id the id of the shippingAddress to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the shippingAddress, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/shipping-addresses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ShippingAddress> getShippingAddress(@PathVariable Long id) {
        log.debug("REST request to get ShippingAddress : {}", id);
        ShippingAddress shippingAddress = shippingAddressRepository.findOne(id);
        return Optional.ofNullable(shippingAddress)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /shipping-addresses/:id : delete the "id" shippingAddress.
     *
     * @param id the id of the shippingAddress to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/shipping-addresses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteShippingAddress(@PathVariable Long id) {
        log.debug("REST request to delete ShippingAddress : {}", id);
        shippingAddressRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("shippingAddress", id.toString())).build();
    }

}
