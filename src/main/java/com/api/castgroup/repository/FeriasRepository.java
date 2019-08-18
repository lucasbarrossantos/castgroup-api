package com.api.castgroup.repository;

import com.api.castgroup.model.Ferias;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeriasRepository extends JpaRepository<Ferias, Long> {

    /**
     * Solução usada para remover o problema do n + 1
     * Resolução http://www.springbrasil.com.br/post/show/61
     */
    @Query(value = "select f from Ferias as f inner join fetch f.funcionario as fu inner join fetch fu.equipe",
            countQuery = "select count (f) from Ferias as f inner join f.funcionario fu inner join fu.equipe")
    Page<Ferias> findAll(Pageable pageable);

    Page<Ferias> findAllByFuncionarioMatricula(Pageable pageable, String matricula);

}
