package sumsum.freeworkout.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import sumsum.freeworkout.db.DbManager;
import sumsum.freeworkout.db.TimeDateUtils;
import sumsum.freeworkout.model.DayResult;
import sumsum.freeworkout.model.MedalType;
import android.test.AndroidTestCase;

public class DbManagerTest extends AndroidTestCase {
	
	private static final String TEST_DATE_1 = "01-07-2013 12:00:00";
	private static final String TEST_DATE_2 = "16-07-2013 12:00:00";
	private static final String TEST_DATE_3 = "31-07-2013 12:00:00";
	private static final String TEST_DATE_4 = "30-06-2013 12:00:00";
	private static final String TEST_DATE_5 = "01-08-2013 12:00:00";
	
	private static final int TEST_COUNTS = 3;
	private static final String TEST_BEGIN_PERIOD = "01-07-2013 12:00:00";
	private static final String TEST_END_PERIOD =   "31-07-2013 12:00:00";
	
	private static final long TEST_TIMESTAMP_1 = TimeDateUtils.getDayLong(TEST_DATE_1); 
	private static final long TEST_TIMESTAMP_2 = TimeDateUtils.getDayLong(TEST_DATE_2);
	private static final long TEST_TIMESTAMP_3 = TimeDateUtils.getDayLong(TEST_DATE_3);
	private static final long TEST_TIMESTAMP_4 = TimeDateUtils.getDayLong(TEST_DATE_4);
	private static final long TEST_TIMESTAMP_5 = TimeDateUtils.getDayLong(TEST_DATE_5);
	
	private static final long TEST_TIMESTAMP_BEGIN_PERIOD = TimeDateUtils.getDayLong(TEST_BEGIN_PERIOD);
	private static final long TEST_TIMESTAMP_END_PERIOD = TimeDateUtils.getDayLong(TEST_END_PERIOD);
	
	private static final long[] TEST_TIMESTAMPS = {TEST_TIMESTAMP_1, 
		TEST_TIMESTAMP_2, TEST_TIMESTAMP_3, TEST_TIMESTAMP_4, TEST_TIMESTAMP_5};
	
	private static final int TEST_RESULT_1 = MedalType.GOLD_MEDAL;
	private static final int TEST_RESULT_2 = MedalType.GOLD_MEDAL;
	private static final int TEST_RESULT_3 = MedalType.SILVER_MEDAL;	
	private static final int TEST_RESULT_4 = MedalType.SILVER_MEDAL;
	private static final int TEST_RESULT_5 = MedalType.GOLD_MEDAL;
	
	DbManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = new DbManager(getContext());
		manager.open();
	}
	
	public void testInsertStatDay() {
		ArrayList <DayResult> listBeans = getTestBeanList(); 
		populateDatabaseByTestBeans(listBeans);
		
		for (int i = 0; i < listBeans.size(); i++) {
			assertEquals(listBeans.get(i), manager.getStatDay(TEST_TIMESTAMPS[i]));
		}
	}
	
	public void testUpdateStatDay() {
		DayResult testBean = getTestBean(TEST_TIMESTAMP_1, TEST_RESULT_1);
		manager.insertStatDay(testBean);
		assertEquals(testBean, manager.getStatDay(TEST_TIMESTAMP_1));
		
		DayResult updatedBean = manager.getStatDay(TEST_TIMESTAMP_1);
		updatedBean.setResultPerDay(TEST_RESULT_3);
		manager.updateStatDay(updatedBean);
		
		assertEquals(updatedBean, manager.getStatDay(TEST_TIMESTAMP_1));
		assertNotSame(testBean, manager.getStatDay(TEST_TIMESTAMP_1));
	}
	
	public void testDeleteStatDay() {
		ArrayList <DayResult> listBeans = getTestBeanList(); 
		populateDatabaseByTestBeans(listBeans);
		
		assertNotNull(manager.getStatDay(TEST_TIMESTAMP_2));
		manager.deleteStatDay(TEST_TIMESTAMP_2);
		assertEquals(MedalType.NO_MEDAL, manager.getStatDay(TEST_TIMESTAMP_2).getResultPerDay());
	}
	
	
	public void testGetStatisticsForPeriod() {
		ArrayList <DayResult> listBeans = getTestBeanList(); 
		populateDatabaseByTestBeans(listBeans);
		
		HashMap <Long, Integer> listPerPeriod = manager
				.getStatisticsForPeriod(TEST_TIMESTAMP_BEGIN_PERIOD, TEST_TIMESTAMP_END_PERIOD);
		assertEquals(TEST_COUNTS, listPerPeriod.size());
		assertTrue(listPerPeriod.containsKey(TEST_TIMESTAMP_1));
		assertTrue(listPerPeriod.containsKey(TEST_TIMESTAMP_2));
		assertTrue(listPerPeriod.containsKey(TEST_TIMESTAMP_3));
		assertFalse(listPerPeriod.containsKey(TEST_TIMESTAMP_4));
		assertFalse(listPerPeriod.containsKey(TEST_TIMESTAMP_5));
		
		assertTrue(listPerPeriod.containsValue(TEST_RESULT_1));
		assertTrue(listPerPeriod.containsValue(TEST_RESULT_2));
		assertTrue(listPerPeriod.containsValue(TEST_RESULT_3));
	}
	
	public void testGetStatisticsForPeriod2() {
		ArrayList <DayResult> listBeans = getTestBeanList(); 
		populateDatabaseByTestBeans(listBeans);
		
		Calendar month = Calendar.getInstance();
		month.set(Calendar.MONTH, Calendar.JUNE);
		HashMap <Long, Integer> results = manager.getStatisticsForPeriod(month);
		assertEquals(1, results.size());
		assertEquals((int) results.get(TEST_TIMESTAMP_4), TEST_RESULT_4);
		
		month.set(Calendar.MONTH, Calendar.JULY);
		results.clear();
		results = manager.getStatisticsForPeriod(month);
		assertEquals(3, results.size());
		assertEquals((int) results.get(TEST_TIMESTAMP_1), TEST_RESULT_1);
		
		month.set(Calendar.MONTH, Calendar.AUGUST);
		results.clear();
		results = manager.getStatisticsForPeriod(month);
		assertEquals(1, results.size());
		assertEquals((int) results.get(TEST_TIMESTAMP_5), TEST_RESULT_5);
	}
	
	public void testDeleteAllStatDays() {
		ArrayList <DayResult> listBeans = getTestBeanList(); 
		populateDatabaseByTestBeans(listBeans);
		assertEquals(listBeans.size(), manager.getStatisticsForPeriod(0, TEST_TIMESTAMP_5 + 1000).size());
		
		manager.deleteAllStatDays();
		assertTrue(manager.getStatisticsForPeriod(0, Calendar.getInstance().getTimeInMillis()) == null);
	}
	
	public void testGetStatDay() {
		DayResult testBean = getTestBean(TEST_TIMESTAMP_1, TEST_RESULT_1);
		manager.insertStatDay(testBean);
		assertNotNull(manager.getStatDay(TEST_TIMESTAMP_1));
		assertEquals(TEST_RESULT_1, manager.getStatDay(TEST_TIMESTAMP_1).getResultPerDay());
		assertEquals(MedalType.NO_MEDAL, manager.getStatDay(TEST_TIMESTAMP_2).getResultPerDay());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.deleteAllStatDays();
		manager.close();
	}
	
	private void populateDatabaseByTestBeans (ArrayList <DayResult> listBeans) {
		for(DayResult dayResult : listBeans) {
			manager.insertStatDay(dayResult);
		}
	}
	
	private ArrayList <DayResult> getTestBeanList () {
		ArrayList <DayResult> list = new ArrayList<DayResult>();
		list.add(getTestBean(TEST_TIMESTAMP_1, TEST_RESULT_1));
		list.add(getTestBean(TEST_TIMESTAMP_2, TEST_RESULT_2));
		list.add(getTestBean(TEST_TIMESTAMP_3, TEST_RESULT_3));
		list.add(getTestBean(TEST_TIMESTAMP_4, TEST_RESULT_4));
		list.add(getTestBean(TEST_TIMESTAMP_5, TEST_RESULT_5));
		return list;
	}
	
	private DayResult getTestBean (long timestamp, int  resultPerDay) {
		DayResult dayResult = new DayResult();
		dayResult.setTimestamp(timestamp);
		dayResult.setResultPerDay(resultPerDay);
		return dayResult;
	}
	
	
}
