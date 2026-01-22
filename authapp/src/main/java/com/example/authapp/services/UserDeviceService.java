package com.example.authapp.services;

import com.example.authapp.dto.DeviceContextDto;
import com.example.authapp.entity.User;
import com.example.authapp.entity.UserDevice;
import com.example.authapp.repository.UserDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    public UserDeviceService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDevice saveOrUpdateUserDevice(User user, DeviceContextDto deviceContext) {
        if (deviceContext == null || deviceContext.getDeviceId() == null)
            return null;

        UserDevice device = userDeviceRepository
                .findByUserAndDeviceId(user, deviceContext.getDeviceId())
                .orElseGet(() -> {
                    UserDevice d = new UserDevice();
                    d.setUser(user);
                    d.setDeviceId(deviceContext.getDeviceId());
                    d.setFirstInstallTimestamp(LocalDateTime.now());
                    return d;
                });

        device.setOsName(deviceContext.getOs());
        device.setOsVersion(deviceContext.getOsVersion());
        device.setAppVersion(deviceContext.getAppVersion());
        device.setLastLoginTimestamp(LocalDateTime.now());

        return userDeviceRepository.save(device);
    }
}
