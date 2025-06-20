package br.com.app.applogicando;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlunoMainActivity extends AppCompatActivity {

    Button btnFormulariosDisponiveis, btnSairAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_main);

        btnFormulariosDisponiveis = findViewById(R.id.btnFormulariosDisponiveis);
        btnSairAluno = findViewById(R.id.btnSairAluno);

        btnFormulariosDisponiveis.setOnClickListener(v -> {
            //Intent intent = new Intent(this, ListaFormulariosAlunoActivity.class);
            //startActivity(intent);
        });

        btnSairAluno.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
