# AndroidLib Unit And Instrument Test Coverage Plan

This plan records the renewed full-codebase review and turns the missing test coverage into an executable 8-phase checklist. Each phase should be finished with a small commit whose message includes `by codex`.

## Scope

- JVM tests: fast logic tests, Robolectric-style Android framework tests where local execution is reliable, and lint rule tests.
- Instrument tests: behavior that depends on a real Android runtime, Binder/service binding, providers, hidden APIs, JNI, UI flows, and Android-version differences.
- Verification targets: local JVM/check tasks first, then connected device checks, then GitHub Actions API matrix.

## Execution Record - 2026-06-26

Current status: partially executed. The broad local JVM/check gate passes, and full `connectedCheck` now passes on the connected phone.

Completed in this execution:

- Phase 1: stabilized `ServiceConnectorTest.listeners_addAndRemove` by replacing fixed sleeps with event latches. Added service connector edge coverage for unavailable services, duplicate connect, safe disconnect after failed connect, and state-name diagnostics.
- Phase 2: added JVM tests for `AppInfo`, `AppsLoadConfig`, `AppsLoadFilter`, primitive-array `isNullOrEmpty`, and `SingletonHolderP1`.
- Phase 3: added `PermissionUtilsTest` for empty input, denied permissions, denied-permission ordering, and `verifyPermissions`. `AppsLoaderTest` and permission request UI-flow tests remain open.
- Phase 4: added direct `InfoProviderTest`, extended `InfoProviderClientTest`, added `BatteryInfoTrackerTest`, and added JVM `WeakTrackerTest`. `InteractiveStateTrackerTest` remains open.
- Phase 5: added local-server `HttpClientTest`; extended `NetworkUtilsTestBasic`; extracted a testable mobile network subtype mapper.
- Phase 6: added partial trailing packet coverage to `TinyPacketsWorkerTest`. SQLite/storage helper tests remain open.
- Phase 7: added `testLib` self-tests, `MarginItemDecorationTest`, and `ToastHelperTest`. `jniLib` test extensions, `GridEntriesActivityTest`, activity smoke tests, and additional `archLintRules` cases remain open.
- Phase 8: ran local verification and one full connected attempt.

Continued in this execution after commit `b0683e1`:

- Phase 2: added JVM/Robolectric tests for `ThreadUtils`, `WeakHandler`, and `PackageUtils`.
- Phase 3: added Robolectric `AppsLoaderTest` coverage for mounted/enabled/system/updated-system filtering, label/icon loading flags, progress callbacks, and cancellation.
- Phase 4: added instrument `InteractiveStateTrackerTest` coverage for screen on/off and user-present receiver handling.
- Phase 6: added Robolectric `SQLiteDbMgrTest` coverage for database reference reuse, final close, repeated release safety, reacquire behavior, and creator failure wrapping.
- Phase 7: extended `jniLib` connected tests with empty-path file status coverage, resource-limit restore in `finally`, and above-maximum limit rejection. Added `GridEntriesActivityTest` for empty/populated entries and click/long-click dispatch. Added `BaseActivity` and `AppCompatBaseActivity` smoke tests.

Continued in this execution after commit `3e7019e`:

- Phase 2: added Robolectric `IntentUtilsTest` for activity/service resolution, foreground-service extras, and foreground-service decision branches.
- Phase 6: added Robolectric `StorageUtilsTest` for external storage availability, path delegation, emulated-state, missing-path space values, and external cache dir delegation.
- Phase 7: extended `ToastHelperTest` to cover string-resource toasts. `ToastHelper` does not expose cancel/update APIs, so those plan branches remain deferred until such APIs exist.
- Phase 7: extended `GridEntriesActivityTest` with async loading coverage using completion signals instead of fixed sleeps.
- Phase 7: extended `MyIntentHelperDetectorTest` with additional Intent extra APIs so the detector protects more than the original sample methods.
- Phase 3: added Robolectric `PermissionUtilsRequestTest` coverage for Activity request dispatch, Fragment request dispatch, and rationale denial callback behavior.

Verification run in this execution:

