package br.edu.ufjf.dcc168.jogo_aquario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestSetEstr {
    private static Aquario vazio(int m, int n) {
        return new Aquario(m, n, 5, 5, 5, 5, new Random(Cenario.SEMENTE));
    }

    private static String erroDoMain(String[] args, String entrada) {
        Cenario.Saida out = new Cenario.Saida();
        assertEquals(-1, Main.executar(args, new ByteArrayInputStream(entrada.getBytes()), out.stream));
        return out.texto();
    }

    @Test
    @DisplayName("CTE01 - X + Y = M x N (aquário cheio) é aceito e todas as células são ocupadas")
    public void cte01CapacidadeExata() {
        Aquario aquario = new Aquario(2, 3, 4, 2, 5, 5, 5, 5, new Random(Cenario.SEMENTE));

        assertEquals(2, aquario.getLinhas());
        assertEquals(3, aquario.getColunas());
        assertEquals(4, aquario.contarPeixes(Tipo.A));
        assertEquals(2, aquario.contarPeixes(Tipo.B));
    }

    @Test
    @DisplayName("CTE02 - Coordenadas fora do aquário em cada um dos quatro limites")
    public void cte02CoordenadasInvalidas() {
        Aquario aquario = vazio(2, 3);

        assertFalse(aquario.coordenadaValida(-1, 0));
        assertFalse(aquario.coordenadaValida(2, 0));
        assertFalse(aquario.coordenadaValida(0, -1));
        assertFalse(aquario.coordenadaValida(0, 3));
        assertTrue(aquario.coordenadaValida(1, 2));
        assertNull(aquario.getPeixe(2, 0));
        assertNull(aquario.removerPeixe(0, 3));
    }

    @Test
    @DisplayName("CTE03 - posicionarPeixe rejeita peixe nulo, coordenada inválida e célula ocupada")
    public void cte03PosicionarPeixeInvalido() {
        Aquario aquario = vazio(1, 2);
        PeixeA peixe = new PeixeA();

        assertFalse(aquario.posicionarPeixe(0, 0, null));
        assertFalse(aquario.posicionarPeixe(0, 2, peixe));
        assertTrue(aquario.posicionarPeixe(0, 0, peixe));
        assertFalse(aquario.posicionarPeixe(0, 0, new PeixeB()));
        assertSame(peixe, aquario.getPeixe(0, 0));
    }

    @Test
    @DisplayName("CTE04 - removerPeixe devolve o peixe e libera a célula")
    public void cte04RemoverPeixe() {
        Aquario aquario = Cenario.montar(5, 5, 5, 5, "B.");
        IPeixe peixe = aquario.getPeixe(0, 0);

        assertSame(peixe, aquario.removerPeixe(0, 0));
        assertNull(aquario.getPeixe(0, 0));
        assertNull(aquario.removerPeixe(0, 1));
    }

    @Test
    @DisplayName("CTE05 - B move-se para célula livre e morre de fome na nova posição (MB = 1)")
    public void cte05PeixeBMoveEMorreNaNovaPosicao() {
        Aquario aquario = Cenario.montar(5, 5, 5, 1, "B.");

        aquario.iterar();

        assertEquals(0, aquario.contarPeixes(Tipo.B));
    }

    @Test
    @DisplayName("CTE06 - B reproduz-se e morre de fome na mesma iteração (MB = 1)")
    public void cte06PeixeBReproduzEMorre() {
        Aquario aquario = Cenario.montar(5, 5, 1, 1, "..");
        PeixeB pai = new PeixeB(1, 0);
        aquario.posicionarPeixe(0, 0, pai);

        aquario.iterar();

        assertNull(aquario.getPeixe(0, 0));
        assertEquals(1, aquario.contarPeixes(Tipo.B));
    }

    @Test
    @DisplayName("CTE07 - B com comidos_B >= RB mas sem célula livre come em vez de reproduzir")
    public void cte07PeixeBSemCelulaLivreNaoReproduz() {
        Aquario aquario = Cenario.montar(5, 5, 1, 5, ".A");
        PeixeB peixe = new PeixeB(1, 0);
        aquario.posicionarPeixe(0, 0, peixe);

        aquario.iterar();

        assertSame(peixe, aquario.getPeixe(0, 1));
        assertEquals(1, aquario.contarPeixes(Tipo.B));
        assertEquals(2, peixe.getPeixesComidos());
    }

    @Test
    @DisplayName("CTE08 - Executar iteração após o fim do jogo lança IllegalStateException")
    public void cte08IteracaoAposFimDeJogo() {
        Game game = new Game(Cenario.montar(5, 5, 5, 5, "B"), Cenario.entrada(""), new Cenario.Saida().stream);
        game.encerrar();

        assertTrue(game.isFimDeJogo());
        assertThrows(IllegalStateException.class, game::executarIteracao);
        assertEquals(0, game.getPontuacao());
    }

    @Test
    @DisplayName("CTE09 - Resposta inválida é recusada e a pergunta é repetida")
    public void cte09RespostaInvalida() {
        Cenario.Saida out = new Cenario.Saida();
        Game game = new Game(Cenario.montar(5, 5, 5, 10, "B"), Cenario.entrada("talvez\nS\nN\n"), out.stream);

        assertEquals(2, game.jogar());
        assertTrue(out.texto().contains("Opção inválida. Digite s ou n:"));
    }

    @Test
    @DisplayName("CTE10 - Fim da entrada (EOF) durante a pergunta encerra o jogo")
    public void cte10FimDaEntrada() {
        Cenario.Saida out = new Cenario.Saida();
        Game game = new Game(Cenario.montar(5, 5, 5, 10, "B"), Cenario.entrada(""), out.stream);

        assertEquals(1, game.jogar());
        assertTrue(out.texto().contains("Fim de jogo: encerrado pelo jogador."));
    }

    @Test
    @DisplayName("CTE11 - Quantidade errada de parâmetros é rejeitada")
    public void cte11QuantidadeErradaDeParametros() {
        String texto = erroDoMain(new String[] { "1", "2" }, "");

        assertTrue(texto.contains("Erro: São esperados 8 parâmetros"));
    }

    @Test
    @DisplayName("CTE12 - Leitura interativa incompleta é rejeitada")
    public void cte12LeituraIncompleta() {
        String texto = erroDoMain(new String[0], "3\n3\n");

        assertTrue(texto.contains("Erro: Entrada incompleta: faltou o parâmetro X."));
    }

    @Test
    @DisplayName("CTE13 - Programa com parâmetros digitados pelo usuário")
    public void cte13ProgramaComLeituraInterativa() {
        Cenario.Saida out = new Cenario.Saida();
        InputStream in = new ByteArrayInputStream("3\n3\n2\n1\n2\n3\n2\n3\nn\n".getBytes());

        int pontuacao = Main.executar(new String[0], in, out.stream);

        assertNotEquals(-1, pontuacao);
        assertTrue(out.texto().contains("M: N: X: Y: RA: MA: RB: MB: "));
        assertTrue(out.texto().contains("Pontuação: " + pontuacao));
    }

    @Test
    @DisplayName("CTE14 - main() com parâmetros na linha de comando")
    public void cte14MainComArgumentos() {
        InputStream inOriginal = System.in;
        PrintStream outOriginal = System.out;
        Cenario.Saida out = new Cenario.Saida();
        try {
            System.setIn(new ByteArrayInputStream("n\n".getBytes()));
            System.setOut(out.stream);
            Main.main(new String[] { "2", "2", "1", "1", "2", "3", "2", "3" });
        } finally {
            System.setIn(inOriginal);
            System.setOut(outOriginal);
        }
        assertTrue(out.texto().contains("Estado inicial:"));
        assertTrue(out.texto().contains("Pontuação: "));
    }
}
