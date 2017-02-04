package com.innvo.web.rest;

import com.innvo.PlaybookApp;
import com.innvo.domain.Playbook;
import com.innvo.repository.PlaybookRepository;
import com.innvo.repository.search.PlaybookSearchRepository;

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
 * Test class for the PlaybookResource REST controller.
 *
 * @see PlaybookResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlaybookApp.class)
public class PlaybookResourceIntTest {
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private PlaybookRepository playbookRepository;

    @Inject
    private PlaybookSearchRepository playbookSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPlaybookMockMvc;

    private Playbook playbook;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PlaybookResource playbookResource = new PlaybookResource();
        ReflectionTestUtils.setField(playbookResource, "playbookSearchRepository", playbookSearchRepository);
        ReflectionTestUtils.setField(playbookResource, "playbookRepository", playbookRepository);
        this.restPlaybookMockMvc = MockMvcBuilders.standaloneSetup(playbookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Playbook createEntity(EntityManager em) {
        Playbook playbook = new Playbook();
        playbook = new Playbook()
                .name(DEFAULT_NAME);
        return playbook;
    }

    @Before
    public void initTest() {
        playbookSearchRepository.deleteAll();
        playbook = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlaybook() throws Exception {
        int databaseSizeBeforeCreate = playbookRepository.findAll().size();

        // Create the Playbook

        restPlaybookMockMvc.perform(post("/api/playbooks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(playbook)))
                .andExpect(status().isCreated());

        // Validate the Playbook in the database
        List<Playbook> playbooks = playbookRepository.findAll();
        assertThat(playbooks).hasSize(databaseSizeBeforeCreate + 1);
        Playbook testPlaybook = playbooks.get(playbooks.size() - 1);
        assertThat(testPlaybook.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Playbook in ElasticSearch
        Playbook playbookEs = playbookSearchRepository.findOne(testPlaybook.getId());
        assertThat(playbookEs).isEqualToComparingFieldByField(testPlaybook);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = playbookRepository.findAll().size();
        // set the field null
        playbook.setName(null);

        // Create the Playbook, which fails.

        restPlaybookMockMvc.perform(post("/api/playbooks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(playbook)))
                .andExpect(status().isBadRequest());

        List<Playbook> playbooks = playbookRepository.findAll();
        assertThat(playbooks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlaybooks() throws Exception {
        // Initialize the database
        playbookRepository.saveAndFlush(playbook);

        // Get all the playbooks
        restPlaybookMockMvc.perform(get("/api/playbooks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(playbook.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getPlaybook() throws Exception {
        // Initialize the database
        playbookRepository.saveAndFlush(playbook);

        // Get the playbook
        restPlaybookMockMvc.perform(get("/api/playbooks/{id}", playbook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(playbook.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPlaybook() throws Exception {
        // Get the playbook
        restPlaybookMockMvc.perform(get("/api/playbooks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlaybook() throws Exception {
        // Initialize the database
        playbookRepository.saveAndFlush(playbook);
        playbookSearchRepository.save(playbook);
        int databaseSizeBeforeUpdate = playbookRepository.findAll().size();

        // Update the playbook
        Playbook updatedPlaybook = playbookRepository.findOne(playbook.getId());
        updatedPlaybook
                .name(UPDATED_NAME);

        restPlaybookMockMvc.perform(put("/api/playbooks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPlaybook)))
                .andExpect(status().isOk());

        // Validate the Playbook in the database
        List<Playbook> playbooks = playbookRepository.findAll();
        assertThat(playbooks).hasSize(databaseSizeBeforeUpdate);
        Playbook testPlaybook = playbooks.get(playbooks.size() - 1);
        assertThat(testPlaybook.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Playbook in ElasticSearch
        Playbook playbookEs = playbookSearchRepository.findOne(testPlaybook.getId());
        assertThat(playbookEs).isEqualToComparingFieldByField(testPlaybook);
    }

    @Test
    @Transactional
    public void deletePlaybook() throws Exception {
        // Initialize the database
        playbookRepository.saveAndFlush(playbook);
        playbookSearchRepository.save(playbook);
        int databaseSizeBeforeDelete = playbookRepository.findAll().size();

        // Get the playbook
        restPlaybookMockMvc.perform(delete("/api/playbooks/{id}", playbook.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean playbookExistsInEs = playbookSearchRepository.exists(playbook.getId());
        assertThat(playbookExistsInEs).isFalse();

        // Validate the database is empty
        List<Playbook> playbooks = playbookRepository.findAll();
        assertThat(playbooks).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPlaybook() throws Exception {
        // Initialize the database
        playbookRepository.saveAndFlush(playbook);
        playbookSearchRepository.save(playbook);

        // Search the playbook
        restPlaybookMockMvc.perform(get("/api/_search/playbooks?query=id:" + playbook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(playbook.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
