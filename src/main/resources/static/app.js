const STORAGE_KEY = "jobtrackr.token";

const JOB_STATUSES = ["DRAFT", "APPLIED", "INTERVIEW", "OFFER", "REJECTED"];
const ATTACHMENT_TYPES = ["JOB_DESCRIPTION", "OFFER_LETTER", "INTERVIEW_PREP", "REJECTION_LETTER", "OTHER"];
const COMMUNICATION_METHODS = ["EMAIL", "CALL", "LINKEDIN", "IN_PERSON"];
const COMMUNICATION_DIRECTIONS = ["INBOUND", "OUTBOUND"];
const SCHEDULED_TYPES = ["INTERVIEW", "ONLINE_ASSESSMENT", "CALL", "IN_PERSON_ASSESSMENT"];

const viewMeta = {
    overview: { eyebrow: "Workspace pulse", title: "Overview" },
    pipeline: { eyebrow: "Execution lane", title: "Pipeline" },
    library: { eyebrow: "Reusable assets", title: "Library" },
    calendar: { eyebrow: "Export and sync", title: "Calendar" },
    account: { eyebrow: "Identity and access", title: "Account" },
    admin: { eyebrow: "Admin surface", title: "Admin" }
};

const state = {
    token: localStorage.getItem(STORAGE_KEY),
    user: null,
    activeView: "overview",
    authMode: "login",
    overview: null,
    analytics: null,
    recommendations: null,
    calendarEvents: [],
    sources: [],
    locations: [],
    resumes: [],
    coverLetters: [],
    applications: {
        content: [],
        number: 0,
        size: 6,
        totalPages: 0,
        totalElements: 0,
        sort: "createdAt,desc"
    },
    selectedApplicationId: null,
    selectedApplication: null,
    selectedTimeline: [],
    selectedAttachments: [],
    selectedCommunications: [],
    selectedSkills: [],
    selectedFollowUps: [],
    selectedScheduled: [],
    selectedMatch: null,
    adminUsers: [],
    auditLogs: []
};

const dom = {};

document.addEventListener("DOMContentLoaded", init);

async function init() {
    cacheDom();
    populateStaticSelects();
    bindEvents();
    switchAuthMode("login");
    setView("overview");

    if (!state.token) {
        render();
        return;
    }

    try {
        await bootstrapWorkspace();
    } catch (error) {
        clearSession();
        showToast("Session expired", error.message || "Please log in again.", "error");
        render();
    }
}

function cacheDom() {
    dom.authScreen = document.getElementById("authScreen");
    dom.workspace = document.getElementById("workspace");
    dom.toastRoot = document.getElementById("toastRoot");
    dom.authTitle = document.getElementById("authTitle");
    dom.authMessage = document.getElementById("authMessage");
    dom.loginForm = document.getElementById("loginForm");
    dom.signupForm = document.getElementById("signupForm");
    dom.loginModeButton = document.getElementById("loginModeButton");
    dom.signupModeButton = document.getElementById("signupModeButton");
    dom.userEmail = document.getElementById("userEmail");
    dom.sessionRole = document.getElementById("sessionRole");
    dom.refreshButton = document.getElementById("refreshButton");
    dom.logoutButton = document.getElementById("logoutButton");
    dom.viewEyebrow = document.getElementById("viewEyebrow");
    dom.viewTitle = document.getElementById("viewTitle");
    dom.adminNavButton = document.getElementById("adminNavButton");
    dom.navButtons = Array.from(document.querySelectorAll(".nav-button"));
    dom.views = {
        overview: document.getElementById("overviewView"),
        pipeline: document.getElementById("pipelineView"),
        library: document.getElementById("libraryView"),
        calendar: document.getElementById("calendarView"),
        account: document.getElementById("accountView"),
        admin: document.getElementById("adminView")
    };
    dom.overviewStats = document.getElementById("overviewStats");
    dom.analyticsGrid = document.getElementById("analyticsGrid");
    dom.sourcePerformance = document.getElementById("sourcePerformance");
    dom.recommendationList = document.getElementById("recommendationList");
    dom.upcomingEvents = document.getElementById("upcomingEvents");
    dom.sourceForm = document.getElementById("sourceForm");
    dom.locationForm = document.getElementById("locationForm");
    dom.jobApplicationForm = document.getElementById("jobApplicationForm");
    dom.jobSourceSelect = document.getElementById("jobSourceSelect");
    dom.jobLocationSelect = document.getElementById("jobLocationSelect");
    dom.jobResumeSelect = document.getElementById("jobResumeSelect");
    dom.jobCoverLetterSelect = document.getElementById("jobCoverLetterSelect");
    dom.applicationSort = document.getElementById("applicationSort");
    dom.applicationsPrev = document.getElementById("applicationsPrev");
    dom.applicationsNext = document.getElementById("applicationsNext");
    dom.applicationPageLabel = document.getElementById("applicationPageLabel");
    dom.applicationList = document.getElementById("applicationList");
    dom.selectedApplicationTitle = document.getElementById("selectedApplicationTitle");
    dom.selectedStatus = document.getElementById("selectedStatus");
    dom.saveStatusButton = document.getElementById("saveStatusButton");
    dom.deleteApplicationButton = document.getElementById("deleteApplicationButton");
    dom.selectedApplicationSummary = document.getElementById("selectedApplicationSummary");
    dom.timelineList = document.getElementById("timelineList");
    dom.matchingPanel = document.getElementById("matchingPanel");
    dom.skillForm = document.getElementById("skillForm");
    dom.skillList = document.getElementById("skillList");
    dom.attachmentForm = document.getElementById("attachmentForm");
    dom.attachmentList = document.getElementById("attachmentList");
    dom.communicationForm = document.getElementById("communicationForm");
    dom.communicationList = document.getElementById("communicationList");
    dom.followUpForm = document.getElementById("followUpForm");
    dom.followUpList = document.getElementById("followUpList");
    dom.scheduledForm = document.getElementById("scheduledForm");
    dom.scheduledList = document.getElementById("scheduledList");
    dom.resumeForm = document.getElementById("resumeForm");
    dom.resumeList = document.getElementById("resumeList");
    dom.coverLetterForm = document.getElementById("coverLetterForm");
    dom.coverLetterList = document.getElementById("coverLetterList");
    dom.sourceList = document.getElementById("sourceList");
    dom.locationList = document.getElementById("locationList");
    dom.calendarFilterForm = document.getElementById("calendarFilterForm");
    dom.downloadCalendarButton = document.getElementById("downloadCalendarButton");
    dom.downloadJsonButton = document.getElementById("downloadJsonButton");
    dom.downloadCsvButton = document.getElementById("downloadCsvButton");
    dom.downloadPdfButton = document.getElementById("downloadPdfButton");
    dom.calendarEventList = document.getElementById("calendarEventList");
    dom.profileForm = document.getElementById("profileForm");
    dom.profileEmailInput = document.getElementById("profileEmailInput");
    dom.accountSnapshot = document.getElementById("accountSnapshot");
    dom.deactivateAccountButton = document.getElementById("deactivateAccountButton");
    dom.adminUserList = document.getElementById("adminUserList");
    dom.auditLogList = document.getElementById("auditLogList");
}

