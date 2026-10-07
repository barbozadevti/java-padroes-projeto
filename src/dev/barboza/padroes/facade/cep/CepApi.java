package dev.barboza.padroes.facade.cep;

import java.util.Map;
import java.util.Optional;

/** Subsistema 2: consulta de CEP (simulada, sem internet). */
public final class CepApi {

    public record Endereco(String cidade, String estado) {
    }

    private static final Map<String, Endereco> BASE = Map.of(
            "29055460", new Endereco("Vitória", "ES"),
            "01310100", new Endereco("São Paulo", "SP"),
            "20040002", new Endereco("Rio de Janeiro", "RJ"),
            "70040010", new Endereco("Brasília", "DF"));

    private static final CepApi INSTANCIA = new CepApi();

    private CepApi() {
    }

    public static CepApi getInstancia() {
        return INSTANCIA;
    }

    public Optional<Endereco> consultar(String cep) {
        return Optional.ofNullable(BASE.get(cep == null ? "" : cep.replaceAll("[^0-9]", "")));
    }
}