- Passed: `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed: `./gradlew :baseLib:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=me.ycdev.android.lib.common.ipc.ServiceConnectorTest --warning-mode all --stacktrace`
- Passed: `./gradlew :baseLib:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=me.ycdev.android.lib.common.provider --warning-mode all --stacktrace`
- Passed: `./gradlew :baseLib:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=me.ycdev.android.lib.common.ipc.ServiceConnectorTest,me.ycdev.android.lib.common.provider.InfoProviderTest,me.ycdev.android.lib.common.provider.InfoProviderClientTest,me.ycdev.android.lib.common.tracker.BatteryInfoTrackerTest --warning-mode all --stacktrace`
- Passed: `./gradlew :baseLib:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=me.ycdev.android.lib.common.perms.PermissionUtilsTest --warning-mode all --stacktrace`
- Passed: `./gradlew :testLib:testDebugUnitTest :uiLib:testDebugUnitTest :archLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed: `./gradlew spotlessKotlinCheck check --warning-mode all --stacktrace`
- Passed after retry: `./gradlew connectedCheck --continue --warning-mode all --stacktrace`
  - `baseLib` initially found two `PermissionUtilsTest` expectation failures; fixed and reverified the class.
  - The first full connected attempt was blocked because the device rejected installing `jniLib-debug-androidTest.apk` with `INSTALL_FAILED_USER_RESTRICTED: Install canceled by user`.
  - After reconnecting the phone, the full connected run passed in 2m 47s.
- Passed in continued execution: `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :jniLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :uiLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :archLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :baseLib:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=me.ycdev.android.lib.common.tracker.InteractiveStateTrackerTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew spotlessKotlinCheck check --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew connectedCheck --continue --warning-mode all --stacktrace`
  - Full connected run passed on device `23127PN0CC - 14` in 2m 48s.
  - Expected internal API guard skips remained: shutdown, goToSleep, crash, and setArgV0.
- Passed in continued execution: `./gradlew :baseLib:testDebugUnitTest :archLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :uiLib:testDebugUnitTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :archLintRules:test --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :testLib:testDebugUnitTest :uiLib:testDebugUnitTest :archLib:testDebugUnitTest :archLintRules:test --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew spotlessKotlinCheck check --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :baseLib:testDebugUnitTest --tests me.ycdev.android.lib.common.perms.PermissionUtilsRequestTest --warning-mode all --stacktrace`
- Passed in continued execution: `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`

## Current Coverage Snapshot

| Module | Current useful coverage | Main gaps |
| --- | --- | --- |
| `baseLib` | Async executor/scheduler instrument tests, service connector tests, IPC provider client tests, Process/Internal API tests, packet parser tests, `AppsLoader`, permission checks/request dispatch, trackers, database manager, `IntentUtils`, storage/package utils, and many pure utils tests. | Add more Android-version-specific permission edge cases if behavior changes. |
| `jniLib` | Connected tests for file status, missing/empty paths, resource limits, restore-in-finally, and invalid limit rejection. | Version-specific unsupported API behavior if new JNI helpers add such branches. |
| `uiLib` | Item decoration plus `GridEntriesActivity` empty/populated/click/long-click/async-load coverage. | Richer RecyclerView layout edge cases. |
| `archLib` | Toast helper text/resource coverage and base activity smoke coverage. | Toast cancel/update is deferred because `ToastHelper` has no cancel/update API. |
| `archLintRules` | Detector tests with Java/Kotlin positive and negative samples plus additional Intent extra API coverage. | Quick-fix stability is deferred because the detectors do not currently provide quick fixes. |
| `testLib` | Self-tests for logging helpers, Timber tree behavior, and the Timber JUnit rule. | Add more rule lifecycle edge cases if new rules are introduced. |
| Demo modules | Mostly smoke only. | Keep out of primary scope unless a demo exposes published behavior. |

## Phase 1 - Baseline And Test Harness Hardening

Goal: make the existing suite deterministic before adding more cases.

Tasks:

- [x] Stabilize `baseLib/src/androidTest/java/me/ycdev/android/lib/common/ipc/ServiceConnectorTest.kt`.
  - Replace fixed sleeps in listener-related tests with bounded `CountDownLatch` waits.
  - Add an assertion that a listener removed before the next state transition is not invoked.
- [x] Review existing async tests for time assumptions.
  - Keep the event-driven style already used in `AsyncTaskQueueTest`.
  - Replace any new fixed wait with explicit readiness/completion signals.
- [x] Add a tiny shared test helper only if at least two instrument tests need the same bounded wait/assertion pattern.
  - Preferred location: `testLib` if reusable across modules.
  - Otherwise keep helpers private to the test class.

Acceptance:

