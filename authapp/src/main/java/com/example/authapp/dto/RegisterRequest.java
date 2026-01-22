package com.example.authapp.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "NAME_REQUIRED")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "NAME_INVALID"
    )
    private String name;

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$",
            message = "EMAIL_MUST_BE_GMAIL"
    )
    private String email;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "PHONE_INVALID"
    )
    private String phone;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
            message = "PASSWORD_WEAK"
    )
    private String password;

    private DeviceContextDto deviceContext;


    // getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DeviceContextDto getDeviceContext() {
        return deviceContext;
    }

    public void setDeviceContext(DeviceContextDto deviceContext) {
        this.deviceContext = deviceContext;
    }
}
