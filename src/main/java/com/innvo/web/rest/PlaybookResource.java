package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Playbook;

import com.innvo.repository.PlaybookRepository;
import com.innvo.repository.search.PlaybookSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Playbook.
 */
@RestController
@RequestMapping("/api")
public class PlaybookResource {

    private final Logger log = LoggerFactory.getLogger(PlaybookResource.class);
        
    @Inject
    private PlaybookRepository playbookRepository;

    @Inject
    private PlaybookSearchRepository playbookSearchRepository;

    /**
     * POST  /playbooks : Create a new playbook.
     *
     * @param playbook the playbook to create
     * @return the ResponseEntity with status 201 (Created) and with body the new playbook, or with status 400 (Bad Request) if the playbook has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/playbooks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Playbook> createPlaybook(@Valid @RequestBody Playbook playbook) throws URISyntaxException {
        log.debug("REST request to save Playbook : {}", playbook);
        if (playbook.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("playbook", "idexists", "A new playbook cannot already have an ID")).body(null);
        }
        Playbook result = playbookRepository.save(playbook);
        playbookSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/playbooks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("playbook", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /playbooks : Updates an existing playbook.
     *
     * @param playbook the playbook to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated playbook,
     * or with status 400 (Bad Request) if the playbook is not valid,
     * or with status 500 (Internal Server Error) if the playbook couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/playbooks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Playbook> updatePlaybook(@Valid @RequestBody Playbook playbook) throws URISyntaxException {
        log.debug("REST request to update Playbook : {}", playbook);
        if (playbook.getId() == null) {
            return createPlaybook(playbook);
        }
        Playbook result = playbookRepository.save(playbook);
        playbookSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("playbook", playbook.getId().toString()))
            .body(result);
    }

    /**
     * GET  /playbooks : get all the playbooks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of playbooks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/playbooks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Playbook>> getAllPlaybooks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Playbooks");
        Page<Playbook> page = playbookRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/playbooks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /playbooks/:id : get the "id" playbook.
     *
     * @param id the id of the playbook to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the playbook, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/playbooks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Playbook> getPlaybook(@PathVariable Long id) {
        log.debug("REST request to get Playbook : {}", id);
        Playbook playbook = playbookRepository.findOne(id);
        return Optional.ofNullable(playbook)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /playbooks/:id : delete the "id" playbook.
     *
     * @param id the id of the playbook to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/playbooks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePlaybook(@PathVariable Long id) {
        log.debug("REST request to delete Playbook : {}", id);
        playbookRepository.delete(id);
        playbookSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("playbook", id.toString())).build();
    }

    /**
     * SEARCH  /_search/playbooks?query=:query : search for the playbook corresponding
     * to the query.
     *
     * @param query the query of the playbook search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/playbooks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Playbook>> searchPlaybooks(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Playbooks for query {}", query);
        Page<Playbook> page = playbookSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/playbooks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