function populateStaticSelects() {
    fillSelect(dom.jobApplicationForm.elements.status, JOB_STATUSES, {
        includeBlank: false,
        selected: "APPLIED"
    });
    fillSelect(dom.selectedStatus, JOB_STATUSES, { includeBlank: false });
    fillSelect(dom.attachmentForm.elements.type, ATTACHMENT_TYPES, { includeBlank: false });
    fillSelect(dom.communicationForm.elements.type, COMMUNICATION_METHODS, { includeBlank: false });
    fillSelect(dom.communicationForm.elements.direction, COMMUNICATION_DIRECTIONS, { includeBlank: false });
    fillSelect(dom.scheduledForm.elements.type, SCHEDULED_TYPES, { includeBlank: false });
}

function bindEvents() {
    dom.loginModeButton.addEventListener("click", () => switchAuthMode("login"));
    dom.signupModeButton.addEventListener("click", () => switchAuthMode("signup"));
    dom.loginForm.addEventListener("submit", handleLogin);
    dom.signupForm.addEventListener("submit", handleSignup);
    dom.logoutButton.addEventListener("click", logout);
    dom.refreshButton.addEventListener("click", refreshActiveView);

    dom.navButtons.forEach((button) => {
        button.addEventListener("click", () => setView(button.dataset.view));
    });

    dom.sourceForm.addEventListener("submit", handleCreateSource);
    dom.locationForm.addEventListener("submit", handleCreateLocation);
    dom.jobApplicationForm.addEventListener("submit", handleCreateApplication);
    dom.applicationSort.addEventListener("change", async () => {
        state.applications.sort = dom.applicationSort.value;
        await loadApplications(0);
        renderPipeline();
    });
    dom.applicationsPrev.addEventListener("click", async () => {
        if (state.applications.number === 0) {
            return;
        }
        await loadApplications(state.applications.number - 1);
        renderPipeline();
    });
    dom.applicationsNext.addEventListener("click", async () => {
        if (state.applications.number + 1 >= state.applications.totalPages) {
            return;
        }
        await loadApplications(state.applications.number + 1);
        renderPipeline();
    });
    dom.applicationList.addEventListener("click", handleApplicationListClick);
    dom.saveStatusButton.addEventListener("click", handleSelectedStatusUpdate);
    dom.deleteApplicationButton.addEventListener("click", handleDeleteApplication);
    dom.skillForm.addEventListener("submit", handleCreateSkill);
    dom.attachmentForm.addEventListener("submit", handleCreateAttachment);
    dom.communicationForm.addEventListener("submit", handleCreateCommunication);
    dom.followUpForm.addEventListener("submit", handleCreateFollowUp);
    dom.scheduledForm.addEventListener("submit", handleCreateScheduled);
    dom.resumeForm.addEventListener("submit", handleCreateResume);
    dom.coverLetterForm.addEventListener("submit", handleCreateCoverLetter);
    dom.sourceList.addEventListener("click", handleResourceDeleteClick);
    dom.locationList.addEventListener("click", handleResourceDeleteClick);
    dom.resumeList.addEventListener("click", handleResourceDeleteClick);
    dom.coverLetterList.addEventListener("click", handleResourceDeleteClick);
    dom.attachmentList.addEventListener("click", handleResourceDeleteClick);
    dom.communicationList.addEventListener("click", handleResourceDeleteClick);
    dom.followUpList.addEventListener("click", handleResourceDeleteClick);
    dom.scheduledList.addEventListener("click", handleResourceDeleteClick);
    dom.skillList.addEventListener("click", handleResourceDeleteClick);
    dom.calendarFilterForm.addEventListener("submit", handleCalendarFilter);
    dom.downloadCalendarButton.addEventListener("click", () => downloadProtectedFile(buildCalendarPath("/api/calendar/events.ics")));
    dom.downloadJsonButton.addEventListener("click", () => downloadProtectedFile("/api/exports/workspace?format=json"));
    dom.downloadCsvButton.addEventListener("click", () => downloadProtectedFile("/api/exports/workspace?format=csv"));
    dom.downloadPdfButton.addEventListener("click", () => downloadProtectedFile("/api/exports/workspace?format=pdf"));
    dom.profileForm.addEventListener("submit", handleProfileUpdate);
    dom.deactivateAccountButton.addEventListener("click", handleDeactivateAccount);
    dom.adminUserList.addEventListener("click", handleAdminUserToggle);
}

function switchAuthMode(mode) {
    state.authMode = mode;
    const isLogin = mode === "login";
    dom.loginModeButton.classList.toggle("is-active", isLogin);
    dom.signupModeButton.classList.toggle("is-active", !isLogin);
    dom.loginForm.hidden = !isLogin;
    dom.signupForm.hidden = isLogin;
    dom.authTitle.textContent = isLogin ? "Sign in to your workspace" : "Create your workspace account";
    dom.authMessage.textContent = "";
}

async function handleLogin(event) {
    event.preventDefault();
    const email = event.currentTarget.elements.email.value.trim();
    const password = event.currentTarget.elements.password.value;
    await authenticate(email, password);
}

async function handleSignup(event) {
    event.preventDefault();
    const email = event.currentTarget.elements.email.value.trim();
    const password = event.currentTarget.elements.password.value;

    try {
        await apiJson("/api/auth/signup", {
            method: "POST",
            body: { email, password },
            auth: false
        });
        showToast("Account created", "Signing you in now.", "success");
        await authenticate(email, password);
    } catch (error) {
        dom.authMessage.textContent = error.message;
        showToast("Signup failed", error.message, "error");
    }
}

async function authenticate(email, password) {
    try {
        const response = await apiJson("/api/auth/login", {
            method: "POST",
            body: { email, password },
            auth: false
        });
        state.token = response.token;
        localStorage.setItem(STORAGE_KEY, state.token);
        await bootstrapWorkspace();
        showToast("Welcome back", "The workspace is ready.", "success");
    } catch (error) {
        dom.authMessage.textContent = error.message;
        showToast("Login failed", error.message, "error");
    }
}

