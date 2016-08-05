package com.jmlw.web.rest;

import com.jmlw.ContactsApp;
import com.jmlw.domain.Phone;
import com.jmlw.repository.PhoneRepository;
import com.jmlw.repository.search.PhoneSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PhoneResource REST controller.
 *
 * @see PhoneResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ContactsApp.class)
@WebAppConfiguration
@IntegrationTest
public class PhoneResourceIntTest {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBB";

    @Inject
    private PhoneRepository phoneRepository;

    @Inject
    private PhoneSearchRepository phoneSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPhoneMockMvc;

    private Phone phone;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PhoneResource phoneResource = new PhoneResource();
        ReflectionTestUtils.setField(phoneResource, "phoneSearchRepository", phoneSearchRepository);
        ReflectionTestUtils.setField(phoneResource, "phoneRepository", phoneRepository);
        this.restPhoneMockMvc = MockMvcBuilders.standaloneSetup(phoneResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        phoneSearchRepository.deleteAll();
        phone = new Phone();
        phone.setPhoneNumber(DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void createPhone() throws Exception {
        int databaseSizeBeforeCreate = phoneRepository.findAll().size();

        // Create the Phone

        restPhoneMockMvc.perform(post("/api/phones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(phone)))
                .andExpect(status().isCreated());

        // Validate the Phone in the database
        List<Phone> phones = phoneRepository.findAll();
        assertThat(phones).hasSize(databaseSizeBeforeCreate + 1);
        Phone testPhone = phones.get(phones.size() - 1);
        assertThat(testPhone.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);

        // Validate the Phone in ElasticSearch
        Phone phoneEs = phoneSearchRepository.findOne(testPhone.getId());
        assertThat(phoneEs).isEqualToComparingFieldByField(testPhone);
    }

    @Test
    @Transactional
    public void getAllPhones() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phones
        restPhoneMockMvc.perform(get("/api/phones?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(phone.getId().intValue())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())));
    }

    @Test
    @Transactional
    public void getPhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get the phone
        restPhoneMockMvc.perform(get("/api/phones/{id}", phone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(phone.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPhone() throws Exception {
        // Get the phone
        restPhoneMockMvc.perform(get("/api/phones/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);
        phoneSearchRepository.save(phone);
        int databaseSizeBeforeUpdate = phoneRepository.findAll().size();

        // Update the phone
        Phone updatedPhone = new Phone();
        updatedPhone.setId(phone.getId());
        updatedPhone.setPhoneNumber(UPDATED_PHONE_NUMBER);

        restPhoneMockMvc.perform(put("/api/phones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPhone)))
                .andExpect(status().isOk());

        // Validate the Phone in the database
        List<Phone> phones = phoneRepository.findAll();
        assertThat(phones).hasSize(databaseSizeBeforeUpdate);
        Phone testPhone = phones.get(phones.size() - 1);
        assertThat(testPhone.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);

        // Validate the Phone in ElasticSearch
        Phone phoneEs = phoneSearchRepository.findOne(testPhone.getId());
        assertThat(phoneEs).isEqualToComparingFieldByField(testPhone);
    }

    @Test
    @Transactional
    public void deletePhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);
        phoneSearchRepository.save(phone);
        int databaseSizeBeforeDelete = phoneRepository.findAll().size();

        // Get the phone
        restPhoneMockMvc.perform(delete("/api/phones/{id}", phone.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean phoneExistsInEs = phoneSearchRepository.exists(phone.getId());
        assertThat(phoneExistsInEs).isFalse();

        // Validate the database is empty
        List<Phone> phones = phoneRepository.findAll();
        assertThat(phones).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);
        phoneSearchRepository.save(phone);

        // Search the phone
        restPhoneMockMvc.perform(get("/api/_search/phones?query=id:" + phone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phone.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())));
    }
}
