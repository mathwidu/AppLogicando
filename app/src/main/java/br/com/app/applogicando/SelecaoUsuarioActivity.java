package br.com.app.applogicando;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SelecaoUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_usuario);

        // Referência aos elementos do layout
        LinearLayout cardAluno = findViewById(R.id.cardAluno);
        LinearLayout cardProfessor = findViewById(R.id.cardProfessor);
        Button btnAjuda = findViewById(R.id.btnAjuda); // <-- Agora é Button

        // Ação ao clicar em "Aluno"
        cardAluno.setOnClickListener(v -> {
            Intent intent = new Intent(SelecaoUsuarioActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Ação ao clicar em "Professor"
        cardProfessor.setOnClickListener(v -> {
            Intent intent = new Intent(SelecaoUsuarioActivity.this, ProfessorResultadosActivity.class);
            startActivity(intent);
        });

        // Ação ao clicar no botão de ajuda
        btnAjuda.setOnClickListener(v -> mostrarDialogoAjuda());
    }

    /**
     * Exibe um AlertDialog explicando o objetivo do app
     */
    private void mostrarDialogoAjuda() {
        new AlertDialog.Builder(this)
                .setTitle("Sobre o aplicativo")
                .setMessage("Este aplicativo permite que participantes das oficinas e eventos do projeto Logicando avaliem sua experiência de forma anônima e segura.\n\n" +
                        "👨‍🎓 Toque em 'Aluno' para iniciar a avaliação.\n" +
                        "👩‍🏫 Toque em 'Professor' para visualizar os resultados coletados.")
                .setPositiveButton("Entendi", null)
                .show();
    }
}
