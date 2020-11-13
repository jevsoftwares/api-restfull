package com.jevsoftwares.apirestfull.apirestfull.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Comparator;

@Entity(name = "historico")
public class HistoricoModel  {

    @Id
    public int id;

    @Column(nullable = false)
    public int id_prod;

    @Column(nullable = false, length = 6)
    public String codigo;

    @Column(nullable = false, length = 60)
    public String descricao;

    public float entrada;

    public float saida;

    @Column(nullable = false, length = 1)
    public String alcoolico;

    @Column(nullable = false, length = 2)
    public String secao;

    @Column(nullable = false, length = 10)
    public String data_alt;

    @Column(nullable = false, length = 8)
    public String hora_alt;

    @Column(nullable = false, length = 60)
    public String usuario;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_prod() {
        return id_prod;
    }

    public void setId_prod(int id_prod) {
        this.id_prod = id_prod;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getEntrada() {
        return entrada;
    }

    public void setEntrada(float entrada) {
        this.entrada = entrada;
    }

    public float getSaida() {
        return saida;
    }

    public void setSaida(float saida) {
        this.saida = saida;
    }

    public String getAlcoolico() {
        return alcoolico;
    }

    public void setAlcoolico(String alcoolico) {
        this.alcoolico = alcoolico;
    }

    public String getSecao() {
        return secao;
    }

    public void setSecao(String secao) {
        this.secao = secao;
    }

    public String getData_alt() {
        return data_alt;
    }

    public void setData_alt(String data_alt) {
        this.data_alt = data_alt;
    }

    public String getHora_alt() {
        return hora_alt;
    }

    public void setHora_alt(String hora_alt) {
        this.hora_alt = hora_alt;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
/*
    @Override
    public int compare(HistoricoModel o1, HistoricoModel o2) {
        int order1 = o1.getId();
        int order2 = o2.getId();

        if (order1 > order2){
            return 1;
        }else  if (order1 < order2){
            return -1;
        }else {
            return 0;
        }

        return o1.getUsuario().compareTo(o2.getUsuario());
    }
    */
}
