package com.example.authapp.services;

import com.example.authapp.dto.DeviceContextDto;
import com.example.authapp.entity.LoginHistory;
import com.example.authapp.repository.LoginHistoryRepository;
import com.example.authapp.util.IpUtil;
import com.example.authapp.util.UserAgentUtil;
import com.example.authapp.util.ParsedUserAgent;
import com.example.authapp.dto.IpLocationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final IpLocationService ipLocationService;

    public LoginHistoryService(
            LoginHistoryRepository loginHistoryRepository,
            IpLocationService ipLocationService) {
        this.loginHistoryRepository = loginHistoryRepository;
        this.ipLocationService = ipLocationService;
    }

    /**
     * Best-effort logging: MUST NEVER break login
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLoginAttempt(
            Long userId,
            String userName,
            boolean success,
            String failureReason,
            HttpServletRequest request,
            DeviceContextDto deviceContext) {
        try {
            LoginHistory history = new LoginHistory();

            history.setUserId(userId);
            history.setUserName(userName);
            history.setSuccess(success);
            history.setFailureReason(failureReason);

            // -------- IP --------
            String ip = IpUtil.getClientIp(request);
            history.setIpAddress(ip);

            // -------- User-Agent --------
            String userAgent = request.getHeader("User-Agent");
            history.setUserAgent(userAgent);

            ParsedUserAgent parsed = UserAgentUtil.parse(userAgent);
            history.setBrowser(parsed.getBrowser());
            history.setOs(parsed.getOs());
            history.setDeviceType(parsed.getDeviceType());

            // -------- DEVICE CONTEXT (Explicit from Mobile/Client) --------
            if (deviceContext != null) {
                if (deviceContext.getDeviceType() != null)
                    history.setDeviceType(deviceContext.getDeviceType());
                if (deviceContext.getOs() != null)
                    history.setOs(deviceContext.getOs());

                if (deviceContext.getLocation() != null) {
                    history.setLatitude(deviceContext.getLocation().getLatitude());
                    history.setLongitude(deviceContext.getLocation().getLongitude());
                    history.setAccuracy(deviceContext.getLocation().getAccuracy());
                }
                if (deviceContext.getDeviceModel() != null) {
                    history.setDeviceModel(deviceContext.getDeviceModel());
                }
            }

            // -------- LOCATION (IP-based fallback) --------
            IpLocationResponse location = ipLocationService.lookup(ip);
            if (location != null && "success".equalsIgnoreCase(location.getStatus())) {
                history.setCountry(location.getCountry());
                history.setCity(location.getCity());
            }

            loginHistoryRepository.save(history);

        } catch (Exception e) {
            // ❌ NEVER throw
            // log.warn("Failed to save login history", e);
        }
    }
}
