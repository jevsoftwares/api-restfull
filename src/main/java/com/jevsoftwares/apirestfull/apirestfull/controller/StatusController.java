package com.jevsoftwares.apirestfull.apirestfull.controller;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.jevsoftwares.apirestfull.apirestfull.model.ArmazemModel;
import com.jevsoftwares.apirestfull.apirestfull.repository.ArmazemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class StatusController {

    @Autowired
    private ArmazemRepository repository;
    private Object ArmazemModel;

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
    public LinkedList consultar(@PathVariable("expressao") String expressao ){
        int id = 0;
        String descricao = null, alcoolico = null,secao = null, all = null, order = null;
        float saldo = 0;

        String[][] arrayParams = geraVariaveis(expressao);

        for (String[] string :arrayParams ) {

            if (string[1] != null ){

                switch (string[1]){
                    case "id":
                        id = Integer.parseInt(string[2]);
                        break;
                    case "alcoolico":
                        alcoolico = string[2];
                        break;
                    case "descricao":
                        descricao = string[2];
                        break;
                    case "secao":
                        secao = string[2];
                        break;
                    case "saldo":
                        saldo = Float.parseFloat(string[2]);
                        break;
                    case "order":
                        order = string[2];
                        break;
                }

            }
        }

        LinkedList<ArmazemModel> list = new LinkedList<>();

        for (ArmazemModel armazemModel : repository.findAll()) {

            if ((id != 0 && armazemModel.getId() == id)
                    || ( secao != null && armazemModel.getSecao().equals(secao) )
                    || ( alcoolico != null && armazemModel.getAlcoolico().equals(alcoolico) )
                    || ( descricao != null && armazemModel.getDescricao().contains(descricao) )){

                list.add(armazemModel);

            }

        }
        if (list.isEmpty()){

            for (ArmazemModel armazemModel : repository.findAll()) {

                list.add(armazemModel);

            }

        }
        Collections.sort(list, new ArmazemModel());

        return  list;

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

        if (armazemModel.getAlcoolico().equals("A") && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 500) //NÃO PODE SER MAIS QUE 500 LITROS PARA ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (secaoQtd[Integer.parseInt(armazemModel.getSecao())] - armazemModel.getSaldo());
        else if (armazemModel.getAlcoolico().equals("N") && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 400) //NÃO PODE SER MAIS QUE 400 LITROS PARA ALCOOLICO
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

    public static String[][] geraVariaveis(String expressao){
        String[][] retorno = {};
        int posicao = 0;
        int indiceR = 0; //REFERENCIA
        int indiceV = 1; //VALOR

        retorno = new String[expressao.length()][expressao.length()];

        while (posicao < expressao.length()){
            if (expressao.substring(posicao,posicao+1).equals("=")){
                indiceV++;
            }
            else if (expressao.substring(posicao,posicao+1).equals(",")){
                indiceR++;
                indiceV = 1;
            }
            else{
                if (retorno[indiceR][indiceV] == null)
                    retorno[indiceR][indiceV] = "";
                retorno[indiceR][indiceV] +=  expressao.substring(posicao,posicao+1);
            }

            posicao++;
        }


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
