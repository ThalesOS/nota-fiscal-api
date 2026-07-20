package com.notafiscalapi.controller;

import com.notafiscalapi.entity.NotaFiscal;
import com.notafiscalapi.service.NotaFiscalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notafiscal")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    public NotaFiscalController(NotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaFiscal createNotaFiscal(@RequestBody NotaFiscal notaFiscal){
        return notaFiscalService.create(notaFiscal);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotaFiscal> findAll(){
        return notaFiscalService.getAll();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotaFiscal findById(@PathVariable Long id){
        return notaFiscalService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotaFiscal updateNotaFiscal(@PathVariable Long id, @RequestBody NotaFiscal notaFiscal){
        return notaFiscalService.update(id, notaFiscal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotaFiscal(@PathVariable Long id){
        notaFiscalService.delete(id);
    }

}
