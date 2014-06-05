package es.vicmonmena.openuax.paint4android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Toast;
import es.vicmonmena.openuax.paint4android.view.CustomView;

/**
 * 
 * @author vicmonmena
 *
 */
public class MainActivity extends Activity implements OnLongClickListener {

	private final String TAG = "MainActivity";
	
	private CustomView customView; 
	private Dialog sizeDialog;
	private Dialog shapeDialog;
	private int currentColor;
	
	// Carpeta donde se guardan los dibujos
	private final String FOLDER_NAME = "paint4android";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customView = (CustomView) findViewById(R.id.customView1);
        currentColor = R.id.imageViewBlack;
        
        findViewById(R.id.imageViewRubber).setOnLongClickListener(this);
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
    		
    	int newColor = Color.parseColor((String) view.getTag());
    	findViewById(currentColor).setBackgroundColor(Color.WHITE);
    	currentColor = view.getId();
    	findViewById(currentColor).setBackgroundColor(Color.GRAY);
    	customView.setPathColor(newColor);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putInt("CURRENT_COLOR", currentColor);
    }
    
   @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		currentColor = savedInstanceState.getInt("CURRENT_COLOR");
		findViewById(R.id.imageViewBlack).setBackgroundColor(Color.WHITE);
		findViewById(currentColor).setBackgroundColor(Color.GRAY);
   }


	@Override
	public boolean onLongClick(View paramView) {
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
		return false;
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
}
