package br.edu.ufjf.dcc168.jogo_aquario;

public class PeixeB extends IPeixe {
    private int peixesComidos;
    private int iteracoesSemComer;

    public PeixeB() {
        this(0, 0);
    }

    public PeixeB(int peixesComidos, int iteracoesSemComer) {
        this.peixesComidos = peixesComidos;
        this.iteracoesSemComer = iteracoesSemComer;
    }

    @Override
    public Tipo getTipo() {
        return Tipo.B;
    }

    public int getPeixesComidos() {
        return peixesComidos;
    }

    public int getIteracoesSemComer() {
        return iteracoesSemComer;
    }

    void registrarRefeicao() {
        peixesComidos++;
        iteracoesSemComer = 0;
    }

    void registrarJejum() {
        iteracoesSemComer++;
    }

    void registrarReproducao() {
        peixesComidos = 0;
        iteracoesSemComer++;
    }
}
