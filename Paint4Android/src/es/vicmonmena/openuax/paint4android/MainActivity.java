package es.vicmonmena.openuax.paint4android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import es.vicmonmena.openuax.paint4android.view.CustomView;

/**
 * 
 * @author vicmonmena
 *
 */
public class MainActivity extends Activity implements OnLongClickListener, SensorEventListener {

	/**
	 * TAG para los mensajes de LOG
	 */
	private final String TAG = "MainActivity";
	
	/**
	 * Límite de aceleración para limpiar pantalla
	 */
	private static final float ACCELERATION_UMBRAL = 30.0f;
	
	/**
	 *  Carpeta donde se guardan los dibujos.
	 */
	private final String FOLDER_NAME = "paint4android";
		
	/**
	 * Contendrá el lienzo y el dibujo
	 */
	private CustomView customView; 
	
	/**
	 * Dialog para seleccionar el tamaño del trazado.
	 */
	private Dialog sizeDialog;
	/**
	 * Dialog para seleccionar la forma del trazado.
	 */
	private Dialog shapeDialog;
	
	/**
	 * Color que está seleccionado actualmente.
	 */
	private int currentColor;
	/**
	 * Color personalizado
	 */
	private int customColor = Color.BLACK;
	
	private ImageView customColorView;
	
	// Manejo de sensores
	 
	private SensorManager sMngr;
	private Sensor sensor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        customView = (CustomView) findViewById(R.id.customView1);
        customColorView = (ImageView) findViewById(R.id.customColorImgView);
        customColorView.setOnLongClickListener(this);
        currentColor = R.id.customColorImgView;
        
        findViewById(R.id.imageViewRubber).setOnLongClickListener(this);
        
