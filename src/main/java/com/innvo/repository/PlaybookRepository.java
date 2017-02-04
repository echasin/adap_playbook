package com.innvo.repository;

import com.innvo.domain.Playbook;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Playbook entity.
 */
@SuppressWarnings("unused")
public interface PlaybookRepository extends JpaRepository<Playbook,Long> {

}
