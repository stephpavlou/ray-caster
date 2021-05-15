/*
*   Written by Stephanos Pavlou in May 2020 
*   
*   Java-caster is a simple implementation of a raycaster in Java. The goal of this project was to learn more 
*   about this particular method of creating pseudo-3D graphics, and to offer some code to the community
*   in the hopes that it may help a struggling programmer in need.
*   
*/


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import java.awt.*;

import java.awt.Color;
import java.awt.Graphics;

import java.lang.Math;

import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.*;

/*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%----------Helper Classes------------%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*/

//implements user controls (i.e. move and look around)
class keyReact implements KeyListener{

    public void keyPressed(KeyEvent e)
    {

        //positional moves
        //move forward
        if(e.getKeyCode()==KeyEvent.VK_W)
        {
            if(raycast.playerDir>=0f && raycast.playerDir<=1.5708f)
            {
                //cos(playerDir)+
                //sin(playerDir)+
                raycast.playerPosx-=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy+=0.4f*(Math.sin(raycast.playerDir));
            }
            else if(raycast.playerDir>=1.5708f && raycast.playerDir<=3.14159f)
            {
                //cos(playerDir)-
                //sin(playerDir)+
                raycast.playerPosx-=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy+=0.4f*(Math.sin(raycast.playerDir));
            }
            else if(raycast.playerDir>=3.14159f && raycast.playerDir<=4.71239f)
            {
                //cos(playerDir)-
                //sin(playerDir)-
                raycast.playerPosx-=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy+=0.4f*(Math.sin(raycast.playerDir));
            }
            else
            {
                //cos(playerDir)+
                //sin(playerDir)-
                raycast.playerPosx-=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy+=0.4f*(Math.sin(raycast.playerDir));
            }
        }
        //move back
        else if(e.getKeyCode()==KeyEvent.VK_S)
        {
            if(raycast.playerDir>=0f && raycast.playerDir<=1.5708f)
            {
                raycast.playerPosx+=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy-=0.4f*(Math.sin(raycast.playerDir));
            }
            else if(raycast.playerDir>=1.5708f && raycast.playerDir<=3.14159f)
            {
                raycast.playerPosx+=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy-=0.4f*(Math.sin(raycast.playerDir));
            }
            else if(raycast.playerDir>=3.14159f && raycast.playerDir<=4.71239f)
            {
                raycast.playerPosx+=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy-=0.4f*(Math.sin(raycast.playerDir));
            }
            else
            {
                raycast.playerPosx+=0.4f*(Math.cos(raycast.playerDir));
                raycast.playerPosy-=0.4f*(Math.sin(raycast.playerDir));
            }
        }

        //directional moves
        //look left
        else if(e.getKeyCode()==KeyEvent.VK_A)
        {
            raycast.playerDir+=0.4f;
            if(raycast.playerDir>6.28319f)
            {
                raycast.playerDir=0.2f;
            }
        } 
        //look right
        else if(e.getKeyCode()==KeyEvent.VK_D)
        {
            raycast.playerDir-=0.4f;
            if(raycast.playerDir<0f)
            {
                raycast.playerDir=6.1f;
            }
        }
        //for help debugging
        else if(e.getKeyCode()==KeyEvent.VK_L)
        {
            for(int i=0;i<100;i++)
            {
                System.out.print(raycast.viewColumns[i]+" ");
            }
            System.out.println();
        }
    }
    public void keyReleased(KeyEvent e)
    {

    }
    public void keyTyped(KeyEvent e)
    {
        
    }
 }

//defines the canvas on the main window that the view will be buffered to
class gCanvas extends JPanel{

    @Override
    public void paintComponent(Graphics g)
    {
        int wallSize,diff;
        for(int i=0;i<60;i++)
        {
            if(raycast.viewColumns[i]<0)
            {
                g.setColor(Color.DARK_GRAY);
                wallSize=400-(Math.round(40*(Math.abs(raycast.viewColumns[i]))));
                diff=Math.round((400-wallSize)/2f);
            }
            else 
            {
                g.setColor(Color.LIGHT_GRAY);
                wallSize=400-(Math.round(40*raycast.viewColumns[i]));
                diff=Math.round((400-wallSize)/2f);
            }
            
            //g.fillRect(x, y, width, height);
            g.fillRect(i*10,diff,10,wallSize);
        }
    }
}

