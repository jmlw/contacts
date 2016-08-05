package com.jmlw.web.rest;

import com.jmlw.ContactsApp;
import com.jmlw.domain.TypeValue;
import com.jmlw.repository.TypeValueRepository;
import com.jmlw.repository.search.TypeValueSearchRepository;

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

import com.jmlw.domain.enumeration.EntityType;

/**
 * Test class for the TypeValueResource REST controller.
 *
 * @see TypeValueResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ContactsApp.class)
@WebAppConfiguration
@IntegrationTest
public class TypeValueResourceIntTest {


    private static final EntityType DEFAULT_ENTITY_TYPE = EntityType.ADDRESS_TYPE;
    private static final EntityType UPDATED_ENTITY_TYPE = EntityType.PHONE_TYPE;
    private static final String DEFAULT_VALUE = "AAAAA";
    private static final String UPDATED_VALUE = "BBBBB";

    @Inject
    private TypeValueRepository typeValueRepository;

    @Inject
    private TypeValueSearchRepository typeValueSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTypeValueMockMvc;

    private TypeValue typeValue;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeValueResource typeValueResource = new TypeValueResource();
        ReflectionTestUtils.setField(typeValueResource, "typeValueSearchRepository", typeValueSearchRepository);
        ReflectionTestUtils.setField(typeValueResource, "typeValueRepository", typeValueRepository);
        this.restTypeValueMockMvc = MockMvcBuilders.standaloneSetup(typeValueResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        typeValueSearchRepository.deleteAll();
        typeValue = new TypeValue();
        typeValue.setEntityType(DEFAULT_ENTITY_TYPE);
        typeValue.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createTypeValue() throws Exception {
        int databaseSizeBeforeCreate = typeValueRepository.findAll().size();

        // Create the TypeValue

        restTypeValueMockMvc.perform(post("/api/type-values")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeValue)))
                .andExpect(status().isCreated());

        // Validate the TypeValue in the database
        List<TypeValue> typeValues = typeValueRepository.findAll();
        assertThat(typeValues).hasSize(databaseSizeBeforeCreate + 1);
        TypeValue testTypeValue = typeValues.get(typeValues.size() - 1);
        assertThat(testTypeValue.getEntityType()).isEqualTo(DEFAULT_ENTITY_TYPE);
        assertThat(testTypeValue.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the TypeValue in ElasticSearch
        TypeValue typeValueEs = typeValueSearchRepository.findOne(testTypeValue.getId());
        assertThat(typeValueEs).isEqualToComparingFieldByField(testTypeValue);
    }

    @Test
    @Transactional
    public void getAllTypeValues() throws Exception {
        // Initialize the database
        typeValueRepository.saveAndFlush(typeValue);

        // Get all the typeValues
        restTypeValueMockMvc.perform(get("/api/type-values?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(typeValue.getId().intValue())))
                .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getTypeValue() throws Exception {
        // Initialize the database
        typeValueRepository.saveAndFlush(typeValue);

        // Get the typeValue
        restTypeValueMockMvc.perform(get("/api/type-values/{id}", typeValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(typeValue.getId().intValue()))
            .andExpect(jsonPath("$.entityType").value(DEFAULT_ENTITY_TYPE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTypeValue() throws Exception {
        // Get the typeValue
        restTypeValueMockMvc.perform(get("/api/type-values/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTypeValue() throws Exception {
        // Initialize the database
        typeValueRepository.saveAndFlush(typeValue);
        typeValueSearchRepository.save(typeValue);
        int databaseSizeBeforeUpdate = typeValueRepository.findAll().size();

        // Update the typeValue
        TypeValue updatedTypeValue = new TypeValue();
        updatedTypeValue.setId(typeValue.getId());
        updatedTypeValue.setEntityType(UPDATED_ENTITY_TYPE);
        updatedTypeValue.setValue(UPDATED_VALUE);

        restTypeValueMockMvc.perform(put("/api/type-values")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTypeValue)))
                .andExpect(status().isOk());

        // Validate the TypeValue in the database
        List<TypeValue> typeValues = typeValueRepository.findAll();
        assertThat(typeValues).hasSize(databaseSizeBeforeUpdate);
        TypeValue testTypeValue = typeValues.get(typeValues.size() - 1);
        assertThat(testTypeValue.getEntityType()).isEqualTo(UPDATED_ENTITY_TYPE);
        assertThat(testTypeValue.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the TypeValue in ElasticSearch
        TypeValue typeValueEs = typeValueSearchRepository.findOne(testTypeValue.getId());
        assertThat(typeValueEs).isEqualToComparingFieldByField(testTypeValue);
    }

    @Test
    @Transactional
    public void deleteTypeValue() throws Exception {
        // Initialize the database
        typeValueRepository.saveAndFlush(typeValue);
        typeValueSearchRepository.save(typeValue);
        int databaseSizeBeforeDelete = typeValueRepository.findAll().size();

        // Get the typeValue
        restTypeValueMockMvc.perform(delete("/api/type-values/{id}", typeValue.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean typeValueExistsInEs = typeValueSearchRepository.exists(typeValue.getId());
        assertThat(typeValueExistsInEs).isFalse();

        // Validate the database is empty
        List<TypeValue> typeValues = typeValueRepository.findAll();
        assertThat(typeValues).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTypeValue() throws Exception {
        // Initialize the database
        typeValueRepository.saveAndFlush(typeValue);
        typeValueSearchRepository.save(typeValue);

        // Search the typeValue
        restTypeValueMockMvc.perform(get("/api/_search/type-values?query=id:" + typeValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
}
