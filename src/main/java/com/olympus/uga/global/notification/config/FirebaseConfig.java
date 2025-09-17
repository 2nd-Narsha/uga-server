package com.olympus.uga.global.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Value("${firebase.config.path:firebase-service-account.json}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials credentials;

                // GitHub Actions 환경에서는 환경 변수로 Firebase 키를 주입
                String firebaseKey = System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY");
                if (firebaseKey != null && !firebaseKey.isEmpty()) {
                    credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(firebaseKey.getBytes(StandardCharsets.UTF_8))
                    );
                } else {
                    // 로컬 환경에서는 파일에서 읽기
                    ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
                    credentials = GoogleCredentials.fromStream(resource.getInputStream());
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패: {}", e.getMessage());
        }
    }
}
