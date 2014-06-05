package es.vicmonmena.openuax.paint4android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author vicmonmena
 *
 */
public class CustomView extends View {

	private final String TAG = "CustomView";
	
	private Paint dPaint;
	private Paint cPaint;
	private Path dPath;
	private Canvas dCanvas;
	private Bitmap cBitmap;

	private final int DEFAULT_COLOR = 0xFF000000;
	
	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		dPaint = new Paint();
		dPath = new Path();
		
		dPaint.setColor(DEFAULT_COLOR);
		dPaint.setAntiAlias(true);
		dPaint.setStrokeWidth(20);
		dPaint.setStyle(Paint.Style.STROKE);
		dPaint.setStrokeJoin(Paint.Join.ROUND);
		dPaint.setStrokeCap(Paint.Cap.ROUND);
		
		cPaint = new Paint(Paint.DITHER_FLAG);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d(TAG, "onSizeChanged");
		cBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		dCanvas = new Canvas(cBitmap);
		// Pintamos el canvas de blanco
		dCanvas.drawColor(Color.WHITE);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw");
		canvas.drawBitmap(cBitmap, 0, 0, cPaint);
		canvas.drawPath(dPath, dPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "onTouchEvent");
		float touchX = event.getX();
		float touchY = event.getY();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Al poner el dedo en la pantalla nos colocamos en esa posición para pintar
			    dPath.moveTo(touchX, touchY);
			    break;
			case MotionEvent.ACTION_MOVE:
				// Al mover el dedo por la pantalla vamos pintando
			    dPath.lineTo(touchX, touchY);
			    break;
			case MotionEvent.ACTION_UP:
				//Levantamos el dedo y reseteamos para la próxima vez que vayamos a pintar
			    dCanvas.drawPath(dPath, dPaint);
			    dPath.reset();
			    break;
			default:
			    return false;
		}
		
		// Refrescamos pantalla
		invalidate();
		return true;
	}
	
	/**
	 * Modifica el tamaño del pincel.
	 * @param size - nuevo tamaño del pincel
	 */
	public void setPathSize(int size) {
		dPaint.setStrokeWidth(size);
	}

	/**
	 * Modifica el color del pincel.
	 * @param color - nuevo color del pincel
	 */
	public void setPathColor(int color) {
		dPaint.setColor(color);
	}
	
	/**
	 * Modifica la forma del trazado
	 * @param cap - nueva forma del trazado
	 */
	public void setPathShape(Paint.Cap cap) {
		dPaint.setStrokeCap(cap);
	}

	/**
	 * Limpia el trazado que existe por completo.
	 */
	public void clearView() {
		dCanvas.drawColor(Color.WHITE);
		invalidate();
	}
	
	/**
	 * @return the cBitmap
	 */
	public Bitmap getcBitmap() {
		return cBitmap;
	}
}