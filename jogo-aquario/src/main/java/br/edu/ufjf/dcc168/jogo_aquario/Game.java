package br.edu.ufjf.dcc168.jogo_aquario;

import java.io.PrintStream;
import java.util.Scanner;

public class Game {
    private final Aquario aquario;
    private final Scanner entrada;
    private final PrintStream saida;
    private int pontuacao;
    private boolean encerradoPeloJogador;

    public Game(Aquario aquario, Scanner entrada, PrintStream saida) {
        this.aquario = aquario;
        this.entrada = entrada;
        this.saida = saida;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public boolean isFimDeJogo() {
        return encerradoPeloJogador || aquario.contarPeixes(Tipo.B) == 0;
    }

    public void encerrar() {
        encerradoPeloJogador = true;
    }

    public void executarIteracao() {
        if (isFimDeJogo())
            throw new IllegalStateException("O jogo já terminou.");
        aquario.iterar();
        pontuacao++;
        saida.println("Iteração " + pontuacao + ":");
        saida.print(aquario);
        saida.println("Peixes A: " + aquario.contarPeixes(Tipo.A) + " | Peixes B: " + aquario.contarPeixes(Tipo.B));
    }

    public int jogar() {
        saida.println("Estado inicial:");
        saida.print(aquario);
        while (!isFimDeJogo()) {
            executarIteracao();
            if (!isFimDeJogo() && !desejaContinuar())
                encerrar();
        }
        if (encerradoPeloJogador)
            saida.println("Fim de jogo: encerrado pelo jogador.");
        else
            saida.println("Fim de jogo: não há mais peixes do tipo B.");
        saida.println("Pontuação: " + pontuacao);
        return pontuacao;
    }

    private boolean desejaContinuar() {
        saida.print("Continuar? (s/n): ");
        while (entrada.hasNextLine()) {
            String resposta = entrada.nextLine().trim();
            if (resposta.equalsIgnoreCase("s"))
                return true;
            if (resposta.equalsIgnoreCase("n"))
                return false;
            saida.print("Opção inválida. Digite s ou n: ");
        }
        return false;
    }
}
