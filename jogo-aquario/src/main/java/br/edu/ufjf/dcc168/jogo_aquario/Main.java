package br.edu.ufjf.dcc168.jogo_aquario;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        executar(args, System.in, System.out);
    }

    static int executar(String[] args, InputStream in, PrintStream out) {
        Scanner entrada = new Scanner(in);
        try {
            int[] p = args.length == 0 ? Parametros.ler(entrada, out) : Parametros.converter(args);
            Aquario aquario = new Aquario(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], new Random());
            return new Game(aquario, entrada, out).jogar();
        } catch (IllegalArgumentException e) {
            out.println("Erro: " + e.getMessage());
            return -1;
        }
    }
}
