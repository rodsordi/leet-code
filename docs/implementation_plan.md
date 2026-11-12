# Plano de Implementação: Higienização do Leaderboard, Cores ANSI e SLA de Memória no Benchmark

> **Decisão do Usuário**: Opção 1 - Higienização do Leaderboard, Cores ANSI & SLA de Memória
> **Status**: Aprovado para Execução

---

## 1. Visão Geral e Objetivos
O objetivo desta demanda é aprimorar a infraestrutura de medição e relatórios de benchmark (`br.com.leetcode.support.benchmark`), aumentando a clareza e diagnóstico para estudos de algoritmos.

> **Restrição Crítica**: É terminantemente proibido alterar qualquer classe de negócio ou teste em `br.com.leetcode.problemset.*` (preservação integral dos arquivos de estudo do usuário). Todas as modificações residirão estritamente no pacote `br.com.leetcode.support.benchmark.*`.

---

## 2. Detalhamento Arquitetural das Melhorias

1. **Higienização Inteligente do Leaderboard**:
   - No `TestBenchmarkExtension`, ignorar automaticamente etapas de aquecimento (`Warmup`) no `Leaderboard` final gerado no `@AfterAll`.
   - Adicionar atributo opcional na anotação `@BenchmarkTest(excludeWarmup = true)` (padrão: `true`).
   - As etapas de warmup continuam existindo e cumprindo seu papel de estabilizar o JIT HotSpot, mas não poluem a tabela consolidada de resultados.

2. **Cores ANSI Semânticas no Console (IntelliJ / Terminal Moderno)**:
   - Adicionar formatação ANSI leve e elegante:
     - Destaque nos primeiros colocados: 🥇 `#1` (Verde Brilhante / Bold), 🥈 `#2` (Ciano), 🥉 `#3` (Azul).
     - Status de SLA: `OK` (Verde), `FAIL` (Vermelho Negrito / Bright Red).
   - Flag de configuração `@BenchmarkTest(ansiColors = true)` com suporte a desativação se necessário.

3. **Suporte Completo a SLA de Memória**:
   - Expandir a anotação `@BenchmarkTest`:
     - `long maxMemoryBytes() default -1;`
     - `boolean failOnMemoryViolation() default false;`
   - No `TestBenchmarkExtension`:
     - Avaliar se `metrics.allocatedBytes() > config.maxMemoryBytes()` (quando `maxMemoryBytes > 0`).
     - Registrar violação de SLA de memória no `BenchmarkEntry` e exibir aviso `[!] MEMORY SLA VIOLATION` no banner e no `Leaderboard`.
     - Lançar `AssertionError` se `failOnMemoryViolation() == true`.

---

## 3. Roteiro de Implementação e Verificação

1. Atualizar [`BenchmarkTest.java`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/benchmark/BenchmarkTest.java):
   - Adicionar `maxMemoryBytes`, `failOnMemoryViolation`, `excludeWarmup` e `ansiColors`.
2. Atualizar [`BenchmarkEntry.java`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/benchmark/BenchmarkEntry.java):
   - Suporte a status de violação de tempo e memória.
3. Atualizar [`TestBenchmarkExtension.java`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/benchmark/TestBenchmarkExtension.java):
   - Implementar filtro de warmup, renderizador ANSI e avaliação de SLA de memória.
4. Atualizar [`TestBenchmarkExtensionTest.java`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/benchmark/TestBenchmarkExtensionTest.java):
   - Adicionar casos de teste para SLA de memória, filtro de warmup e renderização ANSI.
5. Executar `mvn clean test` para garantir que 100% dos testes continuem verdes.
6. Submeter ao `code-reviewer-sentinel-subagent` para fechamento do Quality Gate.
