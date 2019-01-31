package grupocriar.ntalk.persistence.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.MensagemInteracao;
import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.dao.DAO;

/**
 * Created by francisco on 11/28/17.
 */

public class DAOMensagemInteracao implements DAO<MensagemInteracao> {

    private final String TAG_OP_BANCO = "tagOperacaoBanco";

    private DataBase dataBase;

    public DAOMensagemInteracao(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public boolean insert(MensagemInteracao mensagemInteracao) {
        dataBase.open();
        ContentValues contentValues = new ContentValues();
        Log.i(TAG_OP_BANCO, "Preparando o inser na tabela mensagem_interacao_tbl");
        contentValues.put("id_mensagem_iteracao", mensagemInteracao.getIdMensagemInteracao());
        contentValues.put("id_mensagem", mensagemInteracao.getIdMensagem());
        contentValues.put("recebido", mensagemInteracao.isRecebido());
        contentValues.put("lido", mensagemInteracao.isLido());
        Log.i(TAG_OP_BANCO, "Inserindo na tabela mensagem_interacao_tbl");
        long returnTransaction = dataBase.get().insert("mensagem_interacao_tbl", null, contentValues);
        if (returnTransaction != 0) {
            return true;
        }
        contentValues.clear();
        dataBase.close();
        return false;
    }

    @Override
    public List<MensagemInteracao> select(int id) {
        dataBase.open();
        String sql = "select * from mensagem_interacao_tbl where mensagem_interacao_tbl.id_mensagem_iteracao =" + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        MensagemInteracao mensagemInteracao = new MensagemInteracao();
        mensagemInteracao.setIdMensagemInteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem_iteracao"))));
        mensagemInteracao.setIdMensagem(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem"))));
        mensagemInteracao.setLido(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("recebido"))));
        mensagemInteracao.setLido(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("lido"))));
        cursor.close();
        dataBase.close();
        return null;
    }

    @Override
    public MensagemInteracao selectUnit(int id) {
        dataBase.open();
        String sql = "select * from mensagem_interacao_tbl where mensagem_interacao_tbl.id_mensagem_iteracao =" + id;
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        MensagemInteracao mensagemInteracao = new MensagemInteracao();
        mensagemInteracao.setIdMensagemInteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem_iteracao"))));
        mensagemInteracao.setIdMensagem(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem"))));
        mensagemInteracao.setLido(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("recebido"))));
        mensagemInteracao.setLido(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("lido"))));
        cursor.close();
        dataBase.close();
        return mensagemInteracao;

    }

    @Override
    public Set<MensagemInteracao> selectAll() {
        dataBase.open();
        String sql = "select * from mensagem_tbl";
        Cursor cursor = dataBase.get().rawQuery(sql, null);
        try {
            Set<MensagemInteracao> listMensagemInteracaos = new HashSet<>();
            while (cursor.moveToNext()) {
                MensagemInteracao mensagemInteracao = new MensagemInteracao();
                mensagemInteracao.setIdMensagemInteracao(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem_iteracao"))));
                mensagemInteracao.setIdMensagem(Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_mensagem"))));
                mensagemInteracao.setLido(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("recebido"))));
                mensagemInteracao.setLido(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("lido"))));
                listMensagemInteracaos.add(mensagemInteracao);
            }
            return listMensagemInteracaos;
        } finally {
            cursor.close();
            dataBase.close();
        }

    }

    @Override
    public boolean delet(int id) {
        return false;
    }


}