        // Obtenemos el sensor acelerometro
        sMngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sMngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        if (sensor == null) {
        	new AlertDialog.Builder(this)
	    	.setTitle(getString(R.string.warning))
	    	.setMessage(getString(R.string.sensor_not_available))
	    	.setNeutralButton(android.R.string.ok,
	    	new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	dialog.cancel();
	            }
	        }).create().show();
        }
    }

    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	sMngr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	sMngr.unregisterListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
			case R.id.action_help: {
				Toast.makeText(this, getString(R.string.action_help_text), Toast.LENGTH_LONG).show();
				return true;
			}
			case R.id.action_size: {
				// Configuramos el Dialog de los tamaños
				sizeDialog = new Dialog(this);
				sizeDialog.setContentView(R.layout.sizes_layout);
				sizeDialog.setTitle(getString(R.string.path_size));
				
				// Definimos el listener
				sizeDialog.findViewById(R.id.superSize).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathSize(64);
						sizeDialog.dismiss();
						
					}
				});
				sizeDialog.findViewById(R.id.largeSize).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathSize(48);
						sizeDialog.dismiss();
						
					}
				});
				sizeDialog.findViewById(R.id.mediumSize).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathSize(24);
						sizeDialog.dismiss();
						
					}
				});
				sizeDialog.findViewById(R.id.smallSize).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathSize(16);
						sizeDialog.dismiss();
						
					}
				});
				sizeDialog.findViewById(R.id.microSize).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathSize(8);
						sizeDialog.dismiss();
						
					}
				});
				sizeDialog.show();
				return true;
			}
			case R.id.action_shape: {
				// Configuramos el Dialog de los tamaños
				shapeDialog = new Dialog(this);
				shapeDialog.setContentView(R.layout.shapes_layout);
				shapeDialog.setTitle(getString(R.string.path_shape));
				
				// Definimos el listener
				shapeDialog.findViewById(R.id.roundShape).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathShape(Paint.Cap.ROUND);
						shapeDialog.dismiss();
						
					}
				});
				shapeDialog.findViewById(R.id.squareShape).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathShape(Paint.Cap.SQUARE);
						shapeDialog.dismiss();
						
					}
				});
				shapeDialog.findViewById(R.id.buttShape).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						customView.setPathShape(Paint.Cap.BUTT);
						shapeDialog.dismiss();
						
					}
				});
				shapeDialog.show();
				return true;
			}
			case R.id.action_save: {
				
				try {
					saveDrawAsJPG("p4a_" + Calendar.getInstance().getTimeInMillis());
					Toast.makeText(this, getString(R.string.save_ok), Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					Log.e(TAG, "Error saving draw as JPG: " + e.getCause());
					Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_LONG).show();
				}
				return true;
			}
			case R.id.action_share: {
				try {
					shareDraw();
				} catch (IOException e) {
					Log.e(TAG, "Error sharing draw, maybe saving: " + e.getCause());
					Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_LONG).show();
				}
				return true;
			}
			default:
				return super.onOptionsItemSelected(item);
		}
    }
    
    // Captura el evento onClick en la paleta de colores
    public void onImageClicked(View view) {
    	
    	int newColor = Color.BLACK;
    	if (view.getId() == R.id.customColorImgView) {
    		newColor = customColor;
    		if (currentColor != R.id.customColorImgView) {
    			findViewById(currentColor).setBackgroundColor(Color.WHITE);
    		}
    		
    		currentColor = R.id.customColorImgView;
    		findViewById(R.id.customColorImgView).setBackgroundColor(customColor);
    	} else {
    		newColor = Color.parseColor((String) view.getTag());
    		findViewById(currentColor).setBackgroundColor(Color.WHITE);
    		currentColor = view.getId();
    		findViewById(currentColor).setBackgroundColor(Color.GRAY);
    	}
    	
    	customView.setPathColor(newColor);
    }

	@Override
	public boolean onLongClick(View paramView) {

		switch (paramView.getId()) {
			case R.id.imageViewRubber: {
				new AlertDialog.Builder(this)
		    	.setTitle(R.string.clean_message)
		    	.setPositiveButton(android.R.string.yes,
		    	new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	customView.clearView();
		            }
		        })
		    	.setNegativeButton(android.R.string.no,
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                dialog.dismiss();
		            }
		        }).create().show();
				return true;
			}
			case R.id.customColorImgView: {
				
				onImageClicked(paramView);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
		    	.setTitle(getString(R.string.set_custom_color));
		    	final EditText input = new EditText(this);
		    	
		    	// Limitamos le número de caracteres a 6 (RRGGBB)
		    	int maxLength = 6;
		    	InputFilter[] fArray = new InputFilter[1];
		    	fArray[0] = new InputFilter.LengthFilter(maxLength);
		    	input.setFilters(fArray);
		    	
		    	builder.setView(input);
		    	builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						if (!TextUtils.isEmpty(input.getText().toString())) {
							try {
								
								// Obtengo el nuevo color personalizado
								customColor = Color.parseColor("#"+input.getText().toString());
								
								// Muestro el color personalizado en la paleta
								customColorView.setBackgroundColor(customColor);
								currentColor = R.id.customColorImgView;
								// Acutlizo el color del trazado
								customView.setPathColor(customColor);
							} catch (Exception e) {
								Log.d(TAG, "Color not valid!");
							}
						}
					}
				});
		    	builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				});
				AlertDialog alert = builder.create();
		    	alert.show();
				return true;
			}
			default:
				return false;
		}
	}
	
	/**
	 * Guarda la imagen en la sd en la carpeta paint4android
	 * @param fileName - nombre del archivo con el que se guardará en la SD
	 * @throws IOException
	 */
	public void saveDrawAsJPG(String fileName) throws IOException {
		Log.d(TAG, "Saving file as JPG");
		File folder = new File(Environment.getExternalStorageDirectory() 
			+ File.separator + FOLDER_NAME);
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		String fName = Environment.getExternalStorageDirectory() + 
			File.separator + FOLDER_NAME + File.separator + fileName;
		
		OutputStream stream = new FileOutputStream(fName);
		
		// Write bitmap to file using JPEG or PNG and 80% quality hint for JPEG
		customView.getcBitmap().compress(CompressFormat.JPEG, 80, stream);
		stream.close();
	}
	
	/**
	 * Guarda la imagen en la sd en la carpeta paint4android
	 * @throws IOException
	 */
	public void shareDraw() throws IOException {
		Log.d(TAG, "Sharing file");
		
		// Guardamos de forma temporal
		String fileName = "p4a_shared_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
		saveDrawAsJPG(fileName);
		
		// Recuperamos y lo compartimos
		File file = new File(Environment.getExternalStorageDirectory()
			+ File.separator + FOLDER_NAME,  fileName);
		
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/jpeg");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
        startActivity(Intent.createChooser(sendIntent, getString(R.string.action_share))
        	.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
	}

	@Override
	public void onAccuracyChanged(Sensor sensorEvent, int accuracy) {
		// Nothing to do
		
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		// Suma de los valores del evento
		float sum = sensorEvent.values[0] + sensorEvent.values[1] + sensorEvent.values[2];
		
		// Si la suma supera el umbral borramos el contenido de la vista
		if (sum > ACCELERATION_UMBRAL) {
			Log.d(TAG, "Sum > ACCELERATION_UMBRAL");
			customView.clearView();
		}
	}
}
