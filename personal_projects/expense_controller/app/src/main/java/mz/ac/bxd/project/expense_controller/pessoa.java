package mz.ac.bxd.project.expense_controller;

import java.io.Serializable;

public class pessoa implements Serializable{

    private String nome, mail,  sexo, pass;

    public pessoa(String nome, String mail, String sexo, String pass) {
        this.nome = nome;
        this.mail = mail;
        this.sexo = sexo;
        this.pass = pass;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
