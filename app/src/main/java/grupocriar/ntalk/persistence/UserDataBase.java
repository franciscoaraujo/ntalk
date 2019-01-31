package grupocriar.ntalk.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

import grupocriar.ntalk.R;

/**
 * Created by francisco on 11/27/17.
 */

public class UserDataBase extends DataBaseManager {

    private static final String NAME = "user_database";
    private static final String TAG_LOG = "tag_user_database";
    private static final int VERSION = 15;

    public UserDataBase(Context context) {
        super(context, NAME, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAtual, int versaoNova) {
        Log.e(TAG_LOG, "V.atual" + versaoAtual);
        Log.e(TAG_LOG, "Nova V" + versaoNova);
        try {
            db.execSQL(context.getString(R.string.dropTbl1));
            db.execSQL(context.getString(R.string.dropTbl2));
            db.execSQL(context.getString(R.string.dropTbl3));
            db.execSQL(context.getString(R.string.dropTbl4));
        } catch (Exception e) {
            Log.e(TAG_LOG, "onUpgrade", e);
        }
    }

    private void createTable(SQLiteDatabase db) {
        try {
            byFile(R.raw.create_db, db);
        } catch (IOException e) {
            Log.e(TAG_LOG, "createTable", e);
        }
    }
}
