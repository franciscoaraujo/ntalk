package grupocriar.ntalk.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import codigos.cvm.objetos.request.ObVerificaVersaoXML;
import grupocriar.ntalk.BuildConfig;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;
import grupocriar.ntalk.activity.LoginActivity;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    /**
     * Sistema: Nucleo
     * Instancia: Grupo Criar
     * Programa: nTalk
     * Plataforma: Android
     * Ambiente: Testes
     * Chave: nucleo/criar/ntalk/android/teste
     * 1.0.0.0
     */
    private static final int PERMISSAO_STORAGE = 0;
    private static final int PERMISSAO_SEND_SMS = 0;
    private static final int PERMISSAO_CAMERA = 0;
    private static final int ACCESS_FINE_LOCATION = 0;
    private static final int CALL_PHONE = 0;
    //==
    private static final int REQUEST_VERSAO = 10;
    private static final int REQUEST_ATIVACAO = 20;
    //==
    private static final String CHAVE_SISTEMA = "nucleo";
    private static final String CHAVE_INSTANCIA = "criar";
    private static final String CHAVE_PROGRAMA = "ntalk";
    private static final String CHAVE_PLATAFORMA = "android";
    //==aqui vai a classe que devera ser chamada pôs a chamda da autalização
    private static final Class START_CLASS = LoginActivity.class;//<--
    public static final String CHAVE_AMBIENTE_TESTE = "teste";
    public static final String CHAVE_AMBIENTE_PRODUCAO = "producao";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            iniciaVerificacaoVersao();
        }
    }

    protected void iniciaVerificacaoVersao() {
//        LoadApplication.getInstance().setIdDispositivo(0);
        Intent it = new Intent(this, VersaoActivity.class);
        it.putExtra(VersaoActivity.IT_PARAM_OBVERIFICAVERSAO, getObVerificaVersao());
        startActivityForResult(it, REQUEST_VERSAO);
    }

    @Override//recupera o resultado da startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED &&
                (requestCode == REQUEST_VERSAO || requestCode == REQUEST_ATIVACAO)) {
            finish();
            return;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VERSAO) {
                Intent intent = new Intent(this, START_CLASS);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            finish();
        }
    }

    public ObVerificaVersaoXML getObVerificaVersao() {
        ObVerificaVersaoXML obVerificaVersao = new ObVerificaVersaoXML
                (CHAVE_SISTEMA, CHAVE_INSTANCIA, CHAVE_PROGRAMA, CHAVE_PLATAFORMA,
                        LocalApplication.TESTE ? CHAVE_AMBIENTE_TESTE : CHAVE_AMBIENTE_PRODUCAO,
                        BuildConfig.VERSION_NAME,
                        0);
        return obVerificaVersao;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String url = getString(R.string.url_ativacao);
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(it);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                finish();
                break;

            default:
                break;
        }
    }
}
