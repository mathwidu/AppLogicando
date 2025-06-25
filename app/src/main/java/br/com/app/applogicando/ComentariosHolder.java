package br.com.app.applogicando;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComentariosHolder {
    private static final Map<String, List<String>> comentarios = new HashMap<>();

    public static void setComentarios(Map<String, List<String>> dados) {
        comentarios.clear();
        comentarios.putAll(dados);
    }

    public static Map<String, List<String>> getComentarios() {
        return comentarios;
    }
}
