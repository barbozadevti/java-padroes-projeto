package dev.barboza.padroes.facade.crm;

import java.util.ArrayList;
import java.util.List;

/** Subsistema 1: um CRM (simulado) que guarda clientes. */
public class CrmService {

    public record Cliente(String nome, String cep, String cidade, String estado) {
    }

    private final List<Cliente> clientes = new ArrayList<>();

    public Cliente gravarCliente(String nome, String cep, String cidade, String estado) {
        Cliente cliente = new Cliente(nome, cep, cidade, estado);
        clientes.add(cliente);
        return cliente;
    }

    public List<Cliente> clientes() {
        return List.copyOf(clientes);
    }
}
