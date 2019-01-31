package grupocriar.ntalk.utils;

import android.widget.Adapter;
import android.widget.ListView;

/**
 * Created by francisco on 04/01/2018.
 */
public class AndroidLoggerUtils {

    public static void sout(Adapter adapter) {
        StringBuilder sb = new StringBuilder("Adapter [");
        int i;
        for (i = 0; i < adapter.getCount() - 1; ++i) {
            sb.append(adapter.getItem(i)).append(", ");
        }

        if (adapter.getCount() > 0) {
            sb.append(adapter.getItem(i));
        }
        sb.append("]");

        System.out.println(sb.toString());
    }

    public static void sout(ListView list) {
        StringBuilder sb = new StringBuilder("ListView [");
        int i;
        for (i = 0; i < list.getCount() - 1; ++i) {
            sb.append(list.getItemAtPosition(i)).append(", ");
        }

        if (list.getCount() > 0) {
            sb.append(list.getItemAtPosition(i));
        }
        sb.append("]");

        System.out.println(sb.toString());
    }

    private AndroidLoggerUtils() {
        throw new IllegalArgumentException("No AndroidLoggerUtils!");
    }
}
