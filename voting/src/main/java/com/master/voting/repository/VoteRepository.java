package com.master.voting.repository;

import com.master.voting.model.Candidate;
import com.master.voting.model.Election;
import com.master.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {


    Optional<Vote> findByAnonymousVoteIdAndElectionId(String anonymousVoteId, Long electionId);

    long countByElection(Election election);

    long countByElectionAndCandidate(Election election, Candidate candidate);
}