async function bootstrapWorkspace() {
    state.user = await apiJson("/api/auth/me");
    state.activeView = "overview";
    await Promise.all([
        loadReferences(),
        loadOverview(),
        loadApplications(0)
    ]);
    if (state.selectedApplicationId) {
        await loadSelectedApplication(state.selectedApplicationId);
    } else if (state.applications.content.length > 0) {
        await loadSelectedApplication(state.applications.content[0].id);
    }
    if (isAdmin()) {
        await loadAdmin();
    }
    render();
}

async function refreshActiveView() {
    try {
        await loadReferences();
        await loadOverview();
        await loadApplications(state.applications.number);
        if (state.selectedApplicationId) {
            await loadSelectedApplication(state.selectedApplicationId);
        } else if (state.applications.content.length > 0) {
            await loadSelectedApplication(state.applications.content[0].id);
        }
        if (isAdmin()) {
            await loadAdmin();
        }
        render();
        showToast("Workspace refreshed", "The latest backend data is now on screen.", "success");
    } catch (error) {
        showToast("Refresh failed", error.message, "error");
    }
}

function setView(viewName) {
    state.activeView = viewName;
    Object.entries(dom.views).forEach(([name, element]) => {
        const active = name === viewName;
        element.hidden = !active;
        element.classList.toggle("is-active", active);
    });

    dom.navButtons.forEach((button) => {
        button.classList.toggle("is-active", button.dataset.view === viewName);
    });

    const meta = viewMeta[viewName];
    dom.viewEyebrow.textContent = meta.eyebrow;
    dom.viewTitle.textContent = meta.title;
}

async function loadOverview() {
    const [overview, analytics, recommendations, calendarEvents] = await Promise.all([
        apiJson("/api/insights/dashboard"),
        apiJson("/api/insights/analytics"),
        apiJson("/api/insights/recommendations"),
        apiJson("/api/calendar/events")
    ]);
    state.overview = overview;
    state.analytics = analytics;
    state.recommendations = recommendations;
    state.calendarEvents = calendarEvents;
}

async function loadReferences() {
    const [sources, locations, resumes, coverLetters] = await Promise.all([
        apiJson("/api/job-sources"),
        apiJson("/api/locations"),
        apiJson("/api/resumes"),
        apiJson("/api/cover-letters")
    ]);
    state.sources = sources;
    state.locations = locations;
    state.resumes = resumes;
    state.coverLetters = coverLetters;
}

async function loadApplications(page = 0) {
    const params = new URLSearchParams({
        page: String(page),
        size: String(state.applications.size),
        sort: state.applications.sort
    });
    const response = await apiJson(`/api/job-applications?${params.toString()}`);
    state.applications = {
        ...state.applications,
        ...response
    };
    if (state.selectedApplicationId && !state.applications.content.some((item) => item.id === state.selectedApplicationId)) {
        if (!state.selectedApplication) {
            state.selectedApplicationId = state.applications.content[0]?.id ?? null;
        }
    }
}

async function loadSelectedApplication(applicationId) {
    if (!applicationId) {
        clearSelectedApplication();
        return;
    }

    const [detail, attachments, communications, followUps, skills, timeline, match, scheduled] = await Promise.all([
        apiJson(`/api/job-applications/${applicationId}`),
        apiJson(`/api/attachments/job/${applicationId}`),
        apiJson(`/api/communications/job/${applicationId}`),
        apiJson(`/api/follow-ups/job/${applicationId}`),
        apiJson(`/api/skills/job/${applicationId}`),
        apiJson(`/api/timelines/job/${applicationId}`),
        apiJson(`/api/matching/applications/${applicationId}`),
        apiJson("/api/scheduled-communications")
    ]);

    state.selectedApplicationId = applicationId;
    state.selectedApplication = detail;
    state.selectedAttachments = attachments;
    state.selectedCommunications = communications;
    state.selectedFollowUps = followUps;
    state.selectedSkills = skills;
    state.selectedTimeline = timeline;
    state.selectedMatch = match;
    state.selectedScheduled = scheduled.filter((item) => item.jobApplicationId === applicationId);
}

async function loadAdmin() {
    const [users, auditLogs] = await Promise.all([
        apiJson("/api/users"),
        apiJson("/api/audit-log")
    ]);
    state.adminUsers = users;
    state.auditLogs = auditLogs;
}

function render() {
    const authenticated = Boolean(state.token && state.user);
    dom.authScreen.hidden = authenticated;
    dom.workspace.hidden = !authenticated;

    if (!authenticated) {
        return;
    }

    dom.userEmail.textContent = state.user.email;
    dom.sessionRole.textContent = Array.isArray(state.user.roles) && state.user.roles.includes("ADMIN")
        ? "ADMIN"
        : "BASIC";
    dom.adminNavButton.hidden = !isAdmin();

    renderReferenceSelects();
    renderOverview();
    renderPipeline();
    renderLibrary();
    renderCalendar();
    renderAccount();
    renderAdmin();
}

function renderOverview() {
    const overview = state.overview;
    const analytics = state.analytics;
    const recommendations = state.recommendations?.items ?? [];
    const events = overview?.upcomingEvents ?? [];

    dom.overviewStats.innerHTML = overview
        ? [
            metricCard("Total applications", overview.totalApplications),
            metricCard("Applications in range", overview.applicationsInPeriod),
            metricCard("Active pipeline", overview.activeApplications),
            metricCard("Closed outcomes", overview.closedApplications)
        ].join("")
        : emptyBlock("No dashboard data yet.");

    dom.analyticsGrid.innerHTML = analytics
        ? [
            metricCard("Applied to interview", toPercent(analytics.appliedToInterviewRate)),
            metricCard("Interview to offer", toPercent(analytics.interviewToOfferRate)),
            metricCard("Applied to offer", toPercent(analytics.appliedToOfferRate)),
            metricCard("Overdue reminders", analytics.overdueReminders),
            metricCard("Upcoming scheduled", analytics.upcomingScheduledEvents),
            metricCard("Offers", analytics.offerCount)
        ].join("")
        : emptyBlock("No analytics yet.");

    dom.sourcePerformance.innerHTML = analytics && analytics.sourcePerformance.length
        ? analytics.sourcePerformance.map((item) => `
            <article class="list-item">
                <div class="list-item-header">
                    <div>
                        <h3>${escapeHtml(item.sourceName)}</h3>
                        <p class="muted-copy">${item.totalApplications} applications, ${item.interviewStageCount} interview-stage, ${item.offerCount} offers</p>
                    </div>
                    <div class="tag-row">
                        <span class="tag">${toPercent(item.appliedToInterviewRate)} to interview</span>
                        <span class="tag">${toPercent(item.appliedToOfferRate)} to offer</span>
                    </div>
                </div>
            </article>
        `).join("")
        : emptyBlock("Create applications to unlock source performance.");

    dom.recommendationList.innerHTML = recommendations.length
        ? recommendations.map((item) => `
            <article class="list-item">
                <div class="list-item-header">
                    <div>
                        <h3>${escapeHtml(item.title)}</h3>
                        <p class="muted-copy">${escapeHtml(item.description)}</p>
                    </div>
                    <div class="tag-row">
                        <span class="tag">${escapeHtml(item.priority)}</span>
                        <span class="tag tag-neutral">${escapeHtml(item.type)}</span>
                    </div>
                </div>
                <p class="muted-copy">${escapeHtml(item.company || "General")} ${item.jobTitle ? `| ${escapeHtml(item.jobTitle)}` : ""}</p>
                <p class="muted-copy"><strong>Suggested action:</strong> ${escapeHtml(item.suggestedAction)}</p>
            </article>
        `).join("")
        : emptyBlock("Recommendations will appear once you start building the pipeline.");

    dom.upcomingEvents.innerHTML = events.length
        ? events.map(renderCalendarEvent).join("")
        : emptyBlock("No upcoming events are currently scheduled.");
}

