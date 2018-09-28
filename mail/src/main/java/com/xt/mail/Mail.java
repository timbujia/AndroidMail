package com.xt.mail;

import java.io.File;
import java.util.List;

/**
 * Created by xuti on 2018/4/24.
 */

public class Mail {
    public String host;
    public String port;
    public String username;
    public String password;
    public String from;
    public String to;
    public String subject;
    public String content;
    public List<File> files;

    public Mail(String host, String port, String username, String password, String from, String to, String subject, String content, List<File> files) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.files = files;
    }
}
