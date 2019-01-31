package grupocriar.ntalk.adapters.objetos;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;

/**
 * Created by francisco on 02/01/2018.
 */

public class ObAdapterContatos {
    private int idInterlocutor;
    private Mensagem mensagem;
    private String nome;
    private Interlocutor interlocutor;

    public ObAdapterContatos(int idInterlocutor, Mensagem mensagem, String nome, Interlocutor interlocutor) {
        this.idInterlocutor = idInterlocutor;
        this.mensagem = mensagem;
        this.nome = nome;
        this.interlocutor = interlocutor;
    }

    public Interlocutor getInterlocutor() {
        return interlocutor;
    }

    public void setInterlocutor(Interlocutor interlocutor) {
        this.interlocutor = interlocutor;
    }

    public int getIdInterlocutor() {
        return idInterlocutor;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return idInterlocutor + " - " + mensagem + " - " + nome;
    }
}
