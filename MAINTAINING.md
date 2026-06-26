# AndroidLib Maintenance Guide

This document is the operating guide for people and coding agents maintaining
AndroidLib together. It is based on the current repository layout, Gradle
configuration, CI workflow, and recent maintenance history.

## Project Map

| Area | Purpose | Notes for maintainers |
| --- | --- | --- |
| `baseLib` | Core Android utilities, IPC helpers, providers, packet parsers, trackers, hidden/internal API wrappers | Highest regression risk. Prefer targeted JVM and device tests before broad edits. |
| `uiLib` | Shared UI widgets and resources | Resource names use the `ycdev` prefix. Keep UI changes small and compatible. |
| `jniLib` | JNI helpers for file status and system resource limits | Requires NDK/CMake. Always run connected tests when native behavior changes. |
| `testLib` | Shared test helpers, Robolectric base classes, logging helpers | Changes can affect every module's tests. Keep APIs stable. |
| `archLib` | Architecture helpers such as base activities and wrapper helpers | Coupled to `archLintRules`; update lint tests with API changes. |
| `archLintRules` | Custom lint detectors | Unit tests live under `archLintRules/src/test`. Treat detector tests as the source of truth. |
| `archLintRulesTestDemo`, `jniLibDemo` | Demo and integration targets | Useful for lint/JNI validation, not published as libraries. |

Build-wide versions and dependency aliases live in
`android_project_common.gradle`. Shared Android module behavior lives in
`android_module_common.gradle`. Publication metadata and signing behavior live
in `build.gradle`, `publish-root.gradle`, and `publish-module.gradle`.

## Collaboration Model

Use short-lived branches for multi-person or multi-agent work. Keep one branch
focused on one maintenance goal, such as "fix API 34 connected tests" or
"upgrade AGP". Avoid mixing dependency upgrades, behavior changes, and
publishing changes in one commit unless they are inseparable.

Before editing:

1. Run `git status --short --branch`.
2. Identify whether the files you need already contain someone else's changes.
3. Read the nearest tests before changing implementation code.
4. For user-visible behavior, public APIs, logging, publishing, CI, and native
   code, write down the intended verification command before editing.

During editing:

1. Prefer the existing Gradle/Groovy and Kotlin style over new abstractions.
2. Keep public API changes backward compatible unless a release plan explicitly
   allows a breaking change.
3. Do not reformat unrelated files.
4. Do not rewrite shared branch history after pushing.
5. When Codex creates a commit, include `by codex` in the commit message body.

Before handoff:

1. Include the commands that passed and any known warnings.
2. Mention files or modules intentionally left untouched.
3. Leave the worktree clean, or clearly describe staged and unstaged changes.
4. Push only when asked or when the agreed workflow requires it.

## Build Environment

The project currently uses:

- Gradle wrapper: `9.5.1`
- Android Gradle Plugin: `9.2.1`
- Kotlin: `2.4.0`
- Java compatibility for Android modules: Java 11
- CI Java runtime: Zulu JDK 17
- `compileSdk`: 37
- `minSdk`: 24
- NDK: `29.0.14206865`

Use the checked-in Gradle wrapper:

```bash
./gradlew <task>
```

Do not add project-level repositories in individual modules. Dependency
resolution is centralized in `settings.gradle` and `android_project_common.gradle`.

## Verification Matrix

Pick the smallest command that proves the change, then run the broader checks
before committing or handing off.

### Fast Local Checks

```bash
./gradlew spotlessKotlinCheck --stacktrace
./gradlew check --warning-mode all --stacktrace
./gradlew verifyPublishConfig --warning-mode all --stacktrace
```

For code touched in a specific class or package, run targeted tests first. For
example:

```bash
./gradlew :baseLib:testDebugUnitTest --tests me.ycdev.android.lib.common.packets.TinyPacketsWorkerTest --stacktrace
./gradlew :baseLib:compileDebugAndroidTestKotlin --stacktrace
```

### Device Checks

Run device tests for any change touching Android framework behavior, hidden
APIs, IPC, content providers, JNI, permissions, process state, or CI emulator
behavior.

```bash
adb devices -l
./gradlew connectedCheck --continue --warning-mode all --stacktrace
```

For focused device validation:

```bash
./gradlew :baseLib:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=<fully.qualified.TestClass> \
  --stacktrace
```

The CI connected-test matrix is intentionally compact:

```text
API 24, 29, 31, 34, 36
```

Add a new Android API only when it catches a real compatibility boundary or is
needed for latest-platform coverage. Avoid expanding the matrix to every
intermediate Android version.

### Workflow Checks

When editing `.github/workflows/ci.yml`, run:

```bash
actionlint .github/workflows/ci.yml
```

If CI fails after a push, inspect the failed job and uploaded Android test
reports before changing code. Recent history shows failures often come from
emulator boot behavior, API-specific hidden API restrictions, or dependency
resolution issues.

## High-Risk Areas

### Hidden/Internal Android APIs

Files under `baseLib/src/main/java/.../internalapi` use reflection and hidden
framework APIs. Android platform behavior changes across API levels, and newer
Android versions may block calls that used to work.

Guidelines:

- Prefer explicit availability helpers over hard-coded test skips.
- Keep device tests for each wrapper method.
- If reading `/proc/<pid>/cmdline`, treat it as NUL-delimited and stop at the
  first `\u0000`.
