package com.lepu.ecg500.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

//import com.lib.common.BaseApplication;
//import com.lib.common.util.log.KLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.database.Cursor.FIELD_TYPE_BLOB;

/**
 * @author wxd
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    public static final String DATABASE_NAME_MAIN = "main.db";
    //public static final int DATABASE_VERSION_MAIN = 1;
    private static Context mContext = null;
    public static Object mLockObj = new Object();
    private static boolean mainTmpDirSet = false;
    private static DBHelper mInstance = null;

    //private String mDbName = "";

    public static DBHelper getInstance(String dbName, int dbVersion) {
        //KLog.d("DBHelper getInstance");
        if (mContext == null) {
            //mContext = BaseApplication.getInstance();
            mContext = CustomTool.application;
        }

        if(mInstance == null){
            mInstance = new DBHelper(dbName,dbVersion);
        }
        return mInstance;
    }

    private DBHelper(String dbName, int dbVersion) {
        super(mContext, dbName, null, dbVersion);
        //mDbName = dbName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       // KLog.d(TAG,"onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

            // 程序升级，更新为true
            //AppConfig.setConfig(mContext, Const.CONFIG_APP_UPGRADE_USE, true);

            for (int i = (oldVersion + 1); i <= newVersion; i++) {
                upgrade(db, i);
            }
        }
    }

    /**
     * 数据库降级了
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * 执行数据库的降级操作
         * 1、只有新版本比旧版本低的时候才会执行
         * 2、如果不执行降级操作，会抛出异常
         */
        super.onDowngrade(db, oldVersion, newVersion);
    }

