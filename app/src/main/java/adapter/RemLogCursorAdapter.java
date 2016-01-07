package adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dbcontrollers.MainHelper;

public class RemLogCursorAdapter extends CursorAdapter implements Filterable {
	private LayoutInflater mLayoutInflater;

	private int lastOdometer, remColor;
	private Date today, nextIntervalDate;
	private String distanceLabel;
	public RemLogCursorAdapter(Context context, Cursor c, int odometer,
			Date today) {
		super(context, c, 0);
		mLayoutInflater = LayoutInflater.from(context);
		lastOdometer = odometer;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.today = today;
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String key = cursor.getString(cursor.getColumnIndex(MainHelper.KEY));
		String maintElem = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD2R));
		String maintType = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD3R));
		int reminderType = cursor.getInt(cursor
				.getColumnIndex(MainHelper.FIELD4R));
		String interval = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD5R));
		String intervalSize = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD5Ra));
		String lastInterval = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD6R));
		String nextInterval = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD7R));
		String details = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD8R));
		String date = cursor.getString(cursor
				.getColumnIndex(MainHelper.FIELD9R));

		TextView keyView = (TextView) view.findViewById(R.id.remkey);
		keyView.setText(key);

		TextView maintInfo = (TextView) view.findViewById(R.id.maintInfo);
		TextView lastIntervalView = (TextView) view
				.findViewById(R.id.LastInterval);
		TextView nextIntervalView = (TextView) view
				.findViewById(R.id.nextInterval);
		ImageView toDo = (ImageView) view.findViewById(R.id.imageView2);

		ImageView imageLabel = (ImageView) view.findViewById(R.id.imageView1);

		if (context.getResources().getIdentifier(
				"rem" + maintType.toLowerCase(), "drawable",
				context.getPackageName()) != 0) {
			imageLabel.setImageResource(context.getResources().getIdentifier(
					"rem" + maintType.toLowerCase(), "drawable",
					context.getPackageName()));

		} else {
			imageLabel.setImageResource(R.drawable.remother);
		}

		if (context.getResources().getIdentifier(
				maintElem.toLowerCase() + "color", "color",
				context.getPackageName()) != 0) {

			remColor = context.getResources().getColor(
					context.getResources().getIdentifier(
							maintElem.toLowerCase() + "color", "color",
							context.getPackageName()));

		} else {

			remColor = context.getResources().getColor(R.color.othercolor);
		}

		imageLabel.setColorFilter(remColor, Mode.MULTIPLY);
		
		if (MyListFragment.mileageType == 1) {
			distanceLabel = " miles";
		} else {
			distanceLabel = " km's";
		}
		
			
		if (reminderType == 0) {
			maintInfo.setText(maintType + " " + maintElem + " every "
					+ interval + distanceLabel );
			lastIntervalView.setText(lastInterval + distanceLabel);
			nextIntervalView.setText(nextInterval + distanceLabel);

			if (Integer.parseInt(nextInterval) < lastOdometer) {

//				nextIntervalView
//						.setBackgroundColor(Color.parseColor("#cc2a36"));
				toDo.setImageResource(R.drawable.wrench);
				toDo.setVisibility(View.VISIBLE);
				toDo.setColorFilter(Color.parseColor("#cc2a36"), Mode.MULTIPLY);
				
			} else {

//				nextIntervalView
//						.setBackgroundColor(Color.parseColor("#333333"));
				toDo.setVisibility(View.GONE);

			}

		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			try {
				nextIntervalDate = sdf.parse(nextInterval);

			} catch (ParseException e) {

				nextIntervalDate = new Date();
				e.printStackTrace();
			}

			if (nextIntervalDate.before(today)) {
				toDo.setImageResource(R.drawable.wrench);
				toDo.setVisibility(View.VISIBLE);
				toDo.setColorFilter(Color.parseColor("#cc2a36"), Mode.MULTIPLY);
//				nextIntervalView
//						.setBackgroundColor(Color.parseColor("#cc2a36"));

			} else {
//				nextIntervalView
//						.setBackgroundColor(Color.parseColor("#333333"));

				toDo.setVisibility(View.GONE);

			}
			maintInfo.setText(maintType + " " + maintElem + " every "
					+ interval + " " + intervalSize);
			lastIntervalView.setText(lastInterval);
			nextIntervalView.setText(nextInterval);

		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mLayoutInflater.inflate(R.layout.rowrem, parent, false);

		return v;
	}

}
