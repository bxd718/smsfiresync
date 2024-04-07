package mz.ac.bxd.project.sms_listener;

public class Recarga {

    private String idTransacao;
    private int valorRecarga;
    private String mensagem;
    private String dataRecarga;
    private String contacto;

    private boolean estado;


// Construtor

    public Recarga(){

    }

     public Recarga(String idTransacao, String dataRecarga, int valorRecarga, String contacto, String mensagem, boolean estado) {
        this.idTransacao = idTransacao;
        this.valorRecarga = valorRecarga;
        this.dataRecarga = dataRecarga;
        this.contacto = contacto;
        this.mensagem = mensagem;
        this.estado = estado;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    // Getters e Setters
    public String getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(String idTransacao) {
        this.idTransacao = idTransacao;
    }

    public int getValorRecarga() {
        return valorRecarga;
    }

    public void setValorRecarga(int valorRecarga) {
        this.valorRecarga = valorRecarga;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDataRecarga() {
        return dataRecarga;
    }

    public void setDataRecarga(String dataRecarga) {
        this.dataRecarga = dataRecarga;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    // Método toString para representação em String do objeto
    @Override
    public String toString() {
        return "Recarga{" +
                "ID='" + idTransacao + '\'' +
                ", valor:" + valorRecarga +
                ", data:'" + dataRecarga + '\'' +
                ", contacto:'" + contacto + '\'' +
                ", mensagem:'" + mensagem + '\'' +
                '}';
    }
}