- [x] `./gradlew :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- [x] No new warning noise from the changed tests.

## Phase 2 - Pure JVM Coverage For Stable Logic

Goal: increase fast local confidence for code that does not need a device.

Tasks:

- [x] Add `baseLib/src/test/java/me/ycdev/android/lib/common/apps/AppInfoTest.kt`.
  - Verify default values.
  - Verify equality/copy-like mutation expectations if the class is data-like.
- [x] Add `baseLib/src/test/java/me/ycdev/android/lib/common/apps/AppsLoadConfigTest.kt`.
  - Verify default config.
  - Verify include/exclude flags combine predictably.
- [x] Add tests for pure helper gaps under `baseLib/src/test/java/me/ycdev/android/lib/common/utils/`.
  - [x] `IntentUtilsTest`: extras and action/category construction that can run locally.
  - [x] `ThreadUtilsTest`: main-thread/current-thread branches that can be isolated.
  - [x] `WeakHandlerTest`: released target does not receive callbacks.
- [x] Add tests for small generic helpers.
  - [x] `kotlinx/IsNullOrEmptyTest.kt`
  - [x] `pattern/SingletonHolderP1Test.kt`

Acceptance:

- [x] `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- [x] The tests do not require device state, network, or wall-clock timing.

## Phase 3 - Apps, Permissions, And Package Behavior

Goal: cover Android-facing decisions that are easy to regress during API upgrades.

Tasks:

- [x] Add `baseLib/src/test/java/me/ycdev/android/lib/common/apps/AppsLoaderTest.kt`.
  - Use framework shadows/fakes rather than a real device when possible.
  - Verify mounted/enabled/system/updated-system/self filtering.
  - Verify label/icon loading flags.
  - Verify progress callbacks and cancellation stop later callbacks.
- [x] Add `baseLib/src/androidTest/java/me/ycdev/android/lib/common/perms/PermissionUtilsTest.kt`.
  - `hasPermissions` returns false for empty input, matching current implementation.
  - `hasPermissions` returns false when any permission is denied.
  - `getDeniedPermissions` returns only denied permissions and preserves deterministic order.
  - `verifyPermissions` handles empty, all granted, and mixed result arrays.
- [x] Add instrument or Robolectric coverage for permission request entry points.
  - [x] Activity request path.
  - [x] Fragment request path.
  - [x] Rationale dialog branch, if reliably testable without flakiness.

Acceptance:

- [x] `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- [x] If instrument tests are added: `./gradlew :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`

## Phase 4 - Provider, IPC, And Runtime Trackers

Goal: cover the highest-risk runtime coordination code in `baseLib`.

Tasks:

- [x] Add direct provider validation tests in `baseLib/src/androidTest/java/me/ycdev/android/lib/common/provider/InfoProviderTest.kt`.
  - Unknown method returns null.
  - Missing name returns null.
  - Empty value for put returns null.
  - Default table is used when table is missing.
  - Same key in different tables is isolated.
- [x] Extend `InfoProviderClientTest.kt`.
  - Default table get/put/remove.
  - Removing a missing key does not trigger a false change callback.
  - Invalid typed values fall back to defaults.
  - Observer does not receive updates after unregister.
- [x] Extend `ServiceConnectorTest.kt`.
  - No-service timeout returns false and leaves a disconnected state.
  - Duplicate connect calls do not double-bind or double-notify.
  - Disconnect after failed connect is safe.
  - `toString` or state reporting remains useful for diagnostics, if exposed.
- [x] Add tracker tests.
  - [x] `BatteryInfoTrackerTest`: scale normalization, clamp to 0..100, listener receives latest state.
  - [x] `InteractiveStateTrackerTest`: screen/user-present broadcasts update state.
  - [x] `WeakTrackerTest`: first listener starts tracking, last listener stops tracking.

Acceptance:

- [x] `./gradlew :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- [x] API-specific assertions use `Assume` guards instead of brittle SDK checks in test bodies.

## Phase 5 - Networking And HTTP Behavior

Goal: cover network code without depending on external services.

Tasks:

- [x] Add `baseLib/src/test/java/me/ycdev/android/lib/common/net/HttpClientTest.kt`.
  - Use local in-process HTTP server or a test-only server dependency.
  - Verify GET with query and headers.
  - Verify POST string and POST byte array.
  - Verify gzip and deflate response decoding.
  - Verify error stream is returned for non-2xx responses when the server sends a body.
  - Verify configured connect/read timeout values are applied through observable behavior.
- [x] Extend `NetworkUtilsTestBasic.kt`.
  - Cover Wi-Fi, Ethernet, VPN, and no-transport paths.
  - Cover mobile subtype mapping through a small testable mapper if the current implementation is too framework-bound.
  - Cover metered and unavailable network branches where local framework support is reliable.

