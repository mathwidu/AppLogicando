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

public class ListarFormulariosActivity extends AppCompatActivity {

    private ListView listViewFormularios;
    private Button btnVoltar;
    private final ArrayList<String> listaFormularios = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private String username;
    private String senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_formularios);

        listViewFormularios = findViewById(R.id.listViewFormularios);
        btnVoltar = findViewById(R.id.btnVoltar);

        username = getIntent().getStringExtra("username");
        senha = getIntent().getStringExtra("senha");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaFormularios);
        listViewFormularios.setAdapter(adapter);

        btnVoltar.setOnClickListener(view -> finish());

        carregarFormularios();
    }

    private void carregarFormularios() {
        new Thread(() -> {
            try {
                URL url = new URL("https://logicando-api.onrender.com/formularios");
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

                    listaFormularios.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject formulario = jsonArray.getJSONObject(i);
                        String titulo = formulario.getString("titulo");
                        String criador = formulario.getString("criadorNome");
                        String criadoEm = formulario.getString("criadoEm");

                        listaFormularios.add(titulo + "\nCriado por: " + criador + "\nEm: " + criadoEm);
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

                    String errorMsg = "Erro ao buscar formulários.\nCódigo: " + responseCode +
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
