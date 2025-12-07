# Contributing to UpdateApp (FinTrack Development Sandbox)

Thank you for your interest in contributing!  
This repository is the development sandbox for the main FinTrack app.  
All new UI updates, onboarding flows, authentication screens, and experimental features are first built and tested here before merging into the main FinTrack production app.

Please follow the guidelines below to ensure clean and smooth collaboration.

---

## 🧱 Branch Workflow

- `main` - stable reference for current UpdateApp code  
- `dev` — active development branch  
- All contributions must be made through feature branches:


---

## 🛠 What You Can Contribute

This project is mainly for building features that will later be added into **FinTrack**.  
You can contribute:

- 📱 Better onboarding screens  
- 🔐 Login/Signup/OTP improvements  
- 🎨 UI design improvements for XML layouts  
- 🧩 New components (cards, buttons, animations)  
- ⚡ Performance improvements  
- 🧼 Code cleanup following MVVM  
- 🔧 Bug fixes and crashes  
- 🌙 Theme changes (dark/light mode)  
- 🔒 Better authentication flow (Google/Phone)  

If you want to propose a new feature, please open an issue first.

---

## 📌 Coding Guidelines

Please follow these standards to keep the project clean:

### Architecture
- Follow **MVVM (Model–View–ViewModel)**  
- Avoid writing logic in Activities/Fragments  
- Use ViewModel for business logic  

### Code Style
- Use meaningful names  
- No unused or commented-out code  
- No hard-coded strings → use `strings.xml`  
- Keep functions small and readable  
- Reusable components > duplicated code  
- Follow Android Material Design guidelines  

### XML Layouts
- Use proper constraints  
- Avoid nested layouts when possible  
- Make UI responsive for all screen sizes  

---

## ✔️ Commit Message Rules

Small commits are better than one big commit.

---

## 📤 Push & Create Pull Request

After making changes:


Then open a **Pull Request (PR)**.

Your PR **must include**:
- Short description of what you added  
- Screenshots (mandatory for UI changes)  
- Steps to test the feature  
- Issue number (if applicable)  

Example:

---

## 🧪 Before Submitting a PR

Please test the following:

- App runs without crash  
- New features don't break existing screens  
- Layout works on small + large devices  
- Light & dark mode look correct  
- Navigation flow works properly  
- No unused imports or files  

---

## 🔍 PR Review Standards

Your pull request will be reviewed for:

- Code quality  
- Folder structure & MVVM compliance  
- UI/UX consistency with FinTrack’s style  
- No breaking changes  
- Clean commit history  
- Clear description  

Approved PRs will be merged into `dev` → later into `main`.

---

## 🤝 Code of Conduct

- Be respectful, helpful and positive  
- No spamming PRs just for the sake of contribution  
- Do not copy-paste code without explanation  
- This project is beginner-friendly, so maintain a collaborative tone  

---

## ⭐ Thank You!

Your contributions help shape the future of **FinTrack**.  
UpdateApp is where new ideas are born — and your work can directly influence the next public release of FinTrack.

Thank you for contributing! 🙌


Use this format:

