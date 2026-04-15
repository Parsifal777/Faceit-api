package com.faceit.controller;

import com.faceit.dto.MatchRequest;
import com.faceit.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<Void> addMatch(@RequestBody MatchRequest request) {
        matchService.addMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}