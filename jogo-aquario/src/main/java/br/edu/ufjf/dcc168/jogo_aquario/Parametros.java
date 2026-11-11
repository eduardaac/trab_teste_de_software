package br.edu.ufjf.dcc168.jogo_aquario;

import java.io.PrintStream;
import java.util.Scanner;

public final class Parametros {
    public static final String[] NOMES = { "M", "N", "X", "Y", "RA", "MA", "RB", "MB" };

    private Parametros() {
    }

    public static int[] converter(String[] valores) {
        if (valores.length != NOMES.length)
            throw new IllegalArgumentException(
                    "São esperados " + NOMES.length + " parâmetros: " + String.join(" ", NOMES) + ".");
        int[] resultado = new int[NOMES.length];
        for (int k = 0; k < NOMES.length; k++)
            resultado[k] = converterInteiro(NOMES[k], valores[k]);
        return resultado;
    }

    public static int converterInteiro(String nome, String valor) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nome + " deve ser um número inteiro (informado: " + valor + ").");
        }
    }

    public static int[] ler(Scanner entrada, PrintStream saida) {
        String[] valores = new String[NOMES.length];
        for (int k = 0; k < NOMES.length; k++) {
            saida.print(NOMES[k] + ": ");
            if (!entrada.hasNextLine())
                throw new IllegalArgumentException("Entrada incompleta: faltou o parâmetro " + NOMES[k] + ".");
            valores[k] = entrada.nextLine();
        }
        return converter(valores);
    }
}