//    @Override
//    public SQLiteDatabase getReadableDatabase() {
//        if (!mainTmpDirSet) {
//            String tmpPath = "/data/data/com.lepu.SmartEcg/databases/main";
//            boolean rs = new File(tmpPath).mkdir();
//            KLog.d("数据库临时目录创建");
//            String sql = String.format("PRAGMA temp_store_directory = '%s'",tmpPath);
//            super.getReadableDatabase().execSQL(sql);
//            mainTmpDirSet = true;
//            return super.getReadableDatabase();
//        }
//        return super.getReadableDatabase();
//    }

    @Override
    public synchronized void close() {
       // KLog.d("DBHelper close");
        super.close();
    }

    /*
     * 升级程序时，修改了数据库，需要在以下用sql语句
     * 数据库升级，log有时打印不出来
     */
    private void upgrade(SQLiteDatabase db, int version) {
        //KLog.d(TAG,String.valueOf(version));
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public static void checkDatabase(Context context,String dbName,int dbVersion,
                                     int dbRawId,String dbPath) {
        mContext = context;
        DBHelper helper1 = getInstance(dbName,dbVersion);
        if (helper1 != null) {
            helper1.createDatabase(dbRawId,dbPath);
            helper1.close();
        }
    }

    /**
     * 创建数据库
     */
    private void createDatabase(int dbRawId,String dbPath) {
        File pathFile = new File(dbPath);
        if (!pathFile.exists()) {
            try {
                copyDB(dbRawId,dbPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void copyDB(int dbRawId,String dbPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = mContext.getResources().openRawResource(dbRawId);
            fos = new FileOutputStream(dbPath);
            byte[] buffer = new byte[512];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
        } catch (Exception e) {
           // KLog.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    // ==========================================================

    public boolean isTableExists(String tableName) {
        String sql = "select count(*) from sqlite_master where type='table' and name=?";
        String result = executeScalar(sql, new String[]{tableName});
        return !TextUtils.isEmpty(result)
                && Integer.parseInt(result) == 1;
    }

    public String executeScalar(String sql, String[] args) {
        ArrayList<HashMap<String, Object>> resultArray = query(sql, args);
        if (resultArray.size() > 0) {
            HashMap<String, Object> json = resultArray.get(0);
            Iterator<Object> it = json.values().iterator();
            while (it.hasNext()) {
                return (String) it.next();
            }
        }
        return "";
    }

    public synchronized boolean isColumeExists(String tableName,
                                               String columnName) {
        boolean result = false;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();

            // 查询一行
            db.beginTransaction();
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0",
                    null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            //KLog.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                //KLog.e(TAG, Log.getStackTraceString(e));
            }
            try {
                if(db != null && db.inTransaction()){
                    db.endTransaction();
                    //db.close();
                }
            } catch (Exception e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            }
        }

        return result;
    }

    /*
     * 查询
     */
    public ArrayList<HashMap<String, Object>> query(String sql,
                                                    String[] selectionArgs) {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                cursor = db.rawQuery(sql, selectionArgs);
                while (cursor.moveToNext()) {
                    int count = cursor.getColumnCount();
                    HashMap<String, Object> map = new HashMap<String, Object>(count);
                    for (int i = 0; i < count; i++) {
                        map.put(cursor.getColumnName(i),
                                getColumnValue(cursor, i));
                    }
                    result.add(map);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                //KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    //KLog.e(TAG, Log.getStackTraceString(e));
                }
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return result;
    }

    private Object getColumnValue(Cursor cursor, int index) {
        Object result = null;
        try {
            if(cursor.getType(index) == FIELD_TYPE_BLOB){
                result = cursor.getBlob(index);
            }else{
                result = cursor.getString(index);
            }
        } catch (Exception e) {
            //KLog.e(TAG, Log.getStackTraceString(e));
        }
        return result;
    }

    public boolean executeIsExists(String sql, String[] argsArray) {
        boolean exists = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                cursor = db.rawQuery(sql, argsArray);
                exists = cursor.moveToFirst();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                //KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return exists;
    }

    public boolean executeNonQuery(String sql, Object[] args) {
        SQLiteDatabase db = null;
        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                db.execSQL(sql, args);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                //KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return true;
    }

    public boolean executeNonQuery(String sql) {
        SQLiteDatabase db = null;
        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (Exception e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return true;
    }

    /**
     *
     * @param table
     * @param nullColumnHack
     * @param values
     * @return
     */
    public boolean insert(String table, String nullColumnHack,
                          ContentValues values) {
        SQLiteDatabase db = null;
        boolean flag = false;

        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                long count = db.insert(table, nullColumnHack, values);
                flag = count > 0;
                db.setTransactionSuccessful();
            } catch (Exception e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                  //  KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return flag;
    }

    /**
     * replace
     * @param table
     * @param nullColumnHack
     * @param values
     * @return
     */
    public boolean replace(String table, String nullColumnHack,
                          ContentValues values) {
        SQLiteDatabase db = null;
        boolean flag = false;

        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                long count = db.replace(table, nullColumnHack, values);
                flag = count > 0;
                db.setTransactionSuccessful();
            } catch (Exception e) {
                //KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return flag;
    }

    /*
     * delete
     */
    public boolean update(String table, ContentValues values,
                          String whereClause, String[] whereArgs) {
        SQLiteDatabase db = null;
        boolean flag = false;

        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                long count = db.update(table, values, whereClause, whereArgs);
                flag = count > 0;
                db.setTransactionSuccessful();
            } catch (Exception e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                   // KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return flag;
    }

    /*
     * delete
     */
    public boolean delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = null;
        boolean flag = false;

        synchronized (mLockObj) {
            try {
                db = this.getReadableDatabase();

                db.beginTransaction();
                long count = db.delete(table, whereClause, whereArgs);
                flag = count > 0;
                db.setTransactionSuccessful();
            } catch (Exception e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            } finally {
                try {
                    if(db != null && db.inTransaction()){
                        db.endTransaction();
                        //db.close();
                    }
                } catch (Exception e) {
                  //  KLog.e(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return flag;
    }

    /**
     * 拷贝自己的数据库
     * @param srcPath /data/data/com.lepu.SmartEcg/databases/main.db  类似目录
     * @param destPath /sdcard/NeoECG/Temp/main.db  类似目录
     * @return
     */
    public static boolean backupDb(String srcPath,String destPath){

        boolean flag = false;
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        if(srcFile.exists() && destFile.exists()){
            destFile.delete();
        }

        if(srcFile.exists()) {
            FileChannel outF;
            try {
                outF = new FileOutputStream(destFile).getChannel();
                new FileInputStream(srcFile).getChannel().transferTo(0, srcFile.length(), outF);
                flag = true;
            } catch (FileNotFoundException e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            } catch (IOException e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            }
        }
        return flag;
    }

    /**
     * 还原备份的数据库到系统目录下
     * @param srcPath /sdcard/NeoECG/Temp/main.db  类似目录
     * @param destPath /data/data/com.lepu.SmartEcg/databases/main.db  类似目录
     * @return
     */
    public static boolean restoreDb(String srcPath,String destPath){

        boolean flag = false;
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        if(srcFile.exists() && destFile.exists()){
            destFile.delete();
        }

        if(srcFile.exists()) {
            FileChannel outF;
            try {
                outF = new FileOutputStream(destFile).getChannel();
                new FileInputStream(srcFile).getChannel().transferTo(0, srcFile.length(), outF);
                flag = true;
            } catch (FileNotFoundException e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            } catch (IOException e) {
               // KLog.e(TAG, Log.getStackTraceString(e));
            }
        }
        return flag;
    }

}
