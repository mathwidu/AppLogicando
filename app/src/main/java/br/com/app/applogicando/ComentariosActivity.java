package br.com.app.applogicando;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComentariosActivity extends AppCompatActivity {

    private TextView txtPerguntaAtual;
    private LinearLayout layoutComentarios;
    private Button btnAnterior, btnProximo;

    private List<String> perguntas = new ArrayList<>();
    private Map<String, List<String>> comentarios;
    private int indiceAtual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        txtPerguntaAtual = findViewById(R.id.txtPerguntaAtual);
        layoutComentarios = findViewById(R.id.layoutListaComentarios);
        btnAnterior = findViewById(R.id.btnAnteriorComentario);
        btnProximo = findViewById(R.id.btnProximoComentario);

        comentarios = ComentariosHolder.getComentarios();
        perguntas.addAll(comentarios.keySet());

        if (!perguntas.isEmpty()) {
            mostrarComentarios(indiceAtual);
        }

        btnAnterior.setOnClickListener(v -> {
            if (indiceAtual > 0) {
                indiceAtual--;
                mostrarComentarios(indiceAtual);
            }
        });

        btnProximo.setOnClickListener(v -> {
            if (indiceAtual < perguntas.size() - 1) {
                indiceAtual++;
                mostrarComentarios(indiceAtual);
            }
        });
    }

    private void mostrarComentarios(int indice) {
        layoutComentarios.removeAllViews();

        String pergunta = perguntas.get(indice);
        txtPerguntaAtual.setText("Pergunta: " + pergunta);

        List<String> lista = comentarios.get(pergunta);

        for (String comentario : lista) {
            TextView txt = new TextView(this);
            txt.setText("- " + comentario);
            txt.setTextSize(14f);
            txt.setTextColor(0xFFD3DEE9);
            txt.setPadding(0, 8, 0, 8);
            layoutComentarios.addView(txt);
        }
    }
}
