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
    private val taskId1 = 10
    private val taskAffinity1 = "me.ycdev.test.pkg"
    private val taskId2 = 5

    private val testComponent1 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz1")
    private val testComponent2 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz2")
    private val testComponent3 = ComponentName("me.ycdev.test.pkg", "me.ycdev.test.clazz3")

    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()

    private fun addAndCheckActivities(task: ActivityTask, vararg activities: ActivityRunningState) {
        activities.forEach {
            task.addActivity(it)
        }
        checkActivities(task, *activities)
    }

    private fun checkActivities(task: ActivityTask, vararg activities: ActivityRunningState) {
        val lastActivity = activities.last()
        assertThat(task.topActivity()).isEqualTo(lastActivity)
        assertThat(task.lastActivity(lastActivity.componentName, lastActivity.hashCode)).isEqualTo(lastActivity)

        // pop last one
        assertThat(task.popActivity(lastActivity.componentName, lastActivity.hashCode)).isEqualTo(lastActivity)

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
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        val activity2 = ActivityRunningState(testComponent2, 0xa002, taskId1, ActivityRunningState.State.Paused)
        val activity3 = ActivityRunningState(testComponent3, 0xa003, taskId1, ActivityRunningState.State.Resumed)

        addAndCheckActivities(task, activity1, activity2, activity3)
    }

    @Test
    fun addActivity_same() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        val activity2 = ActivityRunningState(testComponent1, 0xa002, taskId1, ActivityRunningState.State.Paused)
        val activity3 = ActivityRunningState(testComponent1, 0xa003, taskId1, ActivityRunningState.State.Resumed)

        addAndCheckActivities(task, activity1, activity2, activity3)
    }

    @Test
    fun addActivity_notMatched() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId2, ActivityRunningState.State.Stopped)

        exceptionRule.expectMessage("Activity taskId[5] != AppTask[10]")
        task.addActivity(activity1)
    }

    @Test
    fun addActivity_noCopy() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        task.addActivity(activity1)

        activity1.state = ActivityRunningState.State.Resumed
        assertThat(task.topActivity().state).isEqualTo(ActivityRunningState.State.Resumed)
    }

    @Test
    fun popActivity_notMatched() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        task.addActivity(activity1)

        exceptionRule.expectMessage("Cannot find ComponentInfo{me.ycdev.test.pkg/me.ycdev.test.clazz2}@a002")
        task.popActivity(testComponent2, 0xa002)
    }

    @Test
    fun lastActivity_notMatched() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        task.addActivity(activity1)

        exceptionRule.expectMessage("Cannot find ComponentInfo{me.ycdev.test.pkg/me.ycdev.test.clazz2}@a002")
        task.lastActivity(testComponent2, 0xa002)
    }

    @Test
    fun lastActivity_noCopy() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        task.addActivity(activity1)

        task.lastActivity(testComponent1, 0xa001).state = ActivityRunningState.State.Resumed
        assertThat(activity1.state).isEqualTo(ActivityRunningState.State.Resumed)
    }

    @Test
    fun topActivity_empty() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        task.addActivity(activity1)
        task.popActivity(testComponent1, 0xa001)

        exceptionRule.expectMessage("The task is empty. Cannot get the top Activity.")
        task.topActivity()
    }

    @Test
    fun topActivity_noCopy() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        task.addActivity(activity1)

        task.topActivity().state = ActivityRunningState.State.Resumed
        assertThat(activity1.state).isEqualTo(ActivityRunningState.State.Resumed)
    }

    @Test
    fun getActivityStack_noCopy() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        val activity2 = ActivityRunningState(testComponent2, 0xa002, taskId1, ActivityRunningState.State.Paused)
        val activity3 = ActivityRunningState(testComponent3, 0xa003, taskId1, ActivityRunningState.State.Resumed)

        task.addActivity(activity1)
        task.addActivity(activity2)
        task.addActivity(activity3)

        task.getActivityStack().forEach {
            it.state = ActivityRunningState.State.Destroyed
        }

        assertThat(activity1.state).isEqualTo(ActivityRunningState.State.Destroyed)
        assertThat(activity2.state).isEqualTo(ActivityRunningState.State.Destroyed)
        assertThat(activity2.state).isEqualTo(ActivityRunningState.State.Destroyed)
    }

    @Test
    fun isEmpty() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)

        assertThat(task.isEmpty()).isTrue()
        task.addActivity(activity1)
        assertThat(task.isEmpty()).isFalse()
        task.popActivity(testComponent1, 0xa001)
        assertThat(task.isEmpty()).isTrue()
    }

    @Test
    fun makeCopy() {
        val task = ActivityTask(taskId1, taskAffinity1)
        val activity1 = ActivityRunningState(testComponent1, 0xa001, taskId1, ActivityRunningState.State.Stopped)
        val activity2 = ActivityRunningState(testComponent2, 0xa002, taskId1, ActivityRunningState.State.Paused)
        val activity3 = ActivityRunningState(testComponent3, 0xa003, taskId1, ActivityRunningState.State.Resumed)

        task.addActivity(activity1)
        task.addActivity(activity2)
        task.addActivity(activity3)

        val copiedTask = task.makeCopy()
        checkActivities(copiedTask, activity1, activity2, activity3)

        copiedTask.getActivityStack().forEach {
            it.state = ActivityRunningState.State.Destroyed
        }
        assertThat(activity1.state).isEqualTo(ActivityRunningState.State.Stopped)
        assertThat(activity2.state).isEqualTo(ActivityRunningState.State.Paused)
        assertThat(activity3.state).isEqualTo(ActivityRunningState.State.Resumed)
    }
}
