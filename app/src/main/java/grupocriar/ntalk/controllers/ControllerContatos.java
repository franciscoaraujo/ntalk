package grupocriar.ntalk.controllers;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.json.JsonUtils;
import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Retorno;
import br.com.grupocriar.ntalk.model.Token;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.UserDataBase;
import grupocriar.ntalk.persistence.dao.DAOInterlocutor;

/**
 * Created by francisco on 18/01/2018.
 */

public class ControllerContatos {

    private static final String URL_INTERLOCUTOR = "/interlocutor/get_interlocutores/0/150";
    private Context context;

    private DAOInterlocutor daoInterlocutor;

    public ControllerContatos(Context context) {
        this.context = context;
        daoInterlocutor = new DAOInterlocutor(new DataBase(new UserDataBase(context)));
    }

    public List<Interlocutor> getContatosBancoLocal() {
        List<Interlocutor> interlocutors = null;
        Set<Interlocutor> interlocutorLocal = daoInterlocutor.selectAll();
        if (!interlocutorLocal.isEmpty()) {
            interlocutors = new ArrayList<>(interlocutorLocal);
        }
        return interlocutors;
    }


    public Retorno<List<Interlocutor>> getContatosServidor(Token token) throws IOException {
        Retorno<List<Interlocutor>> retornoServidor;
        ConnectRequests connector = new ConnectRequests();
        String json = connector.get(URL_INTERLOCUTOR, token.getToken());
        retornoServidor = JsonUtils.fromJson(json, new TypeReference<Retorno<List<Interlocutor>>>() {
        });
        for (Interlocutor interlocutor : retornoServidor.getObjeto()) {
            daoInterlocutor.insert(interlocutor);
        }
        return retornoServidor;
    }
}

