function renderPipeline() {
    dom.applicationSort.value = state.applications.sort;
    dom.applicationPageLabel.textContent = state.applications.totalPages
        ? `Page ${state.applications.number + 1} of ${state.applications.totalPages}`
        : "Page 0 of 0";
    dom.applicationsPrev.disabled = state.applications.number === 0;
    dom.applicationsNext.disabled = state.applications.number + 1 >= state.applications.totalPages;

    dom.applicationList.innerHTML = state.applications.content.length
        ? state.applications.content.map((item) => `
            <article class="application-card ${item.id === state.selectedApplicationId ? "is-selected" : ""}">
                <div class="application-card-header">
                    <div>
                        <h3>${escapeHtml(item.company)} | ${escapeHtml(item.jobTitle)}</h3>
                        <p class="application-meta">${escapeHtml(item.sourceName || "Unknown source")} ${item.locationCity ? `| ${escapeHtml(item.locationCity)}, ${escapeHtml(item.locationCountry || "")}` : ""}</p>
                    </div>
                    <span class="tag">${escapeHtml(item.status || "DRAFT")}</span>
                </div>
                <p class="application-meta">${escapeHtml(shortText(item.jobDescription, 150))}</p>
                <div class="tag-row">
                    ${item.resumeName ? `<span class="tag tag-neutral">Resume: ${escapeHtml(item.resumeName)}</span>` : ""}
                    ${item.coverLetterName ? `<span class="tag tag-neutral">Cover letter: ${escapeHtml(item.coverLetterName)}</span>` : ""}
                    ${item.deadline ? `<span class="tag tag-neutral">Deadline: ${escapeHtml(formatDateTime(item.deadline))}</span>` : ""}
                </div>
                <div class="button-row">
                    <button class="ghost-button" type="button" data-action="select-application" data-id="${item.id}">Open workspace</button>
                </div>
            </article>
        `).join("")
        : emptyBlock("No applications yet. Create one from the form on the left.");

    renderSelectedApplicationPanel();
    toggleSelectedApplicationControls(Boolean(state.selectedApplicationId));
}

