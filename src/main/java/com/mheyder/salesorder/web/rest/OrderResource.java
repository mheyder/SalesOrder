package com.mheyder.salesorder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mheyder.salesorder.domain.Order;
import com.mheyder.salesorder.domain.OrderItem;
import com.mheyder.salesorder.domain.Product;
import com.mheyder.salesorder.domain.enumeration.OrderStatus;
import com.mheyder.salesorder.repository.OrderRepository;
import com.mheyder.salesorder.repository.ProductRepository;
import com.mheyder.salesorder.repository.UserRepository;
import com.mheyder.salesorder.security.SecurityUtils;
import com.mheyder.salesorder.service.OrderService;
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
    private UserRepository userRepository;

    @Inject
    private OrderService orderService;
    
    /**
     * POST  /orders : Create a new order.
     *
     * @param order the order to create
     * @return the ResponseEntity with status 201 (Created) and with body the new order, or with status 400 (Bad Request) if the order has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/orders",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //TODO remove createOrder()
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (order.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("order", "idexists", "A new order cannot already have an ID")).body(null);
        }
        Order result = orderRepository.save(order);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("order", result.getId().toString()))
            .body(result);
    }
    
    @RequestMapping(value = "/cart",
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
        if (orders.isEmpty()) {
        	//create new order
        	order = new Order().status(OrderStatus.NEW)
        			.user(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get())        			
        			.addOrderItem(orderItem);
        } else {
        	order = orders.get(0);
        	boolean isExisted = false;
        	for (OrderItem item : order.getOrderItems()) {
        		if (item.getProduct().getId() == product.getId()) {
        			// product already existed
        			item.setQuantity(item.getQuantity() + orderItem.getQuantity());
        			isExisted = true;
        			break;
        		}
        	}
        	if (!isExisted) order.addOrderItem(orderItem);
        }
        
        Order result = orderRepository.save(order);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("order", result.getId().toString()))
            .body(result);
    }
    
    @RequestMapping(value = "/cart",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
  //TODO create test for updateOrder()
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody Order newOrder) throws URISyntaxException {
        log.debug("REST request to update Order : {}", newOrder);
        List<Order> orders = orderRepository.findByStatusAndUserIsCurrentUser(OrderStatus.NEW);
        Order order = !orders.isEmpty() ? orders.get(0) : null;
        if (newOrder.getId() == null || order == null || newOrder.getId() != order.getId()) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("order", "notexists", "Order not exists")).body(null);
        }
        
        // update order
        order.orderItems(newOrder.getOrderItems()).note(newOrder.getNote());
        
        if (newOrder.getStatus() == OrderStatus.PENDING) {
        	order = orderService.submitOrder(order);
        } else {
        	order = orderRepository.save(order);
        }
        
        return (order != null) ? ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("order", order.getId().toString()))
            .body(order)
            : ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("order", "notvalid", "Order not valid")).body(null);
    }

    /**
     * PUT  /orders : Updates an existing order.
     *
     * @param order the order to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated order,
     * or with status 400 (Bad Request) if the order is not valid,
     * or with status 500 (Internal Server Error) if the order couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/orders",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    //TODO delete updateOrderOld()
    public ResponseEntity<Order> updateOrderOld(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to update Order : {}", order);
        if (order.getId() == null) {
            return createOrder(order);
        }
        Order result = orderRepository.save(order);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("order", order.getId().toString()))
            .body(result);
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
