package grupocriar.ntalk.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;

import br.com.grupocriar.swapandroid.cvm.AsyncCVM;
import br.com.grupocriar.swapandroid.sistema.RuntimeAndroid;
import codigos.cvm.objetos.request.ObVerificaVersaoXML;

import grupocriar.ntalk.R;
import grupocriar.ntalk.main.MainActivity;


/**
 * Created by francisco on 10/23/17.
 */

public class VersaoActivity extends AppCompatActivity implements AsyncCVM.ListenerAsyncCVM {

    public static final String IT_PARAM_OBVERIFICAVERSAO = "ob_verifica_versao";

    private ObVerificaVersaoXML obVerificaVersao;
    private AsyncCVM asyncCVM;
    private TextView tvStatus;
    private String msg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versao_c);
        obVerificaVersao = (ObVerificaVersaoXML) getIntent()
                .getSerializableExtra(IT_PARAM_OBVERIFICAVERSAO);

        tvStatus = findViewById(R.id.tv_status);

        if (savedInstanceState == null) {
            asyncCVM = new AsyncCVM(obVerificaVersao, this, this);
            asyncCVM.execute();
        } else {
            asyncCVM = savedInstanceState.getParcelable("async");
            asyncCVM.setListener(this);
            asyncCVM.setObVerificaVersao(obVerificaVersao);
            msg = savedInstanceState.getString("msg");
            if (msg != null && !msg.isEmpty()) {
                showDialog();
            }
        }



        ShimmerFrameLayout container =
                (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        container.startShimmerAnimation();


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        super.onSaveInstanceState(outState);
        outState.putParcelable("async", asyncCVM);
        outState.putString("msg", msg);
    }

    @Override
    public void updateUI(int status, String... args) {
        StringBuilder msg = new StringBuilder("<b>" +
                asyncCVM.getObVerificaVersao().getChavePrograma().toUpperCase()
                + "</b> - VERSÃO: "
                + asyncCVM.getObVerificaVersao().getVersao() + "<br/> ");
        switch (status) {
            case AsyncCVM.ListenerAsyncCVM.UPDATE_VERIFICANDO:
                msg.append(getString(R.string.verificando_versao));
                break;
            case AsyncCVM.ListenerAsyncCVM.UPDATE_ATUALIZADA:
                msg.append(getString(R.string.versao_atualizada));
                break;
            case AsyncCVM.ListenerAsyncCVM.UPDATE_DESATUALIZADA:
                msg.append(getString(R.string.versao_desatualizada));
                break;
            case AsyncCVM.ListenerAsyncCVM.UPDATE_DOWNLOAD:
                for (String s : args) {
                    msg.append(s + "<br/>");
                }
                break;
            case AsyncCVM.ListenerAsyncCVM.UPDATE_NAO_ENCONTRADO:
                msg.append(getString(R.string.versao_nao_encontrada));
                break;
            case AsyncCVM.ListenerAsyncCVM.UPDATE_INSTALANDO:
                msg.append(getString(R.string.instalando));
                break;
        }
        tvStatus.setTextSize(15);
        tvStatus.setTextColor(Color.parseColor("#FFFFFF"));
        tvStatus.setText(Html.fromHtml(msg.toString()));
    }

    private void continuar(int result) {
        setResult(result);
        finish();
    }

    @Override
    public void postExecute(int result) {
        if (result == AsyncCVM.ListenerAsyncCVM.RESULT_ALERT_GERAL) {
            msg = getString(R.string.msg_cvm_geral, getString(R.string.telefone_suporte));
            showDialog();
            return;
        } else if (result == AsyncCVM.ListenerAsyncCVM.RESULT_ALERT_CONEXAO) {
            msg = getString(R.string.msg_cvm_conexao, getString(R.string.telefone_suporte));
            showDialog();
            return;
        } else if (result == AsyncCVM.ListenerAsyncCVM.RESULT_OK) {
            Log.i(getClass().getName(), "Verificou versão " + asyncCVM.getObVerificaVersao().getChavePrograma());
        }
        continuar(result);
    }

    private void showDialog() {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(getString(R.string.alerta))
                .setMessage(msg).setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        continuar(AsyncCVM.ListenerAsyncCVM.RESULT_OK);
                    }
                }).setNegativeButton(R.string.tentar_novamente, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RuntimeAndroid.backToMain(getBaseContext(), null, MainActivity.class);
                    }
                }).create();
        dlg.show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }


}
