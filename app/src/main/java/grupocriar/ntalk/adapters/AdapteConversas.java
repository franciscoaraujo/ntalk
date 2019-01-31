package grupocriar.ntalk.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;

/**
 * Created by francisco on 15/01/2018.
 */

public class AdapteConversas extends ArrayAdapter<Mensagem> {

    private List<Mensagem> listMensagem;
    private Context context;

    public AdapteConversas(Context context, List<Mensagem> listMensagem) {
        super(context, 0, listMensagem);
        this.context = context;
        this.listMensagem = listMensagem;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if (listMensagem != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            Mensagem mensagem = listMensagem.get(position);
            Interlocutor usuarioCelular = null;
            try {
                usuarioCelular = LocalApplication.getInstance().getUsuarioCelular();
                if (mensagem.getRemetente().getIdInterlocutor() == usuarioCelular.getIdInterlocutor()) {
                    view = inflater.from(parent.getContext())
                            .inflate(R.layout.item_mensagem_direita, parent, false);
                } else {
                    view = inflater.from(parent.getContext())
                            .inflate(R.layout.item_mensagem_esquerda, parent, false);

                }
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String dataFormatada = sdf.format(mensagem.getDataHoraMensagem());
                TextView textoMensagem = view.findViewById(R.id.tv_mensagem);
                TextView textHora = view.findViewById(R.id.tv_hora);


                textoMensagem.setText(mensagem.getConteudo());
                textHora.setText(dataFormatada);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
