package grupocriar.ntalk.adapters.objetos;


import br.com.grupocriar.ntalk.model.Mensagem;

/**
 * Created by francisco on 09/01/2018.
 */

public class ObConversa {

    private Mensagem mensagem;
    private String nomeRemente;
    private String nomeDestinatario;


    public ObConversa(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public ObConversa setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
        return this;
    }

    public String getNomeRemente() {
        return nomeRemente;
    }

    public ObConversa setNomeRemente(String nomeRemente) {
        this.nomeRemente = nomeRemente;
        return this;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public ObConversa setNomeDestinatario(String nomeDestinatario) {
        this.nomeDestinatario = nomeDestinatario;
        return this;
    }

}
