package com.chumore.mail;

import com.chumore.reservation.model.ReservationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.confirmation.link.base-url}")
    private String confirmationBaseUrl;


    // 寄出確認信
    public void sendConfirmationMail(ReservationVO reservation , String token) throws MessagingException {
            // thymeleaf 變數傳送
            Context context = new Context();
            context.setVariable("memberName",reservation.getMember().getMemberName());
            context.setVariable("restName", reservation.getRest().getRestName());
            context.setVariable("reservationDate", reservation.getReservationDate().toString()); // 格式化日期
            context.setVariable("reservationTime", reservation.getReservationTime().toString());
            context.setVariable("guestCount", reservation.getGuestCount());
            context.setVariable("confirmationLink", confirmationBaseUrl + "?token=" + token); // 帶入token

            // 確認信模板
            String htmlContent = templateEngine.process("public/reservation/reservation_confirmation_mail",context);

            // 設定信件 metadata
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(reservation.getMember().getMemberEmail()); //收件者
            helper.setSubject("chumore 訂位確認信"); //主旨
            helper.setText(htmlContent,true); // true 表示此為 html

            // 寄送 mail
            mailSender.send(message);

    }

}
