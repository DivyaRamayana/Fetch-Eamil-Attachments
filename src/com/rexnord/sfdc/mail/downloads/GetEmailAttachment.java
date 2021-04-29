package com.rexnord.sfdc.mail.downloads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class GetEmailAttachment {

	final static Logger logger = Logger.getLogger(GetEmailAttachment.class);
	static Properties props;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		/*
		 * String username = "centasfdc@outlook.com"; String password = "Sfdc2014";
		 * String host = "smtp-mail.outlook.com"; String port ="143"
		 */;

		BasicConfigurator.configure();

		String propertiesFileName = args[0];

		props = new Properties();

		try {

			props.load(new FileInputStream("properties//" + propertiesFileName));

		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		String username = props.getProperty("username").trim();
		String password = props.getProperty("password").trim();
		String host = props.getProperty("host").trim();
		String port = props.getProperty("port").trim();
		String destination_dir = props.getProperty("destination_dir").trim();
		String destination_dir_timestamp = props.getProperty("destination_dir_timestamp").trim();

		try {

			boolean foundSites = false;

			// Get a Properties object
			Properties properties = new Properties();

			properties.put("mail.imap.host", host);
			properties.put("mail.imap.port", port);
			properties.put("mail.imap.starttls.enable", true);
			Session emailSession = Session.getDefaultInstance(properties);

			Store store = emailSession.getStore("imap");
			store.connect(host, username, password);

			logger.info("Connection Successfull");

			// create the folder object and open it
			Folder emailFolder = store.getFolder("Sent");
			emailFolder.open(Folder.READ_ONLY);

			/*
			 * Folder[] fI = store.getDefaultFolder().list(); for(Folder fd:fI)
			 * System.out.println(">> "+fd.getName());
			 */

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			// System.out.println("messages.length---" + messages.length);

			LocalDateTime now = LocalDateTime.now();
			int year = now.getYear();
			int month = now.getMonthValue();
			int day = now.getDayOfMonth();

			for (int i = messages.length - 1; i >= 0; i--) {
				Message message = messages[i];

				Calendar cal = Calendar.getInstance();
				cal.setTime(message.getReceivedDate());
				int yearMessage = cal.get(Calendar.YEAR);
				int monthMessage = cal.get(Calendar.MONTH) + 1;
				int dayMessage = cal.get(Calendar.DAY_OF_MONTH);

				if (year == yearMessage && month == monthMessage && day == dayMessage) {

					if (message.getSubject().equalsIgnoreCase("Centa China Salesforce Integration")) {

						logger.info("Found subject line : Centa China Salesforce Integration");
						
						Multipart multipart = (Multipart) message.getContent();

						List<File> attachments = new ArrayList<>();

						for (int j = 0; j < multipart.getCount(); j++) {
							BodyPart bodyPart = multipart.getBodyPart(j);

							if (multipart.getCount() > 1) {
								if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
									if (j == 1) {
										logger.info("Attachments found on " + dayMessage + monthMessage + yearMessage);
									}
									InputStream is = bodyPart.getInputStream();
									InputStream is1 = bodyPart.getInputStream();
									
									String Dir = destination_dir;
									String Dir1 = destination_dir_timestamp;

									File theDir = new File(Dir);
									if (!theDir.exists()) {
										theDir.mkdirs();
									}

									File theDir1 = new File(Dir1);
									if (!theDir1.exists()) {
										theDir1.mkdirs();
									}

									File f = new File(Dir + bodyPart.getFileName());
									FileOutputStream fos = new FileOutputStream(f);
									byte[] buf = new byte[4096];
									int bytesRead;
									while ((bytesRead = is.read(buf)) != -1) {
										fos.write(buf, 0, bytesRead);
									}
									fos.close();
									attachments.add(f);
									logger.info(bodyPart.getFileName() + " downloaded successfully, Location : " + Dir
											+ bodyPart.getFileName());

									File f1 = new File(Dir1 + bodyPart.getFileName().split("\\.")[0] + "_" + day + "_"
											+ month + "_" + year + ".txt");
									FileOutputStream fos1 = new FileOutputStream(f1);
									byte[] buf1 = new byte[4096];
									int bytesRead1;
									while ((bytesRead1 = is1.read(buf1)) != -1) {
										fos1.write(buf1, 0, bytesRead1);
									}
									fos1.close();
									attachments.add(f1);
									logger.info(bodyPart.getFileName()
											+ " downloaded successfully with timestamp, Location : " + Dir1
											+ bodyPart.getFileName().split("\\.")[0] + "_" + day + "_" + month + "_"
											+ year + ".txt");
									
									foundSites = true;
									
									if (j == multipart.getCount()) {
										break;
									}
								}
							} 
							
							else 
							{
								
								logger.info("No files found");
							}

						}
					}

					else 
					{
						logger.info("Subject line : Centa China Salesforce Integration, Not found..!!");
					}
				}

				if (foundSites) {
					break;
				}

			}
		} catch (Exception e) {
			System.out.println(e);
			logger.error(e);
		}

		logger.info("Connection Ended");
	}
}