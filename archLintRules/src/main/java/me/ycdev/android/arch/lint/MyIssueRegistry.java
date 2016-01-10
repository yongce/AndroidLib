package me.ycdev.android.arch.lint;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import java.util.Arrays;
import java.util.List;

public class MyIssueRegistry extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        System.out.println("!!!!!!!!!!!!! ArchLib lint rules works");
        return Arrays.asList(
                MyToastHelperDetector.ISSUE,
                MyBroadcastHelperDetector.ISSUE,
                MyBaseActivityDetector.ISSUE,
                MyIntentHelperDetector.ISSUE
        );
    }
}
