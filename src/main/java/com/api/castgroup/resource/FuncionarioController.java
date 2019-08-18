package com.api.castgroup.resource;

import com.api.castgroup.event.RecursoCriadoEvent;
import com.api.castgroup.exceptionhandler.CastgroupExceptionHandler;
import com.api.castgroup.model.Funcionario;
import com.api.castgroup.repository.FuncionarioRepository;
import com.api.castgroup.service.FeriasService;
import com.api.castgroup.service.FuncionarioService;
import com.api.castgroup.service.exception.EquipeInexistenteException;
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
@RequestMapping("/funcionario")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FeriasService feriasService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public Page<Funcionario> findAll(Pageable page) {
        return funcionarioRepository.findAll(page);
    }

    @GetMapping("/meses/{meses}")
    public Page<Funcionario> funcionariosDeveSolicitarFerias(Pageable pageable, @PathVariable("meses") int meses) {
        return feriasService.funcionariosDeveSolicitarFerias(pageable, meses);
    }

    @PostMapping
    public ResponseEntity<Funcionario> save(@Valid @RequestBody Funcionario funcionario, HttpServletResponse response){
        Funcionario funcionarioSalva = funcionarioService.save(funcionario);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, funcionarioSalva.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioSalva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Funcionario funcionario = funcionarioRepository.findById(id).orElse(null);
        return funcionario != null ? ResponseEntity.ok(funcionario) : ResponseEntity.notFound().build();
    }

    /**
     * ===== Tratamento de erros =====
     */

    @ExceptionHandler({EquipeInexistenteException.class})
    public ResponseEntity<Object> handleEquipeInexistenteException(EquipeInexistenteException ex) {
        String mensagemUsuario = messageSource.getMessage("equipe.inexistente",
                null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<CastgroupExceptionHandler.Erro> erros = Collections
                .singletonList(new CastgroupExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }


}
