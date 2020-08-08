package me.ycdev.android.lib.common.activity

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.pm.ActivityInfo.LAUNCH_MULTIPLE
import android.content.pm.ActivityInfo.LAUNCH_SINGLE_TASK
import android.content.pm.ActivityInfo.LAUNCH_SINGLE_TOP
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityTaskTrackerTest {
    @Before
    fun setup() {
        ActivityTaskTracker.reset()
    }

    @After
    fun tearDown() {
        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(0)
    }

    @Test
    fun oneTask() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

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

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)

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

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(3)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent3)

        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(1)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(0)
    }

    @Test
    fun oneTask_resumePrevious() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

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

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent2)

        // resume Activity 1
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.topActivity().componentName).isEqualTo(testComponent1)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(0)
    }

    @Test
    fun twoTasks() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

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

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)

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

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(3)

        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent3)

        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(2)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(0)
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
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)

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
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)

        // clear the task
        focusedTask.popActivity(testComponent1, activity1.hashCode())
        assertThat(focusedTask.isEmpty()).isTrue()

        // get again
        focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)

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
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)
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
        assertThat(allTasks[0].topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)
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
        assertThat(allTasks[0].topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)
        assertThat(allTasks[0].topActivity().componentName).isEqualTo(testComponent1)

        assertThat(ActivityTaskTracker.getAllTasks()).hasSize(2)
        val focusedTask = ActivityTaskTracker.getFocusedTask()
        assertThat(focusedTask).isNotNull()
        assertThat(focusedTask!!.taskId).isEqualTo(10)
        assertThat(focusedTask.topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)
        assertThat(focusedTask.topActivity().componentName).isEqualTo(testComponent3)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
    }

    @Test
    fun activityTaskReparenting() {
        // task1
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

        // start Activity 2
        val activity2 = mockActivity(testComponent2, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)

        // task 2
        // start Activity 3
        val activity3 = mockActivity(testComponent3, 5)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity3, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity3)
        // activity 2 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity3)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(3)

        // start Activity 4
        val taskIdProvider4 = TaskIdProvider(5)
        val activity4 = mockActivity(testComponent4, taskIdProvider4)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity4, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity4)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity4)
        // activity 3 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity3)

        // All tasks go to background
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity4)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(4)

        // Activity 4 was re-parented to task1
        taskIdProvider4.taskId = 10
        assertThat(activity4.taskId).isEqualTo(10)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity4)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity4)

        val allTasks = ActivityTaskTracker.getAllTasks()
        assertThat(allTasks).hasSize(2)
        assertThat(allTasks[0].taskId).isEqualTo(10)
        assertThat(allTasks[0].taskAffinity).isEqualTo(taskAffinity1)
        assertThat(allTasks[0].topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)
        assertThat(allTasks[0].topActivity().componentName).isEqualTo(testComponent4)
        assertThat(allTasks[0].getActivityStack()).hasSize(3)
        assertThat(allTasks[1].taskId).isEqualTo(5)
        assertThat(allTasks[1].taskAffinity).isEqualTo(taskAffinity2)
        assertThat(allTasks[1].topActivity().state).isEqualTo(ActivityRunningState.State.Stopped)
        assertThat(allTasks[1].topActivity().componentName).isEqualTo(testComponent3)
        assertThat(allTasks[1].getActivityStack()).hasSize(1)

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity4)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity3)
    }

    @Test
    fun taskClear() {
        // start Activity 1
        val activity1 = mockActivity(testComponent1, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity1, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

        // start Activity 2
        val activity2 = mockActivity(testComponent2, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2)
        // activity 1 went to background
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity1)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(2)

        // start Activity 2 again and clear the task (all existing Activities will be destroyed)
        // Activity 1 destroyed first
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity1)
        ActivityTaskTracker.lifecycleCallback.onActivityPaused(activity2)
        // a new instance of Activity 2 created
        val activity2n = mockActivity(testComponent2, 10)
        ActivityTaskTracker.lifecycleCallback.onActivityCreated(activity2n, null)
        ActivityTaskTracker.lifecycleCallback.onActivityStarted(activity2n)
        ActivityTaskTracker.lifecycleCallback.onActivityResumed(activity2n)
        // old Activity 2 destroyed
        ActivityTaskTracker.lifecycleCallback.onActivityStopped(activity2)
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2)

        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(1)

        ActivityTaskTracker.getAllTasks().let { allTasks ->
            assertThat(allTasks).hasSize(1)
            allTasks[0].getActivityStack().let {
                assertThat(it).hasSize(1)
                assertThat(it[0].componentName).isEqualTo(testComponent2)
                assertThat(it[0].state).isEqualTo(ActivityRunningState.State.Resumed)
                assertThat(it[0].hashCode).isEqualTo(activity2n.hashCode())
            }
        }

        // clean up
        ActivityTaskTracker.lifecycleCallback.onActivityDestroyed(activity2n)
        assertThat(ActivityTaskTracker.getTotalActivitiesCount()).isEqualTo(0)
    }

    private fun mockActivity(componentName: ComponentName, taskId: Int): Activity {
        val activity = mockk<Activity>()
        every { activity.componentName } returns componentName
        every { activity.taskId } returns taskId
        return activity
    }

    private fun mockActivity(componentName: ComponentName, taskIdProvider: TaskIdProvider): Activity {
        val activity = mockk<Activity>()
        every { activity.componentName } returns componentName
        every { activity.taskId }.answers { taskIdProvider.taskId }
        return activity
    }

    private data class TaskIdProvider(var taskId: Int)

    companion object {
        private const val taskAffinity1 = "me.ycdev.test.pkg"
        private const val taskAffinity2 = "me.ycdev.taks2"

        private val testComponent1 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz1")
        private val testMeta1 = ActivityMeta(testComponent1, taskAffinity1, LAUNCH_MULTIPLE, false)
        private val testComponent2 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz2")
        private val testMeta2 = ActivityMeta(testComponent2, taskAffinity1, LAUNCH_SINGLE_TOP, false)
        private val testComponent3 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz3")
        private val testMeta3 = ActivityMeta(testComponent3, taskAffinity2, LAUNCH_SINGLE_TASK, false)
        private val testComponent4 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz4")
        private val testMeta4 = ActivityMeta(testComponent4, taskAffinity1, LAUNCH_MULTIPLE, true)

        @BeforeClass @JvmStatic
        fun setupClass() {
            ActivityMeta.initCache(testMeta1, testMeta2, testMeta3, testMeta4)

            val app = mockk<Application>()
            every { app.registerActivityLifecycleCallbacks(any()) } just Runs
            ActivityTaskTracker.init(app)
        }
    }
}
