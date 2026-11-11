package br.edu.ufjf.dcc168.jogo_aquario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

final class Cenario {
    static final long SEMENTE = 42L;

    private Cenario() {
    }

    static Aquario montar(int ra, int ma, int rb, int mb, String... linhas) {
        Aquario aquario = new Aquario(linhas.length, linhas[0].length(), ra, ma, rb, mb, new Random(SEMENTE));
        for (int i = 0; i < linhas.length; i++)
            for (int j = 0; j < linhas[i].length(); j++) {
                char c = linhas[i].charAt(j);
                if (c == 'A')
                    aquario.posicionarPeixe(i, j, new PeixeA());
                else if (c == 'B')
                    aquario.posicionarPeixe(i, j, new PeixeB());
            }
        return aquario;
    }

    static void iterar(Aquario aquario, int vezes) {
        for (int k = 0; k < vezes; k++)
            aquario.iterar();
    }

    static Scanner entrada(String texto) {
        return new Scanner(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)), "UTF-8");
    }

    static final class Saida {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(buffer, true);

        String texto() {
            return buffer.toString();
        }
    }
}
