package grupocriar.ntalk.persistence.dao;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by francisco on 11/28/17.
 */

public interface DAO<T> {

    boolean insert(T  t);

    List<T> select(int id) throws IOException;

    T selectUnit(int id);

    Set<T> selectAll();

    boolean delet(int id);


}
