/*
 * If you use Maven, add the following to your pom.xml:
 *   <dependency>
 *              <groupId>com.google.code.gson</groupId>
 *              <artifactId>gson</artifactId>
 *              <version>2.8.0</version>
 *   </dependency>
 *   <dependency>
 *          <groupId>commons-codec</groupId>
 *          <artifactId>commons-codec</artifactId>
 *          <version>1.10</version>
 *   </dependency>
 *  
 *  
 * If you don't use Maven, compile this class using this command: 
 *   javac -cp "jars/gson-2.8.0.jar:jars/commons-codec-1.10.jar" TelegramAudioSender.java 
 *   
 * Then, run the class using this command:
 *   java -cp ".:jars/gson-2.8.0.jar:jars/commons-codec-1.10.jar" TelegramAudioSender
 */

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;


public class TelegramAudioSender {
    /**
     * Inner class that captures the information needed to construct the JSON object
     * for sending an audio message.
     */
    class AudioMessage {
        String[] numbers = null;
        String filename = null;
        String audio = null;
    }
    
    // TODO: Replace the following with your gateway instance ID, 
    // Client ID and Secret below.
    private static final String INSTANCE_ID = "YOUR_INSTANCE_ID_HERE";
    private static final String CLIENT_ID = "YOUR_CLIENT_ID_HERE";
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET_HERE";

    private static final String GATEWAY_URL = "http://api.whatsmate.net/v1/telegram/batch/audio/binary/"
            + INSTANCE_ID;

    /**
     * Entry Point
     */
    public static void main(String[] args) throws Exception {
        // TODO: Specify the recipients of your document
        String[] recipients = {"1234556899", "1234567123"};
        // TODO: Specify any filename you like
        String filename = "Ocean.mp3";
        // TODO: Specify the content of your audio file
        Path docPath = Paths.get("../assets/ocean-waves.mp3");
        byte[] docBytes = Files.readAllBytes(docPath);
        
        TelegramAudioSender groupSender = new TelegramAudioSender();
        groupSender.sendAudioMessage(recipients, filename, docBytes);
    }

    /**
     * Sends out a Telegram Audio message 
     */
    public void sendAudioMessage(String[] recipients, String filename, byte[] docBytes)
            throws Exception {
        byte[] encodedBytes = Base64.encodeBase64(docBytes);
        String base64Content = new String(encodedBytes);
        
        AudioMessage docMsgObj = new AudioMessage();
        docMsgObj.numbers = recipients;
        docMsgObj.filename = filename;
        docMsgObj.audio = base64Content;

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(docMsgObj);

        URL url = new URL(GATEWAY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
        conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(jsonPayload.getBytes());
        os.flush();
        os.close();

        int statusCode = conn.getResponseCode();
        System.out.println("Response from Telegram Gateway: \n");
        System.out.println("Status Code: " + statusCode);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? conn.getInputStream()
                        : conn.getErrorStream()));
        String output;
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }
        conn.disconnect();
    }

}

