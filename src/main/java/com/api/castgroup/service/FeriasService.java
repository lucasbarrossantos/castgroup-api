package com.api.castgroup.service;

import com.api.castgroup.model.Ferias;
import com.api.castgroup.model.Funcionario;
import com.api.castgroup.repository.FeriasRepository;
import com.api.castgroup.repository.FuncionarioRepository;
import com.api.castgroup.service.exception.EquipeCom4PessoasException;
import com.api.castgroup.service.exception.FuncionarioComMenosDeUmAnoException;
import com.api.castgroup.service.exception.FuncionarioInexistenteException;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class FeriasService {

    @Autowired
    private FeriasRepository feriasRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EntityManager manager;

    @Transactional
    public Ferias save(Ferias ferias) {
        Funcionario funcionario = validarFuncionario(ferias.getFuncionario());
        int anosDeEmpresa = getAnosDeEmpresa(funcionario);
        validarRecemContratado(anosDeEmpresa);

        if (!validarSeEquipePossue4Pessoas(ferias, funcionario.getEquipe().getId())) {
            ferias = feriasRepository.save(ferias);
        }

        return ferias;
    }

    public Page<Funcionario> funcionariosDeveSolicitarFerias(Pageable pageable, int meses) {
        String sql = "select * from funcionario as f \n" +
                "  where f.id not in (select distinct fe.funcionario_id from ferias as fe)\n" +
                "  and ((cast(now() as date) - cast((f.data_de_contratacao + INTERVAL '" + meses + " month'" + ") as date)) / 365) >= 1 \n" +
                "  and ((cast(now() as date) - cast((f.data_de_contratacao + INTERVAL '" + meses + " month'" + ") as date)) / 365) < 2 ";

        List<Funcionario> funcionarios = manager.createNativeQuery(sql, Funcionario.class).getResultList();
        return new PageImpl<>(funcionarios, pageable, funcionarios.size());
    }

    private Funcionario validarFuncionario(Funcionario funcionario) {
        funcionario = funcionarioRepository.findById(funcionario.getId())
                .orElseThrow(FuncionarioInexistenteException::new);
        return funcionario;
    }

    private boolean validarSeEquipePossue4Pessoas(Ferias feriasParametro, Long equipeId) {
        List<Ferias> feriasGeral = funcionarioRepository.funcionariosQueTiraramFerias(); // Ferias de todos os funcionarios
        List<Funcionario> funcionarios = funcionarioRepository.findAllByEquipeId(equipeId); // Funcionarios por equipe

        if (feriasGeral.stream()
                .anyMatch(ferias ->
                        feriasParametro.getInicioFerias().isAfter(ferias.getInicioFerias())
                                &&
                                feriasParametro.getInicioFerias().isBefore(ferias.getFimFerias())
                                &&
                                funcionarios.size() >= 4
                )) {

            throw new EquipeCom4PessoasException();
        }

        return false;
    }

    private int getAnosDeEmpresa(Funcionario funcionario) {
        return Years
                .yearsBetween(DateTime.parse(funcionario.getDataDeContratacao().toString()), DateTime.now())
                .getYears();
    }

    private void validarRecemContratado(int anosDeEmpresa) {
        if (anosDeEmpresa < 1) {
            throw new FuncionarioComMenosDeUmAnoException();
        }
    }

}
