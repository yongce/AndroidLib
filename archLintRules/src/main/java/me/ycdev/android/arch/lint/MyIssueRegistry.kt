package me.ycdev.android.arch.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.Issue

@Suppress("unused")
class MyIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() {
            println("!!!!!!!!!!!!! ArchLib lint rules works")
            return listOf(
                MyToastHelperDetector.ISSUE,
                MyBroadcastHelperDetector.ISSUE,
                MyBaseActivityDetector.ISSUE,
                MyIntentHelperDetector.ISSUE
            )
        }

    override val vendor: Vendor = Vendor("ycdev", "android-lib", "https://github.com/yongce/AndroidLib")
    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
}
