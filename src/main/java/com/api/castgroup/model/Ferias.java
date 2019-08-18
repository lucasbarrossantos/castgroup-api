package com.api.castgroup.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ferias")
public class Ferias {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Funcionario funcionario;

    // Período das ferias
    @Column(name = "inicio_ferias")
    private LocalDate inicioFerias;

    @Column(name = "fim_ferias")
    private LocalDate fimFerias;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public LocalDate getInicioFerias() {
        return inicioFerias;
    }

    public void setInicioFerias(LocalDate inicioFerias) {
        this.inicioFerias = inicioFerias;
    }

    public LocalDate getFimFerias() {
        return fimFerias;
    }

    public void setFimFerias(LocalDate fimFerias) {
        this.fimFerias = fimFerias;
    }
}
