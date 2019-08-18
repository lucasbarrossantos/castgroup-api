package com.api.castgroup.service;

import com.api.castgroup.model.Equipe;
import com.api.castgroup.repository.EquipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    public Equipe atualizar(Long codigo, Equipe equipe) {
        Equipe pessoaSalva = getEquipe(codigo);
        BeanUtils.copyProperties(equipe, pessoaSalva, "id");
        return equipeRepository.save(pessoaSalva);
    }

    private Equipe getEquipe(Long codigo) {
        Equipe pessoaSalva = equipeRepository.findById(codigo).orElse(null);
        if (pessoaSalva == null) {
            throw new EmptyResultDataAccessException(1);
        }
        return pessoaSalva;
    }

}
