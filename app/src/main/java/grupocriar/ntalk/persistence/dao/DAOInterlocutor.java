package grupocriar.ntalk.persistence.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.Interlocutor;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.persistence.DataBase;

/**
 * Created by francisco on 11/28/17.
 */

public class DAOInterlocutor implements DAO<Interlocutor> {

    private DataBase dataBase;

    public DAOInterlocutor(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public boolean insert(Interlocutor interlocutor) {
        dataBase.open();
        Log.i(LocalApplication.TAG_OPER_DATABASE, "INICIANDO INSERT NA TABELA DE INTERLOCUTOR");
        ContentValues contentValues = new ContentValues();
        contentValues.put("registro", interlocutor.getRegistro());
        contentValues.put("nome", interlocutor.getNome());
        contentValues.put("usuario_nucleo", interlocutor.getUsuarioNucleo());
        contentValues.put("id_interlocutor", interlocutor.getIdInterlocutor());
        long returnTransaction = dataBase.get()
                .insert("interlocutor_tbl", null, contentValues);
        if (returnTransaction != 0) {
            dataBase.close();
            return true;
        }
        contentValues.clear();
        dataBase.close();
        return false;
    }


    @Override
    public Interlocutor selectUnit(int id) {
        String sql = "select * from interlocutor_tbl where interlocutor_tbl.id_interlocutor = " + id;
        dataBase.open();

        Cursor cursor = dataBase.get().rawQuery(sql, null);
        Interlocutor interlocutor = new Interlocutor();
        interlocutor.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
        interlocutor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
        interlocutor.setUsuarioNucleo(cursor.getString(cursor.getColumnIndex("usuario_nucleo")));
        interlocutor.setIdInterlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor"))));
        interlocutor.getHistoricoOperador().setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_historio_operador"))));

        cursor.close();
        dataBase.close();
        return interlocutor;
    }

    @Override
    public List<Interlocutor> select(int id) {
        dataBase.open();
        String sql = "select * from interlocutor_tbl where interlocutor_tbl.id_interlocutor = " + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        Interlocutor interlocutor = new Interlocutor();
        interlocutor.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
        interlocutor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
        interlocutor.setUsuarioNucleo(cursor.getString(cursor.getColumnIndex("usuario_nucleo")));
        interlocutor.setIdInterlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor"))));
        interlocutor.getHistoricoOperador().setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_historio_operador"))));
        cursor.close();
        dataBase.close();
        return null;
    }

    @Override
    public Set<Interlocutor> selectAll() {
        dataBase.open();
        String sql = "select * from interlocutor_tbl";
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        Set<Interlocutor> listaInterlocutor = new LinkedHashSet<>();
        while (cursor.moveToNext()) {
            Interlocutor interlocutor = new Interlocutor();
            interlocutor.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
            interlocutor.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            interlocutor.setUsuarioNucleo(cursor.getString(cursor.getColumnIndex("usuario_nucleo")));
            interlocutor.setIdInterlocutor(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_interlocutor"))));
            listaInterlocutor.add(interlocutor);
        }
        cursor.close();
        dataBase.close();
        return listaInterlocutor;
    }

    @Override
    public boolean delet(int id) {
        return false;
    }


}
