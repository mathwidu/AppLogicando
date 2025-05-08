package br.com.app.applogicando;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;

public class Exportador {

    public static void exportar(Context context, Resposta resposta) {
        exportarCSV(context, resposta);
        exportarJSON(context, resposta);
    }

    private static void exportarCSV(Context context, Resposta r) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, "respostas.csv");
            boolean novoArquivo = !file.exists();

            FileWriter fw = new FileWriter(file, true);

            if (novoArquivo) {
                fw.write("local,horario,comentarioOrg,beneficios,trocas,comprometimento,planejamento,comentarioParticipacao,divulgacao,comentarioDivulgacao\n");
            }

            fw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    r.local, r.horario, r.comentarioOrg,
                    r.beneficios, r.trocas, r.comprometimento, r.planejamento, r.comentarioParticipacao,
                    r.divulgacao, r.comentarioDivulgacao));

            fw.close();

            Log.d("Exportador", "CSV exportado para: " + file.getAbsolutePath());
            Toast.makeText(context, "CSV salvo em Downloads!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Erro CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void exportarJSON(Context context, Resposta r) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, "respostas.json");

            JSONObject obj = new JSONObject();
            obj.put("local", r.local);
            obj.put("horario", r.horario);
            obj.put("comentarioOrg", r.comentarioOrg);
            obj.put("beneficios", r.beneficios);
            obj.put("trocas", r.trocas);
            obj.put("comprometimento", r.comprometimento);
            obj.put("planejamento", r.planejamento);
            obj.put("comentarioParticipacao", r.comentarioParticipacao);
            obj.put("divulgacao", r.divulgacao);
            obj.put("comentarioDivulgacao", r.comentarioDivulgacao);

            FileWriter fw = new FileWriter(file, true);
            fw.write(obj.toString() + "\n");
            fw.close();

            Log.d("Exportador", "JSON exportado para: " + file.getAbsolutePath());
            Toast.makeText(context, "JSON salvo em Downloads!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Erro JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
