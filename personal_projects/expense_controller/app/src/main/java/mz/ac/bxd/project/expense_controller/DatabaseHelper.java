package mz.ac.bxd.project.expense_controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expenseBD.db";
    private static final int DATABASE_VERSION = 1;

    //Para os do Castro principal
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUM_NOME = "nome";
    private static final String COLUM_MAIL = "mail";
    private static final String COLUM_SEXO = "sexo";
    private static final String COLUN_PASS= "pass";

    //Para os cadastrados denttro
    private static final String TABLE_NAME2 = "customers";
    private static final String COLUM_NOME2 = "nome";
    private static final String COLUM_APELIDO2 = "apelido";
    private static final String COLUM_ENDERECO2 = "endereco";
    private static final String COLUM_SEXO2 = "sexo";
    private static final String COLUM_PERSONAL2 = "personal";
    private static final String COLUM_DATA2 = "data";
    private static final String COLUM_NUMBER2 = "numero";
    private static final String COLUM_PESO2 = "peso";
    private static final String COLUM_IDADE2 = "idade";
    private static final String COLUM_VALIDADE2 = "validade";
    private static final String COLUM_DESCONTO2 = "desconto";
    private static final String COLUM_TOTAL2 = "total";

    private Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        //Criacao da primeira tabela
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                COLUM_NOME + " TEXT ," +
                COLUM_MAIL + " TEXT ," +
                COLUM_SEXO + " TEXT ," +
                COLUN_PASS + " TEXT " +
                ");";

        //Criacao da segunda tabela

        String query2 = "CREATE TABLE " + TABLE_NAME2 + " (" +
                COLUM_NOME2 + " TEXT ," +
                COLUM_APELIDO2 + " TEXT , " +
                COLUM_SEXO2+ " TEXT , " +
                COLUM_IDADE2 + " INTEGER ," +
                COLUM_PESO2 + " DOUBLE ," +
                COLUM_NUMBER2 + " INTEGER PRIMARY KEY , " +
                COLUM_ENDERECO2 + " TEXT ," +
                COLUM_DATA2 + " TEXT ," +
                COLUM_VALIDADE2 + " TEXT ," +
                COLUM_PERSONAL2 + " TEXT ," +
                COLUM_TOTAL2 + " DOUBLE " +
                ");";

        DB.execSQL(query);
      //  DB.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists " + TABLE_NAME);
      //  DB.execSQL("drop Table if exists " + TABLE_NAME2);
        this.onCreate(DB);
    }



    public void addCustomers(String nome, String apelido, String sexo, int idade, double peso, int numero,
                             String endereco, String data, String validade,String personal, int total) {

        SQLiteDatabase db = this.getWritableDatabase();

        if (db != null) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUM_NOME2, nome);
            contentValues.put(COLUM_APELIDO2, apelido);
            contentValues.put(COLUM_SEXO2, sexo);
            contentValues.put(COLUM_IDADE2, idade);
            contentValues.put(COLUM_PESO2, peso);
            contentValues.put(COLUM_NUMBER2, numero);
            contentValues.put(COLUM_ENDERECO2, endereco);
            contentValues.put(COLUM_DATA2, data);
            contentValues.put(COLUM_VALIDADE2, validade);
            contentValues.put(COLUM_PERSONAL2, personal);
            contentValues.put(COLUM_TOTAL2, total);

            long result = db.insert(TABLE_NAME2, null, contentValues);

            if(getNumber(numero) == true){

                    db.insert(TABLE_NAME2, null, contentValues);
                    if (result == -1)
                        Toast.makeText(context, "Cliente não adiconado", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Cliente adicionado com sucesso", Toast.LENGTH_LONG).show();

            }else {
                    Toast.makeText(context, "Numero pertence a outra pessoa a outro usuario", Toast.LENGTH_LONG).show();
                    return;
                 }
        }else
            Toast.makeText(context, "SQLiteDatabase is null", Toast.LENGTH_LONG).show();

        db.close();
    }

    public void addUsers(String nome, String mail, String sexo, String pass) {

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUM_NOME, nome);
        contentValues.put(COLUM_MAIL, mail);
        contentValues.put(COLUM_SEXO, sexo);
        contentValues.put(COLUN_PASS, pass);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Usuario não adiconado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Usuario adicionado com sucesso", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    public boolean getNumber(Integer number){

        SQLiteOpenHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery("select * from "+TABLE_NAME2+" where numero = ?", new String[]{String.valueOf(number)});
            if(cursor.getCount()> 0)
                return true;
        }

        return false;


    }



    public boolean getCustomer(String mail, String password){

        SQLiteOpenHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = null;
        if (db != null) {
             cursor = db.rawQuery("select * from "+TABLE_NAME+" where mail = ? and pass = ?", new String[]{mail, password});
            if(cursor.getCount()> 0)
                return true;
        }

        return false;


    }

    //Buscar todos os dados na base de dados "customers" para ir depositar no recicleView
   /** public ArrayList<Customer> getCustumersData() {
        ArrayList<Customer> lista = new ArrayList<>();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_NAME2;
        SQLiteDatabase DB = this.getReadableDatabase();

        if (DB != null) {
            cursor = DB.rawQuery(query, null);

            while (cursor.moveToNext()) {

                    Customer customer = new Customer(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getDouble(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10)
                );

                if (lista.size() < cursor.getCount())
                    lista.add(customer);
            }
        }

        return lista;
    }**/

    public boolean deleteCustomer(String name){

        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_NAME2,  COLUM_NOME2+ "=?", new String[]{name}) > 0;
    }

}
