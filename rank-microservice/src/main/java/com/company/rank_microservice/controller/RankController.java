
package com.company.rank_microservice.controller;

import com.company.rank_microservice.dto.RankRequest;
import com.company.rank_microservice.dto.RankResponse;
import com.company.rank_microservice.service.RankCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rank")
public class RankController {

    private final RankCalculationService rankService;

    public RankController(RankCalculationService rankService) {
        this.rankService = rankService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateRank(@RequestBody RankRequest request) {

        if (request.getCity() == null || request.getCity().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "City is required"));
        }

        RankResponse response = rankService.calculateRank(request);
        return ResponseEntity.ok(response);
    }

}
