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
    MaintenanceDb(Context c) { super(c, "maintenance.db", null, 1); }
    public void onCreate(SQLiteDatabase db) { db.execSQL("CREATE TABLE entries(id INTEGER PRIMARY KEY AUTOINCREMENT,part TEXT NOT NULL,km INTEGER NOT NULL,cost REAL NOT NULL,who TEXT NOT NULL,date TEXT NOT NULL)"); }
    public void onUpgrade(SQLiteDatabase db,int oldV,int newV) {}
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
}
