package services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

public class GoogleAuthService {
    private static final String CLIENT_ID = "172443340892-j5neebcnn33v3q9lskn5utvhldvr3vvl.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final int PORT = 8888;
    private static final String REDIRECT_URI = "http://localhost:" + PORT + "/oauth2callback";
    
    private final GoogleAuthorizationCodeFlow flow;
    private final GoogleIdTokenVerifier verifier;
    private final NetHttpTransport transport;
    private final JacksonFactory jsonFactory;
    
    public GoogleAuthService() {
        try {
            System.out.println("Initialisation de GoogleAuthService...");
            
            transport = new NetHttpTransport();
            jsonFactory = JacksonFactory.getDefaultInstance();
            
            System.out.println("Configuration des secrets client...");
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setAuthUri("https://accounts.google.com/o/oauth2/auth")
                .setTokenUri("https://oauth2.googleapis.com/token");
            
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
            
            System.out.println("Configuration du flux d'autorisation...");
            flow = new GoogleAuthorizationCodeFlow.Builder(
                transport,
                jsonFactory,
                clientSecrets,
                Arrays.asList("email", "profile", "openid"))
                .build();
            
            System.out.println("Configuration du vérificateur de token...");
            verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
                
            System.out.println("GoogleAuthService initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public String startGoogleLogin() {
        try {
            System.out.println("Démarrage de l'authentification Google...");
            
            // Générer l'URL d'authentification
            String authUrl = flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .setState("state")
                .setAccessType("offline")
                .set("prompt", "select_account consent")
                .build();
            
            System.out.println("URL d'authentification générée: " + authUrl);
            
            // Ouvrir le navigateur
            System.out.println("Ouverture du navigateur...");
            Desktop.getDesktop().browse(new URI(authUrl));
            
            // Attendre le code d'autorisation
            System.out.println("Attente du code d'autorisation...");
            String code = waitForCode();
            if (code == null) {
                System.err.println("Code d'autorisation non reçu");
                return null;
            }
            System.out.println("Code d'autorisation reçu: " + code);
            
            // Échanger le code contre un token
            System.out.println("Échange du code contre un token...");
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(REDIRECT_URI)
                .execute();
            
            if (tokenResponse == null) {
                System.err.println("Réponse de token null");
                return null;
            }
            
            String idToken = tokenResponse.getIdToken();
            if (idToken == null) {
                System.err.println("Pas de token ID dans la réponse");
                return null;
            }
            
            System.out.println("Token ID obtenu avec succès");
            return idToken;
            
        } catch (Exception e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String waitForCode() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur en attente sur le port " + PORT);
            Socket socket = serverSocket.accept();
            
            // Lire la requête
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            System.out.println("Requête reçue: " + line);
            
            if (line == null) {
                return null;
            }
            
            // Extraire le code de la requête
            String code = null;
            if (line.contains("code=")) {
                try {
                    // Trouver le début du code
                    int startIndex = line.indexOf("code=") + 5;
                    int endIndex = line.indexOf("&", startIndex);
                    if (endIndex == -1) {
                        endIndex = line.indexOf(" ", startIndex);
                    }
                    
                    // Extraire et décoder le code
                    code = java.net.URLDecoder.decode(
                        line.substring(startIndex, endIndex),
                        "UTF-8"
                    );
                    
                    System.out.println("Code extrait et décodé avec succès");
                } catch (Exception e) {
                    System.err.println("Erreur lors du décodage du code: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Envoyer une réponse HTML
            try (PrintWriter out = new PrintWriter(socket.getOutputStream())) {
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html; charset=utf-8");
                out.println();
                out.println("<!DOCTYPE html>");
                out.println("<html><head><meta charset='utf-8'><title>Authentification réussie</title></head>");
                out.println("<body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>");
                if (code != null) {
                    out.println("<h1 style='color: #4CAF50;'>Authentification réussie !</h1>");
                    out.println("<p>Vous pouvez fermer cette fenêtre et retourner à l'application.</p>");
                } else {
                    out.println("<h1 style='color: #f44336;'>Erreur d'authentification</h1>");
                    out.println("<p>Une erreur s'est produite lors de l'authentification.</p>");
                }
                out.println("</body></html>");
                out.flush();
            }
            
            if (code == null) {
                System.err.println("Aucun code n'a pu être extrait de la requête");
            } else {
                System.out.println("Code d'autorisation extrait avec succès");
            }
            
            return code;
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'attente du code: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public GoogleUserInfo verifyToken(String idTokenString) {
        try {
            System.out.println("Vérification du token ID...");
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                System.out.println("Token ID vérifié avec succès");
                GoogleIdToken.Payload payload = idToken.getPayload();
                
                GoogleUserInfo userInfo = new GoogleUserInfo();
                userInfo.setEmail(payload.getEmail());
                userInfo.setName((String) payload.get("name"));
                userInfo.setPictureUrl((String) payload.get("picture"));
                
                return userInfo;
            } else {
                System.err.println("Token ID invalide");
            }
        } catch (Exception e) {
            System.err.println("Erreur de vérification du token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public static class GoogleUserInfo {
        private String email;
        private String name;
        private String pictureUrl;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPictureUrl() { return pictureUrl; }
        public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    }
} 