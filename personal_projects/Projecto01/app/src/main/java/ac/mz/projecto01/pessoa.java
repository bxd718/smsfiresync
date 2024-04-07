package ac.mz.projecto01;

import java.io.Serializable;

public class pessoa {

    private String nome, numero, mail,  sexo, pass;

    public pessoa(String nome, String numero, String mail, String sexo, String pass) {
        this.nome = nome;
        this.numero = numero;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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
