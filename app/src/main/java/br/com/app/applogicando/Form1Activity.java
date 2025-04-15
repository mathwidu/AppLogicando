package br.com.app.applogicando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Form1Activity extends AppCompatActivity {

    RadioGroup radioLocal, radioHorario;
    EditText editComentariosOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);

        radioLocal = findViewById(R.id.radioLocal);
        radioHorario = findViewById(R.id.radioHorario);
        editComentariosOrg = findViewById(R.id.editComentariosOrg);

        Button btnProximo = findViewById(R.id.btnProximo1);
        btnProximo.setOnClickListener(v -> {
            int selectedLocal = radioLocal.getCheckedRadioButtonId();
            int selectedHorario = radioHorario.getCheckedRadioButtonId();
            String comentarios = editComentariosOrg.getText().toString();

            if (selectedLocal == -1 || selectedHorario == -1) {
                Toast.makeText(this, "Por favor, responda todas as perguntas obrigatórias.", Toast.LENGTH_SHORT).show();
                return;
            }

            DadosFormulario.local = ((RadioButton)findViewById(selectedLocal)).getText().toString();
            DadosFormulario.horario = ((RadioButton)findViewById(selectedHorario)).getText().toString();
            DadosFormulario.comentarioOrg = comentarios;

            Intent intent = new Intent(Form1Activity.this, Form2Activity.class);
            startActivity(intent);
        });
    }
}
