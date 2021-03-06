package com.jevsoftwares.apirestfull.apirestfull.controller;

import com.jevsoftwares.apirestfull.apirestfull.model.ArmazemModel;
import com.jevsoftwares.apirestfull.apirestfull.model.HistoricoModel;
import com.jevsoftwares.apirestfull.apirestfull.repository.ArmazemRepository;
import com.jevsoftwares.apirestfull.apirestfull.repository.HistoricoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Api(value = "Api Rest Armazem de produtos")
@CrossOrigin(origins = "*")
public class ArmazemController {

    @Autowired
    private ArmazemRepository repository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @GetMapping(path = "api/status")
    @ApiOperation(value = "Valida se a api está ativa.")
    public String check(){
        return "online";
    }

    /****************** Armazem ******************/

    /********CONSULTAS ***********/

    @GetMapping(path = "api/armazem/id/{id}")
    @ApiOperation(value = "Consulta de produto por id")
    public ResponseEntity consultar(@PathVariable("id") int id){

        return repository.findById(id)
                        .map(record -> ResponseEntity.ok().body(record))
                        .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping(value = "api/armazem/{expressao}")
    @ApiOperation(value = "Consulta de produtos por expressao | Ex. secao=2,alcoolico=A")
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

    @PostMapping(path = "api/armazem/alterar")
    @ApiOperation(value = "Alterar um produto | Obs. É necessário informar o id na requisição.")
    public String alterar(@RequestBody ArmazemModel armazemModel){
        String      retorno     = "";
        float       qtdEntrada  = 0;
        float       qtdSaida    = 0;
        float[]     secaoQtd    = new float[6]; //STRING CORRESPONDENTE AO ARMAZEM  DE 1 A 5 INGNORANDO O ÍNDICE 0
        String[]    secaoTipo   = new String[6]; //STRING CORRESPONDENTE AO ARMAZEM  DE 1 A 5 INGNORANDO O ÍNDICE 0

        if (Integer.parseInt(armazemModel.getSecao()) > (secaoTipo.length - 1 ) || Integer.parseInt(armazemModel.getSecao()) < 1 )
            return "Erro: A seção deve obrigatóriamente ser de 1 a 5!";

        if (armazemModel.getSaldo() <= 0)
            return "Erro: A quantidade do produto deve ser maior que zero!";

        for (ArmazemModel soma : repository.findAll()) {

                //VERIFICA QUANTIDADE DE ALCOOLICOS E NÃO ALCOOLICOS DE CADA SEÇÃO

                secaoQtd[Integer.parseInt(soma.getSecao())]     = secaoQtd[Integer.parseInt(soma.getSecao())] + soma.getSaldo(); //IGNORADO O ÍNDICE 0 DO ARRAY PARA 5 SEÇÃO
                secaoTipo[Integer.parseInt(soma.getSecao())]    = soma.getAlcoolico(); //IGNORADO O ÍNDICE 0 DO ARRAY PARA 5 SEÇÃO

                if (soma.getSecao().equals(armazemModel.getSecao()) && !soma.getAlcoolico().equals(armazemModel.getAlcoolico()))
                    return "Erro: Seção não disponível para produto " + (armazemModel.getAlcoolico().equals("A") ? "alcoolico" : "não alcoolico.");

            }


        if (armazemModel.getAlcoolico().equals("A") && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 500) //NÃO PODE SER MAIS QUE 500 LITROS PARA ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (500 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) + ", portanto faltam " +(armazemModel.getSaldo() - (500 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) ) + " para alocar o produto!";
        else if (armazemModel.getAlcoolico().equals("N") && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 400) //NÃO PODE SER MAIS QUE 400 LITROS PARA NÃO ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (400 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) + ", portanto faltam " +(armazemModel.getSaldo() - (400 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) ) + " para alocar o produto!";
        else if (!armazemModel.getAlcoolico().contains("N") && !armazemModel.getAlcoolico().contains("A"))
            return "Erro: Informar A para alcoolico e N para não alcoolico.";


        //ALTERAÇÃO
        if (armazemModel.id != 0 && repository.existsById(armazemModel.id)){

            retorno     =  "Alterado com sucesso: codigo " + repository.save(armazemModel).getId();
            qtdEntrada  = armazemModel.getSaldo();

        }//INCLUSÃO
        else {

                armazemModel.setId( (int) repository.count() + 1 );
                retorno =  "Inserido com sucesso: codigo " + repository.save(armazemModel).getId();
                qtdSaida  = armazemModel.getSaldo();

            }
        Date dataAlt = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); //"dd/MM/yyyy HH:mm:ss"
        String data_alt = dateFormat.format(dataAlt);
        String hora_alt = dataAlt.toString().substring(11,19);//dateFormat.format(horaFormat);

        HistoricoModel historicoModel = new HistoricoModel();

        historicoModel.setId( (int) historicoRepository.count() + 1 );
        historicoModel.setId_prod(armazemModel.getId());
        historicoModel.setCodigo(armazemModel.getCodigo());
        historicoModel.setDescricao(armazemModel.getDescricao());
        historicoModel.setEntrada(qtdEntrada);
        historicoModel.setSaida(qtdSaida);
        historicoModel.setAlcoolico(armazemModel.getAlcoolico());
        historicoModel.setSecao(armazemModel.getSecao());
        historicoModel.setData_alt(data_alt);
        historicoModel.setHora_alt(hora_alt);
        historicoModel.setUsuario("jayson.mattoso");

        historicoRepository.save(historicoModel);

        return retorno;
    }

