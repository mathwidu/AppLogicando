package br.com.app.applogicando;

import android.content.Context;
import android.content.SharedPreferences;
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

import br.com.app.applogicando.ApiConfig;

public class ListarFormulariosActivity extends AppCompatActivity {

    private ListView listViewFormularios;
    private Button btnVoltar;
    private final ArrayList<String> listaFormularios = new ArrayList<>();
    private final ArrayList<String> listaFormularioIds = new ArrayList<>();
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

        if (username == null || username.isEmpty() || senha == null || senha.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            if (username == null || username.isEmpty()) {
                username = prefs.getString("username", "");
            }
            if (senha == null || senha.isEmpty()) {
                senha = prefs.getString("senha", "");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaFormularios);
        listViewFormularios.setAdapter(adapter);

        listViewFormularios.setOnItemClickListener((parent, view, position, id) -> {
            String formularioId = listaFormularioIds.get(position);
            android.content.Intent intent = new android.content.Intent(this, VisualizarRespostasActivity.class);
            android.os.Bundle extras = getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            intent.putExtra("formularioId", formularioId);
            startActivity(intent);
        });

        btnVoltar.setOnClickListener(view -> finish());

        carregarFormularios();
    }

    private void carregarFormularios() {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "/formularios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                String basicAuth = ApiConfig.buildBasicAuthHeader(username, senha);
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
                    listaFormularioIds.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject formulario = jsonArray.getJSONObject(i);
                        String id = formulario.optString("id");
                        String titulo = formulario.getString("titulo");
                        String criador = formulario.getString("criadorNome");
                        String criadoEm = formulario.getString("criadoEm");

                        listaFormularioIds.add(id);
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
