package com.innvo.repository.search;

import com.innvo.domain.Playbook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Playbook entity.
 */
public interface PlaybookSearchRepository extends ElasticsearchRepository<Playbook, Long> {
}
