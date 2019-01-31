package grupocriar.ntalk.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import java.io.IOException;

import br.com.grupocriar.ntalk.json.JsonUtils;
import br.com.grupocriar.ntalk.model.Envio;
import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import br.com.grupocriar.ntalk.model.Retorno;
import br.com.grupocriar.ntalk.model.Usuario;
import grupocriar.ntalk.BuildConfig;


/**
 * Created by francisco on 20/12/2017.
 */

public class LocalApplication extends Application {

    public static final boolean TESTE = true;

    public static final String TAG_CONTROLLERS_PERSIST = "tag.controller_pers_";
    public static final String TAG_CONTROLLERS_CONNECT = "tag.controllers_conn_";
    public static final String TAG_DEBUG = "tag.debug_";
    public static final String TAG_OPER_DATABASE = "tag.operacao.banco_";
    public static final String TAG_CONTATOS = "tag.contatos_";

    private static LocalApplication instance;

    private static final String SP_ENVIO = "objeto.envio";
    private static final String SP_RETORNO = "objeto.retorno";
    private static final String SP_USUARIO = "objeto.usuario";
    private static final String SP_INTERLOCUTOR_REMETENTE = "objeto.interlocutor.remetente";
    private static final String SP_INTERLOCUTOR_DESTINATARIO = "objeto.interlocutor.destinatario";
    private static final String SP_MENSAGEM_INTERLOCUTOR = "objeto.mensagem.interlocutor";
    private static final String SP_ID_MENSAGEM_INTERLOCUTOR = "id.mensagem.interlocutor";


    private static final String PRIMEIRO_ACESSO = "primeiro.acesso.ntalk";


    public static LocalApplication getInstance() {
        return instance;
    }

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
    }


    public void setPrimeiroAcesso(boolean valor) {
        sharedPreferences.edit().putBoolean(PRIMEIRO_ACESSO, valor).apply();
    }

    public boolean getPrimeiroAcesso() {
        return sharedPreferences.getBoolean(PRIMEIRO_ACESSO, true);
    }

    public void setObEnvio(Envio envio) throws JSONException, JsonProcessingException {
        String jsonString = JsonUtils.toJson(envio);
        sharedPreferences.edit().putString(SP_ENVIO, jsonString).apply();
    }

    public Envio getEnvio() throws IOException {
        String envioString = sharedPreferences.getString(SP_ENVIO, "{\"objeto\":null}");
        Envio envio = JsonUtils.fromJson(envioString, Envio.class);
        return envio;
    }

    public void setRetorno(Retorno retorno) throws JsonProcessingException {
        String jsonString = JsonUtils.toJson(retorno);
        sharedPreferences.edit().putString(SP_RETORNO, jsonString).apply();
    }

    public Retorno getRetorno() throws IOException {
        String retornoString = sharedPreferences.getString(SP_RETORNO, "{\"codigo\":111}");
        Retorno retorno = JsonUtils.fromJson(retornoString, Retorno.class);
        return retorno;
    }

    public void setUsuarioApplication(Usuario usuario) throws JsonProcessingException {
        String usuarioString = JsonUtils.toJson(usuario);
        sharedPreferences.edit().putString(SP_USUARIO, usuarioString).apply();
    }

    public Usuario getUsuario() throws IOException {
        String usuarioString = sharedPreferences.getString(SP_USUARIO, "{\"usuario\" :null, \"senha\" :null}");
        Usuario usuario = JsonUtils.fromJson(usuarioString, Usuario.class);
        return usuario;
    }

    public void setUsuarioCelular(Interlocutor usuarioCelular) throws JsonProcessingException {
        String interlocutorString = JsonUtils.toJson(usuarioCelular);
        sharedPreferences.edit().putString(SP_INTERLOCUTOR_REMETENTE, interlocutorString).apply();
    }

    public Interlocutor getUsuarioCelular() throws IOException {
        String interlocutorString = sharedPreferences.getString(SP_INTERLOCUTOR_REMETENTE, "{\"usuario_nucleo\":null}");
        Interlocutor usuarioCelular = JsonUtils.fromJson(interlocutorString, Interlocutor.class);
        return usuarioCelular;
    }

    public void setDestinoContato(Interlocutor interlocutor) throws JsonProcessingException {
        String interlocutorString = JsonUtils.toJson(interlocutor);
        sharedPreferences.edit().putString(SP_INTERLOCUTOR_DESTINATARIO, interlocutorString).apply();
    }

    public Interlocutor getDestinoContato() throws IOException {
        String interlocutorString = sharedPreferences.getString(SP_INTERLOCUTOR_DESTINATARIO, "{\"usuario_nucleo\":null}");
        sharedPreferences.edit().remove(SP_INTERLOCUTOR_DESTINATARIO).apply();
        Interlocutor interlocutor = JsonUtils.fromJson(interlocutorString, Interlocutor.class);
        return interlocutor;
    }

    public void setMensagemInterlocutor(Mensagem mensagem) throws JsonProcessingException {
        String mensagemString = JsonUtils.toJson(mensagem);
        sharedPreferences.edit().putString(SP_MENSAGEM_INTERLOCUTOR, mensagemString).apply();
    }

    public void setIdMensagem(int idMensagem) throws JsonProcessingException {
        sharedPreferences.edit().putInt(SP_ID_MENSAGEM_INTERLOCUTOR, idMensagem).apply();
    }

    public int getIdMensagem() throws IOException {
        return sharedPreferences.getInt(SP_ID_MENSAGEM_INTERLOCUTOR, 0);
    }

    public Mensagem getMensagemInterlocutor() throws IOException {
        String mensagemString = sharedPreferences.getString(SP_MENSAGEM_INTERLOCUTOR, "{\"codigo\":111}");
        Mensagem mensagem = JsonUtils.fromJson(mensagemString, Mensagem.class);
        return mensagem;
    }

    public static String TAG = "conexoes";


    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        boolean connection = false;
        if (activeInfo != null && activeInfo.isConnected()) {
            boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Log.i(TAG, "WIFI connected");
                //stsRede.setText("Conectado no WiFi");
                connection = true;
            } else if (mobileConnected) {
                Log.i(TAG, "Mobile Connected");
                //stsRede.setText("Conectado no 3G");
                connection = true;
            }
        } else {
            Log.i(TAG, "Neither Mobile nor WIFi connected.");
            connection = false;

            //stsRede.setText("Sem conexão a internet");
        }
        return connection;
    }
}