function renderSelectedApplicationPanel() {
    const detail = state.selectedApplication;
    if (!detail) {
        dom.selectedApplicationTitle.textContent = "Choose an application";
        dom.selectedApplicationSummary.innerHTML = emptyBlock("Select a record to open its workspace.");
        dom.timelineList.innerHTML = emptyBlock("Timeline entries will appear here.");
        dom.matchingPanel.innerHTML = emptyBlock("Matching guidance appears when an application is selected.");
        dom.skillList.innerHTML = emptyBlock("No tracked skills yet.");
        dom.attachmentList.innerHTML = emptyBlock("No attachments yet.");
        dom.communicationList.innerHTML = emptyBlock("No communications yet.");
        dom.followUpList.innerHTML = emptyBlock("No reminders yet.");
        dom.scheduledList.innerHTML = emptyBlock("No scheduled events yet.");
        return;
    }

    dom.selectedApplicationTitle.textContent = `${detail.company} | ${detail.jobTitle}`;
    dom.selectedStatus.value = detail.status || "DRAFT";
    dom.selectedApplicationSummary.innerHTML = `
        <div class="account-snapshot-grid">
            ${snapshotTile("Status", detail.status || "DRAFT")}
            ${snapshotTile("Source", lookupSourceName(detail.sourceId))}
            ${snapshotTile("Location", detail.locationCity ? `${detail.locationCity}, ${detail.locationCountry || ""}` : "Not set")}
            ${snapshotTile("Applied on", detail.appliedOn ? formatDateTime(detail.appliedOn) : "Not set")}
            ${snapshotTile("Deadline", detail.deadline ? formatDateTime(detail.deadline) : "Not set")}
            ${snapshotTile("Resume", lookupResumeTitle(detail.resumeId))}
            ${snapshotTile("Cover letter", lookupCoverLetterTitle(detail.coverLetterId))}
            ${snapshotTile("Notes", detail.notes ? shortText(detail.notes, 80) : "No notes")}
        </div>
        <div class="list-item" style="margin-top: 1rem;">
            <h3>Description</h3>
            <p class="muted-copy">${escapeHtml(detail.jobDescription || "No description")}</p>
        </div>
    `;

    dom.timelineList.innerHTML = state.selectedTimeline.length
        ? state.selectedTimeline.map((item) => `
            <article class="list-item">
                <div class="list-item-header">
                    <div>
                        <h3>${escapeHtml(item.eventType)}</h3>
                        <p class="muted-copy">${escapeHtml(item.description || "")}</p>
                    </div>
                    <span class="tag tag-neutral">${escapeHtml(formatDateTime(item.eventTime))}</span>
                </div>
            </article>
        `).join("")
        : emptyBlock("Timeline entries will appear here.");

    const match = state.selectedMatch;
    dom.matchingPanel.innerHTML = match
        ? `
            <article class="list-item">
                <div class="list-item-header">
                    <div>
                        <h3>Overall fit score</h3>
                        <p class="muted-copy">${escapeHtml(match.company)} | ${escapeHtml(match.jobTitle)}</p>
                    </div>
                    <span class="tag">${Math.round(match.overallScore * 100)}%</span>
                </div>
                <div class="tag-row">
                    ${(match.matchedSkills || []).map((skill) => `<span class="tag">${escapeHtml(skill)}</span>`).join("")}
                </div>
                <p class="muted-copy">${(match.recommendations || []).map(escapeHtml).join(" | ") || "No recommendation text yet."}</p>
                ${match.recommendedResume ? `
                    <div class="list-item" style="margin-top: 0.75rem;">
                        <h3>Recommended resume</h3>
                        <p class="muted-copy">${escapeHtml(match.recommendedResume.title)} | ${Math.round(match.recommendedResume.score * 100)}%</p>
                        <p class="muted-copy">${escapeHtml(match.recommendedResume.rationale)}</p>
                    </div>
                ` : ""}
                ${match.recommendedCoverLetter ? `
                    <div class="list-item" style="margin-top: 0.75rem;">
                        <h3>Recommended cover letter</h3>
                        <p class="muted-copy">${escapeHtml(match.recommendedCoverLetter.title)} | ${Math.round(match.recommendedCoverLetter.score * 100)}%</p>
                        <p class="muted-copy">${escapeHtml(match.recommendedCoverLetter.rationale)}</p>
                    </div>
                ` : ""}
                ${(match.keywordGaps || []).length ? `
                    <p class="muted-copy"><strong>Keyword gaps:</strong> ${(match.keywordGaps || []).map(escapeHtml).join(", ")}</p>
                ` : ""}
            </article>
        `
        : emptyBlock("Matching guidance appears when an application is selected.");

    dom.skillList.innerHTML = state.selectedSkills.length
        ? renderPillList(state.selectedSkills.map((item) => ({
            id: item.id,
            label: item.skillName,
            kind: "skill"
        })))
        : emptyBlock("No tracked skills yet.");

    dom.attachmentList.innerHTML = renderResourceCards(
        state.selectedAttachments,
        "attachment",
        (item) => `${escapeHtml(item.type)} | ${escapeHtml(item.filePath)}`
    );
    dom.communicationList.innerHTML = renderResourceCards(
        state.selectedCommunications,
        "communication",
        (item) => `${escapeHtml(item.type)} ${escapeHtml(item.direction)} | ${escapeHtml(formatDateTime(item.timestamp))}<br><span class="muted-copy">${escapeHtml(item.message)}</span>`
    );
    dom.followUpList.innerHTML = renderResourceCards(
        state.selectedFollowUps,
        "follow-up",
        (item) => `${escapeHtml(formatDateTime(item.remindOn))}<br><span class="muted-copy">${escapeHtml(item.note)}</span>`
    );
    dom.scheduledList.innerHTML = renderResourceCards(
        state.selectedScheduled,
        "scheduled",
        (item) => `${escapeHtml(item.type)} | ${escapeHtml(formatDateTime(item.scheduledFor))}<br><span class="muted-copy">${escapeHtml(item.notes)}</span>`
    );
}

function renderLibrary() {
    dom.resumeList.innerHTML = renderResourceCards(
        state.resumes,
        "resume",
        (item) => `<strong>${escapeHtml(item.title)}</strong><br><span class="muted-copy">${escapeHtml(item.filePath)}</span>`
    );
    dom.coverLetterList.innerHTML = renderResourceCards(
        state.coverLetters,
        "cover-letter",
        (item) => `<strong>${escapeHtml(item.title)}</strong><br><span class="muted-copy">${escapeHtml(item.filePath)}</span>${item.content ? `<p class="muted-copy">${escapeHtml(shortText(item.content, 180))}</p>` : ""}`
    );
    dom.sourceList.innerHTML = renderResourceCards(
        state.sources,
        "source",
        (item) => `<strong>${escapeHtml(item.name)}</strong>`
    );
    dom.locationList.innerHTML = renderResourceCards(
        state.locations,
        "location",
        (item) => `<strong>${escapeHtml(item.city)}</strong><br><span class="muted-copy">${escapeHtml(item.country)}</span>`
    );
}

function renderCalendar() {
    dom.calendarEventList.innerHTML = state.calendarEvents.length
        ? state.calendarEvents.map(renderCalendarEvent).join("")
        : emptyBlock("No events found for the selected range.");
}

function renderAccount() {
    dom.profileEmailInput.value = state.user?.email || "";
    const roles = Array.isArray(state.user?.roles) ? state.user.roles.join(", ") : "BASIC";
    dom.accountSnapshot.innerHTML = `
        <div class="account-snapshot-grid">
            ${snapshotTile("Email", state.user?.email || "-")}
            ${snapshotTile("Roles", roles)}
            ${snapshotTile("Account enabled", state.user?.enabled ? "Yes" : "No")}
            ${snapshotTile("Selected applications", String(state.applications.totalElements || 0))}
        </div>
    `;
}

function renderAdmin() {
    if (!isAdmin()) {
        dom.adminUserList.innerHTML = emptyBlock("Admin access required.");
        dom.auditLogList.innerHTML = emptyBlock("Admin access required.");
        return;
    }

    dom.adminUserList.innerHTML = state.adminUsers.length
        ? state.adminUsers.map((item) => `
            <article class="list-item">
                <div class="list-item-header">
                    <div>
                        <h3>${escapeHtml(item.email)}</h3>
                        <p class="muted-copy">${escapeHtml((item.roles || []).join(", "))} | ${item.enabled ? "Enabled" : "Disabled"}</p>
                    </div>
                    <button class="ghost-button" type="button" data-action="toggle-user" data-id="${item.id}" data-enabled="${item.enabled}">
                        ${item.enabled ? "Disable" : "Enable"}
                    </button>
                </div>
            </article>
        `).join("")
        : emptyBlock("No users found.");

    dom.auditLogList.innerHTML = state.auditLogs.length
        ? state.auditLogs.map((item) => `
            <article class="list-item">
                <div class="list-item-header">
                    <div>
                        <h3>${escapeHtml(item.action || "UNKNOWN")}</h3>
                        <p class="muted-copy">${escapeHtml(item.description || "No description")}</p>
                    </div>
                    <span class="tag tag-neutral">${escapeHtml(formatDateTime(item.createdAt))}</span>
                </div>
                <p class="muted-copy">User ID: ${escapeHtml(item.userId || "unknown")}</p>
            </article>
        `).join("")
        : emptyBlock("No audit log entries found.");
}

