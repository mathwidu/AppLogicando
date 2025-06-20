package br.com.app.applogicando;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VisualizarRespostasActivity extends AppCompatActivity {

    private ListView listViewRespostas;
    private Button btnVoltarRespostas;
    private final ArrayList<String> listaRespostas = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private String username;
    private String senha;
    private String formularioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_respostas);

        listViewRespostas = findViewById(R.id.listViewRespostas);
        btnVoltarRespostas = findViewById(R.id.btnVoltarRespostas);

        username = getIntent().getStringExtra("username");
        senha = getIntent().getStringExtra("senha");
        formularioId = getIntent().getStringExtra("formularioId");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaRespostas);
        listViewRespostas.setAdapter(adapter);

        btnVoltarRespostas.setOnClickListener(v -> finish());

        carregarRespostas();
    }

    private void carregarRespostas() {
        new Thread(() -> {
            try {
                URL url = new URL("https://logicando-api.onrender.com/respostas/formulario/" + formularioId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                String basicAuth = "Basic " + android.util.Base64.encodeToString(
                        (username + ":" + senha).getBytes(), android.util.Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", basicAuth);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());

                    listaRespostas.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject resposta = jsonArray.getJSONObject(i);
                        listaRespostas.add(resposta.toString());
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } else {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorReader.close();

                    String errorMsg = "Erro ao buscar respostas.\nCódigo: " + responseCode +
                            "\nMensagem: " + errorResponse.toString();

                    runOnUiThread(() -> Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                String fullError = e.getClass().getSimpleName() + ": " + e.getMessage();
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + fullError, Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}

