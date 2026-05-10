import { createContext, useContext, useEffect, useState } from "react";
import {
    HashRouter,
    Link,
    NavLink,
    Navigate,
    Route,
    Routes,
    useLocation,
    useNavigate,
    useParams
} from "react-router-dom";

const STORAGE_KEY = "jobtrackr.token";

const JOB_STATUSES = ["DRAFT", "APPLIED", "INTERVIEW", "OFFER", "REJECTED"];
const ATTACHMENT_TYPES = ["JOB_DESCRIPTION", "OFFER_LETTER", "INTERVIEW_PREP", "REJECTION_LETTER", "OTHER"];
const COMMUNICATION_METHODS = ["EMAIL", "CALL", "LINKEDIN", "IN_PERSON"];
const COMMUNICATION_DIRECTIONS = ["INBOUND", "OUTBOUND"];
const SCHEDULED_TYPES = ["INTERVIEW", "ONLINE_ASSESSMENT", "CALL", "IN_PERSON_ASSESSMENT"];

const INITIAL_APPLICATIONS = {
    content: [],
    number: 0,
    size: 6,
    totalPages: 0,
    totalElements: 0,
    sort: "createdAt,desc"
};

const WorkspaceContext = createContext(null);

export default function App() {
    const [token, setToken] = useState(() => window.localStorage.getItem(STORAGE_KEY));
    const [user, setUser] = useState(null);
    const [sessionState, setSessionState] = useState(token ? "restoring" : "guest");
    const [authMode, setAuthMode] = useState("login");
    const [authMessage, setAuthMessage] = useState("");
    const [toasts, setToasts] = useState([]);

    useEffect(() => {
        if (!token) {
            setUser(null);
            setSessionState("guest");
            return;
        }

        let cancelled = false;
        setSessionState("restoring");

        apiJson("/api/auth/me", { token })
            .then((currentUser) => {
                if (cancelled) {
                    return;
                }
                setUser(currentUser);
                setSessionState("ready");
            })
            .catch((error) => {
                if (cancelled) {
                    return;
                }
                window.localStorage.removeItem(STORAGE_KEY);
                setToken(null);
                setUser(null);
                setSessionState("guest");
                enqueueToast(setToasts, "Session expired", error.message || "Please sign in again.", "error");
            });

        return () => {
            cancelled = true;
        };
    }, [token]);

    async function handleLogin(credentials) {
        try {
            const response = await apiJson("/api/auth/login", {
                method: "POST",
                body: credentials,
                auth: false
            });
            window.localStorage.setItem(STORAGE_KEY, response.token);
            setToken(response.token);
            setAuthMessage("");
            enqueueToast(setToasts, "Welcome back", "The workspace is ready.", "success");
        } catch (error) {
            setAuthMessage(error.message);
            enqueueToast(setToasts, "Login failed", error.message, "error");
        }
    }

    async function handleSignup(credentials) {
        try {
            await apiJson("/api/auth/signup", {
                method: "POST",
                body: credentials,
                auth: false
            });
            enqueueToast(setToasts, "Account created", "Signing you in now.", "success");
            await handleLogin(credentials);
        } catch (error) {
            setAuthMessage(error.message);
            enqueueToast(setToasts, "Signup failed", error.message, "error");
        }
    }

    function logout(options = {}) {
        window.localStorage.removeItem(STORAGE_KEY);
        setToken(null);
        setUser(null);
        setSessionState("guest");
        setAuthMode("login");
        setAuthMessage("");
        if (!options.silent) {
            enqueueToast(
                setToasts,
                options.title || "Signed out",
                options.message || "You have left the workspace.",
                "success"
            );
        }
    }

    const isBusy = sessionState === "restoring";

    return (
        <>
            {sessionState === "ready" && user ? (
                <HashRouter>
                    <WorkspaceApp
                        token={token}
                        user={user}
                        setUser={setUser}
                        logout={logout}
                        setToasts={setToasts}
                    />
                </HashRouter>
            ) : (
                <AuthScreen
                    mode={authMode}
                    busy={isBusy}
                    message={authMessage}
                    onModeChange={(nextMode) => {
                        setAuthMode(nextMode);
                        setAuthMessage("");
                    }}
                    onLogin={handleLogin}
                    onSignup={handleSignup}
                />
            )}

            <ToastRack toasts={toasts} />
        </>
    );
}