    @PutMapping(path = "api/armazem/adicionar")
    @ApiOperation(value = "Adicionar um produto | Obs. Não é necessário informar o id na requisição.")
    public String adicionar(@RequestBody ArmazemModel armazemModel){
        String      retorno     = "";
        float       qtdEntrada  = 0;
        float       qtdSaida    = 0;
        float[]     secaoQtd    = new float[6]; //STRING CORRESPONDENTE AO ARMAZEM  DE 1 A 5 INGNORANDO O ÍNDICE 0
        String[]    secaoTipo   = new String[6]; //STRING CORRESPONDENTE AO ARMAZEM  DE 1 A 5 INGNORANDO O ÍNDICE 0

        if (Integer.parseInt(armazemModel.getSecao()) > (secaoTipo.length - 1 ) || Integer.parseInt(armazemModel.getSecao()) < 1 )
            return "Erro: A seção deve obrigatóriamente ser de 1 a 5!";

        if (armazemModel.getSaldo() <= 0)
            return "Erro: A quantidade do produto deve ser maior que zero!";

        for (ArmazemModel soma : repository.findAll()) {

            //VERIFICA QUANTIDADE DE ALCOOLICOS E NÃO ALCOOLICOS DE CADA SEÇÃO

            secaoQtd[Integer.parseInt(soma.getSecao())]     = secaoQtd[Integer.parseInt(soma.getSecao())] + soma.getSaldo(); //IGNORADO O ÍNDICE 0 DO ARRAY PARA 5 SEÇÃO
            secaoTipo[Integer.parseInt(soma.getSecao())]    = soma.getAlcoolico(); //IGNORADO O ÍNDICE 0 DO ARRAY PARA 5 SEÇÃO

            if (soma.getSecao().equals(armazemModel.getSecao()) && !soma.getAlcoolico().equals(armazemModel.getAlcoolico()))
                return "Erro: Seção não disponível para produto " + (armazemModel.getAlcoolico().equals("A") ? "alcoolico" : "não alcoolico.");

        }


        if (armazemModel.getAlcoolico().equals("A") && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 500) //NÃO PODE SER MAIS QUE 500 LITROS PARA ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (500 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) + ", portanto faltam " +(armazemModel.getSaldo() - (500 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) ) + " para alocar o produto!";
        else if (armazemModel.getAlcoolico().equals("N") && secaoQtd[Integer.parseInt(armazemModel.getSecao())] + armazemModel.getSaldo() > 400) //NÃO PODE SER MAIS QUE 400 LITROS PARA NÃO ALCOOLICO
            return "Erro: Limite de capacidade do estoque por seção atingido. A capacidade disponível é de "+ (400 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) + ", portanto faltam " +(armazemModel.getSaldo() - (400 - secaoQtd[Integer.parseInt(armazemModel.getSecao())] ) ) + " para alocar o produto!";
        else if (!armazemModel.getAlcoolico().contains("N") && !armazemModel.getAlcoolico().contains("A"))
            return "Erro: Informar A para alcoolico e N para não alcoolico.";


        //ALTERAÇÃO
        if (armazemModel.id != 0 && repository.existsById(armazemModel.id)){

            retorno     =  "Alterado com sucesso: codigo " + repository.save(armazemModel).getId();
            qtdEntrada  = armazemModel.getSaldo();

        }//INCLUSÃO
        else {

            armazemModel.setId( (int) repository.count() + 1 );
            retorno =  "Inserido com sucesso: codigo " + repository.save(armazemModel).getId();
            qtdSaida  = armazemModel.getSaldo();

        }
        Date dataAlt = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); //"dd/MM/yyyy HH:mm:ss"
        String data_alt = dateFormat.format(dataAlt);
        String hora_alt = dataAlt.toString().substring(11,19);//dateFormat.format(horaFormat);

