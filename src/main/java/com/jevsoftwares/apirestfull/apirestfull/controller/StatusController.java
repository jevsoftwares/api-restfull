package com.jevsoftwares.apirestfull.apirestfull.controller;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import com.jevsoftwares.apirestfull.apirestfull.model.ArmazemModel;
import com.jevsoftwares.apirestfull.apirestfull.repository.ArmazemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@RestController
public class StatusController {

    @Autowired
    private ArmazemRepository repository;

    @GetMapping(path = "api/status")
    public String check(){
        return "online";
    }

    /****************** Armazem ******************/

    /********CONSULTAS ***********/

    @GetMapping(path = "api/armazem/id/{id}")
    public ResponseEntity consultar(@PathVariable("id") int id){

        return repository.findById(id)
                        .map(record -> ResponseEntity.ok().body(record))
                        .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/api/armazem/{expressao}")
    public ResponseEntity consultar(@PathVariable("expressao") String expressao ){
        int id, auxiliarI = 0,auxiliarF = 0;
        String alcoolico,secao;
        float saldo;
        int corteSubstring;

        geraVariaveis(expressao);

        auxiliarF = expressao.indexOf("=");
        auxiliarF   = expressao.substring(auxiliarI).indexOf(",")+1;
        id          = expressao.substring(0,auxiliarF).equalsIgnoreCase("id") ?
                Integer.parseInt(expressao.substring(expressao.indexOf("=")+1,expressao.indexOf(","))) : 0;

        auxiliarI    = expressao.substring(auxiliarF).indexOf("=");
        auxiliarF   = expressao.substring(auxiliarI).indexOf(",")+2;
        alcoolico   = expressao.substring(auxiliarI,auxiliarF) == "alcoolico"?
                expressao.substring(expressao.substring(auxiliarF).indexOf("=")+1,expressao.substring(auxiliarF).indexOf(",")) : "";

        secao   = expressao.substring(0,expressao.indexOf("=")) == "secao"?
                expressao.substring(expressao.substring(auxiliarF).indexOf("=")+1,expressao.substring(auxiliarF).indexOf(",")) : "";

        repository = repository;

        return (ResponseEntity) repository.findAll();
    }

    /********* GRAVAR / ALTERAR ***********/

    @PostMapping(path = "api/armazem/salvar")
    public String salvar(@RequestBody ArmazemModel armazemModel){
        String      retorno     = "";
        float[]     secaoQtd    = new float[6];
        String[]    secaoTipo   = new String[6];

        if (Integer.parseInt(armazemModel.getSecao()) > (secaoTipo.length - 1 ))
            return "Erro: A seção deve obrigatóriamente ser de 1 a 5!";

        for (ArmazemModel soma : repository.findAll()) {

                //VERIFICA QUANTIDADE DE ALCOOLICOS E NÃO ALCOOLICOS DE CADA SEÇÃO

                secaoQtd[Integer.parseInt(soma.getSecao())]     = secaoQtd[Integer.parseInt(soma.getSecao())] + soma.getSaldo(); //IGNORADO O ÍNDICE 0 DO ARRAY PARA 5 SEÇÃO
                secaoTipo[Integer.parseInt(soma.getSecao())]    = soma.getAlcoolico(); //IGNORADO O ÍNDICE 0 DO ARRAY PARA 5 SEÇÃO

            }

        if (armazemModel.getAlcoolico() == "A" && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 500) //NÃO PODE SER MAIS QUE 500 LITROS PARA ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (secaoQtd[Integer.parseInt(armazemModel.getSecao())] - armazemModel.getSaldo());
        else if (armazemModel.getAlcoolico() == "N" && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 400) //NÃO PODE SER MAIS QUE 400 LITROS PARA ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (secaoQtd[Integer.parseInt(armazemModel.getSecao())] - armazemModel.getSaldo());
        else if (!armazemModel.getAlcoolico().contains("N") || !armazemModel.getAlcoolico().contains("A"))
            return "Erro: Informar A para alcoolico e N para não alcoolico.";

            //ALTERAÇÃO
            if (armazemModel.id != 0 && repository.existsById(armazemModel.id))

                retorno =  "Alterado com sucesso: codigo " + repository.save(armazemModel).getId();

            //INCLUSÃO
            else {

                    armazemModel.setId( (int) repository.count() + 1 );
                    retorno =  "Inserido com sucesso: codigo " + repository.save(armazemModel).getId();

                }


        return retorno;
    }

    public static String[] geraVariaveis(String expressao){
        String[] retorno = {};
        int posicao = 0;
        int indice = 0;

        while (posicao < expressao.length())

            if (expressao.substring(posicao,posicao+1) == "=")
                indice++;
            else if (expressao.substring(posicao,posicao+1) == ",")
                indice++;
            else
                retorno[indice] = retorno[indice] + expressao.substring(posicao,posicao+1);

                posicao++;
        return retorno;
    }

    /*
  public static void gravaLog(String info) {
        String filename = "Desafio_MilTec.log";
        String FILENAME = "C:\\temp\\" + filename;
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(FILENAME, true);
            bw = new BufferedWriter(fw);
            bw.write(info);
            bw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
*/
}
