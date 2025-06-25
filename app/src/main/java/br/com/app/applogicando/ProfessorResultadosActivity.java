package br.com.app.applogicando;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfessorResultadosActivity extends AppCompatActivity {

    private Button btnExportar, btnReiniciar, btnProximo, btnAnterior;
    private TextView txtTituloPergunta;
    private BarChart barChart;

    private List<Map<String, Integer>> resultados = new ArrayList<>();
    private String[] titulos = {
            "Local", "Horário", "Comentário Org", "Benefícios", "Trocas",
            "Comprometimento", "Planejamento", "Comentário Participação",
            "Divulgação", "Comentário Divulgação"
    };

    private int perguntaAtual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_resultados);

        // Bindings
        txtTituloPergunta = findViewById(R.id.txtTituloPergunta);
        barChart = findViewById(R.id.barChart);
        btnExportar = findViewById(R.id.btnExportar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnProximo = findViewById(R.id.btnProximo);
        btnAnterior = findViewById(R.id.btnAnterior);

        // Lê os dados uma vez só
        resultados = RelatorioProcessor.contarFrequencias(this);

        if (!resultados.isEmpty()) {
            exibirGrafico(perguntaAtual);
        } else {
            txtTituloPergunta.setText("Nenhuma resposta disponível.");
        }

        btnProximo.setOnClickListener(v -> {
            if (perguntaAtual < resultados.size() - 1) {
                perguntaAtual++;
                exibirGrafico(perguntaAtual);
            }
        });

        btnAnterior.setOnClickListener(v -> {
            if (perguntaAtual > 0) {
                perguntaAtual--;
                exibirGrafico(perguntaAtual);
            }
        });

        btnExportar.setOnClickListener(v -> enviarArquivoPorEmail());
        btnReiniciar.setOnClickListener(v -> confirmarReinicio());
    }

    private void exibirGrafico(int indice) {
        if (indice < 0 || indice >= resultados.size()) return;

        Map<String, Integer> frequencias = resultados.get(indice);
        txtTituloPergunta.setText("Pergunta: " + titulos[indice]);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int idx = 0;

        for (Map.Entry<String, Integer> entry : frequencias.entrySet()) {
            entries.add(new BarEntry(idx, entry.getValue()));
            labels.add(entry.getKey());
            idx++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Respostas");
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(0xFFFFFFFF); // Branco
        dataSet.setColor(0xFF00BCD4); // Azul claro

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(0xFFFFFFFF);
        xAxis.setTextSize(12f);
        xAxis.setLabelRotationAngle(-45);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(0xFFFFFFFF);
        yAxisLeft.setTextSize(12f);
        yAxisLeft.setDrawGridLines(true);

        barChart.getAxisRight().setEnabled(false);

        barChart.getLegend().setTextColor(0xFFFFFFFF);
        barChart.getLegend().setTextSize(14f);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void enviarArquivoPorEmail() {
        File arquivo = Exportador.getArquivoCSV();
        if (arquivo == null || !arquivo.exists()) {
            Toast.makeText(this, "Arquivo de respostas não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                arquivo
        );

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Respostas da Avaliação");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Segue em anexo o arquivo com as respostas coletadas.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(emailIntent, "Enviar respostas por e-mail..."));
    }

    private void confirmarReinicio() {
        new AlertDialog.Builder(this)
                .setTitle("Reiniciar dados")
                .setMessage("Deseja apagar todas as respostas salvas?\nEssa ação não poderá ser desfeita.")
                .setPositiveButton("Apagar", (dialog, which) -> apagarArquivos())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void apagarArquivos() {
        boolean sucesso = Exportador.apagarArquivos();
        if (sucesso) {
            Toast.makeText(this, "Arquivos apagados com sucesso.", Toast.LENGTH_SHORT).show();
            recreate(); // Reinicia a activity
        } else {
            Toast.makeText(this, "Falha ao apagar os arquivos.", Toast.LENGTH_SHORT).show();
        }
    }
}
