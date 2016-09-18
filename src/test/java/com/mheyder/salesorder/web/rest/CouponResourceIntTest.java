package com.mheyder.salesorder.web.rest;

import com.mheyder.salesorder.SalesOrderApp;

import com.mheyder.salesorder.domain.Coupon;
import com.mheyder.salesorder.repository.CouponRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CouponResource REST controller.
 *
 * @see CouponResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SalesOrderApp.class)
public class CouponResourceIntTest {

    private static final String DEFAULT_CODE = "AAAA";
    private static final String UPDATED_CODE = "BBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_AMOUNT = 1L;
    private static final Long UPDATED_AMOUNT = 2L;

    private static final Boolean DEFAULT_IS_PERCENTAGE = false;
    private static final Boolean UPDATED_IS_PERCENTAGE = true;

    private static final Integer DEFAULT_QUANTITY = 0;
    private static final Integer UPDATED_QUANTITY = 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    @Inject
    private CouponRepository couponRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCouponMockMvc;

    private Coupon coupon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CouponResource couponResource = new CouponResource();
        ReflectionTestUtils.setField(couponResource, "couponRepository", couponRepository);
        this.restCouponMockMvc = MockMvcBuilders.standaloneSetup(couponResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createEntity(EntityManager em) {
        Coupon coupon = new Coupon()
                .code(DEFAULT_CODE)
                .description(DEFAULT_DESCRIPTION)
                .startDate(DEFAULT_START_DATE)
                .endDate(DEFAULT_END_DATE)
                .amount(DEFAULT_AMOUNT)
                .isPercentage(DEFAULT_IS_PERCENTAGE)
                .quantity(DEFAULT_QUANTITY)
                .isActive(DEFAULT_IS_ACTIVE);
        return coupon;
    }

    @Before
    public void initTest() {
        coupon = createEntity(em);
    }

    @Test
    @Transactional
    public void createCoupon() throws Exception {
        int databaseSizeBeforeCreate = couponRepository.findAll().size();

        // Create the Coupon

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isCreated());

        // Validate the Coupon in the database
        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeCreate + 1);
        Coupon testCoupon = coupons.get(coupons.size() - 1);
        assertThat(testCoupon.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCoupon.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCoupon.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testCoupon.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testCoupon.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testCoupon.isIsPercentage()).isEqualTo(DEFAULT_IS_PERCENTAGE);
        assertThat(testCoupon.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testCoupon.isIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setCode(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setStartDate(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setEndDate(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setAmount(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsPercentageIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setIsPercentage(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setQuantity(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = couponRepository.findAll().size();
        // set the field null
        coupon.setIsActive(null);

        // Create the Coupon, which fails.

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isBadRequest());

        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCoupons() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);

        // Get all the coupons
        restCouponMockMvc.perform(get("/api/coupons?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
                .andExpect(jsonPath("$.[*].isPercentage").value(hasItem(DEFAULT_IS_PERCENTAGE.booleanValue())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
                .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    public void getCoupon() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);

        // Get the coupon
        restCouponMockMvc.perform(get("/api/coupons/{id}", coupon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(coupon.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.isPercentage").value(DEFAULT_IS_PERCENTAGE.booleanValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCoupon() throws Exception {
        // Get the coupon
        restCouponMockMvc.perform(get("/api/coupons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCoupon() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);
        int databaseSizeBeforeUpdate = couponRepository.findAll().size();

        // Update the coupon
        Coupon updatedCoupon = couponRepository.findOne(coupon.getId());
        updatedCoupon
                .code(UPDATED_CODE)
                .description(UPDATED_DESCRIPTION)
                .startDate(UPDATED_START_DATE)
                .endDate(UPDATED_END_DATE)
                .amount(UPDATED_AMOUNT)
                .isPercentage(UPDATED_IS_PERCENTAGE)
                .quantity(UPDATED_QUANTITY)
                .isActive(UPDATED_IS_ACTIVE);

        restCouponMockMvc.perform(put("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCoupon)))
                .andExpect(status().isOk());

        // Validate the Coupon in the database
        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeUpdate);
        Coupon testCoupon = coupons.get(coupons.size() - 1);
        assertThat(testCoupon.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCoupon.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCoupon.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testCoupon.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testCoupon.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testCoupon.isIsPercentage()).isEqualTo(UPDATED_IS_PERCENTAGE);
        assertThat(testCoupon.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCoupon.isIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void deleteCoupon() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);
        int databaseSizeBeforeDelete = couponRepository.findAll().size();

        // Get the coupon
        restCouponMockMvc.perform(delete("/api/coupons/{id}", coupon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeDelete - 1);
    }
}
