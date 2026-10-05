package com.faceit.controller;

import com.faceit.api.MatchApi;
import com.faceit.dto.MatchRequest;
import com.faceit.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController implements MatchApi {

    private final MatchService matchService;

    @Override
    public ResponseEntity<Void> addMatch(MatchRequest request) {
        matchService.addMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
