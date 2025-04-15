package br.com.app.applogicando;

public class Resposta {
    public String local;
    public String horario;
    public String comentarioOrg;

    public String beneficios;
    public String trocas;
    public String comprometimento;
    public String planejamento;
    public String comentarioParticipacao;

    public String divulgacao;
    public String comentarioDivulgacao;

    public Resposta(String local, String horario, String comentarioOrg,
                    String beneficios, String trocas, String comprometimento, String planejamento, String comentarioParticipacao,
                    String divulgacao, String comentarioDivulgacao) {
        this.local = local;
        this.horario = horario;
        this.comentarioOrg = comentarioOrg;
        this.beneficios = beneficios;
        this.trocas = trocas;
        this.comprometimento = comprometimento;
        this.planejamento = planejamento;
        this.comentarioParticipacao = comentarioParticipacao;
        this.divulgacao = divulgacao;
        this.comentarioDivulgacao = comentarioDivulgacao;
    }
}
