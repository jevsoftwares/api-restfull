package com.jevsoftwares.apirestfull.apirestfull.controller;

import com.jevsoftwares.apirestfull.apirestfull.model.ArmazemModel;
import com.jevsoftwares.apirestfull.apirestfull.repository.ArmazemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ArmazemController {

    @Autowired
    private ArmazemRepository repository;

    @GetMapping(path = "api/armazem/{id}")
    public ResponseEntity consultar(@PathVariable("id") int id){

        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());

    }

    @PostMapping(path = "api/armazem/salvar")
    public ArmazemModel salvar(@RequestBody ArmazemModel armazemModel){

        return repository.save(armazemModel);
    }
}
