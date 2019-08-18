package com.api.castgroup.resource;

import com.api.castgroup.event.RecursoCriadoEvent;
import com.api.castgroup.exceptionhandler.CastgroupExceptionHandler;
import com.api.castgroup.model.Ferias;
import com.api.castgroup.repository.FeriasRepository;
import com.api.castgroup.service.FeriasService;
import com.api.castgroup.service.exception.EquipeCom4PessoasException;
import com.api.castgroup.service.exception.FuncionarioComMenosDeUmAnoException;
import com.api.castgroup.service.exception.FuncionarioInexistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ferias")
public class FeriasController {

    @Autowired
    private FeriasRepository feriasRepository;

    @Autowired
    private FeriasService feriasService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public Page<Ferias> findAll(Pageable page) {
        return feriasRepository.findAll(page);
    }

    @GetMapping("/por-matricula/{matricula}")
    public Page<Ferias> feriasPorMatricula(Pageable page, @PathVariable("matricula") String matricula) {
        return feriasRepository.findAllByFuncionarioMatricula(page, matricula);
    }

    @PostMapping
    public ResponseEntity<Ferias> save(@Valid @RequestBody Ferias ferias, HttpServletResponse response) {
        Ferias feriasSalva = feriasService.save(ferias);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, feriasSalva.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(feriasSalva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Ferias ferias = feriasRepository.findById(id).orElse(null);
        return ferias != null ? ResponseEntity.ok(ferias) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        feriasRepository.deleteById(id);
    }

    /**
     * ===== Tratamento de erros =====
     */

    @ExceptionHandler({FuncionarioInexistenteException.class})
    public ResponseEntity<Object> handleFuncionarioInexistenteException(FuncionarioInexistenteException ex) {
        String mensagemUsuario = messageSource.getMessage("funcionario.inexistente",
                null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<CastgroupExceptionHandler.Erro> erros = Collections
                .singletonList(new CastgroupExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({EquipeCom4PessoasException.class})
    public ResponseEntity<Object> handleEquipeCom4PessoasException(EquipeCom4PessoasException ex) {
        String mensagemUsuario = messageSource.getMessage("equipe-com-4-pessoas",
                null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<CastgroupExceptionHandler.Erro> erros = Collections
                .singletonList(new CastgroupExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({FuncionarioComMenosDeUmAnoException.class})
    public ResponseEntity<Object> handleFuncionarioComMenosDeUmAnoException(FuncionarioComMenosDeUmAnoException ex) {
        String mensagemUsuario = messageSource.getMessage("funcionario.menos-de-um-ano",
                null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<CastgroupExceptionHandler.Erro> erros = Collections
                .singletonList(new CastgroupExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

}
