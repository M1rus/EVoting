package com.master.voting.controller;

import com.master.voting.model.Candidate;
import com.master.voting.model.Election;
import com.master.voting.model.User;
import com.master.voting.repository.UserRepository;
import com.master.voting.service.CandidateService;
import com.master.voting.service.ElectionService;
import com.master.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/elections")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private CandidateService candidateService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{electionId}/vote")
    public String voteForCandidate(@PathVariable Long electionId,
                                   @RequestParam Long candidateId,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Election> election = electionService.getElectionById(electionId);
        Optional<Candidate> candidate = candidateService.getCandidateById(candidateId);

        if (election.isPresent() && candidate.isPresent()) {

            String username = userDetails.getUsername();
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();


                if (voteService.hasUserVoted(user.getId(), electionId)) {
                    return "redirect:/elections/" + electionId + "?error=already_voted";
                }


                voteService.createVote(user.getId(), election.get(), candidate.get());
                return "redirect:/elections/" + electionId + "?success=voted";
            }
        }

        return "redirect:/elections/" + electionId + "?error=invalid_vote";
    }
}