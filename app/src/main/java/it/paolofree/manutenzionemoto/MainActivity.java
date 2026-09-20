package it.paolofree.manutenzionemoto;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    MaintenanceDb db; List<MaintenanceDb.Entry> entries=new ArrayList<>(); ListView list; TextView empty,count,total; String filter=null;
    final NumberFormat euro=NumberFormat.getCurrencyInstance(Locale.ITALY);

    @Override public void onCreate(Bundle b){ super.onCreate(b); setContentView(R.layout.activity_main); getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        db=new MaintenanceDb(this); list=findViewById(R.id.list);empty=findViewById(R.id.emptyText);count=findViewById(R.id.countText);total=findViewById(R.id.totalText);
        findViewById(R.id.addButton).setOnClickListener(v->edit(null));
        ((RadioGroup)findViewById(R.id.filterGroup)).setOnCheckedChangeListener((g,id)->{filter=id==R.id.filterShop?"Officina":id==R.id.filterMe?"Io":null; refresh();});
        list.setOnItemClickListener((p,v,pos,id)->edit(entries.get(pos)));
        list.setOnItemLongClickListener((p,v,pos,id)->{ confirmDelete(entries.get(pos)); return true; }); refresh();
    }
    void refresh(){ entries=db.all(filter); double sum=0; for(MaintenanceDb.Entry e:entries)sum+=e.cost; count.setText(String.valueOf(entries.size()));total.setText(euro.format(sum));
        boolean none=entries.isEmpty(); empty.setVisibility(none?View.VISIBLE:View.GONE);list.setVisibility(none?View.GONE:View.VISIBLE);list.setAdapter(new EntryAdapter()); }
    TextView label(String s){ TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.rgb(156,179,195));t.setPadding(0,12,0,3);return t; }
    EditText input(String hint,int type){ EditText e=new EditText(this);e.setHint(hint);e.setTextColor(Color.WHITE);e.setHintTextColor(Color.rgb(120,145,160));e.setInputType(type);e.setBackgroundResource(R.drawable.bg_input);return e; }
    void edit(MaintenanceDb.Entry old){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(45,5,45,0);
        EditText part=input("es. Olio e filtro",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText km=input("es. 25000",InputType.TYPE_CLASS_NUMBER); EditText cost=input("es. 89,50",InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        RadioGroup who=new RadioGroup(this);who.setOrientation(RadioGroup.HORIZONTAL); RadioButton shop=new RadioButton(this);shop.setText("Officina");shop.setTextColor(Color.WHITE); RadioButton me=new RadioButton(this);me.setText("Io");me.setTextColor(Color.WHITE);who.addView(shop);who.addView(me);shop.setChecked(true);
        box.addView(label("PEZZO / INTERVENTO"));box.addView(part);box.addView(label("CHILOMETRI"));box.addView(km);box.addView(label("COSTO (€)"));box.addView(cost);box.addView(label("ESEGUITO DA"));box.addView(who);
        if(old!=null){part.setText(old.part);km.setText(String.valueOf(old.km));cost.setText(String.format(Locale.ITALY,"%.2f",old.cost));(old.who.equals("Io")?me:shop).setChecked(true);}
        AlertDialog d=new AlertDialog.Builder(this).setTitle(old==null?"Nuovo intervento":"Modifica intervento").setView(box).setNegativeButton("ANNULLA",null).setPositiveButton("SALVA",null).create();
        d.setOnShowListener(x->d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            String p=part.getText().toString().trim(),ks=km.getText().toString().trim(),cs=cost.getText().toString().trim().replace(',','.');
            if(p.isEmpty()){part.setError("Inserisci il pezzo o intervento");return;} if(ks.isEmpty()){km.setError("Inserisci i km");return;} if(cs.isEmpty()){cost.setError("Inserisci il costo");return;}
            try{int kval=Integer.parseInt(ks);double cval=Double.parseDouble(cs);String w=me.isChecked()?"Io":"Officina";String date=old==null?new SimpleDateFormat("dd/MM/yyyy",Locale.ITALY).format(new Date()):old.date;db.save(old==null?null:old.id,p,kval,cval,w,date);d.dismiss();refresh();}catch(Exception ex){cost.setError("Valore non valido");}
        }));d.show();
    }
    void confirmDelete(MaintenanceDb.Entry e){new AlertDialog.Builder(this).setTitle("Eliminare?").setMessage(e.part+" verrà cancellato.").setNegativeButton("ANNULLA",null).setPositiveButton("ELIMINA",(d,w)->{db.delete(e.id);refresh();}).show();}
    class EntryAdapter extends BaseAdapter{
        public int getCount(){return entries.size();}public Object getItem(int p){return entries.get(p);}public long getItemId(int p){return entries.get(p).id;}
        public View getView(int p,View v,android.view.ViewGroup parent){if(v==null)v=getLayoutInflater().inflate(R.layout.item_maintenance,parent,false);MaintenanceDb.Entry e=entries.get(p);((TextView)v.findViewById(R.id.itemPart)).setText(e.part);((TextView)v.findViewById(R.id.itemCost)).setText(euro.format(e.cost));((TextView)v.findViewById(R.id.itemInfo)).setText(String.format(Locale.ITALY,"%,d km  •  %s  •  %s",e.km,e.who,e.date));return v;}
    }
}
