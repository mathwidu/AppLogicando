package br.com.app.applogicando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Form3Activity extends AppCompatActivity {

    RadioGroup radioDivulgacao;
    EditText editComentariosDivulgacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form3);

        radioDivulgacao = findViewById(R.id.radioDivulgacao);
        editComentariosDivulgacao = findViewById(R.id.editComentariosDivulgacao);

        Button btnEnviar = findViewById(R.id.btnEnviarFinal);
        btnEnviar.setOnClickListener(v -> {
            if (radioDivulgacao.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Por favor, responda a pergunta obrigatória.", Toast.LENGTH_SHORT).show();
                return;
            }

            DadosFormulario.divulgacao = ((RadioButton)findViewById(radioDivulgacao.getCheckedRadioButtonId())).getText().toString();
            DadosFormulario.comentarioDivulgacao = editComentariosDivulgacao.getText().toString();

            Resposta r = new Resposta(
                    DadosFormulario.local, DadosFormulario.horario, DadosFormulario.comentarioOrg,
                    DadosFormulario.beneficios, DadosFormulario.trocas, DadosFormulario.comprometimento, DadosFormulario.planejamento, DadosFormulario.comentarioParticipacao,
                    DadosFormulario.divulgacao, DadosFormulario.comentarioDivulgacao
            );

            Exportador.exportar(Form3Activity.this, r);

            Intent intent = new Intent(Form3Activity.this, ObrigadoActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
