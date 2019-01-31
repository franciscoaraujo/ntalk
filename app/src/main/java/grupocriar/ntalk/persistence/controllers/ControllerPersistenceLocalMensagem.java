package grupocriar.ntalk.persistence.controllers;

import android.content.Context;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import br.com.grupocriar.ntalk.model.Mensagens;
import grupocriar.ntalk.persistence.dao.DAOMensagem;
import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.UserDataBase;

/**
 * Created by francisco on 11/28/17.
 */

public class ControllerPersistenceLocalMensagem {
    private DataBase dataBase;
    private Interlocutor remetente;
    private Interlocutor destinatario;
    private DAOMensagem daoMensagem;

    public ControllerPersistenceLocalMensagem(Context context) {
        dataBase = new DataBase(new UserDataBase(context));
        daoMensagem = new DAOMensagem(dataBase);
    }


    /**
     * @param context
     * @param remetente
     * @param destinatario
     */
    public ControllerPersistenceLocalMensagem(Context context, Interlocutor remetente, Interlocutor destinatario) {
        dataBase = new DataBase(new UserDataBase(context));
        daoMensagem = new DAOMensagem(dataBase);
        this.remetente = remetente;
        this.destinatario = destinatario;
    }


    public void armazenaMensagemLocalmente(Mensagem obMensagem) {
        daoMensagem.insert(obMensagem);

    }

    public void armazenaMensagemLocalmente(String mensagem) {
        Mensagem obMensagem = new Mensagem(remetente, destinatario);
        obMensagem.setConteudo(mensagem);
        obMensagem.setDataHoraMensagem(Calendar.getInstance().getTime());
        obMensagem.setDestinatario(destinatario);
        obMensagem.setRemetente(remetente);
        daoMensagem.insert(obMensagem);

    }

    public void armazenaMensagemLocalmente(List<Mensagem> mensagens) {
        for (Mensagem msg : mensagens) {
            Mensagem obMensagem = new Mensagem(msg.getRemetente(), msg.getDestinatario());
            obMensagem.setConteudo(msg.getConteudo());
            obMensagem.setDataHoraMensagem(Calendar.getInstance().getTime());
            obMensagem.setDestinatario(msg.getDestinatario());
            obMensagem.setRemetente(msg.getRemetente());

            daoMensagem.insert(obMensagem);
        }
    }

    public List<Mensagem> getAllMsgOf(Interlocutor interlocutor) throws IOException {
        int idInterlocutor = interlocutor.getIdInterlocutor();
        return daoMensagem.select(idInterlocutor);
    }

    public List<Mensagem> getAllMsgOf(int idMensagem) throws IOException {
        List<Mensagem> select = daoMensagem.select(idMensagem);
        if (select.size() > 0) {
            Log.i("TAG_DAO:", select.get(0).getConteudo() + "ID:" + select.get(0).getIdMensagemLocal());
        }
        return select;
    }


    public ArrayList<Mensagem> getConversas() {
        Set<Mensagem> mensagems = daoMensagem.selectAll();
        ArrayList<Mensagem> listMensagem = new ArrayList<>(mensagems);
        return listMensagem;
    }


}
