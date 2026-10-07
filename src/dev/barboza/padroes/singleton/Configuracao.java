package dev.barboza.padroes.singleton;

/**
 * Melhoria: um Singleton com uso real (configurações da aplicação) usando {@code enum}, a forma
 * recomendada em "Effective Java": segura entre threads, contra reflexão e contra serialização.
 */
public enum Configuracao {

    INSTANCIA;

    private final String ambiente = System.getProperty("ambiente", "desenvolvimento");
    private int acessos;

    public synchronized int registrarAcesso() {
        return ++acessos;
    }

    public String ambiente() {
        return ambiente;
    }
}
