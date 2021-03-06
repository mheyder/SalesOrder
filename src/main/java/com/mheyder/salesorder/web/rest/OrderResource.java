package com.mheyder.salesorder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mheyder.salesorder.domain.Order;
import com.mheyder.salesorder.domain.OrderItem;
import com.mheyder.salesorder.domain.Product;
import com.mheyder.salesorder.domain.enumeration.OrderStatus;
import com.mheyder.salesorder.repository.OrderRepository;
import com.mheyder.salesorder.repository.ProductRepository;
import com.mheyder.salesorder.security.AuthoritiesConstants;
import com.mheyder.salesorder.security.SecurityUtils;
import com.mheyder.salesorder.service.OrderService;
import com.mheyder.salesorder.service.UserService;
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
 * REST controller for managing Order.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);
        
    @Inject
    private OrderRepository orderRepository;
    
    @Inject
    private ProductRepository productRepository;
    
    @Inject
    private UserService userService;

    @Inject
    private OrderService orderService;
    
    /**
     * POST  /orders : Add OrderItem to a new or existing Order.
     *
     * @param orderItem the orderItem to add
     * @return the ResponseEntity with status 201 (Created) and with body the order, or with status 400 (Bad Request) if not valid
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/orders",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //TODO create test for addOrderItem()
    public ResponseEntity<Order> addOrderItem(@Valid @RequestBody OrderItem orderItem) throws URISyntaxException {
        log.debug("REST request to add OrderItem : {}", orderItem);
        orderItem.setId(null);
        // validate      
        Product product = orderItem.getProduct();
        if (product != null && product.getId() != null) {
        	product = productRepository.findOne(product.getId());
        	orderItem.setProduct(product);
        }
        if (product == null || product.getId() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("product", "notexists", "Product not exists")).body(null);
        }
        if (orderItem.getQuantity() > product.getQuantity()) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("product", "nostock", ((product.getQuantity() == 0) ? "no stock" : "Stock only" + product.getQuantity() + "left"))).body(null);
        }
        
        Order order;
        List<Order> orders = orderRepository.findByStatusAndUserIsCurrentUser(OrderStatus.NEW);
        order = (!orders.isEmpty()) ? orders.get(0) : new Order().status(OrderStatus.NEW)
        		.user(userService.getUserWithAuthorities());
        order.addOrderItem(orderItem);
        
        Order result = orderRepository.save(order);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("order", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /orders : Updates an existing order.
     *
     * @param updatedOrder the order to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated order,
     * or with status 400 (Bad Request) if the order is not valid,
     * or with status 500 (Internal Server Error) if the order couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/orders/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //TODO delete updateOrderOld()
    public ResponseEntity<Order> updateOrderOld(@Valid @RequestBody Order updatedOrder, 
            @PathVariable Long id, @RequestParam("action") String action) throws URISyntaxException {
        log.debug("REST request to update Order : {}", updatedOrder);
        Order currentOrder = (id != null) ? orderRepository.findOne(id) : null;
        if (currentOrder == null || id == null) {
            //bad request
        }
        
        Order result = null;
        OrderStatus currentStatus = currentOrder.getStatus();
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.USER)) {
            if (currentStatus == OrderStatus.NEW) { 
                // user perform update
                currentOrder.orderItems(updatedOrder.getOrderItems())
                .note(updatedOrder.getNote())
                .shippingAddress(updatedOrder.getShippingAddress())
                .coupon(updatedOrder.getCoupon());
                result = (action != null && action.equalsIgnoreCase("submit"))
                        ? orderService.submitOrder(currentOrder) : orderRepository.save(currentOrder);
            } else if (currentStatus == OrderStatus.PENDING && updatedOrder.getPaymentInfo() != null) {
                // user provide payment info
                result = orderRepository.save(currentOrder.paymentInfo(updatedOrder.getPaymentInfo()));
            }
        } else if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            if (currentStatus == OrderStatus.PENDING && action != null && action.equalsIgnoreCase("approve")) {
                // admin approve order
                if (currentOrder.approveOrder()) {
                    result = orderRepository.save(currentOrder);
                }
            } else if (currentStatus == OrderStatus.PENDING && action != null && action.equalsIgnoreCase("reject")) {
                // admin reject order
                result = orderService.rejectOrder(currentOrder);
            } else if (currentStatus == OrderStatus.PAID && updatedOrder != null && updatedOrder.getShipment() != null) {
                // admin provide shipment info
                result = orderRepository.save(currentOrder.shipment(updatedOrder.getShipment()));
            }
        }
        
        return (result != null) ? ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("order", result.getId().toString())).body(result)
                : ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("order", "notvalid", "Request not valid")).body(null);
        
    }

    /**
     * GET  /orders : get all the orders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/orders",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Order>> getAllOrders(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Orders");
        
        Page<Order> page = orderRepository.findAll(pageable);
                
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /orders/:id : get the "id" order.
     *
     * @param id the id of the order to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the order, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/orders/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Order order = orderRepository.findOne(id);
        return Optional.ofNullable(order)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /orders/:id : delete the "id" order.
     *
     * @param id the id of the order to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/orders/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //TODO Delete deleteOrder();
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("order", id.toString())).build();
    }

}
