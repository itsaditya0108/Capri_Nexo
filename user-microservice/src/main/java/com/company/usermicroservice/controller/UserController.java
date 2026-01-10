package com.company.usermicroservice.controller;

import com.company.usermicroservice.dto.RankRequest;
import com.company.usermicroservice.dto.RankResponse;
import com.company.usermicroservice.dto.UserRequest;
import com.company.usermicroservice.entity.UserDetails;
import com.company.usermicroservice.repository.UserRepository;
import com.company.usermicroservice.service.AppConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * Dedicated logger for service-to-service communication.
     * This helps ops teams filter only microservice flow logs.
     */
    private static final Logger msLogger =
            LoggerFactory.getLogger("com.company.usermicroservice.ms");

    private final UserRepository userRepository;
    private final AppConfigService appConfigService;
    private final RestTemplate restTemplate;

    @Autowired
    public UserController(UserRepository userRepository,
                          AppConfigService appConfigService,
                          RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.appConfigService = appConfigService;
        this.restTemplate = restTemplate;
    }

    // ================= GET USER RANK =================
    @PostMapping("/rank/{userId}")
    public ResponseEntity<?> getUserRank(@PathVariable String userId) {

        try {
            msLogger.info("event=USER_REQ userId={}", userId);

            UserDetails user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            RankRequest rankRequest = new RankRequest();
            rankRequest.setUserId(user.getId());
            rankRequest.setCity(user.getCity());

            String rankBaseUrl =
                    appConfigService.getValue("RANK_SERVICE_URL");
            String rankPath =
                    appConfigService.getValue("RANK_CALCULATE_PATH");

            long startTime = System.currentTimeMillis();

            msLogger.info(
                    "event=OUTGOING_CALL target=RANK_SERVICE method=POST city={}",
                    user.getCity()
            );

            RankResponse rankResponse =
                    restTemplate.postForObject(
                            rankBaseUrl + rankPath,
                            rankRequest,
                            RankResponse.class
                    );

            if (rankResponse == null) {
                throw new RuntimeException("Empty response from Rank Service");
            }

            long duration =
                    System.currentTimeMillis() - startTime;

            msLogger.info(
                    "event=INCOMING_RESPONSE target=RANK_SERVICE status=SUCCESS durationMs={} rank={}",
                    duration,
                    rankResponse.getRank()
            );

            return ResponseEntity.ok(rankResponse);

        } catch (ResourceAccessException ex) {

            msLogger.error(
                    "event=INCOMING_RESPONSE target=RANK_SERVICE status=TIMEOUT error={}",
                    ex.getMessage()
            );

            return ResponseEntity.status(503)
                    .body(Map.of("message",
                            "Rank service unavailable"));

        } catch (Exception ex) {

            msLogger.error(
                    "event=USER_RESPONSE status=ERROR error={}",
                    ex.getMessage()
            );

            return ResponseEntity.status(500)
                    .body(Map.of("message",
                            "Internal server error"));
        }
    }

    // ================= GET ALL USERS =================
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        // SELECT * FROM user_details
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ================= ADD USER =================
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody UserRequest request) {

        UserDetails user = new UserDetails();

        String id = "UID" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        user.setId(id);
        user.setName(request.getName());
        user.setPanno(request.getPanno());
        user.setCity(request.getCity());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        // Default status fetched from DB config
        user.setStatus(
                appConfigService.getValue("DEFAULT_USER_STATUS")
        );

        if (request.getAddress() != null) {
            user.setLine1(request.getAddress().getLine1());
            user.setLine2(request.getAddress().getLine2());
        }

        /**
         * Profile image is received as Base64 string.
         * We remove metadata prefix and whitespace
         * before decoding.
         */
        if (request.getProfileImageBase64() != null &&
                !request.getProfileImageBase64().isBlank()) {

            String base64Image = request.getProfileImageBase64();

            if (base64Image.contains(",")) {
                base64Image =
                        base64Image.substring(
                                base64Image.indexOf(",") + 1);
            }

            base64Image = base64Image.replaceAll("\\s", "");

            byte[] imageBytes =
                    Base64.getDecoder().decode(base64Image);

            user.setProfileImage(imageBytes);
        }

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("id", user.getId())
        );
    }

    // ================= UPDATE USER =================
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest request) {

        return userRepository.findById(request.getUserId())
                .map(user -> {
                    user.setName(request.getName());
                    user.setEmail(request.getEmail());
                    user.setMobile(request.getMobile());
                    user.setCity(request.getCity());
                    user.setStatus(request.getStatus());

                    userRepository.save(user);

                    return ResponseEntity.ok(
                            Map.of("message",
                                    "User updated successfully"));
                })
                .orElse(
                        ResponseEntity.status(404)
                                .body(Map.of("message",
                                        "User not found"))
                );
    }

    // ================= SEARCH USER =================
    @PostMapping("/search")
    public ResponseEntity<?> searchUser(@RequestBody UserRequest request) {

        // Search by userId
        if (request.getUserId() != null &&
                !request.getUserId().isBlank()) {

            Optional<UserDetails> optionalUser =
                    userRepository.findById(
                            request.getUserId());

            return optionalUser
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404)
                            .body(Map.of("message",
                                    "User not found")));
        }

        // Partial name search
        if (request.getName() != null &&
                !request.getName().isBlank()) {

            return ResponseEntity.ok(
                    userRepository
                            .searchByNameLike(
                                    request.getName())
            );
        }

        return ResponseEntity.badRequest()
                .body(Map.of("message",
                        "Invalid search request"));
    }

    // ================= REMOVE USER =================
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable String userId) {

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(404)
                    .body(Map.of("message",
                            "User not found"));
        }

        userRepository.deleteById(userId);

        return ResponseEntity.ok(
                Map.of("message",
                        "User removed successfully"));
    }
}
