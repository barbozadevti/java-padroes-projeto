package dev.barboza.padroes.singleton;

/** Singleton "apressado": a instância é criada quando a classe é carregada. Simples e seguro entre threads. */
public final class SingletonEager {

    private static final SingletonEager INSTANCIA = new SingletonEager();

    private SingletonEager() {
    }

    public static SingletonEager getInstancia() {
        return INSTANCIA;
    }
}
