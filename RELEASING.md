# Versioning and Releasing Guide

This project is configured to automate its release process using GitHub Actions and Git tags. When a new release is ready, developers should follow standard Semantic Versioning (SemVer) principles.

## How to Make a New Release

### Step 1: Update the Version in `pom.xml`
Before cutting a release, you must prepare the project by updating its version. We use standard Maven versioning where active development happens on `-SNAPSHOT` versions.

1. Open `pom.xml`.
2. Locate the `<version>` tag (e.g., `<version>1.0-SNAPSHOT</version>`).
3. Remove `-SNAPSHOT` to signify a final release version.
   - *Example*: Change `<version>1.0-SNAPSHOT</version>` to `<version>1.0.0</version>`
4. Commit this change to the `main` branch:
   ```bash
   git add pom.xml
   git commit -m "chore: prep release v1.0.0"
   git push origin main
   ```

### Step 2: Create and Push a Git Tag
Our release automation listens for tags being pushed to the remote repository. The tag **must** start with `v` (e.g., `v1.0.0`), or the automatic release won't trigger.

1. Create a tag marking the current commit as the release version:
   ```bash
   git tag v1.0.0
   ```
2. Push the tag to GitHub:
   ```bash
   git push origin v1.0.0
   ```

### Step 3: Let GitHub Actions Automate the Rest
Once you push the tag, a GitHub Action automatically starts. 
You can view its progress in the **Actions** tab on your GitHub repository.

The workflow will:
1. Spin up an environment with Java 21.
2. Build the project into an executable JAR.
3. Automatically create an Official Release on the GitHub **Releases** page (with auto-generated release notes).
4. Attach the compiled `.jar` file to that release, meaning end users can instantly download the runnable program.

### Step 4: Bump to Next Development Version
After the release tag has been pushed, you should immediately bump the project to the next development snapshot so further work doesn't conflict with the released version.

1. Open `pom.xml` again.
2. Change the `<version>` to the next logical version and add `-SNAPSHOT`.
   - *Example*: Change `<version>1.0.0</version>` to `<version>1.1.0-SNAPSHOT</version>`
3. Commit and push:
   ```bash
   git add pom.xml
   git commit -m "chore: bump version to 1.1.0-SNAPSHOT"
   git push origin main
   ```
