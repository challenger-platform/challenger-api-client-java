package com.challengerplatform;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.junit.Ignore;
import org.junit.Test;

import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ChallengerTest {

    private static final String DOMAIN = "beta.challengerplatform.com";
    private static final String CLIENT_ID = "1";
    private static final String SECRET_KEY = "demo32bitKey____________________";

    @Test
    public void shouldRetrieveWidget() throws Exception {
        Challenger challenger = new Challenger(DOMAIN);
        challenger.setClientId(CLIENT_ID);
        challenger.setKey(SECRET_KEY);
        challenger.addParam("param1", "value1");
        challenger.addParam("param2", "value2");

        String response = challenger.getWidgetHtml();
        // No way to assert, different every time. Paste test output to html and try to open
        out.println(response);
    }

    @Test
    public void shouldGetOkResponseOnWidgetUrl() throws Exception {
        Challenger challenger = new Challenger(DOMAIN);
        challenger.setClientId(CLIENT_ID);
        challenger.setKey(SECRET_KEY);
        challenger.addParam("param1", "value1");
        challenger.addParam("param2", "value2");

        String widgetUrl = challenger.getWidgetUrl();
        out.println("Widget URl: " + widgetUrl);

        HttpResponse<String> response = Unirest.get(widgetUrl).asObject(String.class);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getBody(), is(not("ERROR: decryption error")));
    }

    @Test
    public void shouldGetOkResponseOnWidgetUrlWithUnicodeSymbols() throws Exception {
        Challenger challenger = new Challenger(DOMAIN);
        challenger.setClientId(CLIENT_ID);
        challenger.setKey(SECRET_KEY);
        challenger.addParam("paramŠ", "valueŠ");
        challenger.addParam("paramŪ", "valueŪ");

        String widgetUrl = challenger.getWidgetUrl();
        out.println("Widget URl: " + widgetUrl);

        HttpResponse<String> response = Unirest.get(widgetUrl).asObject(String.class);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getBody(), is(not("ERROR: decryption error")));
    }

    @Test
    public void shouldTrackEvent() throws Exception {
        Challenger challenger = new Challenger(DOMAIN);
        challenger.setClientId(CLIENT_ID);
        challenger.setKey(SECRET_KEY);

        boolean response = challenger.trackEvent("some_event");
        assertThat(response, is(true));
    }

    @Test
    @Ignore
    public void shouldTrackEventThroughHttps() throws Exception {
        Challenger challenger = new Challenger(DOMAIN);
        challenger.setUseHTTPS(true);
        challenger.setClientId(CLIENT_ID);
        challenger.setKey(SECRET_KEY);

        boolean response = challenger.trackEvent("some_event");
        assertThat(response, is(true));
        // Requires "-Djsse.enableSNIExtension=false" because: http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
        // With it fails:
        // javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No subject alternative DNS name matching alpha.challengerplatform.com found.
        // Assume that SSL will be configured properly on prod server
    }
}
