package grupocriar.ntalk.controllers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.grupocriar.ntalk.json.JsonUtils;
import br.com.grupocriar.ntalk.model.Envio;
import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import br.com.grupocriar.ntalk.model.MensagemInteracao;
import br.com.grupocriar.ntalk.model.Retorno;
import br.com.grupocriar.ntalk.model.Token;
import br.com.grupocriar.ntalk.model.Usuario;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;
import grupocriar.ntalk.activity.ConversasActivity;

import static grupocriar.ntalk.activity.ConversasActivity.TAG;

/**
 * Created by francisco on 19/01/2018.
 */

public class ControllerMensagens {

    public static String PENDING_INTENT_MENSAGEM = "pending.intent.mensagem";

    private static String TAG_ENVIO_SERVIDOR = "tag.envio.servidor";
    public static String TAG_RETORNO_SERVIDOR = "tag.retorno.servidor";

    private static final String URL_MENSAGEM = "/mensagem/post_mensagem";
    private static final String URL_GET_MENSAGEM = "/mensagem/get_mensagem/";
    private static final String URL_POST_MENSAGENS_INTERACOES = "/mensagem/post_mensagem_interacao/";

    private Interlocutor interlocutorRemetente;
    private Context context;
    private Usuario usuario;
    private ConnectLogin connectLogin;
    private Token token;

    public ControllerMensagens(Context context, Interlocutor interlocutorRemetente) throws IOException {
        this.context = context;
        this.interlocutorRemetente = interlocutorRemetente;

        usuario = LocalApplication.getInstance().getUsuario();//****_VEM DE FORA PRECISA TOMAR CUIDADO COM ISSO_****
        if (usuario == null) {
            Log.i("TAG_VERIFICACAO", "Usuario nao esta sendo carregado....!!!!");
        }
        connectLogin = new ConnectLogin(usuario);
        token = connectLogin.getToken().getObjeto();
    }


