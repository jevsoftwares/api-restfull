package com.jevsoftwares.apirestfull.apirestfull.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Comparator;

@Entity(name = "armazem")
public class ArmazemModel implements Comparator<ArmazemModel> {

    @Id
    public int id;

    @Column(nullable = false, length = 6)
    public String codigo;

    @Column(nullable = false, length = 60)
    public String descricao;

    public float saldo;

    @Column(nullable = false, length = 1)
    public String alcoolico;

    @Column(nullable = false, length = 2)
    public String secao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getAlcoolico() {
        return alcoolico;
    }

    public void setAlcoolico(String alcoolico) {
        this.alcoolico = alcoolico;
    }

    public String getSecao() { return secao; }

    public void setSecao(String secao) { this.secao = secao; }

    @Override
    public int compare(ArmazemModel o1, ArmazemModel o2) {
        int id1 = o1.getId();
        int id2 = o2.getId();

        if (id1 > id2){
            return 1;
        }else  if (id1 < id2){
            return -1;
        }else {
            return 0;
        }
    }
}