function renderReferenceSelects() {
    fillSelect(dom.jobSourceSelect, state.sources.map((item) => ({ value: item.id, label: item.name })), {
        includeBlank: state.sources.length === 0,
        blankLabel: "Create a source first"
    });
    fillSelect(dom.jobLocationSelect, state.locations.map((item) => ({ value: item.id, label: `${item.city}, ${item.country}` })), {
        includeBlank: true,
        blankLabel: "No location"
    });
    fillSelect(dom.jobResumeSelect, state.resumes.map((item) => ({ value: item.id, label: item.title })), {
        includeBlank: true,
        blankLabel: "No resume"
    });
    fillSelect(dom.jobCoverLetterSelect, state.coverLetters.map((item) => ({ value: item.id, label: item.title })), {
        includeBlank: true,
        blankLabel: "No cover letter"
    });
}

function fillSelect(selectElement, options, config = {}) {
    if (!selectElement) {
        return;
    }

    const includeBlank = config.includeBlank ?? true;
    const blankLabel = config.blankLabel ?? "Select an option";
    const normalized = options.map((item) => typeof item === "string"
        ? { value: item, label: humanize(item) }
        : { value: item.value, label: item.label });

    const markup = [];
    if (includeBlank) {
        markup.push(`<option value="">${escapeHtml(blankLabel)}</option>`);
    }
    markup.push(...normalized.map((item) => `<option value="${escapeHtml(item.value)}">${escapeHtml(item.label)}</option>`));
    selectElement.innerHTML = markup.join("");

    if (config.selected !== undefined && config.selected !== null) {
        selectElement.value = String(config.selected);
    }
}

function renderCalendarEvent(item) {
    return `
        <article class="event-card">
            <div class="list-item-header">
                <div>
                    <h3>${escapeHtml(item.title)}</h3>
                    <p class="event-copy">${escapeHtml(item.company || "General")} ${item.jobTitle ? `| ${escapeHtml(item.jobTitle)}` : ""}</p>
                </div>
                <span class="tag">${escapeHtml(item.type)}</span>
            </div>
            <p class="muted-copy">${escapeHtml(formatDateTime(item.startsAt))}${item.endsAt ? ` to ${escapeHtml(formatDateTime(item.endsAt))}` : ""}</p>
            ${item.description ? `<p class="muted-copy">${escapeHtml(item.description)}</p>` : ""}
        </article>
    `;
}

function renderResourceCards(items, kind, contentRenderer) {
    if (!items.length) {
        return emptyBlock("Nothing here yet.");
    }

    return items.map((item) => `
        <article class="list-item">
            <div class="list-item-header">
                <div>${contentRenderer(item)}</div>
                <button class="ghost-button" type="button" data-delete-kind="${kind}" data-id="${item.id}">Delete</button>
            </div>
        </article>
    `).join("");
}

function renderPillList(items) {
    return `
        <div class="pill-list">
            ${items.map((item) => `
                <span class="pill">
                    ${escapeHtml(item.label)}
                    <button class="ghost-button" type="button" data-delete-kind="${item.kind}" data-id="${item.id}">x</button>
                </span>
            `).join("")}
        </div>
    `;
}

function metricCard(label, value) {
    return `
        <article class="metric-card surface">
            <strong>${escapeHtml(String(value))}</strong>
            <span>${escapeHtml(label)}</span>
        </article>
    `;
}

function snapshotTile(label, value) {
    return `
        <article class="snapshot-tile">
            <span>${escapeHtml(label)}</span>
            <strong>${escapeHtml(String(value ?? "-"))}</strong>
        </article>
    `;
}

function emptyBlock(message) {
    return `<div class="empty-shell">${escapeHtml(message)}</div>`;
}

function isAdmin() {
    return Array.isArray(state.user?.roles) && state.user.roles.includes("ADMIN");
}

function toggleSelectedApplicationControls(enabled) {
    const controls = [
        dom.selectedStatus,
        dom.saveStatusButton,
        dom.deleteApplicationButton,
        dom.skillForm,
        dom.attachmentForm,
        dom.communicationForm,
        dom.followUpForm,
        dom.scheduledForm
    ];

    controls.forEach((item) => {
        if (!item) {
            return;
        }
        if (item instanceof HTMLFormElement) {
            item.querySelectorAll("input, select, textarea, button").forEach((control) => {
                control.disabled = !enabled;
            });
        } else {
            item.disabled = !enabled;
        }
    });
}

async function handleCreateSource(event) {
    event.preventDefault();
    const name = event.currentTarget.elements.name.value.trim();
    if (!name) {
        return;
    }
    await runMutation(async () => {
        await apiJson("/api/job-sources", { method: "POST", body: { name } });
        event.currentTarget.reset();
        await loadReferences();
        renderReferenceSelects();
        renderLibrary();
    }, "Source added");
}

async function handleCreateLocation(event) {
    event.preventDefault();
    const city = event.currentTarget.elements.city.value.trim();
    const country = event.currentTarget.elements.country.value.trim();
    if (!city || !country) {
        return;
    }
    await runMutation(async () => {
        await apiJson("/api/locations", { method: "POST", body: { city, country } });
        event.currentTarget.reset();
        await loadReferences();
        renderReferenceSelects();
        renderLibrary();
    }, "Location added");
}

async function handleCreateResume(event) {
    event.preventDefault();
    const title = event.currentTarget.elements.title.value.trim();
    const filePath = event.currentTarget.elements.filePath.value.trim();
    await runMutation(async () => {
        await apiJson("/api/resumes", { method: "POST", body: { title, filePath } });
        event.currentTarget.reset();
        await loadReferences();
        renderReferenceSelects();
        renderLibrary();
    }, "Resume saved");
}

async function handleCreateCoverLetter(event) {
    event.preventDefault();
    const title = event.currentTarget.elements.title.value.trim();
    const filePath = event.currentTarget.elements.filePath.value.trim();
    const content = event.currentTarget.elements.content.value.trim();
    await runMutation(async () => {
        await apiJson("/api/cover-letters", { method: "POST", body: { title, filePath, content } });
        event.currentTarget.reset();
        await loadReferences();
        renderReferenceSelects();
        renderLibrary();
    }, "Cover letter saved");
}

