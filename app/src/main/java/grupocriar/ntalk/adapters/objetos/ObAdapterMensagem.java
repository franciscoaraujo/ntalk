package grupocriar.ntalk.adapters.objetos;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;

/**
 * Created by francisco on 02/01/2018.
 */

public class ObAdapterMensagem {

    private Mensagem mensagem;
    private Interlocutor interlocutor;


    public ObAdapterMensagem(Mensagem mensagem, Interlocutor interlocutor) {
        this.mensagem = mensagem;
        this.interlocutor = interlocutor;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public Interlocutor getInterlocutor() {
        return interlocutor;
    }
}
