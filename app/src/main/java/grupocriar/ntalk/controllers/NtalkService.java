package grupocriar.ntalk.controllers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import br.com.grupocriar.ntalk.model.Envio;
import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import br.com.grupocriar.ntalk.model.Retorno;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.UserDataBase;
import grupocriar.ntalk.persistence.dao.DAOMensagem;


/**
 * Created by francisco on 11/30/17.
 */

public class NtalkService extends Service {

    public static final String ACTION_MENSAGEM = "action.mensagem";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG_SERVICE", "Iniciando um service chamando o metodo -> onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WorkerReader wkR = new WorkerReader();
        wkR.start();

        WorkerWriter wkW = new WorkerWriter();
        wkW.start();
        return START_REDELIVER_INTENT;
    }

    class WorkerReader extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Interlocutor usuarioCelular = LocalApplication.getInstance().getUsuarioCelular();
                ControllerMensagens controllerMensagens;
                while (true) {
                    controllerMensagens = new ControllerMensagens(getApplicationContext(), usuarioCelular);
                    Retorno<List<Mensagem>> mensagemServidor = controllerMensagens.getMensagemServidor();

                    recebeNovaMensagem(mensagemServidor);

                    if (mensagemServidor.getObjeto().contains("000")) {
                        for (Mensagem m : mensagemServidor.getObjeto()) {
                            new DAOMensagem(new DataBase(new UserDataBase(getApplicationContext()))).insert(m);//**
                            controllerMensagens.enviaNotificacao(mensagemServidor);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void recebeNovaMensagem(Retorno<List<Mensagem>> mensagemServidor) {
        Intent it = new Intent("");
        LocalBroadcastManager.getInstance(this).sendBroadcast(it);
    }

    class WorkerWriter extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Log.i("TAG_SERVICE", "Enviando dados para o servidor...");

                Interlocutor usuarioCelular = LocalApplication.getInstance().getUsuarioCelular();
                ControllerMensagens controllerMensagens;
                while (true) {
                    int ultimoIdMensagem = LocalApplication.getInstance().getIdMensagem();
                    List<Mensagem> allMsgOf = new DAOMensagem(new DataBase(
                            new UserDataBase(getApplicationContext()))).select(ultimoIdMensagem);//**
                    if (!allMsgOf.isEmpty()) {
                        Log.i("TAG_SERVICE", " Enviando para o Servidor--> " + allMsgOf);


                        controllerMensagens = new ControllerMensagens(getApplicationContext(), usuarioCelular);
                        String retorno = controllerMensagens.sendMensagem(new Envio<>(allMsgOf));//**
                        if (retorno.contains("000")) {
                            LocalApplication.getInstance().setIdMensagem(allMsgOf.get(0).getIdMensagemLocal());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
