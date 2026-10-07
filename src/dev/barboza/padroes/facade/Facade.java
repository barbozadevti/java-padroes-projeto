package dev.barboza.padroes.facade;

import dev.barboza.padroes.facade.cep.CepApi;
import dev.barboza.padroes.facade.crm.CrmService;

/**
 * Facade: uma única chamada ({@link #migrarCliente}) esconde a consulta de CEP e a gravação no CRM.
 * Quem usa a fachada não conhece nenhum dos dois subsistemas.
 */
public class Facade {

    private final CrmService crm;
    private final CepApi cep;

    public Facade() {
        this(new CrmService(), CepApi.getInstancia());
    }

    public Facade(CrmService crm, CepApi cep) {
        this.crm = crm;
        this.cep = cep;
    }

    public CrmService.Cliente migrarCliente(String nome, String cepDoCliente) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do cliente.");
        }
        CepApi.Endereco endereco = cep.consultar(cepDoCliente)
                .orElseThrow(() -> new IllegalArgumentException("CEP não encontrado: " + cepDoCliente));
        return crm.gravarCliente(nome.trim(), cepDoCliente.replaceAll("[^0-9]", ""), endereco.cidade(), endereco.estado());
    }

    public CrmService getCrm() {
        return crm;
    }
}
