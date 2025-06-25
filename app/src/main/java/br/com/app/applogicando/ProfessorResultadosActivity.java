package br.com.app.applogicando;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ... [mesmo início de imports e declarações] ...

public class ProfessorResultadosActivity extends AppCompatActivity {

    private Button btnExportar, btnReiniciar, btnProximo, btnAnterior, btnVerComentarios, btnAjudaGrafico;
    private TextView txtTituloPergunta;
    private LinearLayout layoutLegenda;
    private BarChart barChart;

    private final String[] titulosTodas = {
            "Local", "Horário", "Comentário Org",
            "Benefícios", "Trocas", "Comprometimento", "Planejamento", "Comentário Participação",
            "Divulgação", "Comentário Divulgação"
    };

    private final int[] indicesComEscala = {0, 1, 3, 4, 5, 6, 8};

    private final List<Map<String, Integer>> resultadosEscala = new ArrayList<>();
    private final List<String> titulosEscala = new ArrayList<>();
    private final Map<String, List<String>> comentariosPorPergunta = new HashMap<>();

    private int perguntaAtual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_resultados);

        txtTituloPergunta = findViewById(R.id.txtTituloPergunta);
        layoutLegenda = findViewById(R.id.layoutLegenda);
        barChart = findViewById(R.id.barChart);
        btnExportar = findViewById(R.id.btnExportar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnProximo = findViewById(R.id.btnProximo);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnVerComentarios = findViewById(R.id.btnVerComentarios);
        btnAjudaGrafico = findViewById(R.id.btnAjudaGrafico);

        List<Map<String, Integer>> todosResultados = RelatorioProcessor.contarFrequencias(this);
        List<Map<String, List<String>>> todosComentarios = RelatorioProcessor.coletarComentarios(this);

        for (int i : indicesComEscala) {
            resultadosEscala.add(todosResultados.get(i));
            titulosEscala.add(titulosTodas[i]);
        }

        String[] chavesComentario = {"Comentário Org", "Comentário Participação", "Comentário Divulgação"};
        int[] indicesComentarios = {2, 7, 9};
        for (int i = 0; i < indicesComentarios.length; i++) {
            Map<String, List<String>> bloco = todosComentarios.get(indicesComentarios[i]);
            comentariosPorPergunta.put(chavesComentario[i], bloco.get("respostas"));
        }

        // Legenda colorida
        String[] categorias = {"1", "2", "3", "4", "5", "SCO"};
        String[] descricoes = {
                "Muito Insatisfeito", "Insatisfeito", "Parcialmente Satisfeito",
                "Satisfeito", "Muito Satisfeito", "Sem Opinião"
        };
        int[] cores = {
                0xFFF44336, 0xFFFF9800, 0xFFFFEB3B,
                0xFF4CAF50, 0xFF2E7D32, 0xFF9E9E9E
        };
        for (int i = 0; i < categorias.length; i++) {
            TextView item = new TextView(this);
            item.setText(categorias[i] + ": " + descricoes[i]);
            item.setTextColor(cores[i]);
            item.setTextSize(13f);
            item.setPadding(12, 4, 12, 4);
            layoutLegenda.addView(item);
        }

        btnAjudaGrafico.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Ajuda")
                .setMessage("Esta tela mostra gráficos com as respostas dos alunos.\n\n• Eixo Y: Quantidade de alunos.\n• Eixo X: Tipo de resposta (1 a 5 ou SCO).\n\nUse os botões Anterior e Próximo para ver diferentes perguntas.")
                .setPositiveButton("Entendi", null)
                .show()
        );

        if (!resultadosEscala.isEmpty()) {
            exibirGrafico(perguntaAtual);
        } else {
            txtTituloPergunta.setText("Nenhuma resposta disponível.");
        }

        btnProximo.setOnClickListener(v -> {
            if (perguntaAtual < resultadosEscala.size() - 1) {
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
        btnVerComentarios.setOnClickListener(v -> abrirComentarios());
    }

    private void exibirGrafico(int indice) {
        Map<String, Integer> frequencias = resultadosEscala.get(indice);
        txtTituloPergunta.setText("Pergunta: " + titulosEscala.get(indice));

        String[] labelsCurto = {"1", "2", "3", "4", "5", "SCO"};
        String[] chavesCSV = {
                "1 - Muito Insatisfeito",
                "2 - Insatisfeito",
                "3 - Parcialmente Satisfeito",
                "4 - Satisfeito",
                "5 - Muito Satisfeito",
                "Sem condições de opinar"
        };

        Map<String, Integer> coresPorCategoria = new HashMap<>();
        coresPorCategoria.put("1", 0xFFF44336);
        coresPorCategoria.put("2", 0xFFFF9800);
        coresPorCategoria.put("3", 0xFFFFEB3B);
        coresPorCategoria.put("4", 0xFF4CAF50);
        coresPorCategoria.put("5", 0xFF2E7D32);
        coresPorCategoria.put("SCO", 0xFF9E9E9E);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> cores = new ArrayList<>();

        for (int i = 0; i < labelsCurto.length; i++) {
            String categoriaCurta = labelsCurto[i];
            String chaveCSV = chavesCSV[i];
            Integer valor = frequencias.get(chaveCSV);
            if (valor != null) {
                entries.add(new BarEntry(labels.size(), valor));
                labels.add(categoriaCurta);
                cores.add(coresPorCategoria.get(categoriaCurta));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(cores);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(0xFFFFFFFF);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(0xFFFFFFFF);
        xAxis.setTextSize(14f);
        xAxis.setLabelRotationAngle(0);
        xAxis.setYOffset(10f); // espaço para rótulo

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(0xFFFFFFFF);
        yAxisLeft.setTextSize(12f);
        yAxisLeft.setDrawGridLines(true);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setExtraBottomOffset(30f); // espaço extra para "Tipo de resposta"
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void abrirComentarios() {
        ComentariosHolder.setComentarios(comentariosPorPergunta);
        startActivity(new Intent(this, ComentariosActivity.class));
    }

    private void enviarArquivoPorEmail() {
        File arquivo = Exportador.getArquivoCSV();
        if (arquivo == null || !arquivo.exists()) {
            Toast.makeText(this, "Arquivo de respostas não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", arquivo);

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
            recreate();
        } else {
            Toast.makeText(this, "Falha ao apagar os arquivos.", Toast.LENGTH_SHORT).show();
        }
    }
}

