package grupocriar.ntalk.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Usuario;

/**
 * Created by francisco on 03/01/2018.
 */

public class GerenciarUsuarioCelular {


    public static Interlocutor getUsuarioCelular(List<Interlocutor> myDatasetInterlocutores) throws IOException {
        final Usuario usuario = LocalApplication.getInstance().getUsuario();
        Iterator<Interlocutor> it = myDatasetInterlocutores.iterator();
        while (it.hasNext()) {
            Interlocutor interlocutor = it.next();
            if (interlocutor.getUsuarioNucleo().contains(usuario.getUsuario())) {
                it.remove(); //retiro da lista o usuario do celular
                return interlocutor;
            }
        }
        return null;
    }
}
