package br.com.app.applogicando;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CriarFormularioActivity extends AppCompatActivity {

    EditText editTituloFormulario;
    Button btnCriarFormulario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_formulario);

        editTituloFormulario = findViewById(R.id.editTituloFormulario);
        btnCriarFormulario = findViewById(R.id.btnCriarFormularioEnviar);

        btnCriarFormulario.setOnClickListener(v -> {
            String titulo = editTituloFormulario.getText().toString().trim();

            if (titulo.isEmpty()) {
                Toast.makeText(this, "Informe o título do formulário", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("token", null);
            String papel = prefs.getString("papel", "");

            if (!"PROFESSOR".equals(papel)) {
                Toast.makeText(this, "Apenas professores podem criar formulários", Toast.LENGTH_SHORT).show();
                return;
            }

            criarFormulario(titulo, token);
        });
    }

    private void criarFormulario(String titulo, String token) {
        new Thread(() -> {
            try {
                URL url = new URL("https://logicando-api.onrender.com/formularios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + token);
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("titulo", titulo);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200 || responseCode == 201) {
                    String formularioId = null;

                    try {
                        InputStream is = conn.getInputStream();
                        Scanner scanner = new Scanner(is).useDelimiter("\\A");
                        String jsonResponse = scanner.hasNext() ? scanner.next() : "";
                        scanner.close();

                        if (!jsonResponse.isEmpty()) {
                            JSONObject responseJson = new JSONObject(jsonResponse);
                            formularioId = responseJson.optString("id", null);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(); // pode não ter corpo
                    }

                    final String finalFormularioId = formularioId;

                    runOnUiThread(() -> {
                        if (finalFormularioId != null) {
                            Toast.makeText(this, "✅ Formulário criado com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, CriarPerguntaActivity.class);
                            intent.putExtra("formularioId", finalFormularioId);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Formulário criado, mas o ID não foi retornado.", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Erro ao criar formulário", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro de conexão", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
