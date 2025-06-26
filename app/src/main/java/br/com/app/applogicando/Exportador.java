package br.com.app.applogicando;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;

public class Exportador {

    private static final String NOME_CSV = "respostas.csv";
    private static final String NOME_JSON = "respostas.json";

    // Chamada principal de exportação
    public static void exportar(Context context, Resposta resposta) {
        exportarCSV(context, resposta);
        exportarJSON(context, resposta);
    }

    // Exportar como CSV
    private static void exportarCSV(Context context, Resposta r) {
        try {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dir == null) {
                Toast.makeText(context, "Erro: diretório de exportação indisponível.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, NOME_CSV);
            boolean novoArquivo = !file.exists();

            FileWriter fw = new FileWriter(file, true);

            if (novoArquivo) {
                fw.write("local,horario,comentarioOrg,beneficios,trocas,comprometimento,planejamento,comentarioParticipacao,divulgacao,comentarioDivulgacao\n");
            }

            fw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    formatarResposta(r.local),
                    formatarResposta(r.horario),
                    r.comentarioOrg,
                    formatarResposta(r.beneficios),
                    formatarResposta(r.trocas),
                    formatarResposta(r.comprometimento),
                    formatarResposta(r.planejamento),
                    r.comentarioParticipacao,
                    formatarResposta(r.divulgacao),
                    r.comentarioDivulgacao
            ));

            fw.close();

            Log.d("Exportador", "CSV exportado para: " + file.getAbsolutePath());
            Toast.makeText(context, "CSV salvo com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Erro CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Mapeia valores numéricos para descrições esperadas pelo gráfico
    private static String formatarResposta(String valor) {
        switch (valor) {
            case "1": return "1 - Muito Insatisfeito";
            case "2": return "2 - Insatisfeito";
            case "3": return "3 - Parcialmente Satisfeito";
            case "4": return "4 - Satisfeito";
            case "5": return "5 - Muito Satisfeito";
            case "SCO":
            case "sco":
            case "S": return "Sem condições de opinar";
            default: return valor;
        }
    }

    // Exportar como JSON
    private static void exportarJSON(Context context, Resposta r) {
        try {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (dir == null) {
                Toast.makeText(context, "Erro: diretório de exportação indisponível.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, NOME_JSON);

            JSONObject obj = new JSONObject();
            obj.put("local", formatarResposta(r.local));
            obj.put("horario", formatarResposta(r.horario));
            obj.put("comentarioOrg", r.comentarioOrg);
            obj.put("beneficios", formatarResposta(r.beneficios));
            obj.put("trocas", formatarResposta(r.trocas));
            obj.put("comprometimento", formatarResposta(r.comprometimento));
            obj.put("planejamento", formatarResposta(r.planejamento));
            obj.put("comentarioParticipacao", r.comentarioParticipacao);
            obj.put("divulgacao", formatarResposta(r.divulgacao));
            obj.put("comentarioDivulgacao", r.comentarioDivulgacao);

            FileWriter fw = new FileWriter(file, true);
            fw.write(obj.toString() + "\n");
            fw.close();

            Log.d("Exportador", "JSON exportado para: " + file.getAbsolutePath());
            Toast.makeText(context, "JSON salvo com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Erro JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Obter arquivo CSV
    public static File getArquivoCSV(Context context) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        return new File(dir, NOME_CSV);
    }

    // Obter arquivo JSON
    public static File getArquivoJSON(Context context) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        return new File(dir, NOME_JSON);
    }

    // Apagar arquivos
    public static boolean apagarArquivos(Context context) {
        File csv = getArquivoCSV(context);
        File json = getArquivoJSON(context);
        boolean deletadoCSV = !csv.exists() || csv.delete();
        boolean deletadoJSON = !json.exists() || json.delete();
        return deletadoCSV && deletadoJSON;
    }
}
