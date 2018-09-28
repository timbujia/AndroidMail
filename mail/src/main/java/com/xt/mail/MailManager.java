package com.xt.mail;

import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by xuti on 2018/4/24.
 */

public class MailManager {
    private static MailManager instance;

    private MailManager() {

    }

    public static MailManager getInstance() {
        if (instance == null) {
            synchronized (MailManager.class) {
                instance = new MailManager();
            }
        }
        return instance;
    }

    public void sendMail(Mail mail, final Handler handler) throws MessagingException, UnsupportedEncodingException {
        Session session = createSession(mail.host, mail.port, mail.username, mail.password);
        Message message = createMessage(session, mail);

        Transport transport = session.getTransport("smtp");//获取实现了SMTP协议的Transport类
        transport.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) {

            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                handler.obtainMessage(0).sendToTarget();
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {

            }
        });
        transport.addTransportListener(new TransportListener() {
            @Override
            public void messageDelivered(TransportEvent transportEvent) {
                handler.obtainMessage(1).sendToTarget();
            }

            @Override
            public void messageNotDelivered(TransportEvent transportEvent) {
                handler.obtainMessage(2).sendToTarget();
            }

            @Override
            public void messagePartiallyDelivered(TransportEvent transportEvent) {

            }
        });
        transport.connect(mail.host, mail.username, mail.password);//连接服务器
        transport.sendMessage(message, message.getAllRecipients());//发送邮件给所有收件人
        transport.close();//关闭连接
    }


    private Session createSession(String host, String port, final String username,
                                  final String password) {

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host); // 指定主机
        properties.setProperty("mail.smtp.port", port); // 指定端口
        properties.setProperty("mail.smtp.auth", "true"); // 是否需要验证
//        properties.setProperty("mail.smtp.starttls.enable", "true"); // 是否需要验证

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        // 用指定的资源文件进行登录,资源文件中用户名
        return Session.getInstance(properties, auth);

    }

    private Message createMessage(Session session, Mail mail) {
        Message message = new MimeMessage(session);
        try {
//            message.setContent("", "text/plain");//非文本信息内容
//            message.setText(mail.content);//纯文本信息内容
            message.setSubject(mail.subject);//设置邮件主题
            message.setSentDate(new Date());//设置邮件发送日期
            Address addressFrom = new InternetAddress(mail.from); //邮件地址
            message.setFrom(addressFrom);//设置发信人
//        message.setReplyTo(new Address[]{address});//同上
            Address addressTo = new InternetAddress(mail.to);
            message.addRecipient(Message.RecipientType.TO, addressTo);//设置收信人
//        session.setDebug(true);//监控邮件发送过程
//            message.saveChanges(); //保存并发送文件

            Multipart multipart = new MimeMultipart();
            //第一部分（内容）
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(mail.content);
            multipart.addBodyPart(messageBodyPart1);//装填第一部分

            //第二部分（文件附件）
            for (int i = 0; i < mail.files.size(); i++) {
                File file = mail.files.get(i);
                BodyPart messageBodyPart2 = new MimeBodyPart();
                DataSource source = new FileDataSource(file.getAbsolutePath());
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(file.getAbsolutePath());
                multipart.addBodyPart(messageBodyPart2);//装填第二部分
            }
            message.setContent(multipart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    private Handler mailHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
        }
    };

    public void testSendMail(){
        ArrayList<File> arrayList = new ArrayList<>();
        arrayList.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"aa.txt"));
        Mail mail = new Mail("smtp.qq.com", "587", "932583597@qq.com", "tfgajrumpjhibdjb", "932583597@qq.com", "906940830@qq.com", "我蝠日志", "日志内容见附件", arrayList);
        try {
            MailManager.getInstance().sendMail(mail, mailHandler);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
//                            ToastUtils.showShortSafe(e.toString());
        }
    }
}