async function handleCreateApplication(event) {
    event.preventDefault();
    const form = event.currentTarget.elements;
    const payload = {
        jobTitle: form.jobTitle.value.trim(),
        company: form.company.value.trim(),
        status: form.status.value,
        sourceId: form.sourceId.value || null,
        locationId: form.locationId.value || null,
        resumeId: form.resumeId.value || null,
        coverLetterId: form.coverLetterId.value || null,
        jobDescription: form.jobDescription.value.trim(),
        notes: form.notes.value.trim() || null,
        appliedOn: form.appliedOn.value || null,
        deadline: form.deadline.value || null
    };

    await runMutation(async () => {
        const created = await apiJson("/api/job-applications", {
            method: "POST",
            body: pruneEmpty(payload)
        });
        event.currentTarget.reset();
        form.status.value = "APPLIED";
        await loadApplications(0);
        await loadOverview();
        await loadSelectedApplication(created.id);
        renderOverview();
        renderPipeline();
    }, "Application created");
}

async function handleApplicationListClick(event) {
    const button = event.target.closest("[data-action='select-application']");
    if (!button) {
        return;
    }
    await runMutation(async () => {
        await loadSelectedApplication(button.dataset.id);
        renderPipeline();
    }, null);
}

async function handleSelectedStatusUpdate() {
    if (!state.selectedApplicationId) {
        return;
    }
    const status = dom.selectedStatus.value;
    await runMutation(async () => {
        const detail = await apiJson(`/api/job-applications/${state.selectedApplicationId}`, {
            method: "PATCH",
            body: { status }
        });
        state.selectedApplication = detail;
        await Promise.all([
            loadApplications(state.applications.number),
            loadOverview(),
            loadSelectedApplication(state.selectedApplicationId)
        ]);
        renderOverview();
        renderPipeline();
    }, "Application updated");
}

async function handleDeleteApplication() {
    if (!state.selectedApplicationId) {
        return;
    }
    if (!window.confirm("Delete this application? The backend will soft-delete it.")) {
        return;
    }
    await runMutation(async () => {
        await apiJson(`/api/job-applications/${state.selectedApplicationId}`, { method: "DELETE" });
        const fallbackId = state.applications.content.find((item) => item.id !== state.selectedApplicationId)?.id ?? null;
        clearSelectedApplication();
        await loadApplications(0);
        await loadOverview();
        if (fallbackId) {
            await loadSelectedApplication(fallbackId);
        } else if (state.applications.content[0]) {
            await loadSelectedApplication(state.applications.content[0].id);
        }
        renderOverview();
        renderPipeline();
    }, "Application deleted");
}

async function handleCreateSkill(event) {
    event.preventDefault();
    if (!state.selectedApplicationId) {
        return;
    }
    const skillName = event.currentTarget.elements.skillName.value.trim();
    await createSelectedResource(event.currentTarget, "/api/skills", { skillName, jobApplicationId: state.selectedApplicationId }, "Skill added");
}

async function handleCreateAttachment(event) {
    event.preventDefault();
    if (!state.selectedApplicationId) {
        return;
    }
    const form = event.currentTarget.elements;
    await createSelectedResource(event.currentTarget, "/api/attachments", {
        type: form.type.value,
        filePath: form.filePath.value.trim(),
        jobApplicationId: state.selectedApplicationId
    }, "Attachment added");
}

async function handleCreateCommunication(event) {
    event.preventDefault();
    if (!state.selectedApplicationId) {
        return;
    }
    const form = event.currentTarget.elements;
    await createSelectedResource(event.currentTarget, "/api/communications", {
        type: form.type.value,
        direction: form.direction.value,
        timestamp: form.timestamp.value,
        message: form.message.value.trim(),
        jobApplicationId: state.selectedApplicationId
    }, "Communication logged");
}

async function handleCreateFollowUp(event) {
    event.preventDefault();
    if (!state.selectedApplicationId) {
        return;
    }
    const form = event.currentTarget.elements;
    await createSelectedResource(event.currentTarget, "/api/follow-ups", {
        remindOn: form.remindOn.value,
        note: form.note.value.trim(),
        jobApplicationId: state.selectedApplicationId
    }, "Reminder created");
}

async function handleCreateScheduled(event) {
    event.preventDefault();
    if (!state.selectedApplicationId) {
        return;
    }
    const form = event.currentTarget.elements;
    await createSelectedResource(event.currentTarget, "/api/scheduled-communications", {
        type: form.type.value,
        scheduledFor: form.scheduledFor.value,
        notes: form.notes.value.trim(),
        jobApplicationId: state.selectedApplicationId
    }, "Scheduled event created");
}

async function createSelectedResource(formElement, path, payload, successMessage) {
    await runMutation(async () => {
        await apiJson(path, { method: "POST", body: payload });
        formElement.reset();
        await Promise.all([
            loadSelectedApplication(state.selectedApplicationId),
            loadOverview()
        ]);
        renderOverview();
        renderPipeline();
    }, successMessage);
}

async function handleResourceDeleteClick(event) {
    const button = event.target.closest("[data-delete-kind]");
    if (!button) {
        return;
    }

    const { deleteKind, id } = button.dataset;
    const config = {
        source: { path: `/api/job-sources/${id}`, after: refreshReferencesAndLibrary },
        location: { path: `/api/locations/${id}`, after: refreshReferencesAndLibrary },
        resume: { path: `/api/resumes/${id}`, after: refreshReferencesAndLibrary },
        "cover-letter": { path: `/api/cover-letters/${id}`, after: refreshReferencesAndLibrary },
        attachment: { path: `/api/attachments/${id}`, after: refreshSelectedWorkspace },
        communication: { path: `/api/communications/${id}`, after: refreshSelectedWorkspace },
        "follow-up": { path: `/api/follow-ups/${id}`, after: refreshSelectedWorkspace },
        scheduled: { path: `/api/scheduled-communications/${id}`, after: refreshSelectedWorkspace },
        skill: { path: `/api/skills/${id}`, after: refreshSelectedWorkspace }
    }[deleteKind];

    if (!config) {
        return;
    }

    await runMutation(async () => {
        await apiJson(config.path, { method: "DELETE" });
        await config.after();
    }, "Item deleted");
}

async function refreshReferencesAndLibrary() {
    await loadReferences();
    await loadApplications(state.applications.number);
    if (state.selectedApplicationId) {
        await loadSelectedApplication(state.selectedApplicationId);
    }
    renderReferenceSelects();
    renderPipeline();
    renderLibrary();
}

async function refreshSelectedWorkspace() {
    await Promise.all([
        loadSelectedApplication(state.selectedApplicationId),
        loadOverview()
    ]);
    renderOverview();
    renderPipeline();
}

async function handleCalendarFilter(event) {
    event.preventDefault();
    const from = event.currentTarget.elements.from.value;
    const to = event.currentTarget.elements.to.value;
    try {
        state.calendarEvents = await apiJson(buildCalendarPath("/api/calendar/events", from, to));
        renderCalendar();
        showToast("Calendar updated", "The event feed reflects the selected range.", "success");
    } catch (error) {
        showToast("Calendar refresh failed", error.message, "error");
    }
}

