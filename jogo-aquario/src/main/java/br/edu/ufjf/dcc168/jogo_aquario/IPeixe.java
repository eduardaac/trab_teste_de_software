package br.edu.ufjf.dcc168.jogo_aquario;

public abstract class IPeixe {
    private boolean agiuNaIteracao;

    public abstract Tipo getTipo();

    public boolean agiuNaIteracao() {
        return agiuNaIteracao;
    }

    void marcarAcao() {
        agiuNaIteracao = true;
    }

    void liberarAcao() {
        agiuNaIteracao = false;
    }
}
