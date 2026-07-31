package com.dugx.event.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Dugx.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final VNPay vnpay = new VNPay();
    private final Upload upload = new Upload();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public VNPay getVnpay() {
        return vnpay;
    }

    public Upload getUpload() {
        return upload;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class VNPay {

        private String tmnCode;

        private String hashSecret;

        private String payUrl;

        private String returnUrl;

        private String frontendReturnUrl;

        public String getTmnCode() {
            return tmnCode;
        }

        public void setTmnCode(String tmnCode) {
            this.tmnCode = tmnCode;
        }

        public String getHashSecret() {
            return hashSecret;
        }

        public void setHashSecret(String hashSecret) {
            this.hashSecret = hashSecret;
        }

        public String getPayUrl() {
            return payUrl;
        }

        public void setPayUrl(String payUrl) {
            this.payUrl = payUrl;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getFrontendReturnUrl() {
            return frontendReturnUrl;
        }

        public void setFrontendReturnUrl(String frontendReturnUrl) {
            this.frontendReturnUrl = frontendReturnUrl;
        }
    }

    public static class Upload {

        /** Thu muc tren dia de luu anh nguoi dung tai len (banner su kien...). */
        private String dir = "uploads/events";

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
    // jhipster-needle-application-properties-property-class
}
