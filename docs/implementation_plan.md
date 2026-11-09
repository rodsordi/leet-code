# Plano de Implementação: Calibração de Carga para Medição Assintótica em P1TwoSumTest

> **Decisão do Usuário**: Opção 1 - Carga Média Calibrada (initialSize = 20_000)
> **Status**: Aprovado para Execução

---

## 1. Visão Geral e Objetivos
O objetivo desta etapa é otimizar o teste assintótico `@ComplexityTest` no método `measureComplexity` de [`P1TwoSumTest`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/problemset/P1TwoSumTest.java), ajustando a carga inicial de $N = 50.000$ para um valor calibrado mais leve de $N = 20.000$.

Essa calibração reduz significativamente o overhead de memória e tempo de execução na suíte de testes contínua, mantendo cada degrau de medição confortavelmente acima do piso de ruído do sistema operacional (> 0.2 ms), garantindo a estabilidade da Hipótese de Duplicação ($R \approx 2.0\times$ para algoritmos lineares $O(N)$).

---

## 2. Detalhes das Modificações

1. **[`P1TwoSumTest.java`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/problemset/P1TwoSumTest.java)**:
   - Alterar a anotação para `@ComplexityTest(expected = ComplexityOrder.LINEAR, initialSize = 20_000)`.
   - Manter a lógica concisa e declarativa com injeção de parâmetro `int size`.
   - Garantir que os degraus gerados sejam $20.000 \rightarrow 40.000 \rightarrow 80.000$.

2. **[`ComplexityTest.java`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/complexity/ComplexityTest.java)**:
   - Atualizar o `default` de `initialSize` na anotação de `50_000` para `20_000`, estabelecendo uma convenção padrão ainda mais leve para novos problemas do LeetCode.

---

## 3. Verificação & Quality Gate

1. Executar `mvn clean test` para validar a compilação e a estabilidade da asserção de Big-O.
2. Confirmar que a classificação empírica permanece $O(N)$ (Linear) com expoente próximo de $1.0$.
3. Submeter ao `code-reviewer-sentinel-subagent` para fechamento do Quality Gate.
