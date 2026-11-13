# Plano de Implementação: Configuração de Massa Linear O(N) em P20ValidParenthesesTest

> **Decisão do Usuário**: Opção 1 - Comprovar a Complexidade Linear Espacial O(N) por Duplicação
> **Status**: Aprovado para Execução

---

## 1. Visão Geral e Objetivos
O objetivo desta demanda é configurar o método `measureMemoryAllocation` da classe [`P20ValidParenthesesTest`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/problemset/P20ValidParenthesesTest.java) para validar a complexidade de espaço auxiliar linear $O(N)$ inerente a estruturas de dados baseadas em Pilha (LIFO).

A ferramenta `@MemoryProfileTest` executará a sequência geométrica de duplicação ($10.000 \rightarrow 20.000 \rightarrow 40.000$), gerando uma massa de teste balanceada (`"()".repeat(size / 2)`) e comprovando matematicamente que a alocação de memória escala de forma estritamente proporcional $O(N)$ (razão $2.0\times$).

> **Restrição de Estudo**: A classe de implementação [`P20ValidParentheses.java`](file:///c:/git/fiap/phase4/leet-code/src/main/java/br/com/leetcode/problemset/P20ValidParentheses.java) é mantida integralmente intocada pelo assistente, respeitando o espaço de estudo e evolução autônoma do usuário.

---

## 2. Detalhes das Modificações em `P20ValidParenthesesTest.java`

- Atualizar o método `measureMemoryAllocation` para aceitar o parâmetro `(int size)` com anotação linear:
  ```java
  @MemoryProfileTest(expectedAllocation = MemoryAllocation.LINEAR, initialSize = 10_000, steps = 3)
  @DisplayName("Should prove stack memory allocation scales linearly O(N) with input size")
  void measureMemoryAllocation(int size) {
      var s = "()".repeat(size / 2);
      p20ValidParentheses.isValid(s);
  }
  ```

---

## 3. Verificação & Quality Gate

1. Executar `mvn test -Dtest=P20ValidParenthesesTest` para validar a razão de duplicação espacial e os testes funcionais existentes.
2. Executar `mvn clean test` para garantir 100% dos testes verdes no repositório.
3. Submeter ao subagente `code-reviewer-sentinel-subagent` para homologação do Quality Gate.
