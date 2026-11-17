# Plano de Implementação: Manutenção da Separação entre Anotações de Tempo e Espaço

> **Decisão do Usuário**: Opção 1 - Manter as Anotações Especializadas e Independentes (Composição)
> **Status**: Aprovado para Execução

---

## 1. Visão Geral e Diretriz Arquitetural

Com base na deliberação do usuário, a arquitetura de testes mantém a segregação estrita entre:
- **Complexidade de Tempo (Temporal)**: Validada exclusivamente via anotação [`@ComplexityTest`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/complexity/ComplexityTest.java) e [`ComplexityTestExtension`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/complexity/ComplexityTestExtension.java).
- **Complexidade de Espaço (Memória em Heap)**: Validada exclusivamente via anotação [`@MemoryProfileTest`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/memory/MemoryProfileTest.java) e [`MemoryProfileTestExtension`](file:///c:/git/fiap/phase4/leet-code/src/test/java/br/com/leetcode/support/memory/MemoryProfileTestExtension.java).

> 📚 **Referência Canônica**:
> - *Single Responsibility Principle (SRP)* & *Interface Segregation Principle (ISP)* — Robert C. Martin (*Clean Architecture*).
> - Padrão de design do *JMH (Java Microbenchmark Harness - OpenJDK)*, que mantém telemetrias de CPU e alocação de heap (`GCProfiler`) rigorosamente ortogonais para evitar o Efeito Heisenberg (interferência mútua de medição).

---

## 2. Princípios e Justificativas Técnicas

1. **Ortogonalidade das Dimensões Físicas**:
   - Um algoritmo pode ter complexidade $O(N^2)$ de tempo e $O(1)$ de memória auxiliar (ou vice-versa).
   - Manter as anotações independentes permite testar cada dimensão sem falsos positivos.

2. **Prevenção de Ruído Cruzado (Heisenberg Effect)**:
   - A telemetria de memória (`ThreadMXBean.getThreadAllocatedBytes()`) introduz chamadas nativas à JVM que poluiriam as amostras nanométricas de tempo de CPU caso executadas no mesmo laço de medição.
   - O tempo de CPU requer centenas de iterações de aquecimento do compilador C2 JIT, enquanto a alocação de heap é linear e cumulativa.

3. **Clareza de Falha & Diagnóstico**:
   - Falhas de performance temporal acusam desvios assintóticos na curva de tempo.
   - Falhas de memória acusam violação de SLA de bytes ou vazamentos em heap.

---

## 3. Estado da Base de Código

A base de código já se encontra 100% aderente a este modelo arquitetural:
- Ambas as anotações estão modularizadas em pacotes dedicados (`support.complexity` e `support.memory`).
- Todos os testes de suporte e de problemas (`P13RomanToIntegerTest`, `P14LongestCommonPrefixTest`, `P20ValidParenthesesTest`) utilizam essas ferramentas de forma isolada e previsível.
- A suíte completa passa com 100% de sucesso (`mvn clean test`).
