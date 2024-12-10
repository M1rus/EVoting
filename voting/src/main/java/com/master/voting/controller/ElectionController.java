package com.master.voting.controller;

import com.master.voting.model.Candidate;
import com.master.voting.model.Election;
import com.master.voting.model.User;
import com.master.voting.service.CandidateService;
import com.master.voting.service.ElectionService;
import com.master.voting.service.UserService; // Додаємо сервіс користувачів
import com.master.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/elections")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listElections(Model model) {
        model.addAttribute("elections", electionService.getAllElections());
        return "elections";
    }

    @GetMapping("/{id}")
    public String viewElection(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<Election> electionOpt = electionService.getElectionById(id);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();
            model.addAttribute("election", election);


            String username = userDetails.getUsername();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Перевіряємо, чи користувач уже голосував
                boolean hasUserVoted = voteService.hasUserVoted(user.getId(), id);
                model.addAttribute("hasUserVoted", hasUserVoted);
            }

            // Отримуємо статистику голосів та форматовані відсотки
            Map<Candidate, Map<String, Object>> voteStats = electionService.getVoteStatistics(election);
            Map<Candidate, Map<String, Object>> formattedStats = new HashMap<>();

            voteStats.forEach((candidate, stats) -> {
                Map<String, Object> statDetails = stats;
                double percentage = (double) statDetails.get("percentage");


                statDetails.put("formattedPercentage", String.format("%.1f", percentage));
                formattedStats.put(candidate, statDetails);
            });

            model.addAttribute("voteStats", formattedStats);
            return "election";
        } else {
            return "redirect:/elections";
        }
    }

    @PreAuthorize("hasRole('comitee')")
    @PostMapping("/create")
    public String createElection(@ModelAttribute Election election) {
        System.out.println("Creating election: " + election.getName());
        electionService.createElection(election);
        return "redirect:/elections";
    }

    @PreAuthorize("hasRole('comitee')")
    @PostMapping("/{id}/add-candidate")
    public String addCandidateToElection(@PathVariable Long id,
                                         @RequestParam String candidateName,
                                         @RequestParam String candidateParty) {
        Optional<Election> election = electionService.getElectionById(id);
        if (election.isPresent()) {
            Candidate candidate = candidateService.findOrCreateCandidate(candidateName, candidateParty, election.get());
            electionService.addCandidateToElection(election.get(), candidate);
        }
        return "redirect:/elections/" + id; // Повертаємося на сторінку виборів
    }
}