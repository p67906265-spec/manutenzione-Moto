package it.paolofree.manutenzionemoto;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.text.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    MaintenanceDb db; List<MaintenanceDb.Entry> entries=new ArrayList<>(); List<MaintenanceDb.Deadline> deadlines=new ArrayList<>(); ListView list; TextView empty,count,total,currentKmText; String filter=null; boolean showDeadlines=false; int currentKm;
    final NumberFormat euro=NumberFormat.getCurrencyInstance(Locale.ITALY);

    @Override public void onCreate(Bundle b){ super.onCreate(b); setContentView(R.layout.activity_main); getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        db=new MaintenanceDb(this); list=findViewById(R.id.list);empty=findViewById(R.id.emptyText);count=findViewById(R.id.countText);total=findViewById(R.id.totalText);currentKmText=findViewById(R.id.currentKmText);
        currentKm=getPreferences(MODE_PRIVATE).getInt("current_km",0); currentKmText.setText(String.format(Locale.ITALY,"%,d km",currentKm)); createNotificationChannel(); requestNotificationPermission();
        findViewById(R.id.updateKmButton).setOnClickListener(v->updateCurrentKm());
        findViewById(R.id.addButton).setOnClickListener(v->{if(showDeadlines)editDeadline(null);else edit(null);});
        ((RadioGroup)findViewById(R.id.sectionGroup)).setOnCheckedChangeListener((g,id)->{showDeadlines=id==R.id.sectionDeadlines;findViewById(R.id.filterGroup).setVisibility(showDeadlines?View.GONE:View.VISIBLE);findViewById(R.id.summaryCard).setVisibility(showDeadlines?View.GONE:View.VISIBLE);((Button)findViewById(R.id.addButton)).setText(showDeadlines?"＋  NUOVA SCADENZA":"＋  NUOVO INTERVENTO");refresh();});
        ((RadioGroup)findViewById(R.id.filterGroup)).setOnCheckedChangeListener((g,id)->{filter=id==R.id.filterShop?"Officina":id==R.id.filterMe?"Io":null; refresh();});
        list.setOnItemClickListener((p,v,pos,id)->{if(showDeadlines)editDeadline(deadlines.get(pos));else edit(entries.get(pos));});
        list.setOnItemLongClickListener((p,v,pos,id)->{ if(showDeadlines)confirmDeleteDeadline(deadlines.get(pos));else confirmDelete(entries.get(pos)); return true; }); refresh();
    }
    void refresh(){ entries=db.all(filter); deadlines=db.deadlines(); double sum=0; for(MaintenanceDb.Entry e:entries)sum+=e.cost; count.setText(String.valueOf(entries.size()));total.setText(euro.format(sum));
        boolean none=showDeadlines?deadlines.isEmpty():entries.isEmpty(); empty.setText(showDeadlines?"Nessuna scadenza impostata\nPremi + per aggiungerne una":"Nessun intervento registrato\nPremi + per iniziare");empty.setVisibility(none?View.VISIBLE:View.GONE);list.setVisibility(none?View.GONE:View.VISIBLE);list.setAdapter(showDeadlines?new DeadlineAdapter():new EntryAdapter()); }
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
    void updateCurrentKm(){final EditText km=input("Chilometri attuali",InputType.TYPE_CLASS_NUMBER);km.setText(String.valueOf(currentKm));FrameLayout wrap=new FrameLayout(this);wrap.setPadding(45,5,45,0);wrap.addView(km);AlertDialog d=new AlertDialog.Builder(this).setTitle("Aggiorna chilometraggio").setView(wrap).setNegativeButton("ANNULLA",null).setPositiveButton("SALVA",null).create();d.setOnShowListener(x->d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{try{int value=Integer.parseInt(km.getText().toString());if(value<0)throw new Exception();currentKm=value;getPreferences(MODE_PRIVATE).edit().putInt("current_km",value).apply();currentKmText.setText(String.format(Locale.ITALY,"%,d km",value));d.dismiss();checkDeadlines();refresh();}catch(Exception ex){km.setError("Inserisci chilometri validi");}}));d.show();}
    void editDeadline(MaintenanceDb.Deadline old){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(45,5,45,0);EditText title=input("es. Cambio olio",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);EditText target=input("es. 30000",InputType.TYPE_CLASS_NUMBER);box.addView(label("INTERVENTO / PEZZO"));box.addView(title);box.addView(label("SCADENZA A KM"));box.addView(target);if(old!=null){title.setText(old.title);target.setText(String.valueOf(old.targetKm));}AlertDialog d=new AlertDialog.Builder(this).setTitle(old==null?"Nuova scadenza":"Modifica scadenza").setView(box).setNegativeButton("ANNULLA",null).setPositiveButton("SALVA",null).create();d.setOnShowListener(x->d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{String t=title.getText().toString().trim();if(t.isEmpty()){title.setError("Inserisci l'intervento");return;}try{int k=Integer.parseInt(target.getText().toString());db.saveDeadline(old==null?null:old.id,t,k);d.dismiss();checkDeadlines();refresh();}catch(Exception ex){target.setError("Inserisci i km della scadenza");}}));d.show();}
    void confirmDeleteDeadline(MaintenanceDb.Deadline e){new AlertDialog.Builder(this).setTitle("Eliminare scadenza?").setMessage(e.title+" verrà cancellata.").setNegativeButton("ANNULLA",null).setPositiveButton("ELIMINA",(d,w)->{db.deleteDeadline(e.id);refresh();}).show();}
    void createNotificationChannel(){if(android.os.Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel("maintenance_due","Scadenze manutenzione",NotificationManager.IMPORTANCE_HIGH);c.setDescription("Avvisi al raggiungimento dei chilometri");getSystemService(NotificationManager.class).createNotificationChannel(c);}}
    void requestNotificationPermission(){if(android.os.Build.VERSION.SDK_INT>=33&&ContextCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},100);}
    void checkDeadlines(){for(MaintenanceDb.Deadline e:db.deadlines())if(!e.notified&&currentKm>=e.targetKm){Intent i=new Intent(this,MainActivity.class);PendingIntent pi=PendingIntent.getActivity(this,(int)e.id,i,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);NotificationCompat.Builder n=new NotificationCompat.Builder(this,"maintenance_due").setSmallIcon(R.drawable.ic_launcher).setContentTitle("Manutenzione moto in scadenza").setContentText(e.title+" • prevista a "+String.format(Locale.ITALY,"%,d km",e.targetKm)).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).setContentIntent(pi);if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED){NotificationManagerCompat.from(this).notify((int)e.id,n.build());db.markNotified(e.id);}}}
    class EntryAdapter extends BaseAdapter{
        public int getCount(){return entries.size();}public Object getItem(int p){return entries.get(p);}public long getItemId(int p){return entries.get(p).id;}
        public View getView(int p,View v,android.view.ViewGroup parent){if(v==null)v=getLayoutInflater().inflate(R.layout.item_maintenance,parent,false);MaintenanceDb.Entry e=entries.get(p);((TextView)v.findViewById(R.id.itemPart)).setText(e.part);((TextView)v.findViewById(R.id.itemCost)).setText(euro.format(e.cost));((TextView)v.findViewById(R.id.itemInfo)).setText(String.format(Locale.ITALY,"%,d km  •  %s  •  %s",e.km,e.who,e.date));return v;}
    }
    class DeadlineAdapter extends BaseAdapter{
        public int getCount(){return deadlines.size();}public Object getItem(int p){return deadlines.get(p);}public long getItemId(int p){return deadlines.get(p).id;}
        public View getView(int p,View v,android.view.ViewGroup parent){if(v==null)v=getLayoutInflater().inflate(R.layout.item_maintenance,parent,false);MaintenanceDb.Deadline e=deadlines.get(p);int remaining=e.targetKm-currentKm;((TextView)v.findViewById(R.id.itemPart)).setText(e.title);TextView right=v.findViewById(R.id.itemCost);right.setText(remaining<=0?"SCADUTA":String.format(Locale.ITALY,"%,d km",remaining));right.setTextColor(remaining<=0?Color.rgb(255,107,107):Color.rgb(75,196,255));((TextView)v.findViewById(R.id.itemInfo)).setText("Scadenza a "+String.format(Locale.ITALY,"%,d km",e.targetKm)+(remaining<=0?"  •  raggiunta":"  •  mancanti"));return v;}
    }
}
