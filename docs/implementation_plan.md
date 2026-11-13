# Plano de Implementação: Configuração da Massa de Teste em P14LongestCommonPrefixTest

> **Decisão do Usuário**: Opção 1 - Massa com Prefixo Real e Orçamento Constante O(1)
> **Status**: Aprovado para Execução

---

## 1. Visão Geral e Objetivos
O objetivo desta demanda é aplicar a massa de dados representativa com prefixo comum compartilhado no método `measureMemoryAllocation` da classe [`P14LongestCommonPrefixTest`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/problemset/P14LongestCommonPrefixTest.java), ajustando o contrato de memória para `MemoryAllocation.CONSTANT` para comportar a instanciação do objeto `String` resultante do retorno.

> **Restrição de Estudo**: A classe de implementação [`P14LongestCommonPrefix.java`](file:///c:/git/fiap/phase4/leet-code/src/main/java/br/com/leetcode/problemset/P14LongestCommonPrefix.java) é mantida integralmente intocada pelo assistente, respeitando o espaço de estudo e implementação do usuário.

---

## 2. Detalhes das Modificações em `P14LongestCommonPrefixTest.java`

- Atualizar o método `measureMemoryAllocation`:
  ```java
  @MemoryProfileTest(expectedAllocation = MemoryAllocation.CONSTANT, maxAllocatedBytes = 256)
  @DisplayName("Should prove longestCommonPrefix maintains O(1) bounded heap allocation")
  void measureMemoryAllocation() {
      var strs = new String[] {
          "interspecies",
          "interstellar",
          "interstate",
          "interactive",
          "intersection"
      };
      p14LongestCommonPrefix.longestCommonPrefix(strs);
  }
  ```

---

## 3. Verificação
- Executar `mvn test -Dtest=P14LongestCommonPrefixTest`.
- Validar se a medição de memória e os testes funcionais `example1` e `example2` passam.
- Submeter ao `code-reviewer-sentinel-subagent` para validação de conformidade.
