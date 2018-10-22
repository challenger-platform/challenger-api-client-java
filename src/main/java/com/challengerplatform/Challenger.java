package com.challengerplatform;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Challenger {
    /*
        Ugly hack to remove java security restrictions. Otherwise you would need to install
        Java Cryptography Extension (JCE) unlimited strength jurisdiction policy files

        Remove static passage if client is willing to install the files.

        Read more:
        http://stackoverflow.com/questions/1179672/how-to-avoid-installing-unlimited-strength-jce-policy-files-when-deploying-an
    */
    static {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
            field.setAccessible(true);
            field.set(null, java.lang.Boolean.FALSE);
        } catch (Exception ex) {
        }
    }

    private final String domain;
    private String ownerId;
    private String clientId;
    private String key;
    private boolean useHTTPS = false;
    private Map<String, String> params = new HashMap<String, String>();

    /**
     * Construct challenger instance
     *
     * @param domain chalenger domain without protocol ex. yourdomain.challengerplatform.com
     */
    public Challenger(String domain) {
        this.domain = domain;
    }

    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUseHTTPS(boolean useHTTPS) {
        this.useHTTPS = useHTTPS;
    }

    public void addParam(String param, String value) {
        params.put(param, value);
    }

    public String getWidgetHtml() throws Exception {
        assertParameters();
        return widgetHtml();
    }

    public String getWidgetUrl() throws Exception {
        assertParameters();
        return protocol() + domain + "/widget?data=" + urlencode(encryptedWidgetData());
    }

    public boolean trackEvent(String event) throws Exception {
        assertParameters();
        return "ok".equalsIgnoreCase(getResponse(trackEventUrl(event)));
    }

    public boolean deleteClient() throws Exception {
        assertParameters();
        return "ok".equalsIgnoreCase(getResponse(getClientDeletionUrl()));
    }

    public String getClientDeletionUrl() throws Exception {

        return protocol() + domain + "/api/v1/deleteClient?data=" + urlencode(encryptedClientDeletionData());
    }

    private String encryptedClientDeletionData() throws Exception {
        StringBuilder json = new StringBuilder("{");
        json.append(jsonString("client_id", clientId));
        return encryptWithAES(completeJson(json));
    }

    private void assertParameters() {
        if (clientId == null) throw new IllegalArgumentException("clientId is not set");
        if (key == null) throw new IllegalArgumentException("key is not set");
    }

    private String trackEventUrl(String event) throws Exception {
        String ownerIdParameter = this.ownerId != null && !this.ownerId.equals("")
            ? "owner_id=" + urlencode(this.ownerId) + "&"
            : "";

        return protocol() + domain + "/api/v1/trackEvent?" + ownerIdParameter + "data=" + urlencode(encryptedEventData(event, this.ownerId));
    }

    private String protocol() {
        return (useHTTPS ? "https" : "http") + "://";
    }

    private String encryptedWidgetData() throws Exception {
        return encryptWithAES(buildJson(null, null));
    }

    private String encryptedEventData(String event, String ownerId) throws Exception {
        return encryptWithAES(buildJson(event, ownerId));
    }

    private String buildJson(String event, String ownerId) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append(jsonString("client_id", getClientIdHash(clientId, ownerId)));
        json.append(jsonLiteral("params", paramsJson()));
        if (event != null) {
            json.append(jsonString("event", event));
        }
        return completeJson(json);
    }

    private static String getClientIdHash(String clientId, String ownerId) {
        if (ownerId == null || ownerId.equals("")){
            return clientId;
        }
        if (clientId == null){
            return null;
        }
        return getMd5(ownerId + ":" + clientId);
    }

    private static String getMd5(String value) {
        try {
            byte[] hashInBytes = MessageDigest.getInstance("MD5").digest(value.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String jsonString(String key, String value) {
        return String.format("\"%s\": \"%s\",", key, value);
    }

    private String jsonLiteral(String key, String value) {
        return String.format("\"%s\": %s,", key, value);
    }

    private String completeJson(StringBuilder json) {
        if (json.charAt(json.length() - 1) == ',')
            return json.substring(0, json.length() - 1) + "}";
        else
            return json.append("}").toString();
    }

    private String paramsJson() {
        StringBuilder paramsJson = new StringBuilder("{");
        for (String key : params.keySet()) {
            paramsJson.append(jsonString(key, params.get(key)));
        }
        return completeJson(paramsJson);
    }

    private String urlencode(String data) throws UnsupportedEncodingException {
        return URLEncoder.encode(data);
    }

    private String encryptWithAES(String data) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
        String base64EncryptedData = new BASE64Encoder().encode(encryptedData)
                .replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""); // Another hack, because java encoder adds newline chars every 76 character...

        byte[] iv = cipher.getIV();
        String base64EncodedIV = new BASE64Encoder().encode(iv)
                .replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");


        return base64EncryptedData + ":" + base64EncodedIV;
    }

    private String getResponse(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    private String widgetHtml() throws Exception {
        return String.format("<div id=\"_chWidget\"></div>\n" +
                "<script type=\"text/javascript\">\n" +
                "\t<!--\n" +
                "\tvar _chw = _chw || {};\n" +
                "\t_chw.type = \"iframe\";\n" +
                "\t_chw.domain = \"%s\";\n" +
                "\t_chw.data = \"%s\";\n" +
                "\t(function() {\n" +
                "\tvar ch = document.createElement(\"script\"); ch.type = \"text/javascript\"; ch.async = true;\n" +
                "\tch.src = (\"https:\" == document.location.protocol ? \"https://\" : \"http://\") + \"%s/v1/widget/script.js\";\n" +
                "\tvar s = document.getElementsByTagName(\"script\")[0]; s.parentNode.insertBefore(ch, s);\n" +
                "\t})();\n" +
                "\t//-->\n" +
                "</script>\n", domain, encryptedWidgetData(), domain);
    }
}
