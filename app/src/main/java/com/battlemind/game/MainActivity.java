package com.battlemind.game;

import android.app.*; import android.os.*; import android.graphics.*; import android.graphics.drawable.*; import android.view.*; import android.content.*; import java.util.*;

public class MainActivity extends Activity {
  GameView game;
  @Override public void onCreate(Bundle b){super.onCreate(b); game=new GameView(this); setContentView(game);}
  public static class GameView extends View {
    Paint p=new Paint(3); Random r=new Random(); int screen=0, score=0, energy=100; ArrayList<Unit> units=new ArrayList<>(); long last;
    int cyan=Color.rgb(0,229,255), pink=Color.rgb(255,64,129), bg=Color.rgb(7,10,20);
    GameView(Context c){super(c); p.setTypeface(Typeface.create("sans",Typeface.BOLD)); last=System.currentTimeMillis();}
    protected void onDraw(Canvas c){super.onDraw(c); c.drawColor(bg); if(screen==0)menu(c); else battle(c); invalidate();}
    void text(Canvas c,String s,float x,float y,float size,int col){p.setTextSize(size);p.setColor(col);p.setStyle(Paint.Style.FILL);c.drawText(s,x,y,p);}
    void menu(Canvas c){
      text(c,"BATTLEMIND",55,120,42,Color.WHITE); text(c,"TACTICS • COMBAT • LOGIC",58,153,15,cyan);
      card(c,45,215, getWidth()-45,320,"TACTICAL ARENA","Real-time strategy challenge",cyan);
      card(c,45,345, getWidth()-45,450,"BRAIN STRIKE","Solve under pressure",pink);
      card(c,45,475, getWidth()-45,580,"COMMANDER MODE","Build • defend • conquer",Color.YELLOW);
      text(c,"Designed for strategic minds",58,650,16,Color.LTGRAY); text(c,"v1.0 • OFFLINE",58,680,13,Color.GRAY);
    }
    void card(Canvas c,int l,int t,int rr,int b,String a,String d,int col){p.setColor(Color.rgb(18,23,38));p.setStyle(Paint.Style.FILL);c.drawRoundRect(l,t,rr,b,22,22,p);p.setColor(col);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawRoundRect(l,t,rr,b,22,22,p);text(c,a,l+22,t+43,23,Color.WHITE);text(c,d,l+22,t+73,14,Color.LTGRAY);}
    void battle(Canvas c){
      text(c,"TACTICAL ARENA",25,48,23,Color.WHITE); text(c,"SCORE "+score,25,77,15,cyan); text(c,"ENERGY "+energy, getWidth()-145,77,15,Color.YELLOW);
      p.setColor(Color.rgb(14,20,34));p.setStyle(Paint.Style.FILL);c.drawRoundRect(18,100,getWidth()-18,getHeight()-90,25,25,p);
      // grid
      p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);p.setColor(Color.rgb(35,50,72)); for(int x=35;x<getWidth()-20;x+=42)c.drawLine(x,120,x,getHeight()-110,p); for(int y=125;y<getHeight()-100;y+=42)c.drawLine(25,y,getWidth()-25,y,p);
      p.setStyle(Paint.Style.FILL); for(Unit u:units){p.setColor(u.enemy?pink:cyan);c.drawCircle(u.x,u.y,16,p);p.setColor(Color.WHITE);c.drawCircle(u.x,u.y,6,p);}
      p.setColor(Color.rgb(20,27,45));c.drawRoundRect(20,getHeight()-75,getWidth()-20,getHeight()-20,20,20,p);text(c,"TAP TO DEPLOY UNIT",45,getHeight()-42,16,Color.WHITE);text(c,"⌂",getWidth()-70,getHeight()-40,25,cyan);
      if(System.currentTimeMillis()-last>700){ if(units.size()<18) units.add(new Unit(50+r.nextInt(Math.max(1,getWidth()-100)),130+r.nextInt(Math.max(1,getHeight()-260)),r.nextBoolean())); last=System.currentTimeMillis(); }
    }
    public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=1)return true;float x=e.getX(),y=e.getY(); if(screen==0 && y>210&&y<335){screen=1;units.clear();score=0;energy=100;} else if(screen==0&&y>340&&y<465){screen=1;units.clear();score=100;energy=75;} else if(screen==0&&y>470&&y<595){screen=1;units.clear();score=250;energy=120;} else if(screen==1){ if(y>getHeight()-100){screen=0;return true;} units.add(new Unit(x,y,false));score+=10;energy=Math.max(0,energy-3); } return true; }
    static class Unit{float x,y;boolean enemy;Unit(float x,float y,boolean e){this.x=x;this.y=y;this.enemy=e;}}
  }
}
