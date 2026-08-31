package com.evancharlton.mileage.dao;

import java.util.ArrayList;

import android.database.MatrixCursor;

import com.evancharlton.mileage.provider.tables.FillupsTable;
import com.evancharlton.mileage.tests.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FillupSeriesTest extends TestCase {
	private final FillupSeries mSeries = new FillupSeries();
	private static final String[] PROJECTION = FillupsTable.PROJECTION;
	private static int ID = 1;

	private static Object[] createFillup(int vehicleId, double odometer, double volume, double price, boolean partial, boolean restart) {
		Object[] values = new Object[PROJECTION.length];
		int i = 0;
		values[i++] = ID++;
		values[i++] = volume * price;
		values[i++] = price;
		values[i++] = volume;
		values[i++] = odometer;
		values[i++] = 0; // economy
		values[i++] = vehicleId;
		values[i++] = System.currentTimeMillis();
		values[i++] = 0; // latitude
		values[i++] = 0; // longitude
		values[i++] = partial ? 1 : 0;
		values[i++] = restart ? 1 : 0;
		return values;
	}

	@Before
	public void setUp() {
		ID = 1;
	}

	@After
	public void tearDown() {
		mSeries.clear();
	}

	@Test
	public void testLoadSeries() {
		MatrixCursor cursor = new MatrixCursor(FillupsTable.PROJECTION);
		cursor.addRow(createFillup(1, 100, 10, 10, false, false));
		cursor.addRow(createFillup(1, 200, 10, 10, false, false));

		ArrayList<FillupSeries> series = FillupSeries.load(cursor);
		assertEquals(1, series.size());
	}

	@Test
	public void testLoadSeriesWithRestart() {
		MatrixCursor cursor = new MatrixCursor(FillupsTable.PROJECTION);
		int odometer = 0;
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, true));
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));

		ArrayList<FillupSeries> series = FillupSeries.load(cursor);
		assertEquals(2, series.size());
	}

	@Test
	public void testGetTotalDistance() {
		MatrixCursor cursor = new MatrixCursor(FillupsTable.PROJECTION);
		int odometer = 0;
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));

		ArrayList<FillupSeries> series = FillupSeries.load(cursor);
		FillupSeries underTest = series.get(0);

		assertEquals(100D, underTest.getTotalDistance(), 0.001);
	}

	@Test
	public void testGetTotalVolume() {
		MatrixCursor cursor = new MatrixCursor(FillupsTable.PROJECTION);
		int odometer = 0;
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));

		ArrayList<FillupSeries> series = FillupSeries.load(cursor);
		FillupSeries underTest = series.get(0);

		assertEquals(20D, underTest.getTotalVolume(), 0.001);
	}

	@Test
	public void testGetEconomyVolume() {
		MatrixCursor cursor = new MatrixCursor(FillupsTable.PROJECTION);
		int odometer = 0;
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));
		odometer += 100;
		cursor.addRow(createFillup(1, odometer, 10, 10, false, false));

		ArrayList<FillupSeries> series = FillupSeries.load(cursor);
		FillupSeries underTest = series.get(0);

		assertEquals(10D, underTest.getEconomyVolume(), 0.001);
	}
}
