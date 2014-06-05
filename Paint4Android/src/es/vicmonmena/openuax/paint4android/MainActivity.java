package es.vicmonmena.openuax.paint4android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
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

	private CustomView customView; 
	private Dialog sizeDialog;
	private Dialog shapeDialog;
	private int currentColor;
	
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
}
