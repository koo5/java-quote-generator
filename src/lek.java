/**
 * @(#)lek.java
 *
 * lek Applet application
 *
 * @author koom
 * @version 1.00 2010/6/16
 */
 
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import com.threed.jpct.*;
import javax.swing.*;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.World;
public class lek extends Applet implements Runnable
{
Vector <String>quotes;
Image coolImage;
Thread t;
int state=0;
Image offscreenImage;
Graphics oic;//offscreen image context
private Object3D box;
private FrameBuffer buffer = null;
private World world = null;
String message;
int blinker;
Color[] ovalColor=new Color[4];
int lightness = 100;
public void drawintro()
{
//	oic.setColor (Color.red);
//	oic.drawString("Click me!", 0, 60);
//	oic.setColor (Color.green);
//	oic.drawString("In soviet russia, websites click on You!", 0, 120);
    oic.drawImage(coolImage, getSize().width/2-69,getSize().height/2-69,this);
}

protected void setup3d()
{
    world = new World();  // create a new world
	World.setDefaultThread( Thread.currentThread() );

	world.addLight(new SimpleVector(0,100,0),lightness,0,0);
    world.addLight(new SimpleVector(0,-100,0),0,lightness,0);
//    world.addLight(new SimpleVector(0,0,100),0,0,lightness/5);
//    world.addLight(new SimpleVector(0,0,-100),0,0,lightness/5);
 
	// create a new buffer to draw on:
	buffer = new FrameBuffer( getWidth(), getHeight(),
							  FrameBuffer.SAMPLINGMODE_NORMAL );

	
	// Create the box:
	box = Primitives.getCube(10f);
	box.setShadingMode(Object3D.SHADING_GOURAUD);
	box.build();
	// add the box our world:
	world.addObject( box );

	// set the camera's position:
	world.getCamera().setPosition( 50, -50, -5 );

	// Look at the box:
	world.getCamera().lookAt( box.getTransformedCenter() );
}


public void init() 
{
	setup3d(); 	
	
	Font a_Font = new Font("Helvetica", Font.PLAIN, 15);
  	setFont(a_Font);
  	
  	
	this.enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
	
	
 	offscreenImage = createImage(getSize().width, getSize().height);
  	oic = offscreenImage.getGraphics();
  	

  	coolImage = getImage(getCodeBase(), "coolImage.gif");
  	

  	quotes = new Vector<String>(0,1);
  	try
  	{
  		URL url=new URL(getCodeBase(),"quotes.txt");
  		InputStream stream;
  		stream = url.openStream();
  		Reader r = new BufferedReader(new InputStreamReader(stream));
  		StreamTokenizer tokenizer = new StreamTokenizer(r);
  		tokenizer.wordChars(0,255);
  		tokenizer.whitespaceChars('%','%');
  		int token;
  		do
  		{
			if ((token = tokenizer.nextToken()) == tokenizer.TT_WORD)
				quotes.addElement(tokenizer.sval);
  		}
		while(token != tokenizer.TT_EOF);
  	}
  	catch(Exception e){}
	t=new Thread(this);
	t.start();
}

public void start()
{
}

public void stop() { }

public void destroy() { }

public void randomizeColors()
{
	for(int i=0;i<4;i++)
	ovalColor[i]=getRandomColor();
}

public void run () 
{
	randomizeColors();
	while(true)
	{
		repaint();
		try
		{
            world.setLightIntensity(0, lightness, 0 ,0);
            world.setLightIntensity(1, 0, lightness, 0);
            box.rotateX( ovalColor[0].getRed()/25600f );
            box.rotateY( ovalColor[0].getGreen()/25600f );
            box.rotateZ( ovalColor[0].getBlue()/25600f );
            blinker++;
			if(blinker==30)
			{
				blinker=0;
				randomizeColors();
			}
			t.sleep(1000/30);
		}catch(InterruptedException e){}
	}
}

public void drawbox()
{
	buffer.clear();
	world.renderScene(buffer);
	world.draw(buffer);
	buffer.update();
	buffer.display(oic,0,0);
}

public void changemessage()
{
	if(quotes.size()!=0)
	{
		int rnd=(int)(Math.random()*(quotes.size()-1));
		message=(String)quotes.elementAt(rnd);
	}
	else
		message="sorry, no quotes found";

}

public void drawit()
{
	Color mycolor = new Color(255,255,255);
	oic.setColor(mycolor);
	String m=message;	
    Graphics2D graphics2D = (Graphics2D) oic;
    GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font font = new Font("LucidaSans", Font.PLAIN, 16);
    AttributedString messageAS = new AttributedString(m);
    messageAS.addAttribute(TextAttribute.FONT, font);
    AttributedCharacterIterator messageIterator = messageAS.getIterator();
    FontRenderContext messageFRC = graphics2D.getFontRenderContext();
    LineBreakMeasurer messageLBM = new LineBreakMeasurer(messageIterator,
        messageFRC);
        
    float wrappingWidth = getSize().width;
    float x = 0;
    float y = 0;

    while (messageLBM.getPosition() < messageIterator.getEndIndex()) {
      TextLayout textLayout = messageLBM.nextLayout(wrappingWidth);
      y += textLayout.getAscent();
      textLayout.draw(graphics2D, x, y);
      y += textLayout.getDescent() + textLayout.getLeading();
      x = 0;
    }
}

public void switchState()
{
	changemessage();
	state=1;
//	drawit();
//	repaint();
}

protected void processKeyEvent(KeyEvent e)
{
	if(e.getID() == KeyEvent.KEY_PRESSED)
		switchState();
}

protected void processMouseEvent(MouseEvent e)
{
	if(e.getID() == MouseEvent.MOUSE_CLICKED)
		switchState();
}

public Color getRandomColor()
{
	int R = (int)(Math.random()*255);
	int G = (int)(Math.random()*255);
	int B = (int)(Math.random()*255);
	return new Color(R,G,B);
}

public void paint (Graphics g)
{
	if (state==0)
	{
		drawbox();
		
		int radius = 20;
        
        oic.setColor(ovalColor[0]);
        oic.fillOval (40, 40, 2 * radius, 2 * radius);
        oic.setColor(ovalColor[1]);
        oic.fillOval (getSize().width-80, 40, 2 * radius, 2 * radius);
        oic.setColor(ovalColor[2]);
        oic.fillOval (getSize().width-80, getSize().height-80, 2 * radius, 2 * radius);
        oic.setColor(ovalColor[3]);
        oic.fillOval (40, getSize().height-80, 2 * radius, 2 * radius);
        
        drawintro();
	}
	if(state==1)
	{
	    if (lightness > 10)
            lightness = lightness - 1;
		drawbox();
		drawit();
	}
	g.drawImage(offscreenImage,0,0,this);
}


public void update(Graphics g)
{
    paint(g);
}
}