    /**
     * @param envioMensagem
     * @throws IOException
     */
    public String sendMensagem(Envio<Mensagem> envioMensagem) throws IOException {
        Retorno<List<Mensagem>> retornoServidor = null;
        try {
            ConnectRequests connector = new ConnectRequests();
            String json = connector.post(envioMensagem, URL_MENSAGEM, token.getToken());
            Log.i(TAG_RETORNO_SERVIDOR, "CODIGO DE RETORNO: " + connector.getResponseCode());

            if (connector.getResponseCode() == 200) {
                Log.i(TAG_RETORNO_SERVIDOR, json);
                retornoServidor = JsonUtils.fromJson(json, new TypeReference<Retorno<List<Mensagem>>>() {
                });
            } else {
                token = connectLogin.getToken().getObjeto();
                Log.i(TAG_ENVIO_SERVIDOR, "Reenviando mensagem para o servidor...");
                connectLogin = new ConnectLogin(usuario);
                connectLogin.getToken();
                json = connector.post(envioMensagem, URL_MENSAGEM, connectLogin.getToken().getObjeto().getToken());
                retornoServidor = JsonUtils.fromJson(json, new TypeReference<Retorno<List<Mensagem>>>() {
                });
                Log.i(TAG_ENVIO_SERVIDOR, "Mensagem reenvianda com sucesso...");
            }
            Log.i(TAG_RETORNO_SERVIDOR, "Codigo:" + retornoServidor.getCodigo() + " MENSAGEM: " + retornoServidor.getObjeto());
            //retorno se deu certo o envio
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (retornoServidor != null) {
            return retornoServidor.getCodigo();
        } else {
            return "111";
        }
    }

    /**
     * @return
     * @throws IOException
     */
    public Retorno<List<Mensagem>> getMensagemServidor() throws IOException {
        Log.i(TAG_ENVIO_SERVIDOR, "Enviando requisao para para ler mensagens no servidor...");

        Retorno<List<Mensagem>> retorno;
        ConnectRequests connector = new ConnectRequests();
        String jsonGetMensagem = connector.get(URL_GET_MENSAGEM + interlocutorRemetente.getIdInterlocutor(), token.getToken());

        Log.i(TAG_RETORNO_SERVIDOR, "CODIGO DE RETORNO: " + connector.getResponseCode());

        if (connector.getResponseCode() == 200) {
            retorno = JsonUtils.fromJson(jsonGetMensagem, new TypeReference<Retorno<List<Mensagem>>>() {
            });
        } else {
            token = connectLogin.getToken().getObjeto();
            Log.i(TAG_RETORNO_SERVIDOR, "Reenviando requisao para para ler mensagens no servidor...");
            usuario = LocalApplication.getInstance().getUsuario();
            String strToken = connectLogin.getToken().getObjeto().getToken();
            jsonGetMensagem =
                    connector.get(URL_GET_MENSAGEM + interlocutorRemetente.getIdInterlocutor(), strToken);
            retorno = JsonUtils.fromJson(jsonGetMensagem, new TypeReference<Retorno<List<Mensagem>>>() {
            });
            Log.i(TAG_ENVIO_SERVIDOR, "Mensagem interacao reenvianda com sucesso...");
        }
        ArrayList<MensagemInteracao> mensagensRecebidas = fazInteracaoMensagem(retorno);
        enviaMensagemInteracao(token, connector, mensagensRecebidas);
        return retorno;
    }

    @NonNull
    private ArrayList<MensagemInteracao> fazInteracaoMensagem(Retorno<List<Mensagem>> retorno) {
        ArrayList<MensagemInteracao> mensagensRecebidas = new ArrayList<>();
        for (Mensagem msg : retorno.getObjeto()) {
            List<MensagemInteracao> mensagensInteracoes = msg.getMensagensInteracoes();
            if (null == mensagensInteracoes) {
                continue;
            }
            for (MensagemInteracao mensagemInteracao : mensagensInteracoes) {
                mensagemInteracao.setRecebido(true);
                mensagensRecebidas.add(mensagemInteracao);
            }
        }
        return mensagensRecebidas;
    }

    private void enviaMensagemInteracao(Token token, ConnectRequests connector, ArrayList<MensagemInteracao> mensagensRecebidas) {
        try { //aqui esta enviando a interacao da mensagens recebidas
            if (!mensagensRecebidas.isEmpty()) {
                String retornoInteracoes = connector.post(new Envio<>(mensagensRecebidas), URL_POST_MENSAGENS_INTERACOES, token.getToken());
                Log.i(TAG, retornoInteracoes);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void enviaNotificacao(Retorno<List<Mensagem>> retorno) throws JsonProcessingException {
        List<String> listMensagem = new ArrayList<>();
        if ("000".equals(retorno.getCodigo())) {
            for (Mensagem mensagem : retorno.getObjeto()) {
                listMensagem.add(mensagem.getConteudo());
                LocalApplication.getInstance().setMensagemInterlocutor(mensagem);
                if (ConversasActivity.telaAtivaMensagensActivity) {//true
                    return;
                } else {
                    new Thread(() -> {
                        try {
                            criarNotificacao(mensagem);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }


    @SuppressLint("WrongConstant")
    private void criarNotificacao(Mensagem mensagemInterlocutor) throws PendingIntent.CanceledException {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//verificando se a versao do android e Oreo ou maior
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder = builder
                .setSmallIcon(R.drawable.ic_n)
                .setTicker("NTalk Criar")
                .setContentTitle(mensagemInterlocutor.getRemetente().getNome())
                .setContentText(mensagemInterlocutor.getConteudo())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        //Montando uma Intent.
        Intent resultIntent = new Intent(context.getApplicationContext(), ConversasActivity.class);
        resultIntent.putExtra(PENDING_INTENT_MENSAGEM, mensagemInterlocutor);
        //
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ConversasActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        //Intent Pendente
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);


        Notification notification = builder.build();
        notification.vibrate = new long[]{150, 300, 150, 600};
        notificationManager.notify(R.drawable.ic_n, notification);

        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone toque = RingtoneManager.getRingtone(context.getApplicationContext(), som);
        toque.play();
    }
}
