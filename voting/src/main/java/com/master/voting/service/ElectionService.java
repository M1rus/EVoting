package com.master.voting.service;

import com.master.voting.model.Candidate;
import com.master.voting.model.Election;
import com.master.voting.repository.ElectionRepository;
import com.master.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ElectionService {
    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private VoteRepository voteRepository;

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    public Election createElection(Election election) {
        return electionRepository.save(election);
    }

    public void addCandidateToElection(Election election, Candidate candidate) {
        candidate.setElection(election);
        election.getCandidates().add(candidate);
        electionRepository.save(election);
    }

    public Map<Candidate, Map<String, Object>> getVoteStatistics(Election election) {
        List<Candidate> candidates = election.getCandidates();
        Map<Candidate, Map<String, Object>> stats = new HashMap<>();

        long totalVotes = voteRepository.countByElection(election);
        for (Candidate candidate : candidates) {
            long candidateVotes = voteRepository.countByElectionAndCandidate(election, candidate);
            double percentage = (totalVotes > 0) ? (candidateVotes * 100.0 / totalVotes) : 0.0;

            Map<String, Object> data = new HashMap<>();
            data.put("total", candidateVotes);
            data.put("percentage", percentage);
            stats.put(candidate, data);
        }
        return stats;
    }
}