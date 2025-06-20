package br.com.app.applogicando;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ProfessorMainActivity extends AppCompatActivity {

    private Button btnCriarFormulario;
    private Button btnVerFormularios;
    private Button btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_main);

        btnCriarFormulario = findViewById(R.id.btnCriarFormulario);
        btnVerFormularios = findViewById(R.id.btnVerFormularios);
        btnSair = findViewById(R.id.btnSair);

        btnCriarFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorMainActivity.this, CriarFormularioActivity.class);
                startActivity(intent);
            }
        });

        btnVerFormularios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorMainActivity.this, ListarFormulariosActivity.class);
                startActivity(intent);
            }
        });

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
