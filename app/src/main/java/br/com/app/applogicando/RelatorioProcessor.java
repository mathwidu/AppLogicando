package br.com.app.applogicando;

import android.content.Context;
import android.os.Environment;
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
            File arquivo = Exportador.getArquivoCSV();
            if (arquivo == null || !arquivo.exists()) return resultados;

            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String linha;

            // Lê cabeçalho
            String[] cabecalhos = br.readLine().split(",");

            // Inicializa um mapa para cada pergunta
            for (int i = 0; i < cabecalhos.length; i++) {
                resultados.add(new HashMap<>());
            }

            // Lê cada linha
            while ((linha = br.readLine()) != null) {
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

    // Novo método: coleta todas as respostas textuais por pergunta
    public static List<Map<String, List<String>>> coletarComentarios(Context context) {
        List<Map<String, List<String>>> comentarios = new ArrayList<>();

        try {
            File arquivo = Exportador.getArquivoCSV();
            if (arquivo == null || !arquivo.exists()) return comentarios;

            BufferedReader br = new BufferedReader(new FileReader(arquivo));
            String linha;

            // Lê cabeçalho
            String[] cabecalhos = br.readLine().split(",");

            // Inicializa um mapa com uma lista de respostas para cada pergunta
            for (int i = 0; i < cabecalhos.length; i++) {
                Map<String, List<String>> mapa = new HashMap<>();
                mapa.put("respostas", new ArrayList<>());
                comentarios.add(mapa);
            }

            // Lê respostas
            while ((linha = br.readLine()) != null) {
                String[] respostas = linha.split("\",\"");
                for (int i = 0; i < respostas.length && i < comentarios.size(); i++) {
                    String resp = respostas[i].replace("\"", "").trim();
                    if (!resp.isEmpty()) {
                        comentarios.get(i).get("respostas").add(resp);
                    }
                }
            }

            br.close();
        } catch (Exception e) {
            Log.e("RelatorioProcessor", "Erro ao coletar comentários: " + e.getMessage());
        }

        return comentarios;
    }
}
