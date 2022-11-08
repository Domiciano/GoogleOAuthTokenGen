import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            String token = getAccessToken();
            System.out.println(token);
            String data = "{\"message\":{\"topic\":\"alfa\",\"data\":{\"alfa\":\"Nuevo cambio\"}}}";
            System.out.println(data);
            String response = POSTtoFCM(data, token);

            System.out.println(response);


        } catch (IOException e) {
            System.out.println("Error GOOGLE");
            e.printStackTrace();
        }
    }

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("./facelogprueba-firebase-adminsdk-cktmj-9f543c1ca0.json"))
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();
        AccessToken token = googleCredentials.getAccessToken();
        return token.getTokenValue();
    }

    public static String POSTtoFCM(String json, String FCM_KEY) throws IOException{
        URL page = new URL("https://fcm.googleapis.com/v1/projects/facelogprueba/messages:send");
        HttpsURLConnection connection = (HttpsURLConnection) page.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; UTF-8");
        connection.setRequestProperty("Authorization", "Bearer "+FCM_KEY);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        writer.write(json);
        writer.flush();

        if ((""+connection.getResponseCode()).startsWith("2")) {
            return connection.getResponseMessage();
        } else {
            byte[] buffer = new byte[4096];
            int bytes = 0;
            InputStream is = connection.getErrorStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(is != null) {
                while ((bytes = is.read(buffer)) != -1){
                    baos.write(buffer,0, bytes);
                }
                baos.close();
                String out = baos.toString();
                System.out.println(">>>>");
                System.out.println(out);
                return out;
            } else throw new IOException("ERROR");

        }
    }

}

