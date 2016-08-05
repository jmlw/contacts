package com.jmlw.web.rest;

import com.jmlw.ContactsApp;
import com.jmlw.domain.EmailAddress;
import com.jmlw.repository.EmailAddressRepository;
import com.jmlw.repository.search.EmailAddressSearchRepository;

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
 * Test class for the EmailAddressResource REST controller.
 *
 * @see EmailAddressResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ContactsApp.class)
@WebAppConfiguration
@IntegrationTest
public class EmailAddressResourceIntTest {

    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    @Inject
    private EmailAddressRepository emailAddressRepository;

    @Inject
    private EmailAddressSearchRepository emailAddressSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEmailAddressMockMvc;

    private EmailAddress emailAddress;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmailAddressResource emailAddressResource = new EmailAddressResource();
        ReflectionTestUtils.setField(emailAddressResource, "emailAddressSearchRepository", emailAddressSearchRepository);
        ReflectionTestUtils.setField(emailAddressResource, "emailAddressRepository", emailAddressRepository);
        this.restEmailAddressMockMvc = MockMvcBuilders.standaloneSetup(emailAddressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        emailAddressSearchRepository.deleteAll();
        emailAddress = new EmailAddress();
        emailAddress.setEmail(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void createEmailAddress() throws Exception {
        int databaseSizeBeforeCreate = emailAddressRepository.findAll().size();

        // Create the EmailAddress

        restEmailAddressMockMvc.perform(post("/api/email-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(emailAddress)))
                .andExpect(status().isCreated());

        // Validate the EmailAddress in the database
        List<EmailAddress> emailAddresses = emailAddressRepository.findAll();
        assertThat(emailAddresses).hasSize(databaseSizeBeforeCreate + 1);
        EmailAddress testEmailAddress = emailAddresses.get(emailAddresses.size() - 1);
        assertThat(testEmailAddress.getEmail()).isEqualTo(DEFAULT_EMAIL);

        // Validate the EmailAddress in ElasticSearch
        EmailAddress emailAddressEs = emailAddressSearchRepository.findOne(testEmailAddress.getId());
        assertThat(emailAddressEs).isEqualToComparingFieldByField(testEmailAddress);
    }

    @Test
    @Transactional
    public void getAllEmailAddresses() throws Exception {
        // Initialize the database
        emailAddressRepository.saveAndFlush(emailAddress);

        // Get all the emailAddresses
        restEmailAddressMockMvc.perform(get("/api/email-addresses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(emailAddress.getId().intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }

    @Test
    @Transactional
    public void getEmailAddress() throws Exception {
        // Initialize the database
        emailAddressRepository.saveAndFlush(emailAddress);

        // Get the emailAddress
        restEmailAddressMockMvc.perform(get("/api/email-addresses/{id}", emailAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(emailAddress.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmailAddress() throws Exception {
        // Get the emailAddress
        restEmailAddressMockMvc.perform(get("/api/email-addresses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmailAddress() throws Exception {
        // Initialize the database
        emailAddressRepository.saveAndFlush(emailAddress);
        emailAddressSearchRepository.save(emailAddress);
        int databaseSizeBeforeUpdate = emailAddressRepository.findAll().size();

        // Update the emailAddress
        EmailAddress updatedEmailAddress = new EmailAddress();
        updatedEmailAddress.setId(emailAddress.getId());
        updatedEmailAddress.setEmail(UPDATED_EMAIL);

        restEmailAddressMockMvc.perform(put("/api/email-addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEmailAddress)))
                .andExpect(status().isOk());

        // Validate the EmailAddress in the database
        List<EmailAddress> emailAddresses = emailAddressRepository.findAll();
        assertThat(emailAddresses).hasSize(databaseSizeBeforeUpdate);
        EmailAddress testEmailAddress = emailAddresses.get(emailAddresses.size() - 1);
        assertThat(testEmailAddress.getEmail()).isEqualTo(UPDATED_EMAIL);

        // Validate the EmailAddress in ElasticSearch
        EmailAddress emailAddressEs = emailAddressSearchRepository.findOne(testEmailAddress.getId());
        assertThat(emailAddressEs).isEqualToComparingFieldByField(testEmailAddress);
    }

    @Test
    @Transactional
    public void deleteEmailAddress() throws Exception {
        // Initialize the database
        emailAddressRepository.saveAndFlush(emailAddress);
        emailAddressSearchRepository.save(emailAddress);
        int databaseSizeBeforeDelete = emailAddressRepository.findAll().size();

        // Get the emailAddress
        restEmailAddressMockMvc.perform(delete("/api/email-addresses/{id}", emailAddress.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean emailAddressExistsInEs = emailAddressSearchRepository.exists(emailAddress.getId());
        assertThat(emailAddressExistsInEs).isFalse();

        // Validate the database is empty
        List<EmailAddress> emailAddresses = emailAddressRepository.findAll();
        assertThat(emailAddresses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEmailAddress() throws Exception {
        // Initialize the database
        emailAddressRepository.saveAndFlush(emailAddress);
        emailAddressSearchRepository.save(emailAddress);

        // Search the emailAddress
        restEmailAddressMockMvc.perform(get("/api/_search/email-addresses?query=id:" + emailAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(emailAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }
}
