package br.com.app.applogicando;

import android.util.Base64;

public class ApiConfig {
    public static final String BASE_URL = "https://logicando-api.onrender.com";

    public static String buildBasicAuthHeader(String username, String password) {
        return "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
    }
}
