package grupocriar.ntalk.persistence;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by francisco on 11/27/17.
 */

public class DataBase {

    private DataBaseManager dataBaseManager;
    private SQLiteDatabase sqLiteDatabase;

    public DataBase(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    public void open() {
        sqLiteDatabase = dataBaseManager.getWritableDatabase();
    }

    public void openRead() {
        sqLiteDatabase = dataBaseManager.getReadableDatabase();
    }

    public SQLiteDatabase get() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            return sqLiteDatabase;
        }
        throw new IllegalArgumentException();
    }

    public void close() {
        dataBaseManager.close();
    }

    public void closeDatabase() {
        sqLiteDatabase.close();
    }
}
