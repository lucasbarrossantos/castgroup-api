package com.api.castgroup.repository;

import com.api.castgroup.model.Ferias;
import com.api.castgroup.model.Funcionario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findAllByEquipeId(Long id);

    /**
     * Solução usada para remover o problema do n + 1
     * Resolução http://www.springbrasil.com.br/post/show/61
     */
    @Query(value = "select f from Funcionario as f inner join fetch f.equipe",
            countQuery = "select count (f) from Funcionario as f inner join f.equipe")
    Page<Funcionario> findAll(Pageable pageable);

    @Query("select fe " +
            "from Funcionario as f,    \n" +
            "     Equipe as e,  \n" +
            "     Ferias as fe   \n" +
            " where f.equipe.id = e.id \n" +
            " and fe.funcionario.id = f.id")
    List<Ferias> funcionariosQueTiraramFerias();

}
