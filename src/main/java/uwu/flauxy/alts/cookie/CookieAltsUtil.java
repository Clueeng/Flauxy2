package uwu.flauxy.alts.cookie;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.minecraft.util.Session;
import org.json.JSONException;

public final class CookieAltsUtil {
  private static final Gson GSON = new Gson();
  
  private static class MCResponse {
    @Expose
    @SerializedName("access_token")
    private String access_token;
  }
  
  private static class MCProfileResponse {
    @Expose
    @SerializedName("id")
    private String id;
    
    @Expose
    @SerializedName("name")
    private String name;
  }
  
  public static File getCookieFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Cookie File");
    fileChooser.grabFocus();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", new String[] { "txt" }));
    int result = fileChooser.showOpenDialog((Component)null);
    if (result == 0)
      return fileChooser.getSelectedFile(); 
    return null;
  }
  
  public static void disableSSLValidation() throws Exception {
    TrustManager[] trustAllCerts = { new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }
          
          public void checkClientTrusted(X509Certificate[] certs, String authType) {}
          
          public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
  }
  
  public static void loginWithCookie(String cookieFile, Consumer<Session> consumer) throws Exception {
    StringBuilder content = new StringBuilder();
    Scanner scanner = new Scanner(new FileReader(cookieFile));
    disableSSLValidation();
    while (scanner.hasNextLine())
      content.append(scanner.nextLine()).append("\n"); 
    scanner.close();
    String[] cookieString = content.toString().split("\n");
    if (cookieString.length != 0) {
      StringBuilder cookies = new StringBuilder();
      List<String> cookieList = new ArrayList<>();
      for (String cookie : cookieString) {
        String[] splitCookie = cookie.split("\t");
        if (!cookieList.contains(splitCookie[5]) && 
          splitCookie.length > 6) {
          String URL = splitCookie[0];
          if (URL.endsWith("live.com")) {
            cookies.append(splitCookie[5]).append("=").append(splitCookie[6]).append("; ");
            cookieList.add(splitCookie[5]);
          } 
        } 
      } 
      cookies = new StringBuilder(cookies.substring(0, cookies.length() - 2));
      HttpsURLConnection sisuConnection = createURLConnection("https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https://www.minecraft.net/en-us/login&aid=1142970254");
      sisuConnection.connect();
      String location = sisuConnection.getHeaderField("Location").replaceAll(" ", "%20");
      HttpsURLConnection locationRedirectConnection = createURLConnection(location);
      locationRedirectConnection.setRequestProperty("Cookie", cookies.toString());
      locationRedirectConnection.connect();
      String location2 = locationRedirectConnection.getHeaderField("Location");
      if (location2 == null) {
        consumer.accept(null);
        return;
      } 
      HttpsURLConnection location2RedirectConnection = createURLConnection(location2);
      location2RedirectConnection.setRequestProperty("Cookie", cookies.toString());
      location2RedirectConnection.connect();
      String location3 = location2RedirectConnection.getHeaderField("Location");
      String accessToken = location3.split("accessToken=")[1];
      String decodedAccessToken = (new String(Base64.getDecoder().decode(accessToken), StandardCharsets.UTF_8)).split("\"rp://api.minecraftservices.com/\",")[1];
      String token = decodedAccessToken.split("\"Token\":\"")[1].split("\"")[0];
      String uhs = decodedAccessToken.split(Pattern.quote("{\"DisplayClaims\":{\"xui\":[{\"uhs\":\""))[1].split("\"")[0];
      String xbl = "XBL3.0 x=" + uhs + ";" + token;
      MCResponse mcRes = (MCResponse)GSON.fromJson(
          postExternal("https://api.minecraftservices.com/authentication/login_with_xbox", "{\"identityToken\":\"" + xbl + "\",\"ensureLegacyEnabled\":true}", true), MCResponse.class);
      if (mcRes == null || mcRes.access_token == null) {
        consumer.accept(null);
        return;
      } 
      MCProfileResponse mcProfileRes = (MCProfileResponse)GSON.fromJson(
          getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token), MCProfileResponse.class);
      if (mcProfileRes == null || mcProfileRes.id == null) {
        consumer.accept(null);
        return;
      } 
      Session account = new Session(mcProfileRes.name, mcProfileRes.id, mcRes.access_token, Session.Type.LEGACY.name());
      consumer.accept(account);
      return;
    } 
    consumer.accept(null);
  }
  
  private static UUID uuidFromJson(String jsonUUID) throws JSONException {
    String withDashes = jsonUUID.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
    return UUID.fromString(withDashes);
  }
  
  private static String postExternal(String url, String post, boolean json) throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection)(new URL(url)).openConnection();
    connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    byte[] out = post.getBytes(StandardCharsets.UTF_8);
    connection.setFixedLengthStreamingMode(out.length);
    connection.addRequestProperty("Content-Type", json ? "application/json" : "application/x-www-form-urlencoded; charset=UTF-8");
    connection.addRequestProperty("Accept", "application/json");
    connection.connect();
    try (OutputStream os = connection.getOutputStream()) {
      os.write(out);
    } 
    int responseCode = connection.getResponseCode();
    InputStream stream = (responseCode / 100 == 2 || responseCode / 100 == 3) ? connection.getInputStream() : connection.getErrorStream();
    if (stream == null)
      return null; 
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder response = new StringBuilder();
    String lineBuffer;
    while ((lineBuffer = reader.readLine()) != null)
      response.append(lineBuffer); 
    reader.close();
    return response.toString();
  }
  
  public static String getBearerResponse(String url, String bearer) throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection)(new URL(url)).openConnection();
    connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
    connection.addRequestProperty("Accept", "application/json");
    connection.addRequestProperty("Authorization", "Bearer " + bearer);
    InputStream inputStream = (connection.getResponseCode() == 200) ? connection.getInputStream() : connection.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder response = new StringBuilder();
    String lineBuffer;
    while ((lineBuffer = reader.readLine()) != null)
      response.append(lineBuffer); 
    return response.toString();
  }
  
  private static HttpsURLConnection createURLConnection(String URL) throws IOException {
    HttpsURLConnection connection = (HttpsURLConnection)(new URL(URL)).openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8");
    connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");
    connection.setInstanceFollowRedirects(false);
    return connection;
  }
}
