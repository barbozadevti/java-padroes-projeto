package dev.barboza.padroes.singleton;

/**
 * Singleton "Lazy Holder" (Bill Pugh): preguiçoso e seguro entre threads sem {@code synchronized}.
 * A classe interna só é carregada no primeiro {@link #getInstancia()}, e a JVM garante que o
 * carregamento de classe acontece uma única vez.
 */
public final class SingletonLazyHolder {

    private SingletonLazyHolder() {
    }

    private static final class Holder {
        private static final SingletonLazyHolder INSTANCIA = new SingletonLazyHolder();
    }

    public static SingletonLazyHolder getInstancia() {
        return Holder.INSTANCIA;
    }
}
