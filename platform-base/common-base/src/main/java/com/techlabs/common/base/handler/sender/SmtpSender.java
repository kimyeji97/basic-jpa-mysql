package com.techlabs.common.base.handler.sender;

import com.techlabs.common.base.http.exception.PlatformNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Smtp Sender
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpSender
{
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String mailFrom;

    /**
     * 템플릿 메일을 전송한다.
     *
     * @param toAry   전송 이메일 주소 리스트
     * @param subject 제목
     */
    public boolean sendThymeleafMail(String[] toAry, String subject, Map<String, Object> contextMap,
        String templateName)
    {
        if (StringUtils.isBlank(templateName))
        {
            throw new PlatformNotFoundException("templateName");
        }
        try
        {
            MimeMessagePreparator messagePreparator = miemMessage ->
            {
                MimeMessageHelper messageHelper = new MimeMessageHelper(miemMessage);
                messageHelper.setFrom(mailFrom);
                messageHelper.setTo(toAry);
                messageHelper.setSubject(subject);

                Context context = new Context();
                for (Entry<String, Object> entry : contextMap.entrySet())
                {
                    context.setVariable(entry.getKey(), entry.getValue());
                }
                String thymeleafMessage = templateEngine.process(templateName, context);
                messageHelper.setText(thymeleafMessage, true);
            };
            mailSender.send(messagePreparator);
        } catch (MailException e)
        {
            log.error("Failed to send report mail. {}", e.getMessage());
            return false;
        }
        return true;
    }
}
