package com.evancharlton.mileage;

import com.evancharlton.mileage.adapters.FakeAdapter;
import com.evancharlton.mileage.tests.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class VehicleListActivityTest extends TestCase {
	protected VehicleListFragment activity;

	private final FakeAdapter mMockAdapter = new FakeAdapter();

	@Before
	public void setUp() {
		activity = new VehicleListFragment(mMockAdapter);
	}

	@Test
	public void testCanDelete() {
		mMockAdapter.setCount(1);
		assertFalse(activity.canDelete(0));

		mMockAdapter.setCount(2);
		assertTrue(activity.canDelete(0));
	}
}