//defines the minimap in the top right corner of the window
class gMap extends JPanel{

    @Override 
    public void paintComponent(Graphics g)
    {
        g.setColor(Color.WHITE);
        g.fillRect(0,0,100,100);

        g.setColor(Color.BLACK);
        //draw map
        for(int i=0;i<10;i++)
        {
            for(int j=0;j<10;j++)
            {
                if(raycast.map[i][j]==1)
                {
                    g.fillRect(i*10,j*10,10,10);
                }
            }
        }
        g.setColor(Color.LIGHT_GRAY);
        //draw grids
        for(int i=0;i<10;i++)
        {
            g.fillRect(0,10+i*10,100,1);
            for(int j=0;j<10;j++)
            {
                g.fillRect(10+j*10,0,1,100);
            }
        }

        //draw player
        g.setColor(Color.BLACK);
        g.fillRect((int)(raycast.playerPosx*10),(int)(raycast.playerPosy*10),4,4);
        //draw playerDir
        //g.setColor(Color.RED);
        //g.fillRect((int)((raycast.playerPosx*10)-Math.cos((double)raycast.playerDir)*4),(int)((raycast.playerPosy*10)+Math.sin((double)raycast.playerDir)*4),3,3);
        //g.fillRect((int)((raycast.playerPosx*10)-Math.cos((double)raycast.playerDir)*7),(int)((raycast.playerPosy*10)+Math.sin((double)raycast.playerDir)*7),2,2);
        //g.fillRect((int)((raycast.playerPosx*10)-Math.cos((double)raycast.playerDir)*9),(int)((raycast.playerPosy*10)+Math.sin((double)raycast.playerDir)*9),1,1);

        //draw collisions
        //g.setColor(Color.BLUE);
        //g.fillRect((int)(raycast.horCollisions[0]*10-2),(int)(raycast.horCollisions[1]*10-2),4,4);

        //g.setColor(Color.GREEN);
        //g.fillRect((int)(raycast.vertCollisions[0]*10-2),(int)(raycast.vertCollisions[1]*10-2),4,4);
        
        //draw playerDir
        g.setColor(Color.RED);
        for(int i=0;i<raycast.length;i++)
        {
            if(i==raycast.length-1)
            {
                g.setColor(Color.GREEN);
                g.fillRect((int)(raycast.rayx[i]*10),(int)(raycast.rayy[i]*10),2,2);
            }
            else if(i==raycast.length-2)
            {
                g.setColor(Color.BLUE);
                g.fillRect((int)(raycast.rayx[i]*10),(int)(raycast.rayy[i]*10),2,2);
            }
            else
            {
                g.fillRect((int)(raycast.rayx[i]*10),(int)(raycast.rayy[i]*10),2,2);
            }
        }

        System.out.print("playerDir= "+raycast.playerDir);
        System.out.println();
        System.out.println();

    }
}

//controls the window buffer and maintains 60fps
class bufferWindow extends TimerTask{

    public void run()
    {   
        raycast.createView();
        raycast.gView.repaint();
        raycast.mapView.repaint();
        raycast.window.setVisible(true);
    }

} 

/*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%----------RayCaster-----------%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*/

public class raycast {


public static int map[][]={

        {1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,1,0,0,1},
        {1,0,0,0,0,0,1,0,0,1},
        {1,1,1,1,1,1,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1}
    
};

public static float playerPosx=2;
public static float playerPosy=2;
public static float playerDir=1.0472f;
public static float fov=1.4f;
public static float viewColumns[]= new float[60];
//tests
public static float horCollisions[]= new float[2];
public static float vertCollisions[]= new float[2];
public static float rayx[]=new float[60];
public static float rayy[]=new float[60];
public static float length;

public static JFrame window;
public static gCanvas gView;
public static gMap mapView;

