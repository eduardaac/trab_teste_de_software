package br.edu.ufjf.dcc168.jogo_aquario;

public class PeixeA extends IPeixe {
    private int movimentosSeguidos;
    private int iteracoesParado;

    public PeixeA() {
        this(0, 0);
    }

    public PeixeA(int movimentosSeguidos, int iteracoesParado) {
        this.movimentosSeguidos = movimentosSeguidos;
        this.iteracoesParado = iteracoesParado;
    }

    @Override
    public Tipo getTipo() {
        return Tipo.A;
    }

    public int getMovimentosSeguidos() {
        return movimentosSeguidos;
    }

    public int getIteracoesParado() {
        return iteracoesParado;
    }

    void registrarMovimento() {
        movimentosSeguidos++;
        iteracoesParado = 0;
    }

    void registrarImobilidade() {
        movimentosSeguidos = 0;
        iteracoesParado++;
    }

    void registrarReproducao() {
        movimentosSeguidos = 0;
    }
}
