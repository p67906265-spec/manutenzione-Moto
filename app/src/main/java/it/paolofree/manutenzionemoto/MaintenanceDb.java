package it.paolofree.manutenzionemoto;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class MaintenanceDb extends SQLiteOpenHelper {
    public static class Entry {
        long id; String part, who, date; int km; double cost;
        Entry(long id, String part, int km, double cost, String who, String date) { this.id=id; this.part=part; this.km=km; this.cost=cost; this.who=who; this.date=date; }
    }
    public static class Deadline { long id; String title; int targetKm; boolean notified; Deadline(long i,String t,int k,boolean n){id=i;title=t;targetKm=k;notified=n;} }
    MaintenanceDb(Context c) { super(c, "maintenance.db", null, 2); }
    public void onCreate(SQLiteDatabase db) { db.execSQL("CREATE TABLE entries(id INTEGER PRIMARY KEY AUTOINCREMENT,part TEXT NOT NULL,km INTEGER NOT NULL,cost REAL NOT NULL,who TEXT NOT NULL,date TEXT NOT NULL)"); db.execSQL("CREATE TABLE deadlines(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,target_km INTEGER NOT NULL,notified INTEGER NOT NULL DEFAULT 0)"); }
    public void onUpgrade(SQLiteDatabase db,int oldV,int newV) { if(oldV<2) db.execSQL("CREATE TABLE deadlines(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,target_km INTEGER NOT NULL,notified INTEGER NOT NULL DEFAULT 0)"); }
    long save(Long id,String part,int km,double cost,String who,String date) {
        ContentValues v=new ContentValues(); v.put("part",part);v.put("km",km);v.put("cost",cost);v.put("who",who);v.put("date",date);
        if(id==null) return getWritableDatabase().insert("entries",null,v);
        getWritableDatabase().update("entries",v,"id=?",new String[]{String.valueOf(id)}); return id;
    }
    void delete(long id){ getWritableDatabase().delete("entries","id=?",new String[]{String.valueOf(id)}); }
    List<Entry> all(String who){
        List<Entry> out=new ArrayList<>(); String sel=who==null?null:"who=?"; String[] args=who==null?null:new String[]{who};
        try(Cursor c=getReadableDatabase().query("entries",null,sel,args,null,null,"km DESC,id DESC")){
            while(c.moveToNext()) out.add(new Entry(c.getLong(0),c.getString(1),c.getInt(2),c.getDouble(3),c.getString(4),c.getString(5)));
        } return out;
    }
    long saveDeadline(Long id,String title,int target){ ContentValues v=new ContentValues();v.put("title",title);v.put("target_km",target);v.put("notified",0);if(id==null)return getWritableDatabase().insert("deadlines",null,v);getWritableDatabase().update("deadlines",v,"id=?",new String[]{String.valueOf(id)});return id; }
    void deleteDeadline(long id){getWritableDatabase().delete("deadlines","id=?",new String[]{String.valueOf(id)});}
    void markNotified(long id){ContentValues v=new ContentValues();v.put("notified",1);getWritableDatabase().update("deadlines",v,"id=?",new String[]{String.valueOf(id)});}
    List<Deadline> deadlines(){List<Deadline> out=new ArrayList<>();try(Cursor c=getReadableDatabase().query("deadlines",null,null,null,null,null,"target_km ASC")){while(c.moveToNext())out.add(new Deadline(c.getLong(0),c.getString(1),c.getInt(2),c.getInt(3)==1));}return out;}
}
