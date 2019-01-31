package grupocriar.ntalk.persistence.dao;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.HistoricoOperador;
import br.com.grupocriar.ntalk.model.Interlocutor;
import grupocriar.ntalk.persistence.DataBase;

/**
 * Created by francisco on 12/1/17.
 */

public class DAInterlocutor implements DAO<Interlocutor> {

    private final String TAG_OP_BANCO = "tagOperacaoBanco";

    private DataBase dataBase;

    public DAInterlocutor(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public boolean insert(Interlocutor interlocutor) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_interlocutor", interlocutor.getIdInterlocutor());
        contentValues.put("nome", interlocutor.getNome());
        contentValues.put("registro", interlocutor.getRegistro());
        contentValues.put("id_historico_operador", interlocutor.getHistoricoOperador().getIdOperadorAcao());
        long returnTransaction = dataBase.get().insert("interlocutor_tbl", null, contentValues);
        if (returnTransaction != 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Interlocutor> select(int id) {
        String sql = "select * from interlocutor_tbl where interlocutor_tbl.id_interlocutor =" + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        List<Interlocutor> listInterlocutor = new ArrayList<>();
        while (cursor.moveToNext()) {
            Interlocutor interlocutor = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor"))));
            interlocutor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            interlocutor.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
            interlocutor.getHistoricoOperador().setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_historico_operador"))));
            listInterlocutor.add(interlocutor);
        }
        return listInterlocutor;
    }

    @Override
    public Interlocutor selectUnit(int id) {
        String sql = "select * from interlocutor_tbl where interlocutor_tbl.id_interlocutor =" + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        List<Interlocutor> listInterlocutor = new ArrayList<>();
        // while (cursor.moveToNext()) {
        Interlocutor interlocutor = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor"))));
        interlocutor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
        interlocutor.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
        interlocutor.getHistoricoOperador().setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_historico_operador"))));
        listInterlocutor.add(interlocutor);
        //}
        return interlocutor;
    }


    @Override
    public Set<Interlocutor> selectAll() {
        String sql = "select * from interlocutor_tbl";
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        Set<Interlocutor> listInterlocutor = new HashSet<>();
        while (cursor.moveToNext()) {
            Interlocutor interlocutor = new Interlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor"))));
            interlocutor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            interlocutor.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
            interlocutor.getHistoricoOperador().setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_historico_operador"))));
            listInterlocutor.add(interlocutor);
        }
        return listInterlocutor;
    }

    @Override
    public boolean delet(int id) {
        return false;
    }


}