function WorkspaceApp({ token, user, setUser, logout, setToasts }) {
    const [references, setReferences] = useState({
        sources: [],
        locations: [],
        resumes: [],
        coverLetters: []
    });
    const [overview, setOverview] = useState(null);
    const [analytics, setAnalytics] = useState(null);
    const [recommendations, setRecommendations] = useState([]);
    const [overviewEvents, setOverviewEvents] = useState([]);
    const [calendarEvents, setCalendarEvents] = useState([]);
    const [calendarFilters, setCalendarFilters] = useState({ from: "", to: "" });
    const [applications, setApplications] = useState(INITIAL_APPLICATIONS);
    const [detail, setDetail] = useState(createEmptyDetail());
    const [adminUsers, setAdminUsers] = useState([]);
    const [auditLogs, setAuditLogs] = useState([]);
    const [booting, setBooting] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [detailLoading, setDetailLoading] = useState(false);

    const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");

    useEffect(() => {
        let cancelled = false;

        async function bootstrapWorkspace() {
            setBooting(true);
            try {
                const [nextReferences, nextOverview, nextApplications, nextAdmin] = await Promise.all([
                    fetchReferencesBundle(token),
                    fetchOverviewBundle(token),
                    fetchApplicationsPage(token, 0, INITIAL_APPLICATIONS.size, INITIAL_APPLICATIONS.sort),
                    isAdmin ? fetchAdminBundle(token) : Promise.resolve(null)
                ]);

                if (cancelled) {
                    return;
                }

                setReferences(nextReferences);
                setOverview(nextOverview.overview);
                setAnalytics(nextOverview.analytics);
                setRecommendations(nextOverview.recommendations);
                setOverviewEvents(nextOverview.overviewEvents);
                setCalendarEvents(nextOverview.overviewEvents);
                setCalendarFilters({ from: "", to: "" });
                setApplications((current) => ({
                    ...current,
                    ...nextApplications,
                    sort: INITIAL_APPLICATIONS.sort
                }));
                setDetail(createEmptyDetail());

                if (nextAdmin) {
                    setAdminUsers(nextAdmin.users);
                    setAuditLogs(nextAdmin.auditLogs);
                } else {
                    setAdminUsers([]);
                    setAuditLogs([]);
                }
            } catch (error) {
                enqueueToast(setToasts, "Workspace load failed", error.message, "error");
            } finally {
                if (!cancelled) {
                    setBooting(false);
                }
            }
        }

        void bootstrapWorkspace();

        return () => {
            cancelled = true;
        };
    }, [token, user?.email, isAdmin, setToasts]);

    async function loadReferencesData() {
        const nextReferences = await fetchReferencesBundle(token);
        setReferences(nextReferences);
        return nextReferences;
    }

    async function loadOverviewData() {
        const nextOverview = await fetchOverviewBundle(token);
        setOverview(nextOverview.overview);
        setAnalytics(nextOverview.analytics);
        setRecommendations(nextOverview.recommendations);
        setOverviewEvents(nextOverview.overviewEvents);
        if (!calendarFilters.from && !calendarFilters.to) {
            setCalendarEvents(nextOverview.overviewEvents);
        }
        return nextOverview;
    }

    async function loadApplicationsPageData(page = applications.number, sort = applications.sort) {
        const nextApplications = await fetchApplicationsPage(token, page, applications.size, sort);
        setApplications((current) => ({
            ...current,
            ...nextApplications,
            sort
        }));
        return nextApplications;
    }

    async function ensureApplicationLoaded(applicationId, options = {}) {
        if (!applicationId) {
            setDetail(createEmptyDetail());
            return null;
        }

        if (!options.force && detail.loadedId === applicationId) {
            return detail;
        }

        setDetailLoading(true);
        try {
            const nextDetail = await fetchApplicationWorkspace(token, applicationId);
            setDetail(nextDetail);
            return nextDetail;
        } finally {
            setDetailLoading(false);
        }
    }

    async function loadAdminData() {
        if (!isAdmin) {
            setAdminUsers([]);
            setAuditLogs([]);
            return null;
        }

        const nextAdmin = await fetchAdminBundle(token);
        setAdminUsers(nextAdmin.users);
        setAuditLogs(nextAdmin.auditLogs);
        return nextAdmin;
    }

    async function refreshWorkspace() {
        setRefreshing(true);
        try {
            const tasks = [
                loadReferencesData(),
                loadOverviewData(),
                loadApplicationsPageData(applications.number, applications.sort)
            ];

            if (detail.loadedId) {
                tasks.push(ensureApplicationLoaded(detail.loadedId, { force: true }));
            }

            if (isAdmin) {
                tasks.push(loadAdminData());
            }

            if (calendarFilters.from || calendarFilters.to) {
                tasks.push(filterCalendar(calendarFilters.from, calendarFilters.to, { silent: true }));
            }

            await Promise.all(tasks);
            enqueueToast(setToasts, "Workspace refreshed", "The latest backend data is now on screen.", "success");
        } catch (error) {
            enqueueToast(setToasts, "Refresh failed", error.message, "error");
        } finally {
            setRefreshing(false);
        }
    }

    async function runMutation(task, successTitle, successMessage = "The workspace has been updated.") {
        try {
            const result = await task();
            if (successTitle) {
                enqueueToast(setToasts, successTitle, successMessage, "success");
            }
            return result;
        } catch (error) {
            enqueueToast(setToasts, "Request failed", error.message, "error");
            throw error;
        }
    }

    async function syncReferencesAndApplications(options = {}) {
        await Promise.all([
            loadReferencesData(),
            loadApplicationsPageData(applications.number, applications.sort)
        ]);

        if (detail.loadedId && !options.skipDetail) {
            await ensureApplicationLoaded(detail.loadedId, { force: true });
        }
    }

    async function createSource(payload) {
        return runMutation(async () => {
            await apiJson("/api/job-sources", { method: "POST", body: payload, token });
            await loadReferencesData();
        }, "Source ready");
    }

    async function createLocation(payload) {
        return runMutation(async () => {
            await apiJson("/api/locations", { method: "POST", body: payload, token });
            await loadReferencesData();
        }, "Location ready");
    }

    async function createResume(payload) {
        return runMutation(async () => {
            await apiJson("/api/resumes", { method: "POST", body: payload, token });
            await syncReferencesAndApplications();
        }, "Resume saved");
    }

    async function createCoverLetter(payload) {
        return runMutation(async () => {
            await apiJson("/api/cover-letters", { method: "POST", body: payload, token });
            await syncReferencesAndApplications();
        }, "Cover letter saved");
    }

    async function deleteReference(kind, id) {
        const path = {
            source: `/api/job-sources/${id}`,
            location: `/api/locations/${id}`,
            resume: `/api/resumes/${id}`,
            "cover-letter": `/api/cover-letters/${id}`
        }[kind];

        if (!path) {
            return null;
        }

        return runMutation(async () => {
            await apiJson(path, { method: "DELETE", token });
            await syncReferencesAndApplications();
        }, "Item deleted");
    }

    async function createApplication(payload) {
        return runMutation(async () => {
            const created = await apiJson("/api/job-applications", {
                method: "POST",
                body: pruneEmpty(payload),
                token
            });
            await Promise.all([
                loadApplicationsPageData(0, applications.sort),
                loadOverviewData()
            ]);
            await ensureApplicationLoaded(created.id, { force: true });
            return created;
        }, "Application created");
    }

    async function updateApplicationStatus(applicationId, status) {
        return runMutation(async () => {
            await apiJson(`/api/job-applications/${applicationId}`, {
                method: "PATCH",
                body: { status },
                token
            });
            await Promise.all([
                loadApplicationsPageData(applications.number, applications.sort),
                loadOverviewData(),
                ensureApplicationLoaded(applicationId, { force: true })
            ]);
        }, "Application updated");
    }

    async function deleteApplication(applicationId) {
        return runMutation(async () => {
            await apiJson(`/api/job-applications/${applicationId}`, {
                method: "DELETE",
                token
            });

            setDetail(createEmptyDetail());

            const nextApplications = await loadApplicationsPageData(0, applications.sort);
            await loadOverviewData();

            const nextId = nextApplications.content[0]?.id ?? null;
            if (nextId) {
                await ensureApplicationLoaded(nextId, { force: true });
            }
            return nextId;
        }, "Application deleted");
    }

    async function createWorkspaceResource(path, payload, successTitle) {
        return runMutation(async () => {
            await apiJson(path, {
                method: "POST",
                body: payload,
                token
            });
            await Promise.all([
                loadOverviewData(),
                ensureApplicationLoaded(payload.jobApplicationId, { force: true })
            ]);
        }, successTitle);
    }

    async function deleteWorkspaceResource(kind, id) {
        const path = {
            attachment: `/api/attachments/${id}`,
            communication: `/api/communications/${id}`,
            "follow-up": `/api/follow-ups/${id}`,
            scheduled: `/api/scheduled-communications/${id}`,
            skill: `/api/skills/${id}`
        }[kind];

        if (!path || !detail.loadedId) {
            return null;
        }

        return runMutation(async () => {
            await apiJson(path, { method: "DELETE", token });
            await Promise.all([
                loadOverviewData(),
                ensureApplicationLoaded(detail.loadedId, { force: true })
            ]);
        }, "Item deleted");
    }

    async function filterCalendar(from, to, options = {}) {
        try {
            const normalized = { from: from || "", to: to || "" };
            const nextEvents = await apiJson(buildCalendarPath("/api/calendar/events", normalized.from, normalized.to), { token });
            setCalendarFilters(normalized);
            setCalendarEvents(nextEvents);
            if (!options.silent) {
                enqueueToast(setToasts, "Calendar updated", "The event feed reflects the selected range.", "success");
            }
            return nextEvents;
        } catch (error) {
            if (!options.silent) {
                enqueueToast(setToasts, "Calendar refresh failed", error.message, "error");
            }
            throw error;
        }
    }

    async function resetCalendarFilter() {
        setCalendarFilters({ from: "", to: "" });
        setCalendarEvents(overviewEvents);
        enqueueToast(setToasts, "Calendar reset", "Showing the default event feed again.", "success");
    }

    async function updateProfile(payload) {
        return runMutation(async () => {
            const updated = await apiJson("/api/users/me", {
                method: "PATCH",
                body: pruneEmpty(payload),
                token
            });
            setUser((current) => ({
                ...current,
                ...updated
            }));

            if (payload.email && payload.email !== user.email) {
                enqueueToast(
                    setToasts,
                    "Profile updated",
                    "Email changed. Please sign in again with the new address.",
                    "success"
                );
                logout({
                    silent: true
                });
                return updated;
            }

            return updated;
        }, payload.email && payload.email !== user.email ? null : "Profile saved");
    }

    async function deactivateAccount() {
        return runMutation(async () => {
            await apiJson("/api/users/me", {
                method: "DELETE",
                token
            });
            logout({
                silent: true
            });
        }, "Account deactivated", "You have been signed out.");
    }

    async function toggleAdminUser(id, enabled) {
        return runMutation(async () => {
            await apiJson(`/api/users/${id}`, {
                method: "PATCH",
                body: { enabled },
                token
            });
            await loadAdminData();
        }, `User ${enabled ? "enabled" : "disabled"}`);
    }

    async function downloadFile(path) {
        try {
            await downloadProtectedFile(path, token);
            enqueueToast(setToasts, "Download started", "The file is on its way.", "success");
        } catch (error) {
            enqueueToast(setToasts, "Download failed", error.message, "error");
        }
    }

    const value = {
        user,
        isAdmin,
        references,
        overview,
        analytics,
        recommendations,
        overviewEvents,
        calendarEvents,
        calendarFilters,
        applications,
        detail,
        adminUsers,
        auditLogs,
        booting,
        refreshing,
        detailLoading,
        refreshWorkspace,
        ensureApplicationLoaded,
        loadApplicationsPageData,
        loadAdminData,
        createSource,
        createLocation,
        createResume,
        createCoverLetter,
        deleteReference,
        createApplication,
        updateApplicationStatus,
        deleteApplication,
        createWorkspaceResource,
        deleteWorkspaceResource,
        filterCalendar,
        resetCalendarFilter,
        updateProfile,
        deactivateAccount,
        toggleAdminUser,
        downloadFile
    };

    return (
        <WorkspaceContext.Provider value={value}>
            <WorkspaceFrame onLogout={logout} />
        </WorkspaceContext.Provider>
    );
}

