package com.evancharlton.mileage;

import com.evancharlton.mileage.adapters.FakeAdapter;
import com.evancharlton.mileage.tests.TestCase;

public class VehicleListActivityTest extends TestCase {
	protected VehicleListFragment activity;

	private final FakeAdapter mMockAdapter = new FakeAdapter();

	protected void setUp() throws Exception {
		super.setUp();

		activity = new VehicleListFragment(mMockAdapter);
	}

	public void testCanDelete() {
		mMockAdapter.setCount(1);
		assertFalse(activity.canDelete(0));

		mMockAdapter.setCount(2);
		assertTrue(activity.canDelete(0));
	}
}