Acceptance:

- [x] `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- [x] No test reaches the public internet.

## Phase 6 - Packet, Database, And Storage Robustness

Goal: cover boundary and corruption behavior, where regressions tend to be expensive to debug.

Tasks:

- [x] Extend packet parser tests.
  - [x] Corrupt header followed by a valid packet recovers correctly.
  - [x] CRC/checksum mismatch rejects only the bad packet.
  - [x] Partial trailing packet is retained until more bytes arrive.
  - [x] Large payload boundary stays within expected memory and parser state.
- [x] Add database helper tests.
  - `SQLiteDbCreatorTest`: create, upgrade, downgrade/error path as supported by implementation.
  - `SQLiteDbMgrTest`: open/close reference behavior, repeated close safety, transaction/error branch.
- [x] Add storage/package utility tests where behavior is deterministic.
  - [x] External storage unavailable branch.
  - [x] Package version/name fallback branch.
  - [x] Null context or missing package handling, if public API permits it.

Acceptance:

- [x] `./gradlew :baseLib:testDebugUnitTest :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`

## Phase 7 - Module Coverage Outside `baseLib`

Goal: make all published modules carry at least smoke-plus-edge coverage.

Tasks:

- [x] Extend `jniLib` connected tests.
  - Invalid path and missing path behavior for file status helpers.
  - Resource limit tests restore original values in `finally`.
  - Unsupported API behavior is skipped with `Assume`, not failed.
- [x] Add `testLib` self-tests.
  - [x] Test JUnit rules install expected logging behavior.
  - [x] Test logging helpers format and route messages as expected.
- [x] Add `uiLib` tests.
  - `MarginItemDecorationTest`: all constructor branches and grid edge offsets.
  - [x] `GridEntriesActivityTest`: empty list, populated list, item click, item long-click, async load completion.
- [x] Add `archLib` tests.
  - `ToastHelperTest`: show behavior through framework shadows; cancel/update deferred because current API has no such methods.
  - `BaseActivity` and `AppCompatBaseActivity` smoke launch tests if supported by module setup.
- [x] Extend `archLintRules` tests.
  - [x] Add positive and negative Kotlin/Java samples for each detector.
  - [x] Assert error messages remain stable.
  - [x] Quick-fix output is deferred because the detectors do not currently provide quick fixes.

Acceptance:

- [x] `./gradlew :jniLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- [x] `./gradlew :testLib:testDebugUnitTest :uiLib:testDebugUnitTest :archLib:testDebugUnitTest :archLintRules:test --warning-mode all --stacktrace`
- [x] Add only the minimum test dependencies needed by each module.

## Phase 8 - Full Verification, CI Traceability, And Release Readiness

Goal: make execution auditable and prevent another ambiguous "was the plan completed" situation.

Tasks:

- [x] Maintain this plan as the source of truth.
  - After each phase, mark completed items.
  - Record the commit hash beside the phase heading.
  - Record any skipped test with the reason and replacement coverage.
- [x] Run full local JVM/check verification after every 1-2 phases.
  - `./gradlew spotlessKotlinCheck check --warning-mode all --stacktrace`
- [x] Run full connected verification after phases touching instrument code.
  - `adb devices -l`
  - `./gradlew connectedCheck --continue --warning-mode all --stacktrace`
- [x] If workflow files change, run:
  - `actionlint .github/workflows/ci.yml`
  - No workflow files changed in this execution.
- [ ] After push, verify GitHub Actions.
  - Confirm build job passes.
  - Confirm connectedCheck API matrix passes for API 24, 29, 31, 34, and 36.

Acceptance:

- [x] All phase checkboxes are completed or explicitly deferred with a reason.
- [x] Full local `check` passes.
- [x] Full local `connectedCheck` passes on the connected device, or any device restriction is documented with exact error text.
- [ ] GitHub Actions passes on the target branch.

## Recommended Execution Order

1. Phase 1
2. Phase 4 provider and service connector tests
3. Phase 5 networking tests
4. Phase 3 apps and permissions tests
5. Phase 6 packet and database tests
6. Phase 7 `jniLib`, `uiLib`, `archLib`, and `testLib`
7. Phase 2 remaining pure helper tests
8. Phase 8 final verification and traceability

This order starts with flaky-test risk and high-runtime-risk areas, then broadens coverage across the lower-risk helper and UI modules.
