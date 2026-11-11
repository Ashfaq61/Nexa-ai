# Nexus AI Assistant 🌌

**Project Nexus** is an advanced, voice-first autonomous AI assistant designed for Android. It combines real-time voice interaction, Gemini intelligence, autonomous GitHub & Hugging Face AI scouting, daily intelligence reports, and a safe self-improvement module with strict human-in-the-loop governance.

---

## ⚡ Key Highlights

- 🎙️ **Voice-First Interaction**: Seamless voice input (Speech-to-Text) and natural vocal response (Text-to-Speech) with Gemini Live Voice fallback.
- 🧠 **Gemini Intelligence**: Powered by Google Gemini models with full persona prompting and streaming capabilities.
- 🛰️ **Autonomous AI Scouting**: Automated GitHub & Hugging Face scanner tracking trending agents (CrewAI, SmolAgents, LangGraph, Reflexio, AutoGen, SIA, etc.) and top models.
- 🛡️ **Safe Self-Improvement**: Evaluates new tools, creates structured upgrade proposals, and strictly forbids auto-merges or code execution without explicit human approval.
- 🚀 **Automated CI/CD**: Pre-configured GitHub Actions workflow to build release-ready debug APKs and execute daily scout cron jobs.

---

## 🏗️ Repository Architecture

```
nexus_ai_assistant/
├── .github/
│   └── workflows/
│       ├── build-apk.yml          # GitHub Actions workflow to build Android APK
│       └── daily-ai-scout.yml     # Daily cron (06:00 UTC) scouting GitHub & Hugging Face
├── android/                       # Android native configuration & manifests
│   └── app/src/main/
│       └── AndroidManifest.xml    # Permissions: INTERNET, RECORD_AUDIO
├── lib/
│   ├── main.dart                  # Application entry point
│   ├── app.dart                   # Root MaterialApp with dark theme configuration
│   ├── theme/
│   │   └── app_theme.dart         # Futuristic dark aesthetic theme & color palette
│   ├── screens/
│   │   ├── home_screen.dart       # Voice orb, chat stream, status bar
│   │   ├── settings_screen.dart   # Model choice, voice pitch/rate, safe mode
│   │   └── tools_screen.dart      # GitHub/HF scouting, upgrade proposals, reports
│   ├── widgets/
│   │   ├── message_bubble.dart    # Styled chat message bubble
│   │   └── mic_button.dart        # Glowing pulsing microphone button
│   ├── services/
│   │   ├── gemini_service.dart    # Gemini API client with fallback handling
│   │   ├── voice_service.dart     # Voice coordinator (STT -> Gemini -> TTS)
│   │   ├── speech_to_text_service.dart
│   │   ├── text_to_speech_service.dart
│   │   ├── github_search_service.dart
│   │   ├── huggingface_search_service.dart
│   │   ├── memory_service.dart    # Local persistence (SharedPreferences)
│   │   └── self_improvement_service.dart # Safe upgrade proposal engine
│   └── models/
│       ├── chat_message.dart
│       ├── tool_report.dart
│       └── upgrade_proposal.dart
├── prompts/
│   ├── system_prompt.txt          # Nexus core persona & safety rules
│   └── daily_scout_prompt.txt     # Prompt template for daily intelligence reports
├── reports/                       # Generated daily intelligence JSON & Markdown reports
├── backend/                       # Optional FastAPI backend architecture guide
│   └── README.md
├── pubspec.yaml                   # Flutter dependencies
└── README.md
```

---

## 🛠️ Step-by-Step Setup & Deployment

### 1. Running Locally with Flutter

Make sure you have Flutter 3.20+ and Android SDK installed on your workstation.

```bash
# 1. Clone your repository
git clone https://github.com/<your-username>/nexus-ai-assistant.git
cd nexus-ai-assistant

# 2. Install dependencies
flutter pub get

# 3. Connect your Android device or start an Android Emulator

# 4. Run the app with your Gemini API Key
flutter run --dart-define=GEMINI_API_KEY="YOUR_ACTUAL_GEMINI_API_KEY"
```

> ⚠️ **Warning**: Never commit your raw API key to git. Always use `--dart-define` or app settings.

---

### 2. Creating a GitHub Repository & Pushing Code

1. Navigate to [GitHub New Repository](https://github.com/new).
2. Name the repository `nexus-ai-assistant` and choose **Private** or **Public**. Do not initialize with a README (this repository already provides one).
3. Push your code:

```bash
git init
git add .
git commit -m "feat: initial commit of Project Nexus AI Assistant"
git branch -M main
git remote add origin https://github.com/<your-username>/nexus-ai-assistant.git
git push -u origin main
```

---

### 3. Adding `GEMINI_API_KEY` to GitHub Secrets

To allow GitHub Actions to build your APK with the Gemini API configured:

1. In your GitHub repository, navigate to **Settings** > **Secrets and variables** > **Actions**.
2. Click **New repository secret**.
3. Name: `GEMINI_API_KEY`
4. Secret: Paste your Google AI Studio Gemini API Key.
5. Click **Add secret**.

---

### 4. Downloading the APK from GitHub Actions

> ℹ️ **Note on Google AI Studio vs. GitHub Actions**:
> Google AI Studio provides an interactive web workspace and development environment, but automated standalone Android APK artifact distribution is handled via the included **GitHub Actions CI/CD pipeline** (`.github/workflows/build-apk.yml`).

Once pushed to GitHub:
1. In your repository, click the **Actions** tab.
2. Select the **Build Android APK** workflow.
3. Once completed (typically 2-4 minutes), click on the completed run.
4. Scroll down to **Artifacts** and download `nexus-ai-assistant-debug-apk.zip`.
5. Extract `app-debug.apk` and install it on your Android phone using `adb install app-debug.apk` or by sending it to your device!

---

### 5. How the Daily AI Scout Workflow Operates

The workflow in `.github/workflows/daily-ai-scout.yml`:
- Fires every day at **06:00 UTC** (or manually via **Run workflow** button).
- Scans GitHub's search API for trending AI agent frameworks (e.g. CrewAI, SmolAgents, LangGraph, Reflexio).
- Scans Hugging Face's API for top downloaded models and trending spaces.
- Writes structured data to `reports/` and commits an updated Markdown report to `main`.
- In-app, you can view these scouting insights under the **Tools & Proposals** tab.

---

### 6. Enabling Signed Release APKs for Google Play

To distribute a production release signed with your keystore:

1. Generate a keystore:
   ```bash
   keytool -genkey -v -keystore android/app/upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. Create `android/key.properties` (added to `.gitignore`):
   ```properties
   storePassword=yourStorePassword
   keyPassword=yourKeyPassword
   keyAlias=upload
   storeFile=upload-keystore.jks
   ```
3. Update `android/app/build.gradle` to load `key.properties` in `signingConfigs.release`.
4. Build release bundle:
   ```bash
   flutter build appbundle --dart-define=GEMINI_API_KEY="YOUR_KEY"
   ```

---

### 7. Self-Improvement & Safety Directives

Project Nexus adheres to strict self-governance:
1. **Zero Silent Modification**: Nexus never rewrites its own codebase or merges pull requests autonomously.
2. **Transparent Diffing**: Every upgrade suggestion is formatted as an **Upgrade Proposal** showing rationale, risk rating, and exact code changes.
3. **Sandbox Isolation**: Heavy agent testing is conducted in containerized environments.
4. **Data Privacy**: Local chat history and proposals remain stored solely on your device.
