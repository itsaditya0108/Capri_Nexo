package com.company.rank_microservice.service;

import com.company.rank_microservice.dto.RankRequest;
import com.company.rank_microservice.dto.RankResponse;
import org.springframework.stereotype.Service;

@Service
public class RankCalculationService {

    public RankResponse calculateRank(RankRequest request) {

        String rank;
        if ("Mumbai".equalsIgnoreCase(request.getCity())) {
            rank = "GOLD";
        } else {
            rank = "SILVER";
        }

//        try {
//            Thread.sleep(15000); // 15 seconds
//        } catch (InterruptedException e) {}

        return new RankResponse(request.getUserId(), rank);
    }


}
