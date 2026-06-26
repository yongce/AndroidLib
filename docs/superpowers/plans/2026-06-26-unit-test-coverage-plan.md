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

## Current Coverage Snapshot

| Module | Current useful coverage | Main gaps |
| --- | --- | --- |
| `baseLib` | Async executor/scheduler instrument tests, service connector tests, IPC provider client tests, Process/Internal API tests, packet parser tests, many pure utils tests. | `AppsLoader`, permissions, direct `InfoProvider` validation, provider edge cases, trackers, `HttpClient`, database helpers, network subtype mapping, remaining packet corruption/recovery cases. |
| `jniLib` | Connected tests for file status and resource limits. | Error paths, missing/invalid paths, restore-in-finally guarantees, version-specific behavior. |
| `uiLib` | No meaningful test coverage. | Item decoration offsets, grid entry loading, click/long-click dispatch, empty/error states. |
| `archLib` | No meaningful test coverage. | Toast behavior, base activity smoke tests. |
| `archLintRules` | Existing detector tests. | Regression cases for allowed call sites, Java/Kotlin source variants, message stability. |
| `testLib` | No self-tests. | JUnit rules/helpers should be tested so other module tests can trust them. |
| Demo modules | Mostly smoke only. | Keep out of primary scope unless a demo exposes published behavior. |

## Phase 1 - Baseline And Test Harness Hardening

Goal: make the existing suite deterministic before adding more cases.

Tasks:

- [ ] Stabilize `baseLib/src/androidTest/java/me/ycdev/android/lib/common/ipc/ServiceConnectorTest.kt`.
  - Replace fixed sleeps in listener-related tests with bounded `CountDownLatch` waits.
  - Add an assertion that a listener removed before the next state transition is not invoked.
- [ ] Review existing async tests for time assumptions.
  - Keep the event-driven style already used in `AsyncTaskQueueTest`.
  - Replace any new fixed wait with explicit readiness/completion signals.
- [ ] Add a tiny shared test helper only if at least two instrument tests need the same bounded wait/assertion pattern.
  - Preferred location: `testLib` if reusable across modules.
  - Otherwise keep helpers private to the test class.

Acceptance:

- [ ] `./gradlew :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- [ ] No new warning noise from the changed tests.

## Phase 2 - Pure JVM Coverage For Stable Logic

Goal: increase fast local confidence for code that does not need a device.

Tasks:

- [ ] Add `baseLib/src/test/java/me/ycdev/android/lib/common/apps/AppInfoTest.kt`.
  - Verify default values.
  - Verify equality/copy-like mutation expectations if the class is data-like.
- [ ] Add `baseLib/src/test/java/me/ycdev/android/lib/common/apps/AppsLoadConfigTest.kt`.
  - Verify default config.
  - Verify include/exclude flags combine predictably.
- [ ] Add tests for pure helper gaps under `baseLib/src/test/java/me/ycdev/android/lib/common/utils/`.
  - `IntentUtilsTest`: extras and action/category construction that can run locally.
  - `ThreadUtilsTest`: main-thread/current-thread branches that can be isolated.
  - `WeakHandlerTest`: released target does not receive callbacks.
- [ ] Add tests for small generic helpers.
  - `kotlinx/IsNullOrEmptyTest.kt`
  - `pattern/SingletonHolderP1Test.kt`

Acceptance:

- [ ] `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- [ ] The tests do not require device state, network, or wall-clock timing.

## Phase 3 - Apps, Permissions, And Package Behavior

Goal: cover Android-facing decisions that are easy to regress during API upgrades.

Tasks:

- [ ] Add `baseLib/src/test/java/me/ycdev/android/lib/common/apps/AppsLoaderTest.kt`.
  - Use framework shadows/fakes rather than a real device when possible.
  - Verify mounted/enabled/system/updated-system/self filtering.
  - Verify label/icon loading flags.
  - Verify progress callbacks and cancellation stop later callbacks.
- [ ] Add `baseLib/src/test/java/me/ycdev/android/lib/common/perms/PermissionUtilsTest.kt`.
  - `hasPermissions` returns true for empty input.
  - `hasPermissions` returns false when any permission is denied.
  - `getDeniedPermissions` returns only denied permissions and preserves deterministic order.
  - `verifyPermissions` handles empty, all granted, and mixed result arrays.
- [ ] Add instrument or Robolectric coverage for permission request entry points.
  - Activity request path.
  - Fragment request path.
  - Rationale dialog branch, if reliably testable without flakiness.

Acceptance:

- [ ] `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- [ ] If instrument tests are added: `./gradlew :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`

## Phase 4 - Provider, IPC, And Runtime Trackers

Goal: cover the highest-risk runtime coordination code in `baseLib`.

Tasks:

- [ ] Add direct provider validation tests in `baseLib/src/androidTest/java/me/ycdev/android/lib/common/provider/InfoProviderTest.kt`.
  - Unknown method returns null.
  - Missing name returns null.
  - Empty value for put returns null.
  - Default table is used when table is missing.
  - Same key in different tables is isolated.
- [ ] Extend `InfoProviderClientTest.kt`.
  - Default table get/put/remove.
  - Removing a missing key does not trigger a false change callback.
  - Invalid typed values fall back to defaults.
  - Observer does not receive updates after unregister.
- [ ] Extend `ServiceConnectorTest.kt`.
  - No-service timeout returns false and leaves a disconnected state.
  - Duplicate connect calls do not double-bind or double-notify.
  - Disconnect after failed connect is safe.
  - `toString` or state reporting remains useful for diagnostics, if exposed.
- [ ] Add tracker tests.
  - `BatteryInfoTrackerTest`: scale normalization, clamp to 0..100, listener receives latest state.
  - `InteractiveStateTrackerTest`: screen/user-present broadcasts update state.
  - `WeakTrackerTest`: first listener starts tracking, last listener stops tracking.

Acceptance:

- [ ] `./gradlew :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- [ ] API-specific assertions use `Assume` guards instead of brittle SDK checks in test bodies.

