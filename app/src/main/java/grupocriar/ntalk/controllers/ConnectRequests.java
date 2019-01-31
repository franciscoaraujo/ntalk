package grupocriar.ntalk.controllers;

import java.io.IOException;
import java.net.SocketTimeoutException;

import br.com.grupocriar.ntalk.model.Envio;
import br.com.grupocriar.ntalk.utils.JsonUtils;
import swap.connection.http.SwapHttp;

/**
 * Created by francisco on 11/21/17.
 */


public class ConnectRequests {

    private static final String SERVER_URL = "http://cesio.keynet.com.br:8080/ntalk";
    private int reponseCode;

    /**
     * @param envio
     * @param url
     * @param token
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> String post(Envio<T> envio, String url, String token) throws IOException {
        byte[] json = JsonUtils.toByteJson(envio);
        try (SwapHttp httpClient = new SwapHttp(SERVER_URL + trataUrl(url))) {
            httpClient.open(false)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", token)
                    .setContentType("application/json")
                    .setContentLength(json.length)
                    .setDoOutput(true)
                    .setRequestMethod("POST")
                    .setConnectTimeout(1_000)
                    .setReadTimeout(1_000)
                    .connection()
                    .send(json);
            getResponseCode(httpClient);
            return httpClient.readAll(2);
        }
    }

    private void getResponseCode(SwapHttp httpClient) throws IOException {

        this.reponseCode = httpClient.getResponseCode();
    }

    public int getResponseCode() {
        return reponseCode;
    }


    /**
     * @param url
     * @param token
     * @return
     * @throws IOException
     */
    public String get(String url, String token) throws IOException {
        try (SwapHttp httpClient = new SwapHttp(SERVER_URL + trataUrl(url))) {
            httpClient.open(false)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", token)
                    .setRequestMethod("GET")
                    .setConnectTimeout(1_000)
                    .setReadTimeout(1_000)
                    .connection();
            getResponseCode(httpClient);
            return httpClient.readAll(2);

        }
    }


    private static String trataUrl(String url) {
        url = url.trim();
        return url.startsWith("/") ? url : "/" + url;
    }
}