- When a method is blocked by platform policy, skip the device test with a clear
  reason instead of letting it fail as an unexpected reflection error.

### IPC and Providers

`ServiceConnector`, `ServiceClientBase`, and `InfoProviderClient` depend on
Android lifecycle, bind timing, and cross-process state.

Guidelines:

- Avoid infinite waits in tests. Use bounded `CountDownLatch.await` calls and
  assert the return value.
- Validate both remote and local service paths when connection behavior changes.
- Keep tests for observer notification behavior and "same value does not notify"
  contracts.

### Packet Parsers

`TinyPacketsWorker` and `RawPacketsWorker` parse stream-like data. Bugs usually
come from fragmented input, corrupt headers, corrupt CRCs, and parser recovery.

Guidelines:

- Add tests for chunked input when changing parser state machines.
- After corrupt data, verify the next valid packet can still be parsed.
- Keep binary examples stable when changing packet format.

### JNI

`jniLib` builds native code for four ABIs. CI and local builds need compatible
NDK/CMake versions.

Guidelines:

- Run `:jniLib:connectedDebugAndroidTest` or full `connectedCheck` for native
  changes.
- Keep Kotlin JNI wrappers and C++ signatures in sync.
- Do not rely only on JVM checks for native behavior.

### Logging and Shared Test Helpers

`LibLogger`, `FileLogger`, and `testLib` are shared infrastructure. Changes can
affect unrelated modules and tests.

Guidelines:

- Treat logging behavior and shared test helpers as cross-module APIs.
- Keep changes small and explicitly verified.
- Ask for owner confirmation before changing persistence or file-output
  behavior in logging components.

## Dependency and Toolchain Upgrades

Recent history shows AGP, Kotlin, Gradle, AndroidX, UTP, NDK, and CI runner
upgrades tend to reveal unrelated-looking issues. Handle them as staged work:

1. Upgrade one toolchain layer at a time when possible.
2. Run `./gradlew check --warning-mode all --stacktrace`.
3. Run `./gradlew connectedCheck --continue --warning-mode all --stacktrace`
   on a real device or CI emulator.
4. Fix warnings that are caused by project scripts, especially Gradle
   deprecations that will break a future Gradle release.
5. Keep a short note in the commit message explaining why related build-script
   changes were necessary.

Known follow-ups at the time this document was written:

- Gradle lint reports wrapper `9.6.0` as newer than the current `9.5.1`.
- Robolectric reports that Android SDK 36 requires Java 21 when tests are run
  on Java 18. The current module config pins unit tests to SDK 34.

## Publishing

The published library modules are `baseLib`, `uiLib`, `jniLib`, and `testLib`.
Publishing is enabled by `publishEnabled = true` in the root `build.gradle`.

Before a release:

1. Update `mavenMeta.version` in `build.gradle`.
2. Run local checks:

   ```bash
   ./gradlew spotlessKotlinCheck check verifyPublishConfig --warning-mode all --stacktrace
   ./gradlew connectedCheck --continue --warning-mode all --stacktrace
   ./gradlew publishToMavenLocal --dry-run --stacktrace
   ```

3. Confirm Maven metadata through `verifyPublishConfig`.
4. Confirm signing and Sonatype credentials are available only on the publishing
   machine or CI secret store.
5. Tag the release after the release commit is final.
6. Do not execute remote publish tasks without explicit confirmation from the
   release owner.

Publication secrets can come from `local.properties` or environment variables:

```properties
ossrhUsername=
ossrhPassword=
sonatypeStagingProfileId=
signing.keyId=
signing.password=
signing.key=
snapshot=
```

Do not commit real credentials or private keys.

## Commit and Review Checklist

Use this checklist before each commit:

1. The diff is scoped to the stated task.
2. Public API compatibility was considered.
3. Relevant JVM tests passed.
4. Relevant Android device tests passed, or the reason they were not run is
   written down.
5. CI workflow edits passed `actionlint`.
6. Publishing edits passed `verifyPublishConfig` and a publish dry-run.
7. Known warnings are either fixed or documented.
8. Codex-authored commits include `by codex` in the commit message body.

Suggested commit message shape:

```text
Short imperative summary

Why this change is needed, if not obvious.
Verification: ./gradlew ...

by codex
```

## CI Failure Triage

When GitHub Actions fails:

1. Check whether the failure is in `build` or `connectedCheck`.
2. For `connectedCheck`, identify the API level from the matrix entry.
3. Download archived Android test results if the upload step ran.
4. Reproduce locally on a real device when the failure involves hidden APIs,
   process behavior, IPC, providers, JNI, or permissions.
5. Prefer a targeted test command while debugging, then finish with full
   `check` and `connectedCheck`.

Do not remove API levels from the matrix just to make CI green. Remove or add
matrix entries only when the coverage strategy changes deliberately.

## Documentation Upkeep

Update this file when:

- a module is added, removed, renamed, or stops being published;
- the Java, Gradle, AGP, Kotlin, NDK, compile SDK, or min SDK baseline changes;
- the CI matrix changes;
- release or signing steps change;
- a repeated bug pattern is discovered and needs a standard playbook.

Keep this document operational. Prefer commands, invariants, and review rules
over long explanations.
