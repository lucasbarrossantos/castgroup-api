package com.api.castgroup.resource;

import com.api.castgroup.event.RecursoCriadoEvent;
import com.api.castgroup.model.Equipe;
import com.api.castgroup.repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/equipe")
public class EquipeController {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public Page<Equipe> findAll(Pageable page) {
        return equipeRepository.findAll(page);
    }

    @PostMapping
    public ResponseEntity<Equipe> save(@Valid @RequestBody Equipe equipe, HttpServletResponse response){
        Equipe equipeSalva = equipeRepository.save(equipe);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, equipeSalva.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(equipeSalva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Equipe equipe = equipeRepository.findById(id).orElse(null);
        return equipe != null ? ResponseEntity.ok(equipe) : ResponseEntity.notFound().build();
    }

}
