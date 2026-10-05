package com.faceit.api;

import com.faceit.dto.MatchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(MatchApi.BASE_URL)
public interface MatchApi {

    String BASE_URL = "/api/matches";

    @PostMapping
    ResponseEntity<Void> addMatch(@RequestBody MatchRequest request);
}
