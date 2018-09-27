package org.robolectric.shadows.androidx.fragment;

import static com.google.common.truth.Truth.assertThat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;

/** Tests for {@link FragmentController} */
@RunWith(TestRunnerWithManifest.class)
public class FragmentControllerTest {

  @After
  public void tearDown() {
    TranscriptFragment.clearLifecycleEvents();
  }

  @Test
  public void initialNotAttached() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment());

    assertThat(controller.get().getView()).isNull();
    assertThat(controller.get().getActivity()).isNull();
    assertThat(controller.get().isAdded()).isFalse();
  }

  @Test
  public void initialNotAttached_customActivity() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class);

    assertThat(controller.get().getView()).isNull();
    assertThat(controller.get().getActivity()).isNull();
    assertThat(controller.get().isAdded()).isFalse();
  }

  @Test
  public void attachedAfterCreate() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment());

    controller.create();

    assertThat(controller.get().getActivity()).isNotNull();
    assertThat(controller.get().isAdded()).isTrue();
    assertThat(controller.get().isResumed()).isFalse();
  }

  @Test
  public void attachedAfterCreate_customActivity() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class);

    controller.create();

    assertThat(controller.get().getActivity()).isNotNull();
    assertThat(controller.get().getActivity()).isInstanceOf(TestActivity.class);
    assertThat(controller.get().isAdded()).isTrue();
    assertThat(controller.get().isResumed()).isFalse();
  }

  @Test
  public void attachedAfterCreate_customizedViewId() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), CustomizedViewIdTestActivity.class);

    controller.create(R.id.custom_activity_view, null).start();

    assertThat(controller.get().getView()).isNotNull();
    assertThat(controller.get().getActivity()).isNotNull();
    assertThat(controller.get().isAdded()).isTrue();
    assertThat(controller.get().isResumed()).isFalse();
    assertThat((TextView) controller.get().getView().findViewById(R.id.tacos)).isNotNull();
  }

  @Test
  public void hasViewAfterStart() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment());

    controller.create().start();

    assertThat(controller.get().getView()).isNotNull();
  }

  @Test
  public void isResumed() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class);

    controller.create().start().resume();

    assertThat(controller.get().getView()).isNotNull();
    assertThat(controller.get().getActivity()).isNotNull();
    assertThat(controller.get().isAdded()).isTrue();
    assertThat(controller.get().isResumed()).isTrue();
    assertThat((TextView) controller.get().getView().findViewById(R.id.tacos)).isNotNull();
  }

  @Test
  public void isPaused() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class);

    controller.create().start().resume().pause();

    assertThat(controller.get().getView()).isNotNull();
    assertThat(controller.get().getActivity()).isNotNull();
    assertThat(controller.get().isAdded()).isTrue();
    assertThat(controller.get().isResumed()).isFalse();
    assertThat(controller.get().getLifecycleEvents())
        .containsExactly("onCreate", "onStart", "onResume", "onPause")
        .inOrder();
  }

  @Test
  public void isStopped() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class);

    controller.create().start().resume().pause().stop();

    assertThat(controller.get().getView()).isNotNull();
    assertThat(controller.get().getActivity()).isNotNull();
    assertThat(controller.get().isAdded()).isTrue();
    assertThat(controller.get().isResumed()).isFalse();
    assertThat(controller.get().getLifecycleEvents())
        .containsExactly("onCreate", "onStart", "onResume", "onPause", "onStop")
        .inOrder();
  }

  @Test
  public void withIntent() {
    final Intent intent = generateTestIntent();
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class, intent);

    controller.create();
    final Intent intentInFragment = controller.get().getActivity().getIntent();

    assertThat(intentInFragment.getAction()).isEqualTo("test_action");
    assertThat(intentInFragment.getExtras().getString("test_key")).isEqualTo("test_value");
  }

  @Test
  public void withArguments() {
    final Bundle bundle = generateTestBundle();
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class, bundle);

    controller.create();
    final Bundle args = controller.get().getArguments();

    assertThat(args.getString("test_key")).isEqualTo("test_value");
  }

  @Test
  public void withIntentAndArguments() {
    final Bundle bundle = generateTestBundle();
    final Intent intent = generateTestIntent();
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class, intent, bundle);

    controller.create();
    final Intent intentInFragment = controller.get().getActivity().getIntent();
    final Bundle args = controller.get().getArguments();

    assertThat(intentInFragment.getAction()).isEqualTo("test_action");
    assertThat(intentInFragment.getExtras().getString("test_key")).isEqualTo("test_value");
    assertThat(args.getString("test_key")).isEqualTo("test_value");
  }

  @Test
  public void visible() {
    final FragmentController<TranscriptFragment> controller =
        FragmentController.of(new TranscriptFragment(), TestActivity.class);

    controller.create().start().resume();

    assertThat(controller.get().isVisible()).isFalse();

    controller.visible();

    assertThat(controller.get().isVisible()).isTrue();
  }

  @Test
  public void setupFragmentWithFragment_fragmentHasCorrectLifecycle() {
    TranscriptFragment fragment = FragmentController.setupFragment(new TranscriptFragment());

    assertThat(fragment.getLifecycleEvents())
        .containsExactly("onCreate", "onStart", "onResume")
        .inOrder();
    assertThat(fragment.isVisible()).isTrue();
  }

  @Test
  public void setupFragmentWithFragmentAndActivity_fragmentHasCorrectLifecycle() {
    TranscriptFragment fragment =
        FragmentController.setupFragment(new TranscriptFragment(), TestActivity.class);

    assertThat(fragment.getLifecycleEvents())
        .containsExactly("onCreate", "onStart", "onResume")
        .inOrder();
    assertThat(fragment.isVisible()).isTrue();
  }

  @Test
  public void setupFragmentWithFragmentAndActivityAndBundle_fragmentHasCorrectLifecycle() {
    Bundle testBundle = generateTestBundle();
    TranscriptFragment fragment =
        FragmentController.setupFragment(new TranscriptFragment(), TestActivity.class, testBundle);

    assertThat(fragment.getLifecycleEvents())
        .containsExactly("onCreate", "onStart", "onResume")
        .inOrder();
    assertThat(fragment.isVisible()).isTrue();
  }

  @Test
  public void
      setupFragmentWithFragmentAndActivityAndContainViewIdAndBundle_fragmentHasCorrectLifecycle() {
    Bundle testBundle = generateTestBundle();
    TranscriptFragment fragment =
        FragmentController.setupFragment(
            new TranscriptFragment(),
            CustomizedViewIdTestActivity.class,
            R.id.custom_activity_view,
            testBundle);

    assertThat(fragment.getLifecycleEvents())
        .containsExactly("onCreate", "onStart", "onResume")
        .inOrder();
    assertThat(fragment.isVisible()).isTrue();
  }

  private Intent generateTestIntent() {
    final Intent testIntent = new Intent("test_action").putExtra("test_key", "test_value");
    return testIntent;
  }

  private Bundle generateTestBundle() {
    final Bundle testBundle = new Bundle();
    testBundle.putString("test_key", "test_value");

    return testBundle;
  }

  /** A Fragment which can record lifecycle status for test. */
  public static class TranscriptFragment extends Fragment {

    public static final List<String> lifecycleEvents = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      lifecycleEvents.add("onCreate");
    }

    @Override
    public void onStart() {
      super.onStart();
      lifecycleEvents.add("onStart");
    }

    @Override
    public void onResume() {
      super.onResume();
      lifecycleEvents.add("onResume");
    }

    @Override
    public void onPause() {
      super.onPause();
      lifecycleEvents.add("onPause");
    }

    @Override
    public void onStop() {
      super.onStop();
      lifecycleEvents.add("onStop");
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_contents, container, false);
    }

    public List<String> getLifecycleEvents() {
      return lifecycleEvents;
    }

    public static void clearLifecycleEvents() {
      lifecycleEvents.clear();
    }
  }

  /** A Activity which set a default view for test. */
  public static class TestActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      LinearLayout view = new LinearLayout(this);
      view.setId(1);

      setContentView(view);
    }
  }

  /** A Activity which has a custom view for test. */
  public static class CustomizedViewIdTestActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.custom_activity_view);
    }
  }
}
