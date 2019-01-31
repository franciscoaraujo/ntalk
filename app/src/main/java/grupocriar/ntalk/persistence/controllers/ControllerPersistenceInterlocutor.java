package grupocriar.ntalk.persistence.controllers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.HistoricoOperador;
import br.com.grupocriar.ntalk.model.Interlocutor;
import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.UserDataBase;
import grupocriar.ntalk.persistence.dao.DAOInterlocutor;

/**
 * Created by francisco on 12/1/17.
 */

public class ControllerPersistenceInterlocutor {
    private DataBase dataBase;
    private DAOInterlocutor daoInterlocutor;

    public ControllerPersistenceInterlocutor(Context context) {
        dataBase = new DataBase(new UserDataBase(context));
        daoInterlocutor = new DAOInterlocutor(new DataBase(new UserDataBase(context)));
    }

    public void persisteLocalmente(List<Interlocutor> myDatasetInterlocutores) {
        for (Interlocutor interlocutor : myDatasetInterlocutores) {
            daoInterlocutor.insert(interlocutor);
        }
    }

    public Set<Interlocutor> getInterlocutorLocal() {
        return daoInterlocutor.selectAll();
    }

    public Interlocutor getInterlocutorLocal(int id) {
        return daoInterlocutor.selectUnit(id);
    }
}
