package it.paolofree.manutenzionemoto;
import android.content.Context;import android.graphics.*;import android.util.AttributeSet;import androidx.appcompat.widget.AppCompatTextView;
public class HudOdometerView extends AppCompatTextView{
 Paint p=new Paint(1);public HudOdometerView(Context c,AttributeSet a){super(c,a);setGravity(17);setPadding(14,14,14,14);}
 protected void onDraw(Canvas c){float cx=getWidth()/2f,cy=getHeight()/2f,r=Math.min(getWidth(),getHeight())/2f-8;p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(Color.rgb(22,55,72));c.drawArc(cx-r,cy-r,cx+r,cy+r,145,250,false,p);p.setStrokeWidth(5);p.setColor(Color.rgb(64,235,255));p.setShadowLayer(10,0,0,p.getColor());setLayerType(LAYER_TYPE_SOFTWARE,p);c.drawArc(cx-r,cy-r,cx+r,cy+r,145,190,false,p);p.clearShadowLayer();for(int i=0;i<=20;i++){double a=Math.toRadians(145+i*12.5);float x1=cx+(float)Math.cos(a)*(r-8),y1=cy+(float)Math.sin(a)*(r-8),x2=cx+(float)Math.cos(a)*r,y2=cy+(float)Math.sin(a)*r;c.drawLine(x1,y1,x2,y2,p);}super.onDraw(c);}
}
