package test;

import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.internet.MimeMessage.RecipientType;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

public class Main {
	public static void main(String[] args)
			throws AddressException, MessagingException, IOException, InterruptedException {

		
		SendMail s = new SendMail();
		// System.out.println(s.send("一封邮件", "测试语言"));
		FetchMail f = new FetchMail();
		while (true) {
			String r = f.read();
			translateMsg(r, s);
			Thread.sleep(5000);
		}
	}

	// 解析指令
	public static void translateMsg(String cmd, SendMail s) {
		if (cmd == null)
			return;
		String[] m = cmd.split(" ");
		do {
			System.out.print("收到" + m[0] + "指令：");
			if (cmd.startsWith("输出")) {
				System.out.println(cmd.substring(3));
				s.send("执行输出指令", "成功", s.defaultTo);
				break;
			} else if (m.length >= 2) {
				if (m[0].equalsIgnoreCase("启动")) {
					if (m[1].equalsIgnoreCase("计算器")) {
						System.out.println("启动计算器");
						Runtime run = Runtime.getRuntime();
						try {
							run.exec("calc.exe");
						} catch (IOException e) {
							e.printStackTrace();
						}
						s.send("执行启动计算器指令", "成功", s.defaultTo);
						break;
					} else {
						System.out.println("启动应用程序");
						Runtime run = Runtime.getRuntime();
						try {
							run.exec(cmd.substring(3));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				} else if (m.length>3&&m[0].equalsIgnoreCase("发送邮件")) {
					System.out.println("发送邮件");
					s.send(m[2], m[3], m[1]);
					s.send("执行发送邮件指令", "成功", s.defaultTo);
				}
			} else {
				System.out.println("无效指令");
			}
		} while (false);
	}
}

class SendMail {
	Properties properties;
	// 得到回话对象
	Session session;
	// 获取邮件对象
	Message message;

	String defaultTo = "838826997@qq.com";

	public void Init() throws AddressException, MessagingException {
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");// 连接协议
		properties.put("mail.smtp.host", "smtp.qq.com");// 主机名
		properties.put("mail.smtp.port", 465);// 端口号
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接//
														// ---一般都使用

		// 设置是否显示debug信息 true// 会在控制台显示相关信息
		properties.put("mail.debug", "false");

		// 得到回话对象
		session = Session.getInstance(properties);

		// 获取邮件对象
		message = new MimeMessage(session);
		// 设置发件人邮箱地址
		message.setFrom(new InternetAddress("2493988448@qq.com"));

	}

	public SendMail() throws AddressException, MessagingException {
		Init();
	}

	public boolean send(String subject, String content, String to) {
		try {
			// 设置收件人地址
			message.setRecipients(RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
			// 设置邮件标题
			message.setSubject(subject);
			// 设置邮件内容
			message.setText(content);
			// 得到邮差对象
			Transport transport = session.getTransport();
			// 连接自己的邮箱账户

			transport.connect("2493988448@qq.com", "lmwfdxvpwrtjdjag");

			// 密码为刚才得到的授权码 // 发送邮件
			transport.sendMessage(message, message.getAllRecipients());
			return true;
		} catch (MessagingException e) {
			// e.printStackTrace();
			return false;
		}

	}
}

class FetchMail {
	String protocol;
	boolean isSSL;
	String host;
	int port;
	String username;
	String password;

	Properties props;

	Session session;

	Store store;
	Folder folder;

	public void Init() {
		protocol = "imap";
		isSSL = true;
		host = "imap.qq.com";
		port = 993;
		username = "2493988448@qq.com";
		password = "lmwfdxvpwrtjdjag";

		props = new Properties();
		props.put("mail.imap.ssl.enable", isSSL);
		props.put("mail.imap.host", host);
		props.put("mail.imap.port", port);
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
			}
			};

		session = Session.getDefaultInstance(props,auth);

		store = null;
		folder = null;
	}

	public FetchMail() throws IOException, InterruptedException {
		Init();
	}

	public String read() throws IOException, InterruptedException {

		try {
			store = session.getStore(protocol);
			store.connect();

			folder = store.getFolder("INBOX");
 
			folder.open(Folder.READ_ONLY);

			int size = folder.getMessageCount();
			Message message = folder.getMessage(size);

			// 最新邮件
			if (message.isSet(Flags.Flag.SEEN) == false) {
				message.setFlag(Flags.Flag.SEEN, true);
				return (String) ((MimeMultipart) message.getContent()).getBodyPart(0).getContent();
			} else {
				return null;
			}

		} catch (Exception e1) {
			return read();

		} finally {
			/*
			try {
				if (folder != null) {
					folder.close(false);
				}
				if (store != null) {
					store.close();
				}
			} catch (MessagingException e11) {
				e11.printStackTrace();
			}
			*/
		}

	}

}
