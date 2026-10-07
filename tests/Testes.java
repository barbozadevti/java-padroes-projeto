import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import dev.barboza.padroes.Main;
import dev.barboza.padroes.facade.Facade;
import dev.barboza.padroes.facade.cep.CepApi;
import dev.barboza.padroes.facade.crm.CrmService;
import dev.barboza.padroes.singleton.Configuracao;
import dev.barboza.padroes.singleton.SingletonEager;
import dev.barboza.padroes.singleton.SingletonLazy;
import dev.barboza.padroes.singleton.SingletonLazyHolder;
import dev.barboza.padroes.strategy.Comportamento;
import dev.barboza.padroes.strategy.Robo;

/** Testes sem dependências: cada caso imprime ok/FALHA, e o processo sai com erro se algum falhar. */
public class Testes {

    private static int total;
    private static int falhas;

    interface Caso {
        void executar() throws Exception;
    }

    static void caso(String nome, Caso caso) {
        total++;
        try {
            caso.executar();
            System.out.println("ok    " + nome);
        } catch (Throwable e) {
            falhas++;
            System.out.println("FALHA " + nome + "\n      " + e);
        }
    }

    static void verdade(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    static void lanca(Class<? extends Throwable> tipo, Caso caso) {
        try {
            caso.executar();
        } catch (Throwable e) {
            if (tipo.isInstance(e)) {
                return;
            }
            throw new AssertionError("esperava " + tipo.getSimpleName() + ", veio " + e);
        }
        throw new AssertionError("esperava " + tipo.getSimpleName());
    }

    /** Chama o fornecedor em 200 threads ao mesmo tempo e devolve quantas instâncias diferentes apareceram. */
    static int instanciasEmParalelo(Supplier<Object> fornecedor) throws InterruptedException {
        Set<Integer> vistas = ConcurrentHashMap.newKeySet();
        CountDownLatch largada = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newFixedThreadPool(32)) {
            for (int i = 0; i < 200; i++) {
                pool.submit(() -> {
                    largada.await();
                    vistas.add(System.identityHashCode(fornecedor.get()));
                    return null;
                });
            }
            largada.countDown();
        }
        return vistas.size();
    }

    static String capturar(Runnable acao) {
        PrintStream original = System.out;
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
        try {
            acao.run();
        } finally {
            System.setOut(original);
        }
        return saida.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        // ---------- Singleton ----------
        caso("Singleton Lazy devolve sempre a mesma instância (uso em uma thread)",
                () -> verdade(SingletonLazy.getInstancia() == SingletonLazy.getInstancia(), "instâncias diferentes"));
        caso("Singleton Eager devolve sempre a mesma instância",
                () -> verdade(SingletonEager.getInstancia() == SingletonEager.getInstancia(), "instâncias diferentes"));
        caso("Lazy Holder é único mesmo com 200 threads ao mesmo tempo",
                () -> verdade(instanciasEmParalelo(SingletonLazyHolder::getInstancia) == 1, "mais de uma instância"));
        caso("Enum é único mesmo com 200 threads ao mesmo tempo",
                () -> verdade(instanciasEmParalelo(() -> Configuracao.INSTANCIA) == 1, "mais de uma instância"));
        caso("Construtores privados: ninguém cria outra instância por fora", () -> {
            for (Class<?> c : List.of(SingletonLazy.class, SingletonEager.class, SingletonLazyHolder.class)) {
                verdade(c.getDeclaredConstructors().length == 1
                        && java.lang.reflect.Modifier.isPrivate(c.getDeclaredConstructors()[0].getModifiers()), c.getSimpleName());
            }
        });
        caso("Enum conta os acessos sem perder nenhum em paralelo", () -> {
            int antes = Configuracao.INSTANCIA.registrarAcesso();
            try (ExecutorService pool = Executors.newFixedThreadPool(16)) {
                for (int i = 0; i < 1000; i++) {
                    pool.submit(Configuracao.INSTANCIA::registrarAcesso);
                }
            }
            verdade(Configuracao.INSTANCIA.registrarAcesso() == antes + 1001, "perdeu acessos");
        });

        // ---------- Strategy ----------
        caso("Robô começa normal e troca de comportamento", () -> {
            Robo r = new Robo();
            verdade(r.mover().equals("Movendo-se normalmente..."), "normal");
            r.setComportamento(Comportamento.DEFENSIVO);
            verdade(r.mover().equals("Movendo-se defensivamente..."), "defensivo");
            r.setComportamento(Comportamento.AGRESSIVO);
            verdade(r.mover().equals("Movendo-se agressivamente..."), "agressivo");
            verdade(r.getHistorico().size() == 3, "histórico");
        });
        caso("Estratégia nova como lambda, sem criar classe", () -> {
            Robo r = new Robo();
            r.setComportamento(() -> "Dançando...");
            verdade(r.mover().equals("Dançando..."), "lambda");
        });
        caso("Robô escolhe a estratégia pela energia", () -> {
            Robo r = new Robo();
            r.ajustarPelaEnergia(10);
            verdade(r.mover().contains("defensivamente"), "pouca energia");
            r.ajustarPelaEnergia(50);
            verdade(r.mover().contains("normalmente"), "energia média");
            r.ajustarPelaEnergia(95);
            verdade(r.mover().contains("agressivamente"), "muita energia");
            lanca(IllegalArgumentException.class, () -> r.ajustarPelaEnergia(101));
            lanca(NullPointerException.class, () -> r.setComportamento(null));
        });

        // ---------- Facade ----------
        caso("Facade consulta o CEP e grava no CRM numa chamada só", () -> {
            Facade f = new Facade();
            CrmService.Cliente c = f.migrarCliente(" Rafael ", "29055-460");
            verdade(c.equals(new CrmService.Cliente("Rafael", "29055460", "Vitória", "ES")), c.toString());
            verdade(f.getCrm().clientes().size() == 1, "não gravou");
        });
        caso("Facade recusa CEP desconhecido e nome vazio sem gravar nada", () -> {
            Facade f = new Facade(new CrmService(), CepApi.getInstancia());
            lanca(IllegalArgumentException.class, () -> f.migrarCliente("Ana", "99999-999"));
            lanca(IllegalArgumentException.class, () -> f.migrarCliente(" ", "29055-460"));
            verdade(f.getCrm().clientes().isEmpty(), "gravou cliente inválido");
        });

        caso("Main roda do começo ao fim", () -> {
            String saida = capturar(() -> Main.main(new String[0]));
            verdade(saida.contains("Lazy Holder: mesma instância? true"), "singleton");
            verdade(saida.contains("Desviando de obstáculos..."), "strategy");
            verdade(saida.contains("Cliente[nome=Ana Souza, cep=01310100, cidade=São Paulo, estado=SP]"), "facade");
        });

        System.out.println();
        System.out.println((total - falhas) + " de " + total + " testes passaram.");
        if (falhas > 0) {
            System.exit(1);
        }
    }
}
