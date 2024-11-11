package kr.co.steellink.user.common.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private static final String SEND_USER_NAME = "steellink0729@gmail.com";  // 환경 변수 사용
    private static final String PASSWORD = "r d o x k t y d i y q d c q f b";    // 환경 변수 사용
    private static final String FROM_EMAIL = "steellink0729@gmail.com"; // 고정값
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String TEMPLATE_PATH = "templates/mail/mail_form.html";
    private static final String LOGO_PATH = "static/images/common/img/ico_mail.png";
    private static final String MAIN_LOGO_PATH = "static/images/common/img/mail_logo.png";

    /**
     * 이메일을 전송하는 메서드
     *
     * @param email     수신자 이메일 주소
     * @param userName  사용자 이름
     * @param returnUrl 이메일 본문에 포함할 URL
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendEmail(String email, String userName, String returnUrl) {
        try {
            // 이메일 템플릿 로드 후 사용자 정보로 내용을 대체
            String emailContent = loadEmailTemplate()
                    .replace("{title}", userName)
                    .replace("{moneyDate}", email)
                    .replace("{returnUrl}", returnUrl);

            // 이메일 전송
            sendEmailWithContent(email, emailContent);
        } catch (Exception e) {
            log.error("이메일 전송 오류: {}", e.getMessage(), e);
        }
    }

    /**
     * 이메일 템플릿 HTML 파일을 읽어오는 메서드
     *
     * @return 이메일 템플릿 내용
     */
    private String loadEmailTemplate() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
            return contentBuilder.toString();
        } catch (IOException e) {
            log.error("템플릿 로드 오류: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 템플릿 로드 오류", e);
        }
    }

    /**
     * 수신자 이메일 주소와 콘텐츠를 사용하여 이메일을 전송하는 메서드
     *
     * @param recipientEmail 수신자 이메일 주소
     * @param content        이메일 본문 내용
     * @throws MessagingException 이메일 전송 중 발생하는 예외
     */
    private void sendEmailWithContent(String recipientEmail, String content) throws MessagingException {
        MimeMessage message = createMimeMessage(recipientEmail, content);

        // 이메일 전송
        Transport.send(message);
        log.info("이메일 전송 성공: {}", recipientEmail);
    }

    /**
     * MimeMessage 객체를 생성하는 메서드
     *
     * @param recipientEmail 수신자 이메일 주소
     * @param content        이메일 본문 내용
     * @return 생성된 MimeMessage 객체
     * @throws MessagingException 메시지 생성 중 발생하는 예외
     */
    private MimeMessage createMimeMessage(String recipientEmail, String content) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SEND_USER_NAME, PASSWORD);
            }
        });

        MimeMessage message = new MimeMessage(session);
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.toString());

        // 이메일 정보 설정
        helper.setFrom(FROM_EMAIL);
        helper.setTo(recipientEmail);
        helper.setSubject("스틸링크 이메일");
        helper.setText(content, true);
        helper.addInline("logo_mail", new ClassPathResource(LOGO_PATH));

        return message;
    }
}

