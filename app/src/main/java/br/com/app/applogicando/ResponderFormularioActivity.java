package br.com.app.applogicando;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponderFormularioActivity extends AppCompatActivity {

    private LinearLayout layoutPerguntas;
    private Button btnEnviarRespostas;
    private String formularioId;

    private final List<Pergunta> perguntas = new ArrayList<>();
    private final Map<String, Object> respostaViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_formulario);

        formularioId = getIntent().getStringExtra("formularioId");
        if (formularioId == null || formularioId.isEmpty()) {
            Toast.makeText(this, "ID do formulário inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        layoutPerguntas = findViewById(R.id.layoutPerguntas);
        btnEnviarRespostas = findViewById(R.id.btnEnviarRespostas);

        btnEnviarRespostas.setOnClickListener(v -> enviarRespostas());

        carregarPerguntas();
    }

    private void carregarPerguntas() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                String token = prefs.getString("token", "");

                URL url = new URL("https://logicando-api.onrender.com/perguntas/formulario/" + formularioId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + token);

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
                    perguntas.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Pergunta p = new Pergunta();
                        p.id = obj.getString("id");
                        p.texto = obj.getString("texto");
                        p.tipo = obj.getString("tipo");
                        p.opcoes = obj.optJSONArray("opcoes");
                        perguntas.add(p);
                    }

                    runOnUiThread(this::montarPerguntas);
                } else {
                    runOnUiThread(() -> Toast.makeText(this,
                            "Erro ao carregar perguntas", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this,
                        "Erro de conexão", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void montarPerguntas() {
        layoutPerguntas.removeAllViews();
        for (Pergunta p : perguntas) {
            TextView tv = new TextView(this);
            tv.setText(p.texto);
            tv.setTextSize(16f);
            tv.setPadding(0, 16, 0, 8);
            layoutPerguntas.addView(tv);

            if ("MULTIPLA_ESCOLHA".equals(p.tipo) && p.opcoes != null) {
                RadioGroup rg = new RadioGroup(this);
                for (int i = 0; i < p.opcoes.length(); i++) {
                    String opcao = p.opcoes.optString(i);
                    RadioButton rb = new RadioButton(this);
                    rb.setText(opcao);
                    rg.addView(rb);
                }
                layoutPerguntas.addView(rg);
                respostaViews.put(p.id, rg);
            } else {
                EditText et = new EditText(this);
                et.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutPerguntas.addView(et);
                respostaViews.put(p.id, et);
            }
        }
    }

    private void enviarRespostas() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                String token = prefs.getString("token", "");

                JSONArray respostasArray = new JSONArray();
                for (Pergunta p : perguntas) {
                    String resposta = "";
                    Object view = respostaViews.get(p.id);
                    if (view instanceof EditText) {
                        resposta = ((EditText) view).getText().toString().trim();
                    } else if (view instanceof RadioGroup) {
                        RadioGroup rg = (RadioGroup) view;
                        int checked = rg.getCheckedRadioButtonId();
                        if (checked != -1) {
                            RadioButton rb = rg.findViewById(checked);
                            resposta = rb.getText().toString();
                        }
                    }
                    JSONObject respObj = new JSONObject();
                    respObj.put("perguntaId", p.id);
                    respObj.put("resposta", resposta);
                    respostasArray.put(respObj);
                }

                JSONObject payload = new JSONObject();
                payload.put("formularioId", formularioId);
                payload.put("respostas", respostasArray);

                URL url = new URL("https://logicando-api.onrender.com/respostas");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + token);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_CREATED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Respostas enviadas com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, AlunoMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this,
                            "Erro ao enviar respostas", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this,
                        "Erro de conexão", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private static class Pergunta {
        String id;
        String texto;
        String tipo;
        JSONArray opcoes;
    }
}
