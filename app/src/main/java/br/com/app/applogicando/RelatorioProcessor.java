package br.com.app.applogicando;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioProcessor {

    // Cada mapa representa uma pergunta: mapa[resposta] = quantidade
    public static List<Map<String, Integer>> contarFrequencias(Context context) {
        List<Map<String, Integer>> resultados = new ArrayList<>();

        try {
            File arquivo = Exportador.getArquivoCSV(context);
            if (arquivo == null || !arquivo.exists()) return resultados;

            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String cabecalhoLinha = br.readLine();

            if (cabecalhoLinha == null || cabecalhoLinha.trim().isEmpty()) {
                br.close();
                return resultados;
            }

            String[] cabecalhos = cabecalhoLinha.split(",");

            // Inicializa um mapa para cada pergunta
            for (int i = 0; i < cabecalhos.length; i++) {
                resultados.add(new HashMap<>());
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.startsWith("\"") && linha.endsWith("\"")) {
                    linha = linha.substring(1, linha.length() - 1);
                }

                String[] respostas = linha.split("\",\"");

                for (int i = 0; i < respostas.length && i < resultados.size(); i++) {
                    String resp = respostas[i].replace("\"", "").trim();
                    Map<String, Integer> mapaPergunta = resultados.get(i);
                    int atual = mapaPergunta.containsKey(resp) ? mapaPergunta.get(resp) : 0;
                    mapaPergunta.put(resp, atual + 1);
                }
            }

            br.close();
        } catch (Exception e) {
            Log.e("RelatorioProcessor", "Erro ao processar CSV: " + e.getMessage());
        }

        return resultados;
    }

    // Coleta os comentários de cada pergunta
    public static List<Map<String, List<String>>> coletarComentarios(Context context) {
        List<Map<String, List<String>>> comentarios = new ArrayList<>();

        try {
            File arquivo = Exportador.getArquivoCSV(context);
            if (arquivo == null || !arquivo.exists()) return comentarios;

            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String cabecalhoLinha = br.readLine();

            if (cabecalhoLinha == null || cabecalhoLinha.trim().isEmpty()) {
                br.close();
                return comentarios;
            }

            String[] cabecalhos = cabecalhoLinha.split(",");
            for (int i = 0; i < cabecalhos.length; i++) {
                Map<String, List<String>> mapa = new HashMap<>();
                mapa.put("respostas", new ArrayList<>());
                comentarios.add(mapa);
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.startsWith("\"") && linha.endsWith("\"")) {
                    linha = linha.substring(1, linha.length() - 1);
                }

                String[] respostas = linha.split("\",\"");

                for (int i = 0; i < respostas.length && i < comentarios.size(); i++) {
                    String resp = respostas[i].replace("\"", "").trim();
                    comentarios.get(i).get("respostas").add(resp);
                }
            }

            br.close();
        } catch (Exception e) {
            Log.e("RelatorioProcessor", "Erro ao coletar comentários: " + e.getMessage());
        }

        return comentarios;
    }
}
