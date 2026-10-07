package dev.barboza.padroes.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Contexto do Strategy: o robô não sabe como se move, só delega ao comportamento atual. */
public class Robo {

    private Comportamento comportamento = Comportamento.NORMAL;
    private final List<String> historico = new ArrayList<>();

    public void setComportamento(Comportamento comportamento) {
        this.comportamento = Objects.requireNonNull(comportamento, "O robô precisa de um comportamento.");
    }

    public String mover() {
        String acao = comportamento.mover();
        historico.add(acao);
        return acao;
    }

    /**
     * Melhoria: o robô escolhe a estratégia sozinho pela energia restante, sem nenhum
     * {@code if} dentro de {@link #mover()}.
     */
    public void ajustarPelaEnergia(int energia) {
        if (energia < 0 || energia > 100) {
            throw new IllegalArgumentException("Energia vai de 0 a 100.");
        }
        setComportamento(energia < 30 ? Comportamento.DEFENSIVO : energia > 80 ? Comportamento.AGRESSIVO : Comportamento.NORMAL);
    }

    public List<String> getHistorico() {
        return List.copyOf(historico);
    }
}
