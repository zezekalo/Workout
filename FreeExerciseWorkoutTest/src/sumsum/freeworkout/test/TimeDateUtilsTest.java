package sumsum.freeworkout.test;

import java.util.Calendar;

import android.test.AndroidTestCase;
import android.text.format.DateFormat;
import android.util.Log;

public class TimeDateUtilsTest extends AndroidTestCase {
	
	private static final String TAG = TimeDateUtilsTest.class.getSimpleName();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testCalendar()  {
		Calendar month = Calendar.getInstance();
		month.set(Calendar.MONTH, 1);
		month.set(Calendar.DAY_OF_MONTH, 1);
		Log.d(TAG, "calendar = " + DateFormat.format("dd MMMM yyyy", month));
		int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDayWeek = month.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "lastDay = " + lastDay + " firstDayWeek = "  +firstDayWeek);
		assertNotNull(month);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
}