function AuthScreen({ mode, busy, message, onModeChange, onLogin, onSignup }) {
    async function handleSubmit(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const payload = {
            email: String(formData.get("email") || "").trim(),
            password: String(formData.get("password") || "")
        };

        if (!payload.email || !payload.password) {
            return;
        }

        if (mode === "login") {
            await onLogin(payload);
        } else {
            await onSignup(payload);
        }
    }

    return (
        <div className="auth-shell">
            <div className="auth-backdrop">
                <div className="auth-orb auth-orb-left"></div>
                <div className="auth-orb auth-orb-right"></div>
                <div className="auth-grid"></div>
            </div>

            <div className="auth-layout">
                <section className="panel hero-panel">
                    <p className="eyebrow">Actual Frontend</p>
                    <h1>Give every part of the job search its own surface.</h1>
                    <p className="lede">
                        JobTrackr is now organized like a workspace: dashboard, queue, focused application room,
                        library, calendar, account, and admin. You stop scrolling one giant wall of controls and
                        start moving between clear modes.
                    </p>

                    <div className="hero-stat-grid">
                        <article className="hero-stat">
                            <span className="hero-stat-value">6</span>
                            <span className="hero-stat-label">core routes with dedicated layouts</span>
                        </article>
                        <article className="hero-stat">
                            <span className="hero-stat-value">1</span>
                            <span className="hero-stat-label">focused workspace per application</span>
                        </article>
                        <article className="hero-stat">
                            <span className="hero-stat-value">0</span>
                            <span className="hero-stat-label">reasons to keep every action on one page</span>
                        </article>
                    </div>

                    <div className="feature-grid">
                        <article className="feature-card">
                            <h2>Overview first</h2>
                            <p>See momentum, analytics, recommendations, and upcoming events before you dive into details.</p>
                        </article>
                        <article className="feature-card">
                            <h2>Real application rooms</h2>
                            <p>Each role gets its own view for status, timeline, activity, assets, and fit analysis.</p>
                        </article>
                        <article className="feature-card">
                            <h2>Cleaner operations</h2>
                            <p>Documents, references, exports, account settings, and admin tools live on their own pages.</p>
                        </article>
                    </div>
                </section>

                <section className="panel auth-panel">
                    <div className="auth-header">
                        <div>
                            <p className="eyebrow">Secure Access</p>
                            <h2>{mode === "login" ? "Sign in to your workspace" : "Create your workspace account"}</h2>
                        </div>

                        <div className="mode-switch">
                            <button
                                className={mode === "login" ? "mode-button is-active" : "mode-button"}
                                disabled={busy}
                                onClick={() => onModeChange("login")}
                                type="button"
                            >
                                Log in
                            </button>
                            <button
                                className={mode === "signup" ? "mode-button is-active" : "mode-button"}
                                disabled={busy}
                                onClick={() => onModeChange("signup")}
                                type="button"
                            >
                                Sign up
                            </button>
                        </div>
                    </div>

                    <p className="auth-copy">
                        {busy ? "Restoring your session from the browser token." : "Use the API-backed workspace directly from this bundled frontend."}
                    </p>

                    {message ? <div className="form-alert form-alert-error">{message}</div> : null}

                    <form className="stack-form" onSubmit={handleSubmit}>
                        <label>
                            <span>Email</span>
                            <input
                                autoComplete="email"
                                disabled={busy}
                                name="email"
                                placeholder="you@example.com"
                                required
                                type="email"
                            />
                        </label>
                        <label>
                            <span>Password</span>
                            <input
                                autoComplete={mode === "login" ? "current-password" : "new-password"}
                                disabled={busy}
                                name="password"
                                placeholder="Password123!"
                                required
                                type="password"
                            />
                        </label>
                        <button className="primary-button" disabled={busy} type="submit">
                            {busy ? "Restoring session" : mode === "login" ? "Enter JobTrackr" : "Create account"}
                        </button>
                    </form>
                </section>
            </div>
        </div>
    );
}

function WorkspaceFrame({ onLogout }) {
    const { user, isAdmin, booting, refreshing, refreshWorkspace } = useWorkspace();
    const location = useLocation();
    const meta = resolveViewMeta(location.pathname);

    return (
        <div className="workspace-shell">
            <div className="workspace-backdrop">
                <div className="workspace-orb workspace-orb-top"></div>
                <div className="workspace-orb workspace-orb-bottom"></div>
                <div className="workspace-grid"></div>
            </div>

            <div className="workspace-layout">
                <aside className="workspace-sidebar panel">
                    <div className="brand-block">
                        <p className="eyebrow">Workspace</p>
                        <h2>JobTrackr</h2>
                        <p>
                            Separate rooms for overview, applications, library, calendar, account, and admin.
                        </p>
                    </div>

                    <nav className="nav-stack" aria-label="Workspace sections">
                        <SidebarLink end label="Overview" to="/overview" />
                        <SidebarLink label="Applications" to="/applications" />
                        <SidebarLink label="Library" to="/library" />
                        <SidebarLink label="Calendar" to="/calendar" />
                        <SidebarLink label="Account" to="/account" />
                        {isAdmin ? <SidebarLink label="Admin" to="/admin" /> : null}
                    </nav>

                    <div className="sidebar-footer">
                        <span className="status-pill">Local API</span>
                        <span className="status-pill muted-pill">{isAdmin ? "ADMIN" : "BASIC"}</span>
                    </div>
                </aside>

                <div className="workspace-main">
                    <header className="topbar panel">
                        <div>
                            <p className="eyebrow">{meta.eyebrow}</p>
                            <h1>{meta.title}</h1>
                        </div>

                        <div className="topbar-actions">
                            <div className="identity-chip">{user.email}</div>
                            <button className="secondary-button" disabled={booting || refreshing} onClick={refreshWorkspace} type="button">
                                {refreshing ? "Refreshing" : "Refresh"}
                            </button>
                            <button className="ghost-button" onClick={() => onLogout()} type="button">
                                Sign out
                            </button>
                        </div>
                    </header>

                    <main className="page-shell">
                        {booting ? (
                            <LoadingPanel title="Loading workspace" copy="Collecting dashboard, references, applications, and calendar data." />
                        ) : (
                            <Routes>
                                <Route element={<Navigate replace to="/overview" />} path="/" />
                                <Route element={<OverviewPage />} path="/overview" />
                                <Route element={<ApplicationsPage />} path="/applications" />
                                <Route element={<ApplicationWorkspacePage />} path="/applications/:applicationId" />
                                <Route element={<LibraryPage />} path="/library" />
                                <Route element={<CalendarPage />} path="/calendar" />
                                <Route element={<AccountPage />} path="/account" />
                                <Route
                                    element={isAdmin ? <AdminPage /> : <Navigate replace to="/overview" />}
                                    path="/admin"
                                />
                                <Route element={<Navigate replace to="/overview" />} path="*" />
                            </Routes>
                        )}
                    </main>
                </div>
            </div>
        </div>
    );
}