    //creates the viewColums by doing all the trig and calculations
    public static void createView()
    {

        float leftBound=playerDir+fov/2f;
        float ray=leftBound;
        float decrement=fov/60f;

        float horx=0,hory=0,hor2x=0,hor2y=0,vertx=0,verty=0,vert2x=0,vert2y=0;
        float distToHor,distToVert;
        float hOffset=0,vOffset=0;
        float depth=0;
        length=0;

        boolean hitWall=false;

        for(int j=0;j<60;j++)
        {
            hitWall=false;
            //i quadrant
            if(ray>0f && ray<1.5708f)
            {
                //determine horizontal step & offset
                //first h step
                hory=(int)(playerPosy+1);
                horx=(float)(playerPosx-((hory-playerPosy)/(Math.tan((double)ray))));

                //second h step
                hor2y=(int)(playerPosy+2);
                hor2x=(float)(playerPosx-((hor2y-playerPosy)/(Math.tan((double)ray))));

                hOffset=Math.abs(hor2x-horx);

                //determine vertical step & offset
                //checking that the player is not standing in the corner of a map-grid
                if(vertx-(vertx+1)==1)
                {
                    //first v step
                    vertx=(int)(playerPosx-1);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));
                
                    //second v step
                    vert2x=(int)(playerPosx-2);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
                else
                {
                    //first v step
                    vertx=(int)(playerPosx);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));

                    //second v step
                    vert2x=(int)(playerPosx-1);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
            }
            //ii quadrant
            else if(ray>1.5708f && ray<3.14159f)
            {
                //determine horizontal step & offset
                //first h step
                hory=(int)(playerPosy+1);
                horx=(float)(playerPosx-((hory-playerPosy)/(Math.tan((double)ray))));

                //second h step
                hor2y=(int)(playerPosy+2);
                hor2x=(float)(playerPosx-((hor2y-playerPosy)/(Math.tan((double)ray))));

                hOffset=Math.abs(hor2x-horx);

                //determine vertical step & offset
                //checking that the player is not standing in the corner of a map-grid
                if(vertx-(vertx+1)==1)
                {
                    //first v step
                    vertx=(int)(playerPosx+1);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));
                