        HistoricoModel historicoModel = new HistoricoModel();

        historicoModel.setId( (int) historicoRepository.count() + 1 );
        historicoModel.setId_prod(armazemModel.getId());
        historicoModel.setCodigo(armazemModel.getCodigo());
        historicoModel.setDescricao(armazemModel.getDescricao());
        historicoModel.setEntrada(qtdEntrada);
        historicoModel.setSaida(qtdSaida);
        historicoModel.setAlcoolico(armazemModel.getAlcoolico());
        historicoModel.setSecao(armazemModel.getSecao());
        historicoModel.setData_alt(data_alt);
        historicoModel.setHora_alt(hora_alt);
        historicoModel.setUsuario("jayson.mattoso");

        historicoRepository.save(historicoModel);

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

    @GetMapping(path = "api/historico/id/{id}")
    @ApiOperation(value = "Consulta as alterações realizadas na tabela de produto por id")
    public ResponseEntity consultarHistorico(@PathVariable("id") int id){

        return historicoRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "api/historico/{expressao}")
    @ApiOperation(value = "Consulta de produtos por expressao | Ex. secao=2,alcoolico=A")
    public LinkedList consultarHistorico(@PathVariable("expressao") String expressao ){
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

        LinkedList<HistoricoModel> list = new LinkedList<>();

        for (HistoricoModel historicoRepository : historicoRepository.findAll()) {

            if ((id != 0 && historicoRepository.getId() == id)
                    || ( secao != null && historicoRepository.getSecao().equals(secao) )
                    || ( alcoolico != null && historicoRepository.getAlcoolico().equals(alcoolico) )
                    || ( descricao != null && historicoRepository.getDescricao().contains(descricao) )){

                list.add(historicoRepository);

            }

        }
        if (list.isEmpty()){

            for (HistoricoModel historicoModel : historicoRepository.findAll()) {

                list.add(historicoModel);

            }

        }

        if (!order.isEmpty())
            ArmazemController.ordenacaoString(order,list);

        return  list;

    }

    private static void ordenacaoString(String order, LinkedList list) {
        Collections.sort(list, new Comparator<HistoricoModel>() {
            public int compare(HistoricoModel p1, HistoricoModel p2) {

                switch (order) {
                    case "secao":
                        return p1.getSecao().compareTo(p2.getSecao());
                    case "data_alt":
                        return p1.getData_alt().compareTo(p2.getData_alt());
                    case "usuario":
                        return p1.getUsuario().compareTo(p2.getUsuario());
                }
                return 0;
            }
        });


//        Collections.sort(list, new HistoricoModel());
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
