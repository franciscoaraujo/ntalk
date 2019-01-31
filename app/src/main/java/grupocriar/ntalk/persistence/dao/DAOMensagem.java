package grupocriar.ntalk.persistence.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.persistence.DataBase;

/**
 * Created by francisco on 11/28/17.
 */

public class DAOMensagem implements DAO<Mensagem> {

    private static final String TAG_OP_BANCO = "tagOperacaoBanco";

    private final DataBase dataBase;

    public DAOMensagem(DataBase dataBase) {
        this.dataBase = dataBase;
    }


    @Override
    public boolean insert(Mensagem t) {
        dataBase.open();
        Log.i(TAG_OP_BANCO, "Preparando para o insert na tabela mensagem_tbl");
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_mensagem_servidor", t.getIdMensagem());
        contentValues.put("id_atendimento", t.getIdAtendimento());
        contentValues.put("id_interlocutor_remetente", t.getRemetente().getIdInterlocutor());
        contentValues.put("id_interlocutor_destinatario", t.getDestinatario().getIdInterlocutor());
        contentValues.put("conteudo", t.getConteudo());
        contentValues.put("nome_destinatario", t.getDestinatario().getNome());
        contentValues.put("nome_remetente", t.getRemetente().getNome());
        contentValues.put("data_hora_mensagem", t.getDataHoraMensagem().getTime());
        //contentValues.put("id_mensagem_iteracao", t.getMensagensInteracoes().get(0).getIdMensagemInteracao());
        Log.i(TAG_OP_BANCO, "Iniciando o insert na tabela mensagem_tbl");
        long returnTransaction = dataBase.get().insert("mensagem_tbl", null, contentValues);
        if (returnTransaction != 0) {
            dataBase.close();
            return false;
        }
        Log.i(TAG_OP_BANCO, contentValues.toString());

        contentValues.clear();
        dataBase.close();
        Log.i(TAG_OP_BANCO, "Cadastro de Usuarios realiazado com sucesso!");
        return true;
    }

    @Override
    public Mensagem selectUnit(int id) {
        dataBase.open();
        String sql = "select * from mensagem_tbl where mensagem_tbl.id_interlocutor_destinatario =" + id
                + " order by id_mensagem DESC limit 1";
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        List<Mensagem> listMensagens = new ArrayList<>();
        Mensagem mensagem = null;
        while (cursor.moveToNext()) {
            if (cursor.getCount() != 0) {
                Interlocutor interlocutorRemetente = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor_remetente"))));
                interlocutorRemetente.setNome(cursor.getString(cursor.getColumnIndex("nome_remetente")));
                Interlocutor interlocutorDestinatario = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor_destinatario"))));
                interlocutorDestinatario.setNome(cursor.getString(cursor.getColumnIndex("nome_destinatario")));
                mensagem = new Mensagem(interlocutorRemetente, interlocutorDestinatario);
                Date dt = new Date(cursor.getColumnIndex("data_hora_mensagem"));
                mensagem.setDataHoraMensagem(dt)
                        .setIdMensagem(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem"))))
                        .setConteudo(cursor.getString(cursor.getColumnIndex("conteudo")))
                        .setIdAtendimento(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_atendimento"))));
            }
        }
        cursor.close();
        dataBase.close();
        return mensagem;
    }

    @Override
    public Set<Mensagem> selectAll() {
        dataBase.open();
        String sql = "select * from mensagem_tbl group by nome_remetente, nome_destinatario";
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        Set<Mensagem> listMensagens = new HashSet<>();
        while (cursor.moveToNext()) {
            Interlocutor interlocutorRemetente = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor_remetente"))));
            interlocutorRemetente.setNome(cursor.getString(cursor.getColumnIndex("nome_remetente")));
            Interlocutor interlocutorDestinatario = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor_destinatario"))));
            interlocutorDestinatario.setNome(cursor.getString(cursor.getColumnIndex("nome_destinatario")));

            Mensagem mensagem = new Mensagem(interlocutorRemetente, interlocutorDestinatario);
            long data_hora_mensagem = cursor.getLong(cursor.getColumnIndex("data_hora_mensagem"));
            Date dt = new Date(data_hora_mensagem);
            mensagem.setDataHoraMensagem(dt)
                    .setIdMensagem(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem"))))
                    .setConteudo(cursor.getString(cursor.getColumnIndex("conteudo")))
                    .setIdAtendimento(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_atendimento"))));
            // .getMensagensInteracoes().get(0).setIdMensagemInteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem_iteracao"))));
            listMensagens.add(mensagem);
        }
        cursor.close();
        dataBase.close();
        return listMensagens;
    }

    @Override
    public boolean delet(int id) {
        return false;
    }

    @Override
    public List<Mensagem> select(int id) throws IOException {
        List<Mensagem> listMensagens = new ArrayList<>();
        try {
            dataBase.open();
            String sql = "select * from mensagem_tbl where " +
                    "(mensagem_tbl.id_interlocutor_destinatario =" + id + " and mensagem_tbl.id_interlocutor_remetente =" + LocalApplication.getInstance().getUsuarioCelular().getIdInterlocutor() + ") or" +
                    "(mensagem_tbl.id_interlocutor_remetente =" + id + " and mensagem_tbl.id_interlocutor_destinatario =" + LocalApplication.getInstance().getUsuarioCelular().getIdInterlocutor() + ") or " +
                    "(mensagem_tbl.id_mensagem >" + id + ") order by id_mensagem DESC ";

            Cursor cursor = dataBase.get().rawQuery(sql, null);

            while (cursor.moveToNext()) {
                if (cursor.getCount() != 0) {
                    Interlocutor interlocutorRemetente = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor_remetente"))));
                    interlocutorRemetente.setNome(cursor.getString(cursor.getColumnIndex("nome_remetente")));
                    Interlocutor interlocutorDestinatario = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor_destinatario"))));
                    interlocutorDestinatario.setNome(cursor.getString(cursor.getColumnIndex("nome_destinatario")));
                    Mensagem mensagem = new Mensagem(interlocutorRemetente, interlocutorDestinatario);
                    long data_hora_mensagem = cursor.getLong(cursor.getColumnIndex("data_hora_mensagem"));
                    Date dt = new Date(data_hora_mensagem);
                    mensagem.setDataHoraMensagem(dt)
                            .setIdMensagem(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem_servidor"))))
                            .setConteudo(cursor.getString(cursor.getColumnIndex("conteudo")))
                            .setIdMensagemLocal(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem"))))
                            .setIdAtendimento(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_atendimento"))));
                    listMensagens.add(mensagem);
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            dataBase.close();
        }

        return listMensagens;
    }
}

