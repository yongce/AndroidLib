package me.ycdev.android.lib.common.activity

import android.content.ComponentName
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityTaskTest {
    private val testComponent1 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz1")
    private val testComponent2 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz2")
    private val testComponent3 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz3")

    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()

    private fun addAndCheckActivities(task: ActivityTask, vararg activities: ActivityInfo) {
        activities.forEach {
            task.addActivity(it)
        }
        checkActivities(task, *activities)
    }

    private fun checkActivities(task: ActivityTask, vararg activities: ActivityInfo) {
        val lastActivity = activities.last()
        assertThat(task.topActivity()).isEqualTo(lastActivity)
        assertThat(task.lastActivity(lastActivity.componentName)).isEqualTo(lastActivity)

        // pop last one
        assertThat(task.popActivity(lastActivity.componentName)).isEqualTo(lastActivity)

        // check the remaining stack
        val stack = task.getActivityStack()
        assertThat(stack).hasSize(activities.size - 1)
        var index = activities.size - 2
        while (!stack.isEmpty()) {
            assertThat(stack.pop()).isEqualTo(activities[index--])
        }
    }

    @Test
    fun addActivity_order() {
        val taskId = 10
        val task = ActivityTask(taskId)
        val activity1 = ActivityInfo(testComponent1, taskId, ActivityInfo.State.Stopped)
        val activity2 = ActivityInfo(testComponent2, taskId, ActivityInfo.State.Paused)
        val activity3 = ActivityInfo(testComponent3, taskId, ActivityInfo.State.Resumed)

        addAndCheckActivities(task, activity1, activity2, activity3)
    }

    @Test
    fun addActivity_same() {
        val taskId = 10
        val task = ActivityTask(taskId)
        val activity1 = ActivityInfo(testComponent1, taskId, ActivityInfo.State.Stopped)
        val activity2 = ActivityInfo(testComponent1, taskId, ActivityInfo.State.Paused)
        val activity3 = ActivityInfo(testComponent1, taskId, ActivityInfo.State.Resumed)

        addAndCheckActivities(task, activity1, activity2, activity3)
    }

    @Test
    fun addActivity_notMatched() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 5, ActivityInfo.State.Stopped)

        exceptionRule.expectMessage("Activity taskId[5] != AppTask[10]")
        task.addActivity(activity1)
    }

    @Test
    fun addActivity_noCopy() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)
        task.addActivity(activity1)

        activity1.state = ActivityInfo.State.Resumed
        assertThat(task.topActivity().state).isEqualTo(ActivityInfo.State.Resumed)
    }

    @Test
    fun popActivity_notMatched() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)
        task.addActivity(activity1)

        exceptionRule.expectMessage("Cannot find ComponentInfo{me.ycdev.test.pkg/me.ycdev.test.clazz2}")
        task.popActivity(testComponent2)
    }

    @Test
    fun lastActivity_notMatched() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)
        task.addActivity(activity1)

        exceptionRule.expectMessage("Cannot find ComponentInfo{me.ycdev.test.pkg/me.ycdev.test.clazz2}")
        task.lastActivity(testComponent2)
    }

    @Test
    fun lastActivity_noCopy() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)
        task.addActivity(activity1)

        task.lastActivity(testComponent1).state = ActivityInfo.State.Resumed
        assertThat(activity1.state).isEqualTo(ActivityInfo.State.Resumed)
    }

    @Test
    fun topActivity_empty() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)
        task.addActivity(activity1)
        task.popActivity(testComponent1)

        exceptionRule.expectMessage("The task is empty. Cannot get the top Activity.")
        task.topActivity()
    }

    @Test
    fun topActivity_noCopy() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)
        task.addActivity(activity1)

        task.topActivity().state = ActivityInfo.State.Resumed
        assertThat(activity1.state).isEqualTo(ActivityInfo.State.Resumed)
    }

    @Test
    fun getActivityStack_noCopy() {
        val taskId = 10
        val task = ActivityTask(taskId)
        val activity1 = ActivityInfo(testComponent1, taskId, ActivityInfo.State.Stopped)
        val activity2 = ActivityInfo(testComponent2, taskId, ActivityInfo.State.Paused)
        val activity3 = ActivityInfo(testComponent3, taskId, ActivityInfo.State.Resumed)

        task.addActivity(activity1)
        task.addActivity(activity2)
        task.addActivity(activity3)

        task.getActivityStack().forEach {
            it.state = ActivityInfo.State.Destroyed
        }

        assertThat(activity1.state).isEqualTo(ActivityInfo.State.Destroyed)
        assertThat(activity2.state).isEqualTo(ActivityInfo.State.Destroyed)
        assertThat(activity2.state).isEqualTo(ActivityInfo.State.Destroyed)
    }

    @Test
    fun isEmpty() {
        val task = ActivityTask(10)
        val activity1 = ActivityInfo(testComponent1, 10, ActivityInfo.State.Stopped)

        assertThat(task.isEmpty()).isTrue()
        task.addActivity(activity1)
        assertThat(task.isEmpty()).isFalse()
        task.popActivity(testComponent1)
        assertThat(task.isEmpty()).isTrue()
    }

    @Test
    fun makeCopy() {
        val taskId = 10
        val task = ActivityTask(taskId)
        val activity1 = ActivityInfo(testComponent1, taskId, ActivityInfo.State.Stopped)
        val activity2 = ActivityInfo(testComponent2, taskId, ActivityInfo.State.Paused)
        val activity3 = ActivityInfo(testComponent3, taskId, ActivityInfo.State.Resumed)

        task.addActivity(activity1)
        task.addActivity(activity2)
        task.addActivity(activity3)

        val copiedTask = task.makeCopy()
        checkActivities(copiedTask, activity1, activity2, activity3)

        copiedTask.getActivityStack().forEach {
            it.state = ActivityInfo.State.Destroyed
        }
        assertThat(activity1.state).isEqualTo(ActivityInfo.State.Stopped)
        assertThat(activity2.state).isEqualTo(ActivityInfo.State.Paused)
        assertThat(activity3.state).isEqualTo(ActivityInfo.State.Resumed)
    }
}
