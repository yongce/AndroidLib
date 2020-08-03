package me.ycdev.android.lib.common.activity

import android.app.Activity
import android.content.ComponentName
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityTaskTrackerTest {
    private val testComponent1 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz1")
    private val testComponent2 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz2")
    private val testComponent3 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz3")

    @Before
    fun setup() {
        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(0)
    }

    @Test
    fun oneTask() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        var focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent1)

        // start Activity 2
        val activity2 = mockActivity(testComponent2, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent2)

        // start Activity 3
        val activity3 = mockActivity(testComponent3, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity3, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity3)
        // activity 2 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent3)

        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(1)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun oneTask_resumePrevious() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        var focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent1)

        // start Activity 2
        // activity 1 went to background first
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity1)
        val activity2 = mockActivity(testComponent2, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent2)

        // resume Activity 1
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent1)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun twoTasks() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        var focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent1)

        // start Activity 2
        val activity2 = mockActivity(testComponent2, 5)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(5)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent2)

        // start Activity 3
        val activity3 = mockActivity(testComponent3, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity3, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity3)
        // activity 2 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent3)

        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(2)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun getFocusedTask_none() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        val focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityInfo.State.Resumed)

        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity1)
        assertThat(ActivityTaskTracker.getFocusedTask()).isNull()

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun getFocusedTask_order() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        var focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent1)

        // start Activity 2 (order case 1)
        val activity2 = mockActivity(testComponent2, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent2)

        // start Activity 3 (order case 2)
        val activity3 = mockActivity(testComponent3, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity3, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity3)
        // activity 2 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity3)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent3)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun getFocusedTask_makeCopy() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        var focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityInfo.State.Resumed)

        // clear the task
        focusedTask.popActivity(testComponent1)
        assertThat(focusedTask.isEmpty()).isTrue()

        // get again
        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityInfo.State.Resumed)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun getAllTasks_focused_position() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        // start Activity 2
        val activity2 = mockActivity(testComponent2, 5)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        // start Activity 3
        val activity3 = mockActivity(testComponent3, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity3, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity3)
        // activity 2 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity3)

        val allTasks = ActivityTaskTracker.getAllTasks()
        assertThat(allTasks).hasSize(2)
        val focusedTask = allTasks[0]
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityInfo.State.Resumed)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent3)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun getAllTasks_makeCopy() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        val allTasks = ActivityTaskTracker.getAllTasks()
        assertThat(allTasks).hasSize(1)
        assertThat(allTasks[0].taskId).isEqualTo(10)
        assertThat(allTasks[0].topActivity().state).isEqualTo(ActivityInfo.State.Resumed)
        assertThat(allTasks[0].topActivity().componentName).isEqualTo(testComponent1)

        // start Activity 2
        val activity2 = mockActivity(testComponent2, 5)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        // start Activity 3
        val activity3 = mockActivity(testComponent3, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity3, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity3)
        // activity 2 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity3)

        // check the preivous copied tasks again
        assertThat(allTasks).hasSize(1)
        assertThat(allTasks[0].taskId).isEqualTo(10)
        assertThat(allTasks[0].topActivity().state).isEqualTo(ActivityInfo.State.Resumed)
        assertThat(allTasks[0].topActivity().componentName).isEqualTo(testComponent1)

        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(2)
        val focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityInfo.State.Resumed)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent3)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    private fun mockActivity(componentName: ComponentName, taskId: Int): Activity {
        val activity = mockk<Activity>()
        every { activity.componentName } returns componentName
        every { activity.taskId } returns taskId
        return activity
    }
}
