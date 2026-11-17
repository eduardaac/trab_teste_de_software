# Jogo do Aquário

Trabalho da disciplina **DCC168 — Teste de Software** (UFJF, 2026-3), com aplicação de Teste Funcional, Teste Estrutural e Teste Baseado em Defeitos sobre o Jogo do Aquário.

| Parte | Conteúdo | Situação |
|---|---|---|
| I | Teste Funcional: classes de equivalência, valor limite e grafo causa-efeito | Entregue |
| II | Implementação em Java, automação com JUnit e cobertura com EclEmma e Baduíno | Implementada |
| III | Teste de mutação com PITest | Pendente |

## O jogo

O aquário é uma matriz `M × N` com dois tipos de peixe:

- **Peixe A** come plâncton. Move-se para uma célula livre ao redor; depois de `RA` movimentos seguidos, reproduz-se se houver espaço; morre se ficar `MA` iterações seguidas sem conseguir se mover.
- **Peixe B** come peixes A. Se houver um peixe A ao redor, move-se até ele e o come; senão, move-se para uma célula livre. Depois de comer `RB` peixes A, reproduz-se se não houver outro B ao redor e houver espaço; morre se ficar `MB` iterações sem comer.

A cada iteração o programa mostra o aquário e pergunta se o jogador quer continuar. O jogo termina quando não restam peixes B ou quando o jogador encerra. A pontuação é o número de iterações executadas.

### Parâmetros

| Parâmetro | Descrição |
|---|---|
| `M`, `N` | Linhas e colunas do aquário |
| `X`, `Y` | Quantidade inicial de peixes A e B |
| `RA` | Movimentos seguidos para o peixe A se reproduzir |
| `MA` | Iterações seguidas sem se mover para o peixe A morrer |
| `RB` | Peixes A comidos para o peixe B se reproduzir |
| `MB` | Iterações sem comer para o peixe B morrer |

Todos devem ser inteiros positivos, e `X + Y ≤ M × N`. Entradas inválidas são rejeitadas com uma mensagem de erro.

### Regras adotadas na implementação

- "À sua volta" são as até 8 células vizinhas, incluindo as diagonais.
- Em cada iteração a matriz é percorrida linha a linha e cada peixe age uma única vez; filhotes só agem a partir da iteração seguinte.
- A reprodução acontece no lugar do movimento: o pai permanece na célula e o filho ocupa uma célula livre vizinha.
- As posições iniciais e as escolhas entre células possíveis são aleatórias. Nos testes usa-se uma semente fixa para que os resultados sejam reprodutíveis.

## Estrutura

```
jogo-aquario/
├── pom.xml
└── src/
    ├── main/java/br/edu/ufjf/dcc168/jogo_aquario/
    │   ├── Main.java          entrada pelo console
    │   ├── Parametros.java    leitura e conversão dos parâmetros
    │   ├── Game.java          laço do jogo, fim de jogo e pontuação
    │   ├── Aquario.java       matriz, validação e regras de cada iteração
    │   ├── IPeixe.java
    │   ├── PeixeA.java
    │   ├── PeixeB.java
    │   └── Tipo.java
    └── test/java/br/edu/ufjf/dcc168/jogo_aquario/
        ├── TestSetFunc.java   CT01–CT19, casos funcionais da Parte I
        ├── TestSetEstr.java   CTE01–CTE14, casos adicionados pela cobertura estrutural
        └── Cenario.java       montagem de cenários usada pelos testes
```


## Tecnologias

Java 8 · Maven · JUnit 5.10 · EclEmma (JaCoCo) · Baduíno · PITest (Parte III)

## Integrantes

- Bruno dos Santos Silva
- Darlan Henrique da Costa Silva
- Eduarda Araujo Carvalho

Docente: André Luiz de Oliveira
