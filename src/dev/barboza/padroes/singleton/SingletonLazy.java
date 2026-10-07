package dev.barboza.padroes.singleton;

/**
 * Singleton "preguiçoso": a instância só é criada no primeiro uso.
 * Sem sincronização, duas threads podem criar duas instâncias ao mesmo tempo (veja o teste).
 */
public final class SingletonLazy {

    private static SingletonLazy instancia;

    private SingletonLazy() {
    }

    public static SingletonLazy getInstancia() {
        if (instancia == null) {
            instancia = new SingletonLazy();
        }
        return instancia;
    }
}
