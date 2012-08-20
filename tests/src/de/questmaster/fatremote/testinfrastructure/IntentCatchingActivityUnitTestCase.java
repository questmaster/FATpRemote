package de.questmaster.fatremote.testinfrastructure;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class IntentCatchingActivityUnitTestCase<T extends Activity> extends ActivityUnitTestCase<T> {

    protected Activity mActivity;
    protected Instrumentation mInst;
    protected Intent[] mCaughtIntents;
    protected IntentCatchingContext mContextWrapper;

    protected class IntentCatchingContext extends ContextWrapper {
        public IntentCatchingContext(Context base) {
            super(base);
        }

        @Override
        public ComponentName startService(Intent service) {
            mCaughtIntents = new Intent[] { service };
            return service.getComponent();
        }

        @Override
        public void startActivities(Intent[] intents) {
            mCaughtIntents = intents;
            super.startActivities(intents);
        }

        @Override
        public void startActivity(Intent intent) {
            mCaughtIntents = new Intent[] { intent };
            super.startActivity(intent);
        }

        @Override
        public boolean stopService(Intent intent) {
            mCaughtIntents = new Intent[] { intent };
            return super.stopService(intent);
        }
    }

    // --//
    public IntentCatchingActivityUnitTestCase(Class<T> activityClass) {
        super(activityClass);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mContextWrapper = new IntentCatchingContext(getInstrumentation().getTargetContext());
        setActivityContext(mContextWrapper);
        startActivity(new Intent(), null, null);
        mInst = getInstrumentation();
        mActivity = getActivity();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}