package dev.barboza.padroes;

import dev.barboza.padroes.facade.Facade;
import dev.barboza.padroes.singleton.Configuracao;
import dev.barboza.padroes.singleton.SingletonEager;
import dev.barboza.padroes.singleton.SingletonLazy;
import dev.barboza.padroes.singleton.SingletonLazyHolder;
import dev.barboza.padroes.strategy.Comportamento;
import dev.barboza.padroes.strategy.Robo;

/** Demonstração dos três padrões do laboratório: Singleton, Strategy e Facade. */
public class Main {

    public static void main(String[] args) {
        System.out.println("== Singleton ==");
        System.out.println("Lazy: mesma instância? " + (SingletonLazy.getInstancia() == SingletonLazy.getInstancia()));
        System.out.println("Eager: mesma instância? " + (SingletonEager.getInstancia() == SingletonEager.getInstancia()));
        System.out.println("Lazy Holder: mesma instância? " + (SingletonLazyHolder.getInstancia() == SingletonLazyHolder.getInstancia()));
        Configuracao.INSTANCIA.registrarAcesso();
        System.out.println("Enum: ambiente " + Configuracao.INSTANCIA.ambiente() + ", acessos " + Configuracao.INSTANCIA.registrarAcesso());

        System.out.println();
        System.out.println("== Strategy ==");
        Robo robo = new Robo();
        System.out.println(robo.mover());
        robo.setComportamento(Comportamento.AGRESSIVO);
        System.out.println(robo.mover());
        robo.ajustarPelaEnergia(20);
        System.out.println(robo.mover());
        robo.setComportamento(() -> "Desviando de obstáculos...");   // estratégia nova, sem criar classe
        System.out.println(robo.mover());

        System.out.println();
        System.out.println("== Facade ==");
        Facade facade = new Facade();
        System.out.println(facade.migrarCliente("Rafael Barboza", "29055-460"));
        System.out.println(facade.migrarCliente("Ana Souza", "01310-100"));
    }
}
