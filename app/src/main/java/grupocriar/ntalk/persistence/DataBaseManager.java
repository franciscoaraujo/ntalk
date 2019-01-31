package grupocriar.ntalk.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by francisco on 11/27/17.
 */

public abstract class DataBaseManager extends SQLiteOpenHelper {

    protected Context context;

    /***
     *
     * @param context
     * @param name
     * @param version
     */
    public DataBaseManager(Context context, String name, int version) {
        super(context, name, null, version);
        this.context = context;
    }

    /***
     *
     * @param db
     */
    public abstract void onCreate(SQLiteDatabase db);

    /**
     * @param db
     * @param versaoAtual
     * @param versaoNova
     */
    public abstract void onUpgrade(SQLiteDatabase db, int versaoAtual, int versaoNova);

    /**
     * @param fileID
     * @param db
     * @throws IOException
     */
    protected void byFile(int fileID, SQLiteDatabase db) throws IOException {
        StringBuilder sql = new StringBuilder();
        BufferedReader bfr = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(fileID)));
        String line;
        while ((line = bfr.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0) {
                sql.append(line);
                if (line.endsWith(";")) {
                    db.execSQL(sql.toString());
                    sql.delete(0, sql.length());
                }
            }
        }
    }
}
