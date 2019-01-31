package grupocriar.ntalk.persistence.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.HistoricoOperador;
import grupocriar.ntalk.persistence.DataBase;

/**
 * Created by francisco on 11/28/17.
 */

public class DAOHistoricoOperador implements DAO<HistoricoOperador> {

    private final String TAG_OP_BANCO = "tagOperacaoBanco";

    private DataBase dataBase;


    public DAOHistoricoOperador(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public boolean insert(HistoricoOperador historicoOperador) {
        dataBase.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_operador_acao", historicoOperador.getIdOperadorAcao());
        contentValues.put("id_operador_cadastro", historicoOperador.getIdOperadorCadastro());
        contentValues.put("data_hora_cadastro", historicoOperador.getDataHoraCadastro().toString());
        contentValues.put("id_operador_alteracao", historicoOperador.getIdOperadorAlteracao());
        contentValues.put("id_operador_exclusao", historicoOperador.getIdOperadorExclusao());
        contentValues.put("data_hora_exclusao", historicoOperador.getDataHoraExclusao().toString());

        long returnTransaction = dataBase.get().insert("historico_operador_tbl", null, contentValues);
        if (returnTransaction != 0) {
            return true;
        }
        contentValues.clear();
        dataBase.close();
        return false;
    }


    @Override
    public List<HistoricoOperador> select(int id) {
        dataBase.close();
        String sql = "select * from historico_operador_tbl where historico_operador_tbl.id_operador_cadastro =" + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        String idOperadorAcao = cursor.getString(cursor.getColumnIndex("id_operador_acao"));
        HistoricoOperador historicoOperador = new HistoricoOperador(Integer.parseInt(idOperadorAcao));
        historicoOperador.setIdOperadorCadastro(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_cadastro"))));
        historicoOperador.setDataHoraCadastro(new Date(cursor.getString(cursor.getColumnIndex("data_hora_cadastro"))));
        historicoOperador.setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_alteracao"))));
        historicoOperador.setIdOperadorExclusao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_exclusao"))));
        historicoOperador.setDataHoraExclusao(new Date(cursor.getString(cursor.getColumnIndex("data_hora_exclusao"))));
        cursor.close();
        dataBase.close();
        return null;
    }

    @Override
    public HistoricoOperador selectUnit(int id) {
        dataBase.open();
        String sql = "select * from historico_operador_tbl where historico_operador_tbl.id_operador_cadastro =" + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        String idOperadorAcao = cursor.getString(cursor.getColumnIndex("id_operador_acao"));
        HistoricoOperador historicoOperador = new HistoricoOperador(Integer.parseInt(idOperadorAcao));
        historicoOperador.setIdOperadorCadastro(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_cadastro"))));
        historicoOperador.setDataHoraCadastro(new Date(cursor.getString(cursor.getColumnIndex("data_hora_cadastro"))));
        historicoOperador.setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_alteracao"))));
        historicoOperador.setIdOperadorExclusao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_exclusao"))));
        historicoOperador.setDataHoraExclusao(new Date(cursor.getString(cursor.getColumnIndex("data_hora_exclusao"))));
        cursor.close();
        dataBase.close();
        return historicoOperador;
    }

    @Override
    public Set<HistoricoOperador> selectAll() {
        dataBase.open();
        String sql = "select * from historico_operador_tbl";
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        Set<HistoricoOperador> listHistoricoOperadors = new HashSet<>();
        while (cursor.moveToNext()) {
            String idOperadorAcao = cursor.getString(cursor.getColumnIndex("id_operador_acao"));
            HistoricoOperador historicoOperador = new HistoricoOperador(Integer.parseInt(idOperadorAcao));

            historicoOperador.setIdOperadorCadastro(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_cadastro"))));
            historicoOperador.setDataHoraCadastro(new Date(cursor.getString(cursor.getColumnIndex("data_hora_cadastro"))));
            historicoOperador.setIdOperadorAlteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_alteracao"))));
            historicoOperador.setIdOperadorExclusao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_operador_exclusao"))));
            historicoOperador.setDataHoraExclusao(new Date(cursor.getString(cursor.getColumnIndex("data_hora_exclusao"))));

            listHistoricoOperadors.add(historicoOperador);
        }
        cursor.close();
        dataBase.close();
        return listHistoricoOperadors;
    }

    @Override
    public boolean delet(int id) {
        return false;
    }


}