## Phase 5 - Networking And HTTP Behavior

Goal: cover network code without depending on external services.

Tasks:

- [ ] Add `baseLib/src/test/java/me/ycdev/android/lib/common/net/HttpClientTest.kt`.
  - Use local in-process HTTP server or a test-only server dependency.
  - Verify GET with query and headers.
  - Verify POST string and POST byte array.
  - Verify gzip and deflate response decoding.
  - Verify error stream is returned for non-2xx responses when the server sends a body.
  - Verify configured connect/read timeout values are applied through observable behavior.
- [ ] Extend `NetworkUtilsTestBasic.kt`.
  - Cover Wi-Fi, Ethernet, VPN, and no-transport paths.
  - Cover mobile subtype mapping through a small testable mapper if the current implementation is too framework-bound.
  - Cover metered and unavailable network branches where local framework support is reliable.

Acceptance:

- [ ] `./gradlew :baseLib:testDebugUnitTest --warning-mode all --stacktrace`
- [ ] No test reaches the public internet.

## Phase 6 - Packet, Database, And Storage Robustness

Goal: cover boundary and corruption behavior, where regressions tend to be expensive to debug.

Tasks:

- [ ] Extend packet parser tests.
  - Corrupt header followed by a valid packet recovers correctly.
  - CRC/checksum mismatch rejects only the bad packet.
  - Partial trailing packet is retained until more bytes arrive.
  - Large payload boundary stays within expected memory and parser state.
- [ ] Add database helper tests.
  - `SQLiteDbCreatorTest`: create, upgrade, downgrade/error path as supported by implementation.
  - `SQLiteDbMgrTest`: open/close reference behavior, repeated close safety, transaction/error branch.
- [ ] Add storage/package utility tests where behavior is deterministic.
  - External storage unavailable branch.
  - Package version/name fallback branch.
  - Null context or missing package handling, if public API permits it.

Acceptance:

- [ ] `./gradlew :baseLib:testDebugUnitTest :baseLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`

## Phase 7 - Module Coverage Outside `baseLib`

Goal: make all published modules carry at least smoke-plus-edge coverage.

Tasks:

- [ ] Extend `jniLib` connected tests.
  - Invalid path and missing path behavior for file status helpers.
  - Resource limit tests restore original values in `finally`.
  - Unsupported API behavior is skipped with `Assume`, not failed.
- [ ] Add `testLib` self-tests.
  - Test JUnit rules run setup/teardown exactly once.
  - Test logging helpers format and route messages as expected.
- [ ] Add `uiLib` tests.
  - `MarginItemDecorationTest`: all constructor branches and grid edge offsets.
  - `GridEntriesActivityTest`: empty list, populated list, item click, item long-click, async load completion.
- [ ] Add `archLib` tests.
  - `ToastHelperTest`: show/cancel/update behavior through framework shadows.
  - `BaseActivity` and `AppCompatBaseActivity` smoke launch tests if supported by module setup.
- [ ] Extend `archLintRules` tests.
  - Add positive and negative Kotlin/Java samples for each detector.
  - Assert error messages and quick-fix output remain stable.

Acceptance:

- [ ] `./gradlew :jniLib:connectedDebugAndroidTest --continue --warning-mode all --stacktrace`
- [ ] `./gradlew :testLib:testDebugUnitTest :uiLib:testDebugUnitTest :archLib:testDebugUnitTest :archLintRules:test --warning-mode all --stacktrace`
- [ ] Add only the minimum test dependencies needed by each module.

## Phase 8 - Full Verification, CI Traceability, And Release Readiness

Goal: make execution auditable and prevent another ambiguous "was the plan completed" situation.

Tasks:

- [ ] Maintain this plan as the source of truth.
  - After each phase, mark completed items.
  - Record the commit hash beside the phase heading.
  - Record any skipped test with the reason and replacement coverage.
- [ ] Run full local JVM/check verification after every 1-2 phases.
  - `./gradlew spotlessKotlinCheck check --warning-mode all --stacktrace`
- [ ] Run full connected verification after phases touching instrument code.
  - `adb devices -l`
  - `./gradlew connectedCheck --continue --warning-mode all --stacktrace`
- [ ] If workflow files change, run:
  - `actionlint .github/workflows/ci.yml`
- [ ] After push, verify GitHub Actions.
  - Confirm build job passes.
  - Confirm connectedCheck API matrix passes for API 24, 29, 31, 34, and 36.

Acceptance:

- [ ] All phase checkboxes are completed or explicitly deferred with a reason.
- [ ] Full local `check` passes.
- [ ] Full local `connectedCheck` passes on the connected device, or any device restriction is documented with exact error text.
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
