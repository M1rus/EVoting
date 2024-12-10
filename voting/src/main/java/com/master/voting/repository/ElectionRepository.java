package com.master.voting.repository;

import com.master.voting.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface ElectionRepository extends JpaRepository<Election, Long> {
}
