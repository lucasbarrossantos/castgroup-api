package com.api.castgroup.service;

import com.api.castgroup.model.Equipe;
import com.api.castgroup.model.Funcionario;
import com.api.castgroup.repository.EquipeRepository;
import com.api.castgroup.repository.FuncionarioRepository;
import com.api.castgroup.service.exception.EquipeInexistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    public Funcionario save(Funcionario funcionario) {
        validarEquipe(funcionario.getEquipe());
        funcionario = funcionarioRepository.save(funcionario);
        return funcionario;
    }

    private void validarEquipe(Equipe equipe) {
        equipeRepository.findById(equipe.getId())
                .orElseThrow(EquipeInexistenteException::new);
    }

}
