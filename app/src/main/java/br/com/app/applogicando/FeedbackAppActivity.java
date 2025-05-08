package br.com.app.applogicando;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackAppActivity extends AppCompatActivity {

    RadioGroup radioNavegacao, radioVisual, radioDesempenho;
    EditText editGostou, editMelhorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_app);

        // Referências aos componentes
        radioNavegacao = findViewById(R.id.radioNavegacao);
        radioVisual = findViewById(R.id.radioVisual);
        radioDesempenho = findViewById(R.id.radioDesempenho);
        editGostou = findViewById(R.id.editGostou);
        editMelhorias = findViewById(R.id.editMelhorias);
        Button btnEnviar = findViewById(R.id.btnEnviarFeedback);

        // Ação do botão Enviar
        btnEnviar.setOnClickListener(v -> {
            if (radioNavegacao.getCheckedRadioButtonId() == -1 ||
                    radioVisual.getCheckedRadioButtonId() == -1 ||
                    radioDesempenho.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Por favor, responda todas as perguntas obrigatórias.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Coletar respostas
            String respostaNavegacao = ((RadioButton) findViewById(radioNavegacao.getCheckedRadioButtonId())).getText().toString();
            String respostaVisual = ((RadioButton) findViewById(radioVisual.getCheckedRadioButtonId())).getText().toString();
            String respostaDesempenho = ((RadioButton) findViewById(radioDesempenho.getCheckedRadioButtonId())).getText().toString();
            String textoGostou = editGostou.getText().toString().trim();
            String textoMelhorias = editMelhorias.getText().toString().trim();

            // Exibir resumo (pode ser adaptado para salvar em CSV/JSON futuramente)
            String resumo = "Feedback coletado:\n"
                    + "Navegação: " + respostaNavegacao + "\n"
                    + "Visual: " + respostaVisual + "\n"
                    + "Desempenho: " + respostaDesempenho + "\n"
                    + "Gostou: " + textoGostou + "\n"
                    + "Melhorias: " + textoMelhorias;

            Toast.makeText(this, "Obrigado pelo feedback!", Toast.LENGTH_LONG).show();

            // Finaliza tela
            finish();
        });
    }
}
