package dev.barboza.padroes.strategy;

/** Estratégia de movimento do robô. É uma interface funcional: uma estratégia nova pode ser uma lambda. */
@FunctionalInterface
public interface Comportamento {

    String mover();

    Comportamento NORMAL = () -> "Movendo-se normalmente...";
    Comportamento DEFENSIVO = () -> "Movendo-se defensivamente...";
    Comportamento AGRESSIVO = () -> "Movendo-se agressivamente...";
}