                    //second v step
                    vert2x=(int)(playerPosx+2);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
                else
                {
                    //first v step
                    vertx=(int)(playerPosx+1);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));

                    //second v step
                    vert2x=(int)(playerPosx+2);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
            }
            //iii quadrant
            else if(ray>3.14159f && ray<4.71239f)
            {
                //determine horizontal step & offset
                //first h step
                hory=(int)(playerPosy);
                horx=(float)(playerPosx-((hory-playerPosy)/(Math.tan((double)ray))));

                //second h step
                hor2y=(int)(playerPosy-1);
                hor2x=(float)(playerPosx-((hor2y-playerPosy)/(Math.tan((double)ray))));

                hOffset=Math.abs(hor2x-horx);

                //determine vertical step & offset
                //checking that the player is not standing the corner of a map-grid
                if(vertx-(vertx+1)==1)
                {
                    //first v step
                    vertx=(int)(playerPosx+1);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));
                
                    //second v step
                    vert2x=(int)(playerPosx+2);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
                else
                {
                    //first v step
                    vertx=(int)(playerPosx+1);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));

                    //second v step
                    vert2x=(int)(playerPosx+2);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
            }
            //iv quadrant
            else if(ray>4.71239f && ray<6.28319f)
            {
                //determine horizontal step & offset
                //first h step
                hory=(int)(playerPosy);
                horx=(float)(playerPosx-((hory-playerPosy)/(Math.tan((double)ray))));

                //second h step
                hor2y=(int)(playerPosy-1);
                hor2x=(float)(playerPosx-((hor2y-playerPosy)/(Math.tan((double)ray))));

                hOffset=Math.abs(hor2x-horx);

                //determine vertical step & offset
                if(vertx-(vertx+1)==1)
                {
                    //first v step
                    vertx=(int)(playerPosx);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));
                
                    //second v step
                    vert2x=(int)(playerPosx-1);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
                else
                {
                    //first v step
                    vertx=(int)(playerPosx);
                    verty=(float)(playerPosy-(Math.tan((double)ray)*(vertx-playerPosx)));

                    //second v step
                    vert2x=(int)(playerPosx-1);
                    vert2y=(float)(playerPosy-(Math.tan((double)ray)*(vert2x-playerPosx)));

                    vOffset=Math.abs(vert2y-verty);
                }
            }
            //special cases
            //ray=90 or ray=270
            else if(ray==1.5708f || ray==4.71239f)
            {
                //set vertical step to where player is
                vertx=playerPosx;
                verty=playerPosy;

                //calculate dist to vert step
                distToVert=(float)Math.sqrt((double)(((Math.pow((double)(vertx-playerPosx),2)
                +(Math.pow((double)(verty-playerPosy),2))))));

                //main loop
                hitWall=false;
                depth=0;
                length=0;
                while(hitWall=false && depth<20)
                {
                    if(ray==1.5708f)
                    {
                        if(map[(int)vertx][(int)verty]==1)
                        {
                            hitWall=true;
                            if(ray>playerDir)
                            {
                                viewColumns[j]=(-1)*(float)(distToVert*Math.cos(ray-playerDir));
                            }
                            else
                            {
                                viewColumns[j]=(-1)*(float)(distToVert*Math.cos(playerDir-ray));
                            }
                        }
                        verty++;
                    }
                    else 
                    {
                        if(map[(int)vertx][(int)verty-1]==1)
                        {
                            hitWall=true;
                            if(ray>playerDir)
                            {
                                viewColumns[j]=(-1)*(float)(distToVert*Math.cos(ray-playerDir));
                            }
                            else
                            {
                                viewColumns[j]=(-1)*(float)(distToVert*Math.cos(playerDir-ray));
                            }
                        }
                        verty--;
                    }
                    depth++;
                }
            }
            //ray=0/360 or ray=180
            else if((ray==0f || ray==6.28319f) || ray==3.14159f)
            {
                //set horizontal step to where player is
                horx=playerPosx;
                hory=playerPosy;
                
                //calculate dist to hor step
                distToHor=(float)Math.sqrt((double)(((Math.pow((double)(horx-playerPosx),2)
                            +(Math.pow((double)(hory-playerPosy),2))))));
                
                //main loop
                hitWall=false;
                depth=0;
                length=0;
                while(hitWall=false && depth<20)
                {
                    if(ray==0f || ray==6.28319f)
                    {
                        if(map[(int)horx-1][(int)hory]==1)
                        {
                            hitWall=true;
                            if(ray>playerDir)
                            {
                                viewColumns[j]=(float)(distToHor*Math.cos(ray-playerDir));
                            }
                            else
                            {
                                viewColumns[j]=(float)(distToHor*Math.cos(playerDir-ray));
                            }
                        }
                        horx--;
                    }
                    else 
                    {
                        if(map[(int)horx][(int)hory]==1)
                        {
                            hitWall=true;
                            if(ray>playerDir)
                            {
                                viewColumns[j]=(float)(distToHor*Math.cos(ray-playerDir));
                            }
                            else
                            {
                                viewColumns[j]=(float)(distToHor*Math.cos(playerDir-ray));
                            }
                        }
                        horx++;
                    }
                    depth++;
                }
            }
            //main loop
            depth=0;
            length=0;    
            int i=0;
            while(hitWall==false && depth<20)
            {
                //determine distance to two collision points
                distToHor=(float)Math.sqrt((double)(((Math.pow((double)(horx-playerPosx),2)
                            +(Math.pow((double)(hory-playerPosy),2))))));
                distToVert=(float)Math.sqrt((double)(((Math.pow((double)(vertx-playerPosx),2)
                            +(Math.pow((double)(verty-playerPosy),2))))));

                //depending on which step is nearer
                if(distToHor<distToVert)
                {
                    //i quadrant
                    if(ray>0f && ray<1.5708f)
                    {
                        if(horx>=0 && horx<10 && hory>=0 && hory<10 && map[(int)horx][(int)hory]==1)
                        {
                            System.out.println("hit wall at ("+horx+", "+hory+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=horx;
                        rayy[i]=hory;
                        length++;
    
                        horx-=hOffset;
                        hory++;
                    }
                    //ii quadrant
                    else if(ray>1.5708f && ray<3.14159f)
                    {
                        if(horx>=0 && horx<10 && hory>=0 && hory<10 && map[(int)horx][(int)hory]==1)
                        {
                            System.out.println("hit wall at ("+horx+", "+hory+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=horx;
                        rayy[i]=hory;
                        length++;
    
                        horx+=hOffset;
                        hory++;
                    }
                    //iii quadrant
                    else if(ray>3.14159f && ray<4.71239f)
                    {
                        if(horx>=0 && horx<10 && hory>=0 && hory<10 && map[(int)horx][(int)hory-1]==1)
                        {
                            System.out.println("hit wall at ("+horx+", "+hory+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=horx;
                        rayy[i]=hory;
                        length++;
    
                        horx+=hOffset;
                        hory--;
                    }
                    //iv quadrant
                    else if(ray>4.71239f && ray<6.28319f)
                    {
                        if(horx>=0 && horx<10 && hory>=0 && hory<10 && map[(int)horx][(int)hory-1]==1)
                        {
                            System.out.println("hit wall at ("+horx+", "+hory+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(-1)*(float)(distToHor*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=horx;
                        rayy[i]=hory;
                        length++;
    
                        horx-=hOffset;
                        hory--;
                    }
                }
                else
                {
                    //i quadrant
                    if(ray>0f && ray<1.5708f)
                    {
                        if(vertx>=0 && vertx<10 && verty>=0 && verty<10 && map[(int)vertx-1][(int)verty]==1)
                        {
                            System.out.println("hit wall at ("+vertx+", "+verty+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=vertx;
                        rayy[i]=verty;
                        length++;
    
                        vertx--;
                        verty+=vOffset;
                    }
                    //ii quadrant
                    else if(ray>1.5708f && ray<3.14159f)
                    {
                        if(vertx>=0 && vertx<10 && verty>=0 && verty<10 && map[(int)vertx][(int)verty]==1)
                        {
                            System.out.println("hit wall at ("+vertx+", "+verty+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=vertx;
                        rayy[i]=verty;
                        length++;
    
                        vertx++;
                        verty+=vOffset;
                    }
                    //iii quadrant
                    else if(ray>3.14159f && ray<4.71239f)
                    {
                        if(vertx>=0 && vertx<10 && verty>=0 && verty<10 && map[(int)vertx][(int)verty]==1)
                        {
                            System.out.println("hit wall at ("+vertx+", "+verty+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=vertx;
                        rayy[i]=verty;
                        length++;
    
                        vertx++;
                        verty-=vOffset;
                    }
                    //iv quadrant
                    else if(ray>4.71239f && ray<6.28319f)
                    {
                        if(vertx>=0 && vertx<10 && verty>=0 && verty<10 && map[(int)vertx-1][(int)verty]==1)
                        {
                            System.out.println("hit wall at ("+vertx+", "+verty+")");
                            hitWall=true;
                            //calculate distance along player direction and store in viewColumns
                            if(playerDir>ray)
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(playerDir-ray));
                            }
                            else
                            {
                                viewColumns[j]=(float)(distToVert*Math.cos(ray-playerDir));
                            }
                        }
                        rayx[i]=vertx;
                        rayy[i]=verty;
                        length++;
    
                        vertx--;
                        verty-=vOffset;
                    }
                }

                i++;
                depth++;
            }
        
            //move to next ray within the fov
            ray-=decrement;
            //if the ray moves past the x-axis, treat as 360deg
            if(ray<0f)
            {
                ray=6.28319f;
            }
        }
    }
    public static void main(String args[])
    {
        window=new JFrame("demo raycaster");
        window.setSize(600,420);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel container=new JPanel(){
            public boolean isOptimizedDrawingEnabled() {
               return false;
            }
         };
        LayoutManager overlay=new OverlayLayout(container);
        container.setLayout(overlay);

        mapView=new gMap();
        mapView.setOpaque(false);
        container.add(mapView);

        gView=new gCanvas();
        container.add(gView);

        window.add(container, BorderLayout.CENTER);

        //fills the view columns, allowing us to print them to the screen

        window.addKeyListener(new keyReact());
        window.setVisible(true);

        bufferWindow buffer=new bufferWindow();
        Timer fpsKeeper=new Timer();
        fpsKeeper.schedule(buffer,0,16);

    }
}
