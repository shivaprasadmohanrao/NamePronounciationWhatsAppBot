package com.wf.hackathon2022.whatsappnpvp.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.wf.hackathon2022.whatsappnpvp.model.UserParam;
/**
 * This Controller acts as interface and drives User Whatsapp session.
 * User sends "Hi" message to Twilio Free account whatsapp Number +1-415-523-8886, twilio platform would allow only registered or joined user to this bot.
 * User's whatsapp messages/ media message are forwarded to this controller using webhook. The user can start the conversation with "hi" or any "string".
 * The controller sends the welcome message and media file configured as below. 
 * The ngrok running on this machine makes sure to listen to the port and twilio to send to-fro messages from user to this application.
 * To send messages to user, we would need Twilio USER SID and USER AUTH_TOKEN
 * 
 * Steps to test:
 * 1. add +1-415-523-8886 to your contacts.
 * 2. from your Whatsapp send a message exactly as " join hardly-represent " without quotes, please check the case. This is to validate your whatsapp number with the application account.
 * 3. you will receive Thank you message from the bot.
 * 4. You can send "hi or hello" to the bot to start the conversation.
 * 5. Bot replies as : Welcome to WellsFargo Whatsapp - Name Collector Bot, with Wellsfargo logo and a template to fill your basic details. 
 * 6. FirstName : XXXXX, this is mandatory to be send if not everything.
 * 7. Bot replies :  Now Please record your Name by using  Microphone icon  on your right corner of your mobile screen. 
 * 8. Please record your name and send the audio message.
 * 9. This will be captured along with the employee id, name and sent to Database for admin approval.
 * 10. Once admin approves, this will be published for others to listen on web portal
 * */

@Controller
public class WhatsAppController {
	 public static final String ACCOUNT_SID = "AC788ea0b506ec5b925ae49xx4cbd02c2a0e";//Shivaprasadmohanrao
	 public static final String AUTH_TOKEN =  "e0d06e7b964c47b42733bxx0a99c25def3"; //Shivaprasadmohanrao
	 public Map<String, Long> userSessionInfo = new HashMap<>(); 
	 private boolean newSession = true;
	
	@PostMapping(path="/rcvSms",produces="application/xml", consumes="application/x-www-form-urlencoded")
	public ResponseEntity<Void> whatsAppBot(HttpServletRequest request,UserParam message) throws Exception{

		
		
		Long currentTime= System.currentTimeMillis();
		String userPhoneNumber = message.getFrom().substring(9, message.getFrom().length());
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		
		
		if(userSessionInfo.get(userPhoneNumber)== null) {
			userSessionInfo.put(userPhoneNumber, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2));
			System.out.println("1. User WhatsApp Number : " + message.getFrom());
			System.out.println("2. User Added to Session ");
			Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  //1️⃣ 💶 🔢 2️⃣🏪3️⃣4️⃣5️⃣6️⃣7️⃣🔚☎️💵💰💳📌✅💲☑🎙️🎤️
	                "🏦 Welcome to *WellsFargo* Whatsapp - *Name Collector Bot* 🏦 "
	                + "\n") .create();
			Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),        
			"Please send the following in the same template as below:"
	                + "\n" +		 "*FirstName* : example --> Rahul"
	                + "\n" +		 "*LastName* : example --> Dravid"
	                + "\n" +		 "*e-mailId* : example --> rahul.dravid@wellsfargo.com"
	                + "\n" +		 "*Country* : example --> US or IN"
	                + "\n" +		 "*employee Number* : example --> 2012682") .setMediaUrl(Arrays.asList(URI.create("http://db57-49-207-224-149.ngrok.io/wellslogo.png")))   
	            .create();
			System.out.println("3. User is sent welcome message with Media file. Waiting for user Input");
		}
		else {
		if((currentTime  < userSessionInfo.get(userPhoneNumber)) && (message.getBody().toLowerCase().contains("name"))) {
			userSessionInfo.put(userPhoneNumber, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2));
			System.out.println("4. Recieved User Message as : " + message.getBody());
			System.out.println("5. Requesting User for Audio Record of their Name");
			Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  
	                "Now Please *record* your *Name* by using  🎤 *Microphone* icon 🎙️ on your right corner of your mobile screen ")      
	            .create();
		}
		else if((currentTime  < userSessionInfo.get(userPhoneNumber)) &&
				(request.getParameter("MediaUrl0") != null)) {
			userSessionInfo.put(userPhoneNumber, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2));
			String audioUrl = request.getParameter("MediaUrl0");
			System.out.println("Audio URL " + audioUrl);
			//saveAudio("1747154",audioUrl);
			Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  
	                "Your *name* is saved with us")      
	            .create(); 
			System.out.println("6. User Name recording is saved");
			Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  
	                "Please check *https://www.namepro.wellsfargo.com* for the latest *status*. "
	                + "Your *name* will be available for your colleagues to listen based on *admin approval*. ") .create();
			Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"), 
	                "*Thank you* for using *Name collector Bot*").setMediaUrl(Arrays.asList(URI.create("http://db57-49-207-224-149.ngrok.io/thanks.jpg"))) .create();
			userSessionInfo.put(userPhoneNumber, null);
			System.out.println("7. User Session is closed " + message.getFrom());
		}
			
		else {
			if(userPhoneNumber.startsWith("+91") || userPhoneNumber.startsWith("+1"))  {
				userSessionInfo.put(userPhoneNumber, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2));
				if(newSession) {
					newSession = false;
				Message.creator( 
	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  
	                "🏦 Welcome to *WellsFargo* Whatsapp - Name Collector Bot 🏦 1 "
	    	                + "\n" + "Please send the following in the same template as below:"
	    	                		+ "FirstName : Rahul"
	    	                		+ "LastName : Dravid"
	    	                		+ "e-mailId : rahul.dravid@wellsfargo.com"
	    	                		+ "employee Number : 2012682").setMediaUrl(Arrays.asList(URI.create("http://db57-49-207-224-149.ngrok.io/wflogo.jpg")))      
	    	            .create();
				}
				else {
	            	Message.creator( 
	    	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	    	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  
	    	                "🏦 Welcome to *WellsFargo* Whatsapp - Name Collector Bot 🏦  2"
	    	    	                + "\n" + "Please send the following in the same template as below:"
	    	    	                		+ "FirstName : Rahul"
	    	    	                		+ "LastName : Dravid"
	    	    	                		+ "e-mailId : rahul.dravid@wellsfargo.com"
	    	    	                		+ "employee Number : 2012682").setMediaUrl(Arrays.asList(URI.create("http://db57-49-207-224-149.ngrok.io/wflogo.jpg")))      
	    	    	            .create();
	            }
				
			}else {
	            	Message.creator( 
	    	                new com.twilio.type.PhoneNumber(message.getFrom()), 
	    	                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),  
	    	                "Sorry you are not *authorized* to receieve messages for this service!!. " + "\n"
	    	                + "*Please send message from your *registered Whatsapp number*")      
	    	            .create();
	            }
		}
	}//end else big
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	//this needs to move to service package
		public static void saveAudio(String empId,String audioUrl) throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			//body.add("multipartFile", getTestFile(audioUrl));
			body.add("empId", "17471565");
			body.add("channel", "whatsapp");
			body.add("audio_file_url", audioUrl);
			
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			String serverUrl = "https://92d2-124-123-173-69.in.ngrok.io/api/v1/npsrecords/updateEmpAudioRecord";

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
			System.out.println("Audio Save Status " + response.getStatusCodeValue());

		}
}
