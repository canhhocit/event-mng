import { useState } from "react";
import { useApi } from "../hooks/useApi";
import Sidebar, { MENU } from "../components/admin/Sidebar";
import DashboardPage  from "../components/admin/DashboardPage";
import EventsPage     from "../components/admin/EventsPage";
import UsersPage      from "../components/admin/UsersPage";
import CategoriesPage from "../components/admin/CategoriesPage";
import StatisticsPage     from "../components/admin/StatisticsPage";

export default function AdminDashboard() {
  const api = useApi();
  const [active, setActive] = useState("dashboard");

  const PAGE = {
    dashboard:  <DashboardPage  api={api} />,
    events:     <EventsPage     api={api} />,
    users:      <UsersPage      api={api} />,
    categories: <CategoriesPage api={api} />,
    statis:     <StatisticsPage     api={api} />,
  };

  const activeMenu = MENU.find((m) => m.key === active);

  return (
    <div className="d-flex" style={{ minHeight: "100vh", backgroundColor: "#f8f9fa" }}>
      <Sidebar active={active} onSelect={setActive} />

      <div className="flex-grow-1" style={{ overflow: "hidden", display: "flex", flexDirection: "column" }}>
        {/* Top Navbar */}
        <header className="bg-white px-4 py-3 d-flex justify-content-between align-items-center shadow-sm border-bottom" style={{ minHeight: '64px' }}>
          <div className="d-flex align-items-center">
            <h5 className="mb-0 fw-bold me-2" style={{ color: '#2d3436' }}>
              {activeMenu?.icon} {activeMenu?.label}
            </h5>
          </div>
          <div className="d-flex align-items-center gap-3">
             <button className="btn btn-light btn-sm rounded-circle p-2" title="Notifications">🔔</button>
             <span className="badge bg-danger-subtle text-danger border border-danger-subtle px-3 py-2" style={{ borderRadius: '8px' }}>ADMIN</span>
          </div>
        </header>

        {/* Dynamic Page Content */}
        <main className="flex-grow-1 p-4" style={{ overflowY: "auto" }}>
          {PAGE[active] ?? <div className="text-center mt-5 text-muted">Đang phát triển...</div>}
        </main>
      </div>
    </div>
  );
}