function OverviewPage() {
    const { overview, analytics, recommendations, overviewEvents } = useWorkspace();

    return (
        <div className="page-grid">
            <section className="metric-grid">
                <MetricCard label="Total applications" value={overview?.totalApplications ?? 0} />
                <MetricCard label="Applications in range" value={overview?.applicationsInPeriod ?? 0} />
                <MetricCard label="Active pipeline" value={overview?.activeApplications ?? 0} />
                <MetricCard label="Closed outcomes" value={overview?.closedApplications ?? 0} />
            </section>

            <div className="dashboard-grid">
                <Panel eyebrow="Analytics" title="Pipeline conversion">
                    {analytics ? (
                        <>
                            <div className="metric-grid compact-grid">
                                <MetricCard label="Applied to interview" value={toPercent(analytics.appliedToInterviewRate)} />
                                <MetricCard label="Interview to offer" value={toPercent(analytics.interviewToOfferRate)} />
                                <MetricCard label="Applied to offer" value={toPercent(analytics.appliedToOfferRate)} />
                                <MetricCard label="Overdue reminders" value={analytics.overdueReminders ?? 0} />
                                <MetricCard label="Upcoming scheduled" value={analytics.upcomingScheduledEvents ?? 0} />
                                <MetricCard label="Offers" value={analytics.offerCount ?? 0} />
                            </div>

                            <div className="section-divider"></div>

                            <div className="stack-block">
                                <div className="section-heading">
                                    <div>
                                        <p className="eyebrow">Sources</p>
                                        <h3>Source performance</h3>
                                    </div>
                                </div>

                                {analytics.sourcePerformance?.length ? (
                                    <div className="list-stack">
                                        {analytics.sourcePerformance.map((item) => (
                                            <article className="list-card" key={item.sourceName}>
                                                <div className="list-card-header">
                                                    <div>
                                                        <h4>{item.sourceName}</h4>
                                                        <p>
                                                            {item.totalApplications} applications, {item.interviewStageCount} interview-stage,
                                                            {" "}
                                                            {item.offerCount} offers
                                                        </p>
                                                    </div>
                                                    <div className="tag-row">
                                                        <Tag>{toPercent(item.appliedToInterviewRate)} to interview</Tag>
                                                        <Tag tone="neutral">{toPercent(item.appliedToOfferRate)} to offer</Tag>
                                                    </div>
                                                </div>
                                            </article>
                                        ))}
                                    </div>
                                ) : (
                                    <EmptyState copy="Create applications to unlock source performance." />
                                )}
                            </div>
                        </>
                    ) : (
                        <EmptyState copy="No analytics yet." />
                    )}
                </Panel>

                <Panel eyebrow="Momentum" title="Recommendations">
                    {recommendations.length ? (
                        <div className="list-stack">
                            {recommendations.map((item, index) => (
                                <article className="list-card" key={`${item.title}-${index}`}>
                                    <div className="list-card-header">
                                        <div>
                                            <h4>{item.title}</h4>
                                            <p>{item.description}</p>
                                        </div>
                                        <div className="tag-row">
                                            <Tag>{item.priority}</Tag>
                                            <Tag tone="neutral">{item.type}</Tag>
                                        </div>
                                    </div>
                                    <p className="support-copy">
                                        {item.company || "General"}
                                        {item.jobTitle ? ` | ${item.jobTitle}` : ""}
                                    </p>
                                    <p className="support-copy">
                                        <strong>Suggested action:</strong> {item.suggestedAction}
                                    </p>
                                </article>
                            ))}
                        </div>
                    ) : (
                        <EmptyState copy="Recommendations will appear once you start building the pipeline." />
                    )}
                </Panel>
            </div>

            <Panel eyebrow="Calendar" title="Upcoming events">
                {overviewEvents.length ? (
                    <div className="list-stack">
                        {overviewEvents.map((item) => (
                            <EventCard item={item} key={`${item.title}-${item.startsAt}`} />
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No upcoming events are currently scheduled." />
                )}
            </Panel>
        </div>
    );
}

function ApplicationsPage() {
    const {
        references,
        applications,
        loadApplicationsPageData,
        createApplication,
        createLocation,
        createSource
    } = useWorkspace();
    const navigate = useNavigate();

    async function handleCreateSource(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const name = String(formData.get("name") || "").trim();
        if (!name) {
            return;
        }
        await createSource({ name });
        event.currentTarget.reset();
    }

    async function handleCreateLocation(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const city = String(formData.get("city") || "").trim();
        const country = String(formData.get("country") || "").trim();
        if (!city || !country) {
            return;
        }
        await createLocation({ city, country });
        event.currentTarget.reset();
    }

    async function handleCreateApplication(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const payload = {
            jobTitle: String(formData.get("jobTitle") || "").trim(),
            company: String(formData.get("company") || "").trim(),
            status: String(formData.get("status") || "APPLIED"),
            sourceId: toNullableValue(formData.get("sourceId")),
            locationId: toNullableValue(formData.get("locationId")),
            resumeId: toNullableValue(formData.get("resumeId")),
            coverLetterId: toNullableValue(formData.get("coverLetterId")),
            jobDescription: String(formData.get("jobDescription") || "").trim(),
            notes: String(formData.get("notes") || "").trim(),
            appliedOn: toNullableValue(formData.get("appliedOn")),
            deadline: toNullableValue(formData.get("deadline"))
        };

        const created = await createApplication(payload);
        event.currentTarget.reset();
        navigate(`/applications/${created.id}`);
    }

    return (
        <div className="applications-layout">
            <div className="application-control-column">
                <Panel eyebrow="Quick setup" title="Create sources and locations">
                    <div className="stack-cluster">
                        <form className="stack-form compact-form" onSubmit={handleCreateSource}>
                            <label>
                                <span>Job source</span>
                                <input name="name" placeholder="LinkedIn, Referral, Company site" required />
                            </label>
                            <button className="ghost-button" type="submit">Add source</button>
                        </form>

                        <form className="stack-form compact-form" onSubmit={handleCreateLocation}>
                            <label>
                                <span>City</span>
                                <input name="city" placeholder="London" required />
                            </label>
                            <label>
                                <span>Country</span>
                                <input name="country" placeholder="United Kingdom" required />
                            </label>
                            <button className="ghost-button" type="submit">Add location</button>
                        </form>
                    </div>
                </Panel>

                <Panel eyebrow="New application" title="Start a record">
                    <form className="stack-form" onSubmit={handleCreateApplication}>
                        <div className="field-grid">
                            <label>
                                <span>Job title</span>
                                <input name="jobTitle" placeholder="Backend Engineer" required />
                            </label>
                            <label>
                                <span>Company</span>
                                <input name="company" placeholder="OpenAI" required />
                            </label>
                        </div>

                        <div className="field-grid">
                            <label>
                                <span>Status</span>
                                <select defaultValue="APPLIED" name="status">
                                    {JOB_STATUSES.map((status) => (
                                        <option key={status} value={status}>{humanize(status)}</option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                <span>Source</span>
                                <select disabled={!references.sources.length} name="sourceId" required>
                                    <option value="">{references.sources.length ? "Select a source" : "Create a source first"}</option>
                                    {references.sources.map((source) => (
                                        <option key={source.id} value={source.id}>{source.name}</option>
                                    ))}
                                </select>
                            </label>
                        </div>

                        <div className="field-grid">
                            <label>
                                <span>Location</span>
                                <select name="locationId">
                                    <option value="">No location</option>
                                    {references.locations.map((location) => (
                                        <option key={location.id} value={location.id}>
                                            {location.city}, {location.country}
                                        </option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                <span>Resume</span>
                                <select name="resumeId">
                                    <option value="">No resume</option>
                                    {references.resumes.map((resume) => (
                                        <option key={resume.id} value={resume.id}>{resume.title}</option>
                                    ))}
                                </select>
                            </label>
                        </div>

                        <div className="field-grid">
                            <label>
                                <span>Cover letter</span>
                                <select name="coverLetterId">
                                    <option value="">No cover letter</option>
                                    {references.coverLetters.map((coverLetter) => (
                                        <option key={coverLetter.id} value={coverLetter.id}>{coverLetter.title}</option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                <span>Applied on</span>
                                <input name="appliedOn" type="datetime-local" />
                            </label>
                        </div>

                        <label>
                            <span>Deadline</span>
                            <input name="deadline" type="datetime-local" />
                        </label>

                        <label>
                            <span>Job description</span>
                            <textarea name="jobDescription" placeholder="Paste the role description, expectations, or notes from the listing."></textarea>
                        </label>

                        <label>
                            <span>Notes</span>
                            <textarea name="notes" placeholder="Why this role matters, who referred you, interview prep notes, and similar context."></textarea>
                        </label>

                        <button className="primary-button" disabled={!references.sources.length} type="submit">
                            Create application
                        </button>
                    </form>
                </Panel>
            </div>

            <div className="application-list-column">
                <Panel
                    actions={
                        <div className="toolbar-cluster">
                            <label className="inline-control">
                                <span>Sort</span>
                                <select
                                    onChange={(event) => loadApplicationsPageData(0, event.target.value)}
                                    value={applications.sort}
                                >
                                    <option value="createdAt,desc">Newest first</option>
                                    <option value="createdAt,asc">Oldest first</option>
                                    <option value="company,asc">Company A-Z</option>
                                    <option value="jobTitle,asc">Title A-Z</option>
                                </select>
                            </label>
                        </div>
                    }
                    eyebrow="Applications"
                    title="Queue and triage"
                >
                    <div className="panel-copy">
                        Open a role into its own workspace for status changes, skills, documents, reminders, activity, and matching.
                    </div>

                    {applications.content.length ? (
                        <div className="application-grid">
                            {applications.content.map((item) => (
                                <article className="application-card" key={item.id}>
                                    <div className="list-card-header">
                                        <div>
                                            <h3>{item.company} | {item.jobTitle}</h3>
                                            <p>
                                                {item.sourceName || "Unknown source"}
                                                {item.locationCity ? ` | ${item.locationCity}, ${item.locationCountry || ""}` : ""}
                                            </p>
                                        </div>
                                        <Tag>{item.status || "DRAFT"}</Tag>
                                    </div>

                                    <p className="support-copy">{shortText(item.jobDescription, 170)}</p>

                                    <div className="tag-row">
                                        {item.resumeName ? <Tag tone="neutral">Resume: {item.resumeName}</Tag> : null}
                                        {item.coverLetterName ? <Tag tone="neutral">Cover: {item.coverLetterName}</Tag> : null}
                                        {item.deadline ? <Tag tone="neutral">Deadline: {formatDateTime(item.deadline)}</Tag> : null}
                                    </div>

                                    <div className="button-row">
                                        <Link className="ghost-button link-button" to={`/applications/${item.id}`}>
                                            Open workspace
                                        </Link>
                                    </div>
                                </article>
                            ))}
                        </div>
                    ) : (
                        <EmptyState copy="No applications yet. Create one from the forms on this page." />
                    )}

                    <div className="pagination-row">
                        <button
                            className="secondary-button"
                            disabled={applications.number === 0}
                            onClick={() => loadApplicationsPageData(applications.number - 1, applications.sort)}
                            type="button"
                        >
                            Previous
                        </button>
                        <span className="pagination-label">
                            {applications.totalPages ? `Page ${applications.number + 1} of ${applications.totalPages}` : "Page 0 of 0"}
                        </span>
                        <button
                            className="secondary-button"
                            disabled={applications.number + 1 >= applications.totalPages}
                            onClick={() => loadApplicationsPageData(applications.number + 1, applications.sort)}
                            type="button"
                        >
                            Next
                        </button>
                    </div>
                </Panel>
            </div>
        </div>
    );
}

function ApplicationWorkspacePage() {
    const {
        detail,
        detailLoading,
        references,
        ensureApplicationLoaded,
        updateApplicationStatus,
        deleteApplication,
        createWorkspaceResource,
        deleteWorkspaceResource
    } = useWorkspace();
    const { applicationId } = useParams();
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState("summary");
    const [status, setStatus] = useState("DRAFT");

    useEffect(() => {
        let cancelled = false;
        setActiveTab("summary");

        async function loadDetail() {
            try {
                await ensureApplicationLoaded(applicationId, { force: true });
            } catch (error) {
                if (!cancelled) {
                    navigate("/applications", { replace: true });
                }
            }
        }

        void loadDetail();

        return () => {
            cancelled = true;
        };
    }, [applicationId, navigate]);

    useEffect(() => {
        if (detail.application?.status) {
            setStatus(detail.application.status);
        }
    }, [detail.application]);

    async function handleStatusSave() {
        if (!detail.application) {
            return;
        }
        await updateApplicationStatus(detail.application.id, status);
    }

    async function handleDelete() {
        if (!detail.application || !window.confirm("Delete this application? The backend will soft-delete it.")) {
            return;
        }
        const nextId = await deleteApplication(detail.application.id);
        navigate(nextId ? `/applications/${nextId}` : "/applications", { replace: true });
    }

    async function handleSkillCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const skillName = String(formData.get("skillName") || "").trim();
        if (!skillName || !detail.application) {
            return;
        }
        await createWorkspaceResource("/api/skills", {
            skillName,
            jobApplicationId: detail.application.id
        }, "Skill added");
        event.currentTarget.reset();
    }

    async function handleAttachmentCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        if (!detail.application) {
            return;
        }
        await createWorkspaceResource("/api/attachments", {
            type: String(formData.get("type") || ""),
            filePath: String(formData.get("filePath") || "").trim(),
            jobApplicationId: detail.application.id
        }, "Attachment added");
        event.currentTarget.reset();
    }

    async function handleCommunicationCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        if (!detail.application) {
            return;
        }
        await createWorkspaceResource("/api/communications", {
            type: String(formData.get("type") || ""),
            direction: String(formData.get("direction") || ""),
            timestamp: toNullableValue(formData.get("timestamp")),
            message: String(formData.get("message") || "").trim(),
            jobApplicationId: detail.application.id
        }, "Communication logged");
        event.currentTarget.reset();
    }

    async function handleFollowUpCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        if (!detail.application) {
            return;
        }
        await createWorkspaceResource("/api/follow-ups", {
            remindOn: toNullableValue(formData.get("remindOn")),
            note: String(formData.get("note") || "").trim(),
            jobApplicationId: detail.application.id
        }, "Reminder created");
        event.currentTarget.reset();
    }

    async function handleScheduledCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        if (!detail.application) {
            return;
        }
        await createWorkspaceResource("/api/scheduled-communications", {
            type: String(formData.get("type") || ""),
            scheduledFor: toNullableValue(formData.get("scheduledFor")),
            notes: String(formData.get("notes") || "").trim(),
            jobApplicationId: detail.application.id
        }, "Scheduled event created");
        event.currentTarget.reset();
    }

    if (detailLoading && detail.loadedId !== applicationId) {
        return <LoadingPanel title="Opening application" copy="Loading status, timeline, matching, activity, and linked assets." />;
    }

    if (!detail.application) {
        return (
            <Panel eyebrow="Focused record" title="Application workspace">
                <EmptyState copy="Choose an application from the queue to open its dedicated workspace." />
            </Panel>
        );
    }

    return (
        <div className="detail-page-stack">
            <div className="detail-header-row">
                <Link className="back-link" to="/applications">Back to applications</Link>
                <div className="detail-header-title">
                    <p className="eyebrow">Focused record</p>
                    <h2>{detail.application.company} | {detail.application.jobTitle}</h2>
                    <p>
                        {lookupSourceName(references.sources, detail.application.sourceId)}
                        {detail.application.locationCity ? ` | ${detail.application.locationCity}, ${detail.application.locationCountry || ""}` : ""}
                    </p>
                </div>
                <div className="detail-header-actions">
                    <label className="inline-control">
                        <span>Status</span>
                        <select onChange={(event) => setStatus(event.target.value)} value={status}>
                            {JOB_STATUSES.map((item) => (
                                <option key={item} value={item}>{humanize(item)}</option>
                            ))}
                        </select>
                    </label>
                    <button className="secondary-button" onClick={handleStatusSave} type="button">Save status</button>
                    <button className="danger-button" onClick={handleDelete} type="button">Delete</button>
                </div>
            </div>

            <div className="tab-row">
                <button
                    className={activeTab === "summary" ? "tab-button is-active" : "tab-button"}
                    onClick={() => setActiveTab("summary")}
                    type="button"
                >
                    Summary
                </button>
                <button
                    className={activeTab === "activity" ? "tab-button is-active" : "tab-button"}
                    onClick={() => setActiveTab("activity")}
                    type="button"
                >
                    Activity
                </button>
                <button
                    className={activeTab === "assets" ? "tab-button is-active" : "tab-button"}
                    onClick={() => setActiveTab("assets")}
                    type="button"
                >
                    Assets
                </button>
            </div>

            {activeTab === "summary" ? (
                <div className="dashboard-grid">
                    <Panel eyebrow="Snapshot" title="Application summary">
                        <div className="snapshot-grid">
                            <SnapshotCard label="Status" value={detail.application.status || "DRAFT"} />
                            <SnapshotCard label="Source" value={lookupSourceName(references.sources, detail.application.sourceId)} />
                            <SnapshotCard label="Location" value={detail.application.locationCity ? `${detail.application.locationCity}, ${detail.application.locationCountry || ""}` : "Not set"} />
                            <SnapshotCard label="Applied on" value={detail.application.appliedOn ? formatDateTime(detail.application.appliedOn) : "Not set"} />
                            <SnapshotCard label="Deadline" value={detail.application.deadline ? formatDateTime(detail.application.deadline) : "Not set"} />
                            <SnapshotCard label="Resume" value={lookupResumeTitle(references.resumes, detail.application.resumeId)} />
                            <SnapshotCard label="Cover letter" value={lookupCoverLetterTitle(references.coverLetters, detail.application.coverLetterId)} />
                            <SnapshotCard label="Notes" value={detail.application.notes ? shortText(detail.application.notes, 80) : "No notes"} />
                        </div>

                        <div className="content-block">
                            <h3>Description</h3>
                            <p>{detail.application.jobDescription || "No description"}</p>
                        </div>
                    </Panel>

                    <Panel eyebrow="Matching" title="Fit guidance">
                        {detail.match ? (
                            <div className="stack-block">
                                <article className="list-card">
                                    <div className="list-card-header">
                                        <div>
                                            <h4>Overall fit score</h4>
                                            <p>{detail.match.company} | {detail.match.jobTitle}</p>
                                        </div>
                                        <Tag>{Math.round(detail.match.overallScore * 100)}%</Tag>
                                    </div>

                                    <div className="tag-row">
                                        {(detail.match.matchedSkills || []).map((skill) => (
                                            <Tag key={skill}>{skill}</Tag>
                                        ))}
                                    </div>

                                    <p className="support-copy">
                                        {(detail.match.recommendations || []).length
                                            ? detail.match.recommendations.join(" | ")
                                            : "No recommendation text yet."}
                                    </p>

                                    {detail.match.recommendedResume ? (
                                        <div className="nested-note">
                                            <h4>Recommended resume</h4>
                                            <p>
                                                {detail.match.recommendedResume.title} | {Math.round(detail.match.recommendedResume.score * 100)}%
                                            </p>
                                            <p>{detail.match.recommendedResume.rationale}</p>
                                        </div>
                                    ) : null}

                                    {detail.match.recommendedCoverLetter ? (
                                        <div className="nested-note">
                                            <h4>Recommended cover letter</h4>
                                            <p>
                                                {detail.match.recommendedCoverLetter.title} | {Math.round(detail.match.recommendedCoverLetter.score * 100)}%
                                            </p>
                                            <p>{detail.match.recommendedCoverLetter.rationale}</p>
                                        </div>
                                    ) : null}

                                    {detail.match.keywordGaps?.length ? (
                                        <p className="support-copy"><strong>Keyword gaps:</strong> {detail.match.keywordGaps.join(", ")}</p>
                                    ) : null}
                                </article>
                            </div>
                        ) : (
                            <EmptyState copy="Matching guidance appears when an application is selected." />
                        )}
                    </Panel>

                    <Panel eyebrow="Timeline" title="Recorded events">
                        {detail.timeline.length ? (
                            <div className="list-stack">
                                {detail.timeline.map((item) => (
                                    <article className="list-card" key={item.id || `${item.eventType}-${item.eventTime}`}>
                                        <div className="list-card-header">
                                            <div>
                                                <h4>{item.eventType}</h4>
                                                <p>{item.description || "No description"}</p>
                                            </div>
                                            <Tag tone="neutral">{formatDateTime(item.eventTime)}</Tag>
                                        </div>
                                    </article>
                                ))}
                            </div>
                        ) : (
                            <EmptyState copy="Timeline entries will appear here." />
                        )}
                    </Panel>
                </div>
            ) : null}

            {activeTab === "activity" ? (
                <div className="dashboard-grid">
                    <Panel eyebrow="Communications" title="Log contact">
                        <form className="stack-form compact-form" onSubmit={handleCommunicationCreate}>
                            <div className="field-grid">
                                <label>
                                    <span>Method</span>
                                    <select defaultValue={COMMUNICATION_METHODS[0]} name="type">
                                        {COMMUNICATION_METHODS.map((item) => (
                                            <option key={item} value={item}>{humanize(item)}</option>
                                        ))}
                                    </select>
                                </label>
                                <label>
                                    <span>Direction</span>
                                    <select defaultValue={COMMUNICATION_DIRECTIONS[0]} name="direction">
                                        {COMMUNICATION_DIRECTIONS.map((item) => (
                                            <option key={item} value={item}>{humanize(item)}</option>
                                        ))}
                                    </select>
                                </label>
                            </div>
                            <label>
                                <span>Timestamp</span>
                                <input name="timestamp" required type="datetime-local" />
                            </label>
                            <label>
                                <span>Message</span>
                                <textarea name="message" placeholder="What happened, who said what, and what matters next." required></textarea>
                            </label>
                            <button className="ghost-button" type="submit">Log communication</button>
                        </form>

                        <div className="section-divider"></div>

                        {detail.communications.length ? (
                            <div className="list-stack">
                                {detail.communications.map((item) => (
                                    <ResourceDeleteCard
                                        description={`${item.type} ${item.direction} | ${formatDateTime(item.timestamp)}`}
                                        id={item.id}
                                        key={item.id}
                                        kind="communication"
                                        onDelete={deleteWorkspaceResource}
                                        title={item.message}
                                    />
                                ))}
                            </div>
                        ) : (
                            <EmptyState copy="No communications yet." />
                        )}
                    </Panel>

                    <Panel eyebrow="Follow-up" title="Reminders">
                        <form className="stack-form compact-form" onSubmit={handleFollowUpCreate}>
                            <label>
                                <span>Remind on</span>
                                <input name="remindOn" required type="datetime-local" />
                            </label>
                            <label>
                                <span>Note</span>
                                <textarea name="note" placeholder="What you need to do when the reminder arrives." required></textarea>
                            </label>
                            <button className="ghost-button" type="submit">Create reminder</button>
                        </form>

                        <div className="section-divider"></div>

                        {detail.followUps.length ? (
                            <div className="list-stack">
                                {detail.followUps.map((item) => (
                                    <ResourceDeleteCard
                                        description={formatDateTime(item.remindOn)}
                                        id={item.id}
                                        key={item.id}
                                        kind="follow-up"
                                        onDelete={deleteWorkspaceResource}
                                        title={item.note}
                                    />
                                ))}
                            </div>
                        ) : (
                            <EmptyState copy="No reminders yet." />
                        )}
                    </Panel>

                    <Panel eyebrow="Scheduled" title="Upcoming steps">
                        <form className="stack-form compact-form" onSubmit={handleScheduledCreate}>
                            <label>
                                <span>Type</span>
                                <select defaultValue={SCHEDULED_TYPES[0]} name="type">
                                    {SCHEDULED_TYPES.map((item) => (
                                        <option key={item} value={item}>{humanize(item)}</option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                <span>Scheduled for</span>
                                <input name="scheduledFor" required type="datetime-local" />
                            </label>
                            <label>
                                <span>Notes</span>
                                <textarea name="notes" placeholder="Interview round, preparation notes, or attendance details." required></textarea>
                            </label>
                            <button className="ghost-button" type="submit">Schedule event</button>
                        </form>

                        <div className="section-divider"></div>

                        {detail.scheduled.length ? (
                            <div className="list-stack">
                                {detail.scheduled.map((item) => (
                                    <ResourceDeleteCard
                                        description={`${item.type} | ${formatDateTime(item.scheduledFor)}`}
                                        id={item.id}
                                        key={item.id}
                                        kind="scheduled"
                                        onDelete={deleteWorkspaceResource}
                                        title={item.notes}
                                    />
                                ))}
                            </div>
                        ) : (
                            <EmptyState copy="No scheduled events yet." />
                        )}
                    </Panel>
                </div>
            ) : null}

            {activeTab === "assets" ? (
                <div className="dashboard-grid">
                    <Panel eyebrow="Linked docs" title="Current application assets">
                        <div className="snapshot-grid">
                            <SnapshotCard label="Resume" value={lookupResumeTitle(references.resumes, detail.application.resumeId)} />
                            <SnapshotCard label="Cover letter" value={lookupCoverLetterTitle(references.coverLetters, detail.application.coverLetterId)} />
                            <SnapshotCard label="Source" value={lookupSourceName(references.sources, detail.application.sourceId)} />
                            <SnapshotCard label="Deadline" value={detail.application.deadline ? formatDateTime(detail.application.deadline) : "Not set"} />
                        </div>
                    </Panel>

                    <Panel eyebrow="Skills" title="Track relevant skills">
                        <form className="stack-form compact-form" onSubmit={handleSkillCreate}>
                            <label>
                                <span>Skill</span>
                                <input name="skillName" placeholder="Spring Boot, SQL tuning, interview storytelling" required />
                            </label>
                            <button className="ghost-button" type="submit">Add skill</button>
                        </form>

                        <div className="section-divider"></div>

                        {detail.skills.length ? (
                            <div className="pill-list">
                                {detail.skills.map((item) => (
                                    <span className="pill" key={item.id}>
                                        {item.skillName}
                                        <button
                                            className="pill-delete"
                                            onClick={() => deleteWorkspaceResource("skill", item.id)}
                                            type="button"
                                        >
                                            Delete
                                        </button>
                                    </span>
                                ))}
                            </div>
                        ) : (
                            <EmptyState copy="No tracked skills yet." />
                        )}
                    </Panel>

                    <Panel eyebrow="Attachments" title="Store related files">
                        <form className="stack-form compact-form" onSubmit={handleAttachmentCreate}>
                            <label>
                                <span>Type</span>
                                <select defaultValue={ATTACHMENT_TYPES[0]} name="type">
                                    {ATTACHMENT_TYPES.map((item) => (
                                        <option key={item} value={item}>{humanize(item)}</option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                <span>File path</span>
                                <input name="filePath" placeholder="/Users/you/Documents/role-notes.pdf" required />
                            </label>
                            <button className="ghost-button" type="submit">Add attachment</button>
                        </form>

                        <div className="section-divider"></div>

                        {detail.attachments.length ? (
                            <div className="list-stack">
                                {detail.attachments.map((item) => (
                                    <ResourceDeleteCard
                                        description={item.filePath}
                                        id={item.id}
                                        key={item.id}
                                        kind="attachment"
                                        onDelete={deleteWorkspaceResource}
                                        title={humanize(item.type)}
                                    />
                                ))}
                            </div>
                        ) : (
                            <EmptyState copy="No attachments yet." />
                        )}
                    </Panel>
                </div>
            ) : null}
        </div>
    );
}

function LibraryPage() {
    const { references, createResume, createCoverLetter, deleteReference } = useWorkspace();

    async function handleResumeCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const title = String(formData.get("title") || "").trim();
        const filePath = String(formData.get("filePath") || "").trim();
        if (!title || !filePath) {
            return;
        }
        await createResume({ title, filePath });
        event.currentTarget.reset();
    }

    async function handleCoverLetterCreate(event) {
        event.preventDefault();
        const formData = new FormData(event.currentTarget);
        const title = String(formData.get("title") || "").trim();
        const filePath = String(formData.get("filePath") || "").trim();
        const content = String(formData.get("content") || "").trim();
        if (!title || !filePath) {
            return;
        }
        await createCoverLetter({ title, filePath, content });
        event.currentTarget.reset();
    }

    return (
        <div className="dashboard-grid">
            <Panel eyebrow="Documents" title="Resumes">
                <form className="stack-form compact-form" onSubmit={handleResumeCreate}>
                    <label>
                        <span>Title</span>
                        <input name="title" placeholder="General backend resume" required />
                    </label>
                    <label>
                        <span>File path</span>
                        <input name="filePath" placeholder="/Users/you/Documents/resume.pdf" required />
                    </label>
                    <button className="primary-button" type="submit">Save resume</button>
                </form>

                <div className="section-divider"></div>

                {references.resumes.length ? (
                    <div className="list-stack">
                        {references.resumes.map((item) => (
                            <ResourceDeleteCard
                                description={item.filePath}
                                id={item.id}
                                key={item.id}
                                kind="resume"
                                onDelete={deleteReference}
                                title={item.title}
                            />
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No resumes saved yet." />
                )}
            </Panel>

            <Panel eyebrow="Documents" title="Cover letters">
                <form className="stack-form compact-form" onSubmit={handleCoverLetterCreate}>
                    <label>
                        <span>Title</span>
                        <input name="title" placeholder="Platform engineering cover letter" required />
                    </label>
                    <label>
                        <span>File path</span>
                        <input name="filePath" placeholder="/Users/you/Documents/cover-letter.docx" required />
                    </label>
                    <label>
                        <span>Content</span>
                        <textarea name="content" placeholder="Optional text body for quick reference."></textarea>
                    </label>
                    <button className="primary-button" type="submit">Save cover letter</button>
                </form>

                <div className="section-divider"></div>

                {references.coverLetters.length ? (
                    <div className="list-stack">
                        {references.coverLetters.map((item) => (
                            <ResourceDeleteCard
                                description={item.content ? shortText(item.content, 180) : item.filePath}
                                id={item.id}
                                key={item.id}
                                kind="cover-letter"
                                onDelete={deleteReference}
                                title={item.title}
                            />
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No cover letters saved yet." />
                )}
            </Panel>

            <Panel eyebrow="References" title="Sources">
                {references.sources.length ? (
                    <div className="list-stack">
                        {references.sources.map((item) => (
                            <ResourceDeleteCard
                                description="Reusable source reference"
                                id={item.id}
                                key={item.id}
                                kind="source"
                                onDelete={deleteReference}
                                title={item.name}
                            />
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No sources saved yet." />
                )}
            </Panel>

            <Panel eyebrow="References" title="Locations">
                {references.locations.length ? (
                    <div className="list-stack">
                        {references.locations.map((item) => (
                            <ResourceDeleteCard
                                description={item.country}
                                id={item.id}
                                key={item.id}
                                kind="location"
                                onDelete={deleteReference}
                                title={item.city}
                            />
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No locations saved yet." />
                )}
            </Panel>
        </div>
    );
}

function CalendarPage() {
    const { calendarEvents, calendarFilters, filterCalendar, resetCalendarFilter, downloadFile } = useWorkspace();
    const [from, setFrom] = useState(calendarFilters.from);
    const [to, setTo] = useState(calendarFilters.to);

    useEffect(() => {
        setFrom(calendarFilters.from);
        setTo(calendarFilters.to);
    }, [calendarFilters.from, calendarFilters.to]);

    async function handleSubmit(event) {
        event.preventDefault();
        await filterCalendar(from, to);
    }

    return (
        <div className="dashboard-grid">
            <Panel eyebrow="Calendar" title="Filter event feed">
                <form className="stack-form compact-form" onSubmit={handleSubmit}>
                    <div className="field-grid">
                        <label>
                            <span>From</span>
                            <input onChange={(event) => setFrom(event.target.value)} type="date" value={from} />
                        </label>
                        <label>
                            <span>To</span>
                            <input onChange={(event) => setTo(event.target.value)} type="date" value={to} />
                        </label>
                    </div>

                    <div className="button-row">
                        <button className="primary-button" type="submit">Apply range</button>
                        <button className="secondary-button" onClick={resetCalendarFilter} type="button">Reset</button>
                    </div>
                </form>
            </Panel>

            <Panel eyebrow="Exports" title="Download calendar and workspace data">
                <div className="button-grid">
                    <button className="secondary-button" onClick={() => downloadFile(buildCalendarPath("/api/calendar/events.ics", from, to))} type="button">
                        Download ICS
                    </button>
                    <button className="secondary-button" onClick={() => downloadFile("/api/exports/workspace?format=json")} type="button">
                        Export JSON
                    </button>
                    <button className="secondary-button" onClick={() => downloadFile("/api/exports/workspace?format=csv")} type="button">
                        Export CSV
                    </button>
                    <button className="secondary-button" onClick={() => downloadFile("/api/exports/workspace?format=pdf")} type="button">
                        Export PDF
                    </button>
                </div>
            </Panel>

            <Panel eyebrow="Events" title="Calendar stream">
                {calendarEvents.length ? (
                    <div className="list-stack">
                        {calendarEvents.map((item) => (
                            <EventCard item={item} key={`${item.title}-${item.startsAt}`} />
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No events found for the selected range." />
                )}
            </Panel>
        </div>
    );
}

function AccountPage() {
    const { user, applications, updateProfile, deactivateAccount } = useWorkspace();
    const [email, setEmail] = useState(user.email || "");
    const [password, setPassword] = useState("");

    useEffect(() => {
        setEmail(user.email || "");
        setPassword("");
    }, [user.email]);

    async function handleSubmit(event) {
        event.preventDefault();
        await updateProfile({
            email: email.trim(),
            password: password.trim()
        });
        setPassword("");
    }

    async function handleDeactivate() {
        if (!window.confirm("Deactivate your own account? You will be signed out immediately.")) {
            return;
        }
        await deactivateAccount();
    }

    return (
        <div className="dashboard-grid">
            <Panel eyebrow="Account" title="Identity snapshot">
                <div className="snapshot-grid">
                    <SnapshotCard label="Email" value={user.email || "-"} />
                    <SnapshotCard label="Roles" value={Array.isArray(user.roles) ? user.roles.join(", ") : "BASIC"} />
                    <SnapshotCard label="Account enabled" value={user.enabled ? "Yes" : "No"} />
                    <SnapshotCard label="Tracked applications" value={String(applications.totalElements || 0)} />
                </div>
            </Panel>

            <Panel eyebrow="Profile" title="Update account details">
                <form className="stack-form" onSubmit={handleSubmit}>
                    <label>
                        <span>Email</span>
                        <input onChange={(event) => setEmail(event.target.value)} type="email" value={email} />
                    </label>
                    <label>
                        <span>New password</span>
                        <input onChange={(event) => setPassword(event.target.value)} placeholder="Leave blank to keep current password" type="password" value={password} />
                    </label>
                    <button className="primary-button" type="submit">Save profile</button>
                </form>
            </Panel>

            <Panel eyebrow="Danger zone" title="Deactivate account">
                <p className="panel-copy">
                    This disables your account and signs you out immediately.
                </p>
                <button className="danger-button" onClick={handleDeactivate} type="button">
                    Deactivate account
                </button>
            </Panel>
        </div>
    );
}

function AdminPage() {
    const { adminUsers, auditLogs, loadAdminData, toggleAdminUser } = useWorkspace();

    useEffect(() => {
        if (!adminUsers.length && !auditLogs.length) {
            void loadAdminData();
        }
    }, [adminUsers.length, auditLogs.length]);

    return (
        <div className="dashboard-grid">
            <Panel eyebrow="Users" title="Account access">
                {adminUsers.length ? (
                    <div className="list-stack">
                        {adminUsers.map((item) => (
                            <article className="list-card" key={item.id}>
                                <div className="list-card-header">
                                    <div>
                                        <h4>{item.email}</h4>
                                        <p>{(item.roles || []).join(", ")} | {item.enabled ? "Enabled" : "Disabled"}</p>
                                    </div>
                                    <button
                                        className="secondary-button"
                                        onClick={() => toggleAdminUser(item.id, !item.enabled)}
                                        type="button"
                                    >
                                        {item.enabled ? "Disable" : "Enable"}
                                    </button>
                                </div>
                            </article>
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No users found." />
                )}
            </Panel>

            <Panel eyebrow="Audit log" title="Recorded admin activity">
                {auditLogs.length ? (
                    <div className="list-stack">
                        {auditLogs.map((item) => (
                            <article className="list-card" key={item.id || `${item.action}-${item.createdAt}`}>
                                <div className="list-card-header">
                                    <div>
                                        <h4>{item.action || "UNKNOWN"}</h4>
                                        <p>{item.description || "No description"}</p>
                                    </div>
                                    <Tag tone="neutral">{formatDateTime(item.createdAt)}</Tag>
                                </div>
                                <p className="support-copy">User ID: {item.userId || "unknown"}</p>
                            </article>
                        ))}
                    </div>
                ) : (
                    <EmptyState copy="No audit log entries found." />
                )}
            </Panel>
        </div>
    );
}

function Panel({ eyebrow, title, actions, children }) {
    return (
        <section className="panel surface-panel">
            <div className="section-heading">
                <div>
                    <p className="eyebrow">{eyebrow}</p>
                    <h2>{title}</h2>
                </div>
                {actions}
            </div>
            {children}
        </section>
    );
}

function MetricCard({ label, value }) {
    return (
        <article className="metric-card panel">
            <strong>{value}</strong>
            <span>{label}</span>
        </article>
    );
}

function SnapshotCard({ label, value }) {
    return (
        <article className="snapshot-card">
            <span>{label}</span>
            <strong>{value}</strong>
        </article>
    );
}

function EventCard({ item }) {
    return (
        <article className="event-card">
            <div className="list-card-header">
                <div>
                    <h4>{item.title}</h4>
                    <p>
                        {item.company || "General"}
                        {item.jobTitle ? ` | ${item.jobTitle}` : ""}
                    </p>
                </div>
                <Tag>{item.type}</Tag>
            </div>
            <p className="support-copy">
                {formatDateTime(item.startsAt)}
                {item.endsAt ? ` to ${formatDateTime(item.endsAt)}` : ""}
            </p>
            {item.description ? <p className="support-copy">{item.description}</p> : null}
        </article>
    );
}

function ResourceDeleteCard({ title, description, kind, id, onDelete }) {
    return (
        <article className="list-card">
            <div className="list-card-header">
                <div>
                    <h4>{title}</h4>
                    <p>{description}</p>
                </div>
                <button className="ghost-button" onClick={() => onDelete(kind, id)} type="button">
                    Delete
                </button>
            </div>
        </article>
    );
}

function EmptyState({ copy }) {
    return <div className="empty-state">{copy}</div>;
}

function LoadingPanel({ title, copy }) {
    return (
        <section className="panel loading-panel">
            <div className="spinner"></div>
            <div>
                <h2>{title}</h2>
                <p>{copy}</p>
            </div>
        </section>
    );
}

function Tag({ children, tone = "default" }) {
    return <span className={`tag tag-${tone}`}>{children}</span>;
}

function SidebarLink({ label, to, end = false }) {
    return (
        <NavLink className={({ isActive }) => isActive ? "nav-link is-active" : "nav-link"} end={end} to={to}>
            {label}
        </NavLink>
    );
}

function ToastRack({ toasts }) {
    return (
        <div className="toast-rack" aria-atomic="true" aria-live="polite">
            {toasts.map((toast) => (
                <article className={`toast toast-${toast.type}`} key={toast.id}>
                    <p className="toast-title">{toast.title}</p>
                    <p className="toast-copy">{toast.message}</p>
                </article>
            ))}
        </div>
    );
}

function useWorkspace() {
    const context = useContext(WorkspaceContext);
    if (!context) {
        throw new Error("Workspace context is unavailable.");
    }
    return context;
}

function resolveViewMeta(pathname) {
    if (pathname.startsWith("/applications/")) {
        return {
            eyebrow: "Focused record",
            title: "Application workspace"
        };
    }

    if (pathname.startsWith("/applications")) {
        return {
            eyebrow: "Execution lane",
            title: "Applications"
        };
    }

    if (pathname.startsWith("/library")) {
        return {
            eyebrow: "Reusable assets",
            title: "Library"
        };
    }

    if (pathname.startsWith("/calendar")) {
        return {
            eyebrow: "Export and sync",
            title: "Calendar"
        };
    }

    if (pathname.startsWith("/account")) {
        return {
            eyebrow: "Identity and access",
            title: "Account"
        };
    }

    if (pathname.startsWith("/admin")) {
        return {
            eyebrow: "Admin surface",
            title: "Admin"
        };
    }

    return {
        eyebrow: "Workspace pulse",
        title: "Overview"
    };
}

function createEmptyDetail() {
    return {
        loadedId: null,
        application: null,
        timeline: [],
        attachments: [],
        communications: [],
        skills: [],
        followUps: [],
        scheduled: [],
        match: null
    };
}

async function fetchOverviewBundle(token) {
    const [overview, analytics, recommendations, overviewEvents] = await Promise.all([
        apiJson("/api/insights/dashboard", { token }),
        apiJson("/api/insights/analytics", { token }),
        apiJson("/api/insights/recommendations", { token }),
        apiJson("/api/calendar/events", { token })
    ]);

    return {
        overview,
        analytics,
        recommendations: recommendations?.items ?? [],
        overviewEvents
    };
}

async function fetchReferencesBundle(token) {
    const [sources, locations, resumes, coverLetters] = await Promise.all([
        apiJson("/api/job-sources", { token }),
        apiJson("/api/locations", { token }),
        apiJson("/api/resumes", { token }),
        apiJson("/api/cover-letters", { token })
    ]);

    return {
        sources,
        locations,
        resumes,
        coverLetters
    };
}

async function fetchApplicationsPage(token, page, size, sort) {
    const params = new URLSearchParams({
        page: String(page),
        size: String(size),
        sort
    });

    return apiJson(`/api/job-applications?${params.toString()}`, { token });
}

async function fetchApplicationWorkspace(token, applicationId) {
    const [application, attachments, communications, followUps, skills, timeline, match, scheduled] = await Promise.all([
        apiJson(`/api/job-applications/${applicationId}`, { token }),
        apiJson(`/api/attachments/job/${applicationId}`, { token }),
        apiJson(`/api/communications/job/${applicationId}`, { token }),
        apiJson(`/api/follow-ups/job/${applicationId}`, { token }),
        apiJson(`/api/skills/job/${applicationId}`, { token }),
        apiJson(`/api/timelines/job/${applicationId}`, { token }),
        apiJson(`/api/matching/applications/${applicationId}`, { token }),
        apiJson("/api/scheduled-communications", { token })
    ]);

    return {
        loadedId: applicationId,
        application,
        attachments,
        communications,
        followUps,
        skills,
        timeline,
        match,
        scheduled: scheduled.filter((item) => item.jobApplicationId === applicationId)
    };
}

async function fetchAdminBundle(token) {
    const [users, auditLogs] = await Promise.all([
        apiJson("/api/users", { token }),
        apiJson("/api/audit-log", { token })
    ]);

    return {
        users,
        auditLogs
    };
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
    if (options.auth !== false && options.token) {
        headers.Authorization = `Bearer ${options.token}`;
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
    } catch {
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

function pruneEmpty(payload) {
    return Object.fromEntries(
        Object.entries(payload).filter(([, value]) => value !== "" && value !== null && value !== undefined)
    );
}

function buildCalendarPath(basePath, from = "", to = "") {
    const params = new URLSearchParams();
    if (from) {
        params.set("from", from);
    }
    if (to) {
        params.set("to", to);
    }
    return params.toString() ? `${basePath}?${params.toString()}` : basePath;
}

async function downloadProtectedFile(path, token) {
    const response = await fetch(path, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const parsed = await parseResponseBody(response);
        throw new Error(resolveErrorMessage(parsed, response.statusText));
    }

    const blob = await response.blob();
    const fileName = fileNameFromResponse(response) || "download";
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = fileName;
    document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
    window.URL.revokeObjectURL(url);
}

function fileNameFromResponse(response) {
    const contentDisposition = response.headers.get("content-disposition");
    if (!contentDisposition) {
        return null;
    }
    const match = contentDisposition.match(/filename="([^"]+)"/);
    return match ? match[1] : null;
}

function enqueueToast(setToasts, title, message, type = "info") {
    const id = window.crypto?.randomUUID?.() || `${Date.now()}-${Math.random()}`;
    setToasts((current) => [...current, { id, title, message, type }]);
    window.setTimeout(() => {
        setToasts((current) => current.filter((toast) => toast.id !== id));
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

function lookupSourceName(sources, id) {
    return sources.find((item) => item.id === id)?.name || "Not linked";
}

function lookupResumeTitle(resumes, id) {
    return resumes.find((item) => item.id === id)?.title || "Not linked";
}

function lookupCoverLetterTitle(coverLetters, id) {
    return coverLetters.find((item) => item.id === id)?.title || "Not linked";
}

function toNullableValue(value) {
    const nextValue = String(value || "").trim();
    return nextValue || null;
}