async function handleProfileUpdate(event) {
    event.preventDefault();
    const email = event.currentTarget.elements.email.value.trim();
    const password = event.currentTarget.elements.password.value.trim();
    const emailChanged = email && email !== state.user.email;
    const payload = pruneEmpty({ email, password });

    await runMutation(async () => {
        const updated = await apiJson("/api/users/me", { method: "PATCH", body: payload });
        state.user = { ...state.user, ...updated };
        renderAccount();
        if (emailChanged) {
            showToast("Profile updated", "Email changed. Please sign in again with the new address.", "success");
            logout();
            return;
        }
        event.currentTarget.reset();
        dom.profileEmailInput.value = state.user.email;
    }, emailChanged ? null : "Profile saved");
}

async function handleDeactivateAccount() {
    if (!window.confirm("Deactivate your own account? You will be signed out immediately.")) {
        return;
    }
    await runMutation(async () => {
        await apiJson("/api/users/me", { method: "DELETE" });
        clearSession();
        render();
    }, "Account deactivated");
}

async function handleAdminUserToggle(event) {
    const button = event.target.closest("[data-action='toggle-user']");
    if (!button) {
        return;
    }
    const nextEnabled = button.dataset.enabled !== "true";
    await runMutation(async () => {
        await apiJson(`/api/users/${button.dataset.id}`, {
            method: "PATCH",
            body: { enabled: nextEnabled }
        });
        await loadAdmin();
        renderAdmin();
    }, `User ${nextEnabled ? "enabled" : "disabled"}`);
}

async function downloadProtectedFile(path) {
    try {
        const response = await fetch(path, {
            headers: {
                Authorization: `Bearer ${state.token}`
            }
        });
        if (!response.ok) {
            const parsed = await parseResponseBody(response);
            throw new Error(resolveErrorMessage(parsed, response.statusText));
        }

        const blob = await response.blob();
        const fileName = fileNameFromResponse(response) || "download";
        const url = URL.createObjectURL(blob);
        const anchor = document.createElement("a");
        anchor.href = url;
        anchor.download = fileName;
        document.body.appendChild(anchor);
        anchor.click();
        anchor.remove();
        URL.revokeObjectURL(url);
        showToast("Download started", fileName, "success");
    } catch (error) {
        showToast("Download failed", error.message, "error");
    }
}

function buildCalendarPath(basePath, from = "", to = "") {
    const form = dom.calendarFilterForm?.elements;
    const effectiveFrom = from || form?.from?.value || "";
    const effectiveTo = to || form?.to?.value || "";
    const params = new URLSearchParams();
    if (effectiveFrom) {
        params.set("from", effectiveFrom);
    }
    if (effectiveTo) {
        params.set("to", effectiveTo);
    }
    return params.toString() ? `${basePath}?${params.toString()}` : basePath;
}

function logout() {
    clearSession();
    switchAuthMode("login");
    render();
}

function clearSession() {
    state.token = null;
    state.user = null;
    clearSelectedApplication();
    localStorage.removeItem(STORAGE_KEY);
}

function clearSelectedApplication() {
    state.selectedApplicationId = null;
    state.selectedApplication = null;
    state.selectedTimeline = [];
    state.selectedAttachments = [];
    state.selectedCommunications = [];
    state.selectedSkills = [];
    state.selectedFollowUps = [];
    state.selectedScheduled = [];
    state.selectedMatch = null;
}

async function runMutation(task, successMessage) {
    try {
        await task();
        if (successMessage) {
            showToast(successMessage, "The workspace has been updated.", "success");
        }
    } catch (error) {
        showToast("Request failed", error.message, "error");
    }
}

async function apiJson(path, options = {}) {
    const response = await fetch(path, {
        method: options.method || "GET",
        headers: buildHeaders(options),
        body: options.body ? JSON.stringify(options.body) : undefined
    });
    const parsed = await parseResponseBody(response);

    if (!response.ok) {
        throw new Error(resolveErrorMessage(parsed, response.statusText));
    }

    return parsed;
}

function buildHeaders(options) {
    const headers = {};
    if (options.body) {
        headers["Content-Type"] = "application/json";
    }
    if (options.auth !== false && state.token) {
        headers.Authorization = `Bearer ${state.token}`;
    }
    return headers;
}

async function parseResponseBody(response) {
    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    const text = await response.text();
    try {
        return JSON.parse(text);
    } catch (error) {
        return text;
    }
}

function resolveErrorMessage(parsed, fallback) {
    if (!parsed) {
        return fallback || "Request failed";
    }
    if (typeof parsed === "string") {
        return parsed;
    }
    return parsed.message || parsed.error || parsed.description || fallback || "Request failed";
}

function fileNameFromResponse(response) {
    const contentDisposition = response.headers.get("content-disposition");
    if (!contentDisposition) {
        return null;
    }
    const match = contentDisposition.match(/filename="([^"]+)"/);
    return match ? match[1] : null;
}

function pruneEmpty(payload) {
    return Object.fromEntries(Object.entries(payload).filter(([, value]) => value !== "" && value !== null && value !== undefined));
}

function showToast(title, message, type = "info") {
    const toast = document.createElement("article");
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <p class="toast-title">${escapeHtml(title)}</p>
        <p class="toast-copy">${escapeHtml(message || "")}</p>
    `;
    dom.toastRoot.appendChild(toast);
    window.setTimeout(() => {
        toast.remove();
    }, 3600);
}

function humanize(value) {
    return String(value).replaceAll("_", " ").toLowerCase().replace(/\b\w/g, (match) => match.toUpperCase());
}

function shortText(value, maxLength) {
    if (!value) {
        return "";
    }
    return value.length > maxLength ? `${value.slice(0, maxLength - 1)}...` : value;
}

function formatDateTime(value) {
    if (!value) {
        return "Not set";
    }
    return new Intl.DateTimeFormat("en-GB", {
        year: "numeric",
        month: "short",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    }).format(new Date(value));
}

function toPercent(value) {
    return `${Math.round((Number(value) || 0) * 100)}%`;
}

function lookupSourceName(id) {
    return state.sources.find((item) => item.id === id)?.name || "Not linked";
}

function lookupResumeTitle(id) {
    return state.resumes.find((item) => item.id === id)?.title || "Not linked";
}

function lookupCoverLetterTitle(id) {
    return state.coverLetters.find((item) => item.id === id)?.title || "Not linked";
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}
