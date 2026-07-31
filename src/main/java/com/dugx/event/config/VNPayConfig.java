package com.dugx.event.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {

    private final ApplicationProperties applicationProperties;

    public VNPayConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String getTmnCode() {
        return applicationProperties.getVnpay().getTmnCode();
    }

    public String getHashSecret() {
        return applicationProperties.getVnpay().getHashSecret();
    }

    public String getPayUrl() {
        return applicationProperties.getVnpay().getPayUrl();
    }

    public String getReturnUrl() {
        return applicationProperties.getVnpay().getReturnUrl();
    }

    /** URL of the Angular page that displays the payment result to the user. */
    public String getFrontendReturnUrl() {
        return applicationProperties.getVnpay().getFrontendReturnUrl();
    }
}
