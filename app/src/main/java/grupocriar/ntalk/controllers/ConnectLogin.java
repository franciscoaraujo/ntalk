package grupocriar.ntalk.controllers;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

import br.com.grupocriar.ntalk.model.Envio;
import br.com.grupocriar.ntalk.model.Retorno;
import br.com.grupocriar.ntalk.model.Token;
import br.com.grupocriar.ntalk.model.Usuario;
import br.com.grupocriar.ntalk.utils.JsonUtils;
import grupocriar.ntalk.utils.LocalApplication;

/**
 * Created by francisco on 18/01/2018.
 */

public class ConnectLogin {

    private final static String URL_TOKEN = "/autenticacao/post_autenticacao/";

    private Usuario usuario;
    private Retorno<Token> retorno;

    public ConnectLogin(Usuario usuario) {
        this.usuario = usuario;
    }

    public Retorno<Token> getToken() throws IOException {
        Log.i("TAG_LOGIN", "Executando o login no sistema....");

        ConnectRequests connector = new ConnectRequests();
        String json = connector.post(new Envio<>(usuario), URL_TOKEN, "");
        retorno = JsonUtils.fromJson(json, new TypeReference<Retorno<Token>>() {
        });
        LocalApplication.getInstance().setRetorno(retorno);
        Log.i("TAG_LOGIN", "Retorno do login no sistema.....: " + retorno);

        return retorno;
    }
}
