package com.synergyresources.gcp.auth.service;

import com.synergyresources.gcp.auth.config.AuthProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  private final JavaMailSender mailSender;
  private final AuthProperties props;

  public MailService(JavaMailSender mailSender, AuthProperties props) {
    this.mailSender = mailSender;
    this.props = props;
  }

  public void sendOtp(String toEmail, String plainCode) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(props.getMail().getFrom());
    msg.setTo(toEmail);
    msg.setSubject("Your GCP verification code");
    msg.setText("Your verification code is: " + plainCode + "\n\nThis code is valid for "
        + (props.getOtp().getExpirySeconds() / 60) + " minutes. Do not share it with anyone.");
    mailSender.send(msg);
  }
}
