package com.master.voting.service;

import com.master.voting.model.Candidate;
import com.master.voting.model.Election;
import com.master.voting.model.Vote;
import com.master.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;
    
    public boolean hasUserVoted(Long userId, Long electionId) {
        String anonymousVoteId = hashUserId(userId, electionId);
        return voteRepository.findByAnonymousVoteIdAndElectionId(anonymousVoteId, electionId).isPresent();
    }


    public Vote createVote(Long userId, Election election, Candidate candidate) {
        Vote vote = new Vote();
        vote.setAnonymousVoteId(hashUserId(userId, election.getId()));
        vote.setElection(election);
        vote.setCandidate(candidate);
        return voteRepository.save(vote);
    }


    private String hashUserId(Long userId, Long electionId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String combined = userId + ":" + electionId;
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return UUID.nameUUIDFromBytes(hash).toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating vote ID", e);
        }
    }
}
