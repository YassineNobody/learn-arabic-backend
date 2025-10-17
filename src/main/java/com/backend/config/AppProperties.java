package com.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Frontend frontend = new Frontend();

    public Frontend getFrontend() {
        return frontend;
    }

    public void setFrontend(Frontend frontend) {
        this.frontend = frontend;
    }

    public static class Frontend {
        private String resetPasswordUrl;
        private String verifyEmailUrl;

        public String getResetPasswordUrl() {
            return resetPasswordUrl;
        }

        public void setResetPasswordUrl(String resetPasswordUrl) {
            this.resetPasswordUrl = resetPasswordUrl;
        }

        public String getVerifyEmailUrl() {
            return verifyEmailUrl;
        }

        public void setVerifyEmailUrl(String verifyEmailUrl) {
            this.verifyEmailUrl = verifyEmailUrl;
        }
    }
}
