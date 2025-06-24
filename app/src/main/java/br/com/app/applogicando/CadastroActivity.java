package br.com.app.applogicando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.app.applogicando.ApiConfig;

public class CadastroActivity extends AppCompatActivity {

    EditText editNome, editUsernameCadastro, editSenhaCadastro;
    RadioGroup radioGroupPapel;
    Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Referências
        editNome = findViewById(R.id.editNome);
        editUsernameCadastro = findViewById(R.id.editUsernameCadastro);
        editSenhaCadastro = findViewById(R.id.editSenhaCadastro);
        radioGroupPapel = findViewById(R.id.radioGroupPapel);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String username = editUsernameCadastro.getText().toString().trim();
            String senha = editSenhaCadastro.getText().toString().trim();
            String papel = (radioGroupPapel.getCheckedRadioButtonId() == R.id.radioAluno) ? "ALUNO" : "PROFESSOR";

            if (nome.isEmpty() || username.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            cadastrarUsuario(nome, username, senha, papel);
        });
    }

    private void cadastrarUsuario(String nome, String username, String senha, String papel) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + "/usuarios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("name", nome);
                json.put("username", username);
                json.put("password", senha);
                json.put("role", papel);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 201) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show());
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
