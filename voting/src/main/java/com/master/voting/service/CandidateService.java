package com.master.voting.service;

import com.master.voting.model.Candidate;
import com.master.voting.model.Election;
import com.master.voting.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    public Candidate findOrCreateCandidate(String name, String party, Election election) {
        Optional<Candidate> existingCandidate = candidateRepository.findByName(name);
        if (existingCandidate.isPresent()) {
            return existingCandidate.get();
        } else {
            Candidate newCandidate = new Candidate();
            newCandidate.setName(name);
            newCandidate.setParty(party);
            newCandidate.setElection(election);
            return candidateRepository.save(newCandidate);
        }
    }

    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }
}
