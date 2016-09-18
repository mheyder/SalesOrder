package com.mheyder.salesorder.web.rest;

import com.mheyder.salesorder.SalesOrderApp;

import com.mheyder.salesorder.domain.ShippingAddress;
import com.mheyder.salesorder.domain.User;
import com.mheyder.salesorder.repository.ShippingAddressRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ShippingAddressResource REST controller.
 *
 * @see ShippingAddressResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SalesOrderApp.class)
public class ShippingAddressResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_PHONE = "AAAAA";
    private static final String UPDATED_PHONE = "BBBBB";
    private static final String DEFAULT_ADDRESS = "AAAAA";
    private static final String UPDATED_ADDRESS = "BBBBB";

    @Inject
    private ShippingAddressRepository shippingAddressRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restShippingAddressMockMvc;

    private ShippingAddress shippingAddress;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ShippingAddressResource shippingAddressResource = new ShippingAddressResource();
        ReflectionTestUtils.setField(shippingAddressResource, "shippingAddressRepository", shippingAddressRepository);
        this.restShippingAddressMockMvc = MockMvcBuilders.standaloneSetup(shippingAddressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShippingAddress createEntity(EntityManager em) {
        ShippingAddress shippingAddress = new ShippingAddress()
                .name(DEFAULT_NAME)
                .phone(DEFAULT_PHONE)
                .address(DEFAULT_ADDRESS);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        shippingAddress.setUser(user);
        return shippingAddress;
    }

    @Before
    public void initTest() {
        shippingAddress = createEntity(em);
    }

    @Test
    @Transactional
    public void createShippingAddress() throws Exception {
        int databaseSizeBeforeCreate = shippingAddressRepository.findAll().size();

        // Create the ShippingAddress

        restShippingAddressMockMvc.perform(post("/api/shipping-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
                .andExpect(status().isCreated());

        // Validate the ShippingAddress in the database
        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findAll();
        assertThat(shippingAddresses).hasSize(databaseSizeBeforeCreate + 1);
        ShippingAddress testShippingAddress = shippingAddresses.get(shippingAddresses.size() - 1);
        assertThat(testShippingAddress.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testShippingAddress.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testShippingAddress.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = shippingAddressRepository.findAll().size();
        // set the field null
        shippingAddress.setName(null);

        // Create the ShippingAddress, which fails.

        restShippingAddressMockMvc.perform(post("/api/shipping-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
                .andExpect(status().isBadRequest());

        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findAll();
        assertThat(shippingAddresses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = shippingAddressRepository.findAll().size();
        // set the field null
        shippingAddress.setPhone(null);

        // Create the ShippingAddress, which fails.

        restShippingAddressMockMvc.perform(post("/api/shipping-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
                .andExpect(status().isBadRequest());

        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findAll();
        assertThat(shippingAddresses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = shippingAddressRepository.findAll().size();
        // set the field null
        shippingAddress.setAddress(null);

        // Create the ShippingAddress, which fails.

        restShippingAddressMockMvc.perform(post("/api/shipping-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(shippingAddress)))
                .andExpect(status().isBadRequest());

        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findAll();
        assertThat(shippingAddresses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllShippingAddresses() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);

        // Get all the shippingAddresses
        restShippingAddressMockMvc.perform(get("/api/shipping-addresses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(shippingAddress.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE.toString())))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())));
    }

    @Test
    @Transactional
    public void getShippingAddress() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);

        // Get the shippingAddress
        restShippingAddressMockMvc.perform(get("/api/shipping-addresses/{id}", shippingAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(shippingAddress.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingShippingAddress() throws Exception {
        // Get the shippingAddress
        restShippingAddressMockMvc.perform(get("/api/shipping-addresses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateShippingAddress() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);
        int databaseSizeBeforeUpdate = shippingAddressRepository.findAll().size();

        // Update the shippingAddress
        ShippingAddress updatedShippingAddress = shippingAddressRepository.findOne(shippingAddress.getId());
        updatedShippingAddress
                .name(UPDATED_NAME)
                .phone(UPDATED_PHONE)
                .address(UPDATED_ADDRESS);

        restShippingAddressMockMvc.perform(put("/api/shipping-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedShippingAddress)))
                .andExpect(status().isOk());

        // Validate the ShippingAddress in the database
        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findAll();
        assertThat(shippingAddresses).hasSize(databaseSizeBeforeUpdate);
        ShippingAddress testShippingAddress = shippingAddresses.get(shippingAddresses.size() - 1);
        assertThat(testShippingAddress.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testShippingAddress.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testShippingAddress.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void deleteShippingAddress() throws Exception {
        // Initialize the database
        shippingAddressRepository.saveAndFlush(shippingAddress);
        int databaseSizeBeforeDelete = shippingAddressRepository.findAll().size();

        // Get the shippingAddress
        restShippingAddressMockMvc.perform(delete("/api/shipping-addresses/{id}", shippingAddress.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findAll();
        assertThat(shippingAddresses).hasSize(databaseSizeBeforeDelete - 1);
    }
}
