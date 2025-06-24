package br.com.app.applogicando;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.app.applogicando.ApiConfig;

public class CriarPerguntaActivity extends AppCompatActivity {

    EditText editTextoPergunta;
    EditText editOpcao1, editOpcao2, editOpcao3;
    Spinner spinnerTipoPergunta;
    Button btnAdicionarPergunta;
    String formularioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_pergunta);

        formularioId = getIntent().getStringExtra("formularioId");
        if (formularioId == null || formularioId.isEmpty()) {
            Toast.makeText(this, "ID do formulário inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextoPergunta = findViewById(R.id.editTextoPergunta);
        spinnerTipoPergunta = findViewById(R.id.spinnerTipoPergunta);
        btnAdicionarPergunta = findViewById(R.id.btnAdicionarPergunta);
        editOpcao1 = findViewById(R.id.editOpcao1);
        editOpcao2 = findViewById(R.id.editOpcao2);
        editOpcao3 = findViewById(R.id.editOpcao3);

        spinnerTipoPergunta.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String tipo = spinnerTipoPergunta.getSelectedItem().toString();
                boolean mostrarOpcoes = tipo.equals("MULTIPLA_ESCOLHA");
                editOpcao1.setVisibility(mostrarOpcoes ? View.VISIBLE : View.GONE);
                editOpcao2.setVisibility(mostrarOpcoes ? View.VISIBLE : View.GONE);
                editOpcao3.setVisibility(mostrarOpcoes ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnAdicionarPergunta.setOnClickListener(v -> {
            String texto = editTextoPergunta.getText().toString().trim();
            String tipo = spinnerTipoPergunta.getSelectedItem().toString();

            if (texto.isEmpty()) {
                Toast.makeText(this, "Digite o texto da pergunta", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray opcoes = new JSONArray();
            if (tipo.equals("MULTIPLA_ESCOLHA")) {
                String o1 = editOpcao1.getText().toString().trim();
                String o2 = editOpcao2.getText().toString().trim();
                String o3 = editOpcao3.getText().toString().trim();

                if (o1.isEmpty() || o2.isEmpty() || o3.isEmpty()) {
                    Toast.makeText(this, "Preencha todas as opções", Toast.LENGTH_SHORT).show();
                    return;
                }

                opcoes.put(o1);
                opcoes.put(o2);
                opcoes.put(o3);
            }

            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("token", null);

            adicionarPergunta(formularioId, texto, tipo, token, opcoes);
        });
    }

    private void adicionarPergunta(String formularioId, String texto, String tipo, String token, JSONArray opcoes) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "/perguntas");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + token);
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("formularioId", formularioId);
                json.put("texto", texto);
                json.put("tipo", tipo);
                if (tipo.equals("MULTIPLA_ESCOLHA")) {
                    json.put("opcoes", opcoes);
                }

                Log.d("CRIAR_PERGUNTA", json.toString());

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 201 || responseCode == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Pergunta adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                        editTextoPergunta.setText("");
                        editOpcao1.setText("");
                        editOpcao2.setText("");
                        editOpcao3.setText("");
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Erro ao adicionar pergunta", Toast.LENGTH_SHORT).show()
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
