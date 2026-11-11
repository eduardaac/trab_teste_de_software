package br.edu.ufjf.dcc168.jogo_aquario;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestSetFunc {
    private static String erroDoMain(String... args) {
        Cenario.Saida out = new Cenario.Saida();
        assertEquals(-1, Main.executar(args, new ByteArrayInputStream(new byte[0]), out.stream));
        return out.texto();
    }

    @Test
    @DisplayName("CT01 - Inicialização com valores válidos mínimos (CE01, CE04, CE07, CE09, CE12, CE15, CE18, CE21, CE24)")
    public void ct01InicializacaoValoresMinimos() {
        Aquario aquario = assertDoesNotThrow(() -> new Aquario(1, 2, 1, 1, 1, 1, 1, 1, new Random(Cenario.SEMENTE)));
        assertEquals(1, aquario.contarPeixes(Tipo.A));
        assertEquals(1, aquario.contarPeixes(Tipo.B));

        Cenario.Saida out = new Cenario.Saida();
        int pontuacao = new Game(aquario, Cenario.entrada("n\n"), out.stream).jogar();

        assertTrue(out.texto().contains("Estado inicial:"));
        assertTrue(out.texto().contains("Iteração 1:"));
        assertEquals(1, pontuacao);
    }

    @Test
    @DisplayName("CT02 - Dimensão M inválida (CE02)")
    public void ct02DimensaoMInvalida() {
        String texto = erroDoMain("0", "5", "1", "1", "2", "2", "2", "2");
        assertTrue(texto.contains("Erro: M deve ser um inteiro positivo"));
    }

    @Test
    @DisplayName("CT03 - Peixes excedem a capacidade da matriz (CE08)")
    public void ct03PeixesExcedemCapacidade() {
        String texto = erroDoMain("2", "2", "3", "2", "2", "2", "2", "2");
        assertTrue(texto.contains("Erro: A quantidade de peixes (X + Y = 5) excede a capacidade do aquário (M x N = 4)"));
    }

    @Test
    @DisplayName("CT04 - Parâmetro RA inválido (CE16)")
    public void ct04ParametroRAInvalido() {
        String texto = erroDoMain("5", "5", "2", "2", "0", "2", "2", "2");
        assertTrue(texto.contains("Erro: RA deve ser um inteiro positivo"));
    }

    @Test
    @DisplayName("CT05 - Entrada não inteira (CE03)")
    public void ct05EntradaNaoInteira() {
        String texto = erroDoMain("3.5", "3", "1", "1", "2", "2", "2", "2");
        assertTrue(texto.contains("Erro: M deve ser um número inteiro"));
    }

    @Test
    @DisplayName("CT06 - Movimento do peixe A para célula livre (CE27)")
    public void ct06MovimentoPeixeA() {
        Aquario aquario = Cenario.montar(5, 5, 5, 5, "A.");
        IPeixe peixe = aquario.getPeixe(0, 0);

        aquario.iterar();

        assertNull(aquario.getPeixe(0, 0));
        assertSame(peixe, aquario.getPeixe(0, 1));
        assertEquals(1, ((PeixeA) peixe).getMovimentosSeguidos());
    }

    @Test
    @DisplayName("CT07 - Reprodução de A no limite RA (CE29, AVL mov_A = RA-1 e mov_A = RA)")
    public void ct07ReproducaoPeixeANoLimiteRA() {
        Aquario aquario = Cenario.montar(2, 5, 5, 5, "...");
        PeixeA pai = new PeixeA(1, 0);
        aquario.posicionarPeixe(0, 0, pai);

        aquario.iterar();
        assertSame(pai, aquario.getPeixe(0, 1));
        assertEquals(1, aquario.contarPeixes(Tipo.A));
        assertEquals(2, pai.getMovimentosSeguidos());

        aquario.iterar();
        assertSame(pai, aquario.getPeixe(0, 1));
        assertEquals(2, aquario.contarPeixes(Tipo.A));
        assertEquals(0, pai.getMovimentosSeguidos());
    }

    @Test
    @DisplayName("CT08 - RA atingido sem espaço: A não reproduz (CE31)")
    public void ct08ReproducaoPeixeASemEspaco() {
        Aquario aquario = Cenario.montar(2, 5, 5, 5, ".A", "AA");
        PeixeA cercado = new PeixeA(2, 0);
        aquario.posicionarPeixe(0, 0, cercado);

        aquario.iterar();

        assertSame(cercado, aquario.getPeixe(0, 0));
        assertEquals(4, aquario.contarPeixes(Tipo.A));
        assertEquals(0, cercado.getMovimentosSeguidos());
        assertEquals(1, cercado.getIteracoesParado());
    }

    @Test
    @DisplayName("CT09 - Fome de A no limite MA-1: permanece vivo (CE33, AVL parado_A = MA-1)")
    public void ct09PeixeASobreviveEmMAmenos1() {
        Aquario aquario = Cenario.montar(5, 3, 5, 5, "A");
        PeixeA peixe = (PeixeA) aquario.getPeixe(0, 0);

        Cenario.iterar(aquario, 2);

        assertSame(peixe, aquario.getPeixe(0, 0));
        assertEquals(2, peixe.getIteracoesParado());
    }

    @Test
    @DisplayName("CT10 - Morte por fome de A no limite MA (CE32, AVL parado_A = MA)")
    public void ct10PeixeAMorreEmMA() {
        Aquario aquario = Cenario.montar(5, 3, 5, 5, "A");

        Cenario.iterar(aquario, 3);

        assertNull(aquario.getPeixe(0, 0));
        assertEquals(0, aquario.contarPeixes(Tipo.A));
    }

    @Test
    @DisplayName("CT11 - B come peixe A adjacente (CE34)")
    public void ct11PeixeBComePeixeA() {
        Aquario aquario = Cenario.montar(5, 5, 5, 5, ".A");
        PeixeB predador = new PeixeB(0, 2);
        aquario.posicionarPeixe(0, 0, predador);

        aquario.iterar();

        assertNull(aquario.getPeixe(0, 0));
        assertSame(predador, aquario.getPeixe(0, 1));
        assertEquals(0, aquario.contarPeixes(Tipo.A));
        assertEquals(1, predador.getPeixesComidos());
        assertEquals(0, predador.getIteracoesSemComer());
    }

    @Test
    @DisplayName("CT12 - B move-se para célula livre sem A por perto (CE35)")
    public void ct12PeixeBMoveParaCelulaLivre() {
        Aquario aquario = Cenario.montar(5, 5, 5, 5, "B.");
        PeixeB peixe = (PeixeB) aquario.getPeixe(0, 0);

        aquario.iterar();

        assertNull(aquario.getPeixe(0, 0));
        assertSame(peixe, aquario.getPeixe(0, 1));
        assertEquals(1, peixe.getIteracoesSemComer());
    }

    @Test
    @DisplayName("CT13 - Reprodução de B com sucesso (CE37, AVL comidos_B = RB)")
    public void ct13ReproducaoPeixeB() {
        Aquario aquario = Cenario.montar(5, 5, 2, 5, "..");
        PeixeB pai = new PeixeB(2, 0);
        aquario.posicionarPeixe(0, 0, pai);

        aquario.iterar();

        assertSame(pai, aquario.getPeixe(0, 0));
        assertInstanceOf(PeixeB.class, aquario.getPeixe(0, 1));
        assertNotSame(pai, aquario.getPeixe(0, 1));
        assertEquals(0, pai.getPeixesComidos());
    }

    @Test
    @DisplayName("CT14 - Reprodução de B impedida por B adjacente (CE39)")
    public void ct14ReproducaoPeixeBImpedidaPorBAdjacente() {
        Aquario aquario = Cenario.montar(5, 5, 2, 5, ".B", "..");
        PeixeB peixe = new PeixeB(2, 0);
        aquario.posicionarPeixe(0, 0, peixe);

        aquario.iterar();

        assertEquals(2, aquario.contarPeixes(Tipo.B));
        assertEquals(2, peixe.getPeixesComidos());
    }

    @Test
    @DisplayName("CT15 - Fome de B no limite MB-1: permanece vivo (CE42, AVL jejum_B = MB-1)")
    public void ct15PeixeBSobreviveEmMBmenos1() {
        Aquario aquario = Cenario.montar(5, 5, 5, 3, "B");
        PeixeB peixe = (PeixeB) aquario.getPeixe(0, 0);

        Cenario.iterar(aquario, 2);

        assertSame(peixe, aquario.getPeixe(0, 0));
        assertEquals(2, peixe.getIteracoesSemComer());
    }

    @Test
    @DisplayName("CT16 - Morte por fome de B no limite MB (CE41, AVL jejum_B = MB)")
    public void ct16PeixeBMorreEmMB() {
        Aquario aquario = Cenario.montar(5, 5, 5, 3, "B");

        Cenario.iterar(aquario, 3);

        assertNull(aquario.getPeixe(0, 0));
        assertEquals(0, aquario.contarPeixes(Tipo.B));
    }

    @Test
    @DisplayName("CT17 - Término do jogo por extinção dos peixes B (CE43)")
    public void ct17FimPorExtincaoDosPeixesB() {
        Aquario aquario = Cenario.montar(5, 5, 5, 2, "B");
        Cenario.Saida out = new Cenario.Saida();

        int pontuacao = new Game(aquario, Cenario.entrada("s\n"), out.stream).jogar();

        assertEquals(2, pontuacao);
        assertTrue(out.texto().contains("Fim de jogo: não há mais peixes do tipo B."));
        assertTrue(out.texto().contains("Pontuação: 2"));
    }

    @Test
    @DisplayName("CT18 - Término do jogo por ação do jogador (CE44)")
    public void ct18FimPorAcaoDoJogador() {
        Aquario aquario = Cenario.montar(5, 5, 5, 10, "B");
        Cenario.Saida out = new Cenario.Saida();

        int pontuacao = new Game(aquario, Cenario.entrada("s\ns\nn\n"), out.stream).jogar();

        assertEquals(3, pontuacao);
        assertTrue(out.texto().contains("Fim de jogo: encerrado pelo jogador."));
        assertTrue(out.texto().contains("Pontuação: 3"));
    }

    @Test
    @DisplayName("CT19 - Continuidade do ecossistema (CE45)")
    public void ct19ContinuidadeDoEcossistema() {
        Aquario aquario = Cenario.montar(5, 5, 5, 10, "B");
        Cenario.Saida out = new Cenario.Saida();

        int pontuacao = new Game(aquario, Cenario.entrada("s\nn\n"), out.stream).jogar();

        assertTrue(out.texto().contains("Iteração 1:"));
        assertTrue(out.texto().contains("Iteração 2:"));
        assertEquals(2, pontuacao);
        assertEquals(1, aquario.contarPeixes(Tipo.B));
    }
}
