package com.example.authapp.services;

import com.example.authapp.dto.IpLocationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IpLocationService {

    private static final String URL = "http://ip-api.com/json/";
    private final RestTemplate restTemplate = new RestTemplate();

    public IpLocationResponse lookup(String ip) {
        try {
            if (ip == null) {
                return null;
            }

            // Mock for Localhost to test DB persistence
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                IpLocationResponse mock = new IpLocationResponse();
                mock.setStatus("success");
                mock.setCountry("Local Country");
                mock.setCity("Local City");
                return mock;
            }

            return restTemplate.getForObject(
                    URL + ip,
                    IpLocationResponse.class);
        } catch (Exception e) {
            return null; // best-effort only
        }
    }
}
