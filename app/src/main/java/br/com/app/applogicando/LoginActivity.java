package br.com.app.applogicando;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editSenha;
    Button btnLogin;
    TextView txtCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editSenha = findViewById(R.id.editSenha);
        btnLogin = findViewById(R.id.btnLogin);
        txtCadastro = findViewById(R.id.txtCadastro);

        btnLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String senha = editSenha.getText().toString().trim();

            if (username.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = Base64.encodeToString((username + ":" + senha).getBytes(), Base64.NO_WRAP);
            autenticarUsuario(username, senha, token);
        });

        txtCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void autenticarUsuario(String username, String senha, String token) {
        new Thread(() -> {
            try {
                URL url = new URL("https://logicando-api.onrender.com/usuarios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + token);

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    Scanner scanner = new Scanner(is);
                    String jsonResponse = scanner.useDelimiter("\\A").next();
                    scanner.close();

                    JSONObject usuario = new JSONObject(jsonResponse);
                    String papel = usuario.getString("role");

                    SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    prefs.edit()
                            .putString("token", token)
                            .putString("username", username)
                            .putString("senha", senha)
                            .putString("papel", papel)
                            .apply();

                    runOnUiThread(() -> {
                        Intent intent;
                        if (papel.equals("ALUNO")) {
                            intent = new Intent(this, AlunoMainActivity.class);
                        } else {
                            intent = new Intent(this, ProfessorMainActivity.class);
                        }
                        intent.putExtra("username", username);
                        intent.putExtra("senha", senha);
                        startActivity(intent);
                        finish();
                    });

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Usuário ou senha inválidos (erro " + responseCode + ")", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}
