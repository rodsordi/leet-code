# Plano de Implementação: Infraestrutura de Perfil e Asserção de Memória (@MemoryProfileTest)

> **Decisão do Usuário**: Opção 1 - Anotação Declarativa @MemoryProfileTest (Zero-Allocation & Complexidade Espacial)
> **Status**: Aprovado para Execução

---

## 1. Visão Geral e Objetivos
O objetivo desta demanda é fornecer uma infraestrutura nativa e declarativa no JUnit 5 para metrificação, perfil e asserção de consumo de memória Heap na JVM (`br.com.leetcode.support.memory`).

A ferramenta atenderá diretamente a problemas como o **Problema 9 (*Palindrome Number*)**, onde o desafio de engenharia é operar com **Zero Alocação no Heap** ($O(1)$ de Espaço Auxiliar com variáveis locais em registradores/pilha), contrastando com implementações ingênuas baseadas em Strings.

---

## 2. Componentes Arquiteturais (`br.com.leetcode.support.memory`)

1. **`MemoryAllocation.java` (Enum)**:
   - `ZERO`: Exatamente 0 bytes alocados no Heap da JVM.
   - `CONSTANT`: Alocação fixa restrita a um limite configurável em bytes ($O(1)$ espaço auxiliar).
   - `LINEAR`: Alocação que cresce proporcionalmente ao tamanho de entrada $N$ ($O(N)$ espaço).

2. **`MemoryAssessment.java` (Record)**:
   - Encapsula medições em bytes alocados, formatação legível (Bytes, KB, MB) e geração do relatório ASCII tabular `EMPIRICAL MEMORY ASSESSMENT REPORT`.

3. **`@MemoryProfileTest` (Anotação Meta-Anotada com `@TestTemplate`)**:
   - `MemoryAllocation expectedAllocation() default MemoryAllocation.ZERO;`
   - `long maxAllocatedBytes() default 0L;`
   - `int initialSize() default 20_000;`
   - `int steps() default 1;`
   - `boolean warmup() default true;`
   - `int warmupIterations() default 15;`

4. **`MemoryProfileTestExtension.java` (Extensão SPI do JUnit 5)**:
   - Implementa `TestTemplateInvocationContextProvider`.
   - Suporta métodos sem parâmetros `()` e com parâmetro `(int size)`:
     - Métodos `()`: Avaliação direta de orçamento de Heap / Zero-Allocation.
     - Métodos `(int size)`: Avaliação assintótica espacial com sequência de duplicação ($N, 2N, 4N$).
   - Estabilização JIT via 15 iterações de aquecimento prévio para compilação C2 (HotSpot).
   - Medição determinística via `com.sun.management.ThreadMXBean.getThreadAllocatedBytes(...)`.
   - Emissão de relatório tabular no console e asserção contra o orçamento esperado.

---

## 3. Correção de Sanidade no `P9PalindromeNumber.java`
- Avaliação de caso de borda no algoritmo:
  - `x = 0`: O número `0` é um palíndromo válido (LeetCode aceita `0` como `true`).
  - Atualizar condição inicial: números negativos (`x < 0`) e múltiplos de 10 diferentes de zero (`x % 10 == 0 && x != 0`) retornam `false`. O número `0` retorna `true`.

---

## 4. Roteiro de Implementação e Verificação

1. Criar o pacote `src/test/java/br/com/leetcode/support/memory/` contendo:
   - `MemoryAllocation.java`
   - `MemoryAssessment.java`
   - `MemoryProfileTest.java`
   - `MemoryProfileTestExtension.java`
2. Criar testes unitários da extensão:
   - `src/test/java/br/com/leetcode/support/memory/MemoryProfileTestExtensionTest.java`
3. Ajustar `P9PalindromeNumber.java` (caso `x = 0`) e `P9PalindromeNumberTest.java` (usando `@MemoryProfileTest`).
4. Executar `mvn clean test`.
5. Acionar o subagente `code-reviewer-sentinel-subagent` para homologação do Quality Gate.
