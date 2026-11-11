package br.edu.ufjf.dcc168.jogo_aquario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Aquario {
    private final int linhas;
    private final int colunas;
    private final IPeixe[][] matriz;
    private final int reproducaoA;
    private final int mortalidadeA;
    private final int reproducaoB;
    private final int mortalidadeB;
    private final Random random;

    public Aquario(int m, int n, int ra, int ma, int rb, int mb, Random random) {
        validarPositivo("M", m);
        validarPositivo("N", n);
        validarPositivo("RA", ra);
        validarPositivo("MA", ma);
        validarPositivo("RB", rb);
        validarPositivo("MB", mb);
        this.linhas = m;
        this.colunas = n;
        this.matriz = new IPeixe[m][n];
        this.reproducaoA = ra;
        this.mortalidadeA = ma;
        this.reproducaoB = rb;
        this.mortalidadeB = mb;
        this.random = Objects.requireNonNull(random);
    }

    public Aquario(int m, int n, int x, int y, int ra, int ma, int rb, int mb, Random random) {
        this(m, n, ra, ma, rb, mb, random);
        validarPositivo("X", x);
        validarPositivo("Y", y);
        long capacidade = (long) m * n;
        if ((long) x + y > capacidade)
            throw new IllegalArgumentException("A quantidade de peixes (X + Y = " + ((long) x + y)
                    + ") excede a capacidade do aquário (M x N = " + capacidade + ").");
        distribuirPeixes(x, y);
    }

    private static void validarPositivo(String nome, int valor) {
        if (valor < 1)
            throw new IllegalArgumentException(nome + " deve ser um inteiro positivo (informado: " + valor + ").");
    }

    private void distribuirPeixes(int x, int y) {
        List<int[]> celulas = new ArrayList<>();
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++)
                celulas.add(new int[] { i, j });
        Collections.shuffle(celulas, random);
        for (int k = 0; k < x + y; k++) {
            int[] celula = celulas.get(k);
            matriz[celula[0]][celula[1]] = k < x ? new PeixeA() : new PeixeB();
        }
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public boolean coordenadaValida(int linha, int coluna) {
        return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
    }

    public IPeixe getPeixe(int linha, int coluna) {
        if (!coordenadaValida(linha, coluna))
            return null;
        return matriz[linha][coluna];
    }

    public boolean posicionarPeixe(int linha, int coluna, IPeixe peixe) {
        if (peixe == null || !coordenadaValida(linha, coluna) || matriz[linha][coluna] != null)
            return false;
        matriz[linha][coluna] = peixe;
        return true;
    }

    public IPeixe removerPeixe(int linha, int coluna) {
        if (!coordenadaValida(linha, coluna))
            return null;
        IPeixe peixe = matriz[linha][coluna];
        matriz[linha][coluna] = null;
        return peixe;
    }

    public int contarPeixes(Tipo tipo) {
        int total = 0;
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++)
                if (matriz[i][j] != null && matriz[i][j].getTipo() == tipo)
                    total++;
        return total;
    }

    public void iterar() {
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++) {
                IPeixe peixe = matriz[i][j];
                if (peixe != null && !peixe.agiuNaIteracao()) {
                    peixe.marcarAcao();
                    if (peixe.getTipo() == Tipo.A)
                        agirPeixeA((PeixeA) peixe, i, j);
                    else
                        agirPeixeB((PeixeB) peixe, i, j);
                }
            }
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++)
                if (matriz[i][j] != null)
                    matriz[i][j].liberarAcao();
    }

    private void agirPeixeA(PeixeA peixe, int i, int j) {
        List<int[]> livres = adjacentes(i, j, null);
        if (livres.isEmpty()) {
            peixe.registrarImobilidade();
            if (peixe.getIteracoesParado() >= mortalidadeA)
                matriz[i][j] = null;
        } else if (peixe.getMovimentosSeguidos() >= reproducaoA) {
            gerarFilho(new PeixeA(), livres);
            peixe.registrarReproducao();
        } else {
            mover(i, j, escolher(livres));
            peixe.registrarMovimento();
        }
    }

    private void agirPeixeB(PeixeB peixe, int i, int j) {
        List<int[]> livres = adjacentes(i, j, null);
        List<int[]> presas = adjacentes(i, j, Tipo.A);
        if (peixe.getPeixesComidos() >= reproducaoB && !livres.isEmpty() && adjacentes(i, j, Tipo.B).isEmpty()) {
            gerarFilho(new PeixeB(), livres);
            peixe.registrarReproducao();
        } else if (!presas.isEmpty()) {
            mover(i, j, escolher(presas));
            peixe.registrarRefeicao();
            return;
        } else if (!livres.isEmpty()) {
            int[] destino = escolher(livres);
            mover(i, j, destino);
            i = destino[0];
            j = destino[1];
            peixe.registrarJejum();
        } else {
            peixe.registrarJejum();
        }
        if (peixe.getIteracoesSemComer() >= mortalidadeB)
            matriz[i][j] = null;
    }

    private void gerarFilho(IPeixe filho, List<int[]> livres) {
        int[] celula = escolher(livres);
        filho.marcarAcao();
        matriz[celula[0]][celula[1]] = filho;
    }

    private void mover(int i, int j, int[] destino) {
        matriz[destino[0]][destino[1]] = matriz[i][j];
        matriz[i][j] = null;
    }

    private int[] escolher(List<int[]> opcoes) {
        return opcoes.get(random.nextInt(opcoes.size()));
    }

    private List<int[]> adjacentes(int linha, int coluna, Tipo tipo) {
        List<int[]> resultado = new ArrayList<>();
        for (int di = -1; di <= 1; di++)
            for (int dj = -1; dj <= 1; dj++) {
                int l = linha + di;
                int c = coluna + dj;
                if ((di != 0 || dj != 0) && coordenadaValida(l, c) && corresponde(matriz[l][c], tipo))
                    resultado.add(new int[] { l, c });
            }
        return resultado;
    }

    private static boolean corresponde(IPeixe peixe, Tipo tipo) {
        if (tipo == null)
            return peixe == null;
        return peixe != null && peixe.getTipo() == tipo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (j > 0)
                    sb.append(' ');
                sb.append(matriz[i][j] == null ? "." : matriz[i][j].getTipo().name());
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
