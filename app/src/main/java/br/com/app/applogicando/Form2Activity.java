package br.com.app.applogicando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Form2Activity extends AppCompatActivity {

    RadioGroup radioBeneficios, radioTrocas, radioComprometimento, radioPlanejamento;
    EditText editComentariosParticipacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form2);

        radioBeneficios = findViewById(R.id.radioBeneficios);
        radioTrocas = findViewById(R.id.radioTrocas);
        radioComprometimento = findViewById(R.id.radioComprometimento);
        radioPlanejamento = findViewById(R.id.radioPlanejamento);
        editComentariosParticipacao = findViewById(R.id.editComentariosParticipacao);

        Button btnProximo = findViewById(R.id.btnProximo2);
        btnProximo.setOnClickListener(v -> {
            if (radioBeneficios.getCheckedRadioButtonId() == -1 ||
                    radioTrocas.getCheckedRadioButtonId() == -1 ||
                    radioComprometimento.getCheckedRadioButtonId() == -1 ||
                    radioPlanejamento.getCheckedRadioButtonId() == -1) {

                Toast.makeText(this, "Por favor, responda todas as perguntas obrigatórias.", Toast.LENGTH_SHORT).show();
                return;
            }

            DadosFormulario.beneficios = ((RadioButton)findViewById(radioBeneficios.getCheckedRadioButtonId())).getText().toString();
            DadosFormulario.trocas = ((RadioButton)findViewById(radioTrocas.getCheckedRadioButtonId())).getText().toString();
            DadosFormulario.comprometimento = ((RadioButton)findViewById(radioComprometimento.getCheckedRadioButtonId())).getText().toString();
            DadosFormulario.planejamento = ((RadioButton)findViewById(radioPlanejamento.getCheckedRadioButtonId())).getText().toString();
            DadosFormulario.comentarioParticipacao = editComentariosParticipacao.getText().toString();

            Intent intent = new Intent(Form2Activity.this, Form3Activity.class);
            startActivity(intent);
        });
    }
}
