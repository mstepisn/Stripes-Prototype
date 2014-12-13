package mss.stripesprototype;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;


public class DrawingView extends View {

	private Paint drawPaint, canvasPaint;
	private int paintColor = 0xFF660000;
	
	private Paint tapeColor; // for placing tape 
	private Canvas drawCanvas; 
	private Bitmap canvasBitmap; // for saving the image
	
	int x1, x2, y1, y2, dx, dy; // used for tracking tough motion
	private int stripeArray[][];  // used to hold stripes saved by tape
	private int blueTape = 0xFF00AAFF; // color of tape
	
	public DrawingView(Context context, AttributeSet attrs){
	    super(context, attrs);
	    setupDrawing();
	}
	
	private void setupDrawing(){
		//get drawing area setup for interaction     
	 	drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		
		tapeColor = new Paint();
		tapeColor.setColor(blueTape);
		tapeColor.setStrokeWidth(40);
		
		}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	//view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
		stripeArray = new int[drawCanvas.getWidth()][drawCanvas.getHeight()];
		for (int i = 0; i < stripeArray.length; i ++){
			for (int j = 0; j < stripeArray[i].length; j ++){
				stripeArray[i][j] = blueTape; // initialize to be all tape color
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint); // display canvas
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int touchX = (int)event.getX();
		int touchY = (int)event.getY();
		String direction;
		
		if(MainActivity.paintOn == true){ // if paint action selected
		setCanvasColor(); // fill entire canvas
		}
		
		
		else if(MainActivity.placeTapeOn == true){ // if place tape action selected
		
			switch(event.getAction()) {
			    case(MotionEvent.ACTION_DOWN): // get location of first touch
			        x1 = (int)event.getX();
			        y1 = (int)event.getY();
			        break;
			    case(MotionEvent.ACTION_UP): // get location of end of motion
			        x2 = (int)event.getX();
			        y2 = (int)event.getY();
			        dx = x2-x1;
			            dy = y2-y1;
			            
			        // determine direction of movement
			        if(Math.abs(dx) > Math.abs(dy)) {
			            if(dx>0) direction = "right";
			            else direction = "left";
			        } else {
			            if(dy>0) direction = "down";
			            else direction = "up";
			        }
			        placeTape(direction,x1,y1); // draw the tape following the correct path
			        break;
			    default:
				    return false;
			    }
			}
		
		else if(MainActivity.removeTapeOn == true){ // if remove tape action is selected
			for (int i = 0; i < stripeArray.length; i ++){
				for (int j = 0; j < stripeArray[i].length; j ++){
					if (stripeArray[i][j] != blueTape){ // display saved stripes on new color
			           canvasBitmap.setPixel(i, j, stripeArray[i][j]);
					}
					stripeArray[i][j] = blueTape; // reset stripeArray 
		     }
			}
		}
		invalidate();
		return true;
	}
	
	public void setColor(String newColor){ // used for paint selection  
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
		}
	
	public void setCanvasColor(){
		int color= paintColor;
		float r = (color >> 16) & 0xFF;
		float g = (color >> 8) & 0xFF;
		float b = (color >> 0) & 0xFF;
		float a = (color >> 24) & 0xFF;
		drawCanvas.drawARGB(255,(int)r,(int)g,(int)b); 
		invalidate();
		}
	
	/** 
	 * Tape is drawn depending on the direction of the touch motion.
	 * Before tape is placed on the canvas,
	 * the color that is meant to be saved is stored in the stripeArray.
	 * If tape is placed over an existing piece of tape,
	 * the stripeArray does not update that pixel.
	 * 
	 * @param dir - direction of touch motion
	 * @param xLoc - starting x location
	 * @param yLoc - starting y location
	 */
	
	public void placeTape(String dir, float xLoc, float yLoc){
		if(dir == "right"){
			for(int i = 0; i < drawCanvas.getWidth(); i++){
				for (int j = (int)yLoc - 20; j < (int)yLoc + 20; j ++){
					if(canvasBitmap.getPixel(i, j) != blueTape){
						stripeArray[i][j] = canvasBitmap.getPixel(i,j);
					}
				}
			}
			drawCanvas.drawLine(drawCanvas.getWidth(), yLoc, 0, yLoc, tapeColor); 
		}
		
		else if(dir == "left"){
			for(int i = 0; i < drawCanvas.getWidth(); i++){
				for (int j = (int)yLoc - 20; j < (int)yLoc + 20; j ++){
					if(canvasBitmap.getPixel(i, j) != blueTape){
						stripeArray[i][j] = canvasBitmap.getPixel(i,j);
					}
				}
			}
			drawCanvas.drawLine(0, yLoc, drawCanvas.getWidth(), yLoc, tapeColor);
		}
		
		else if(dir == "up"){	
			for(int i = (int)xLoc - 20; i < (int)xLoc + 20; i++){
				for (int j = 0; j < drawCanvas.getHeight(); j ++){
					if(canvasBitmap.getPixel(i, j) != blueTape){
						stripeArray[i][j] = canvasBitmap.getPixel(i,j);
					}
				}
			}
			drawCanvas.drawLine(xLoc, drawCanvas.getHeight(), xLoc, 0, tapeColor);
		}
		
		else if(dir == "down"){
			for(int i = (int)xLoc - 20; i < (int)xLoc + 20; i++){
				for (int j = 0; j < drawCanvas.getHeight(); j ++){
					if(canvasBitmap.getPixel(i, j) != blueTape){
						stripeArray[i][j] = canvasBitmap.getPixel(i,j);
					}
				}
			}
			drawCanvas.drawLine(xLoc, 0, xLoc, drawCanvas.getHeight(), tapeColor);
		}
		
		invalidate();
	}
	
}
