import { useState, useEffect, useRef } from "react";
import { StatusBadge, Pagination } from "../../utils/helpers";

// ── Quick Stat Card ──────────────────────────────────────────────────────────
const QuickStat = ({ label, value, color, icon }) => (
  <div className="card border-0 shadow-sm h-100" style={{ borderRadius: '12px', borderLeft: `4px solid ${color}` }}>
    <div className="card-body p-3 d-flex align-items-center justify-content-between">
      <div>
        <p className="text-muted small mb-0 fw-medium text-uppercase" style={{ fontSize: '10px', letterSpacing: '0.5px' }}>{label}</p>
        <h5 className="fw-bold mb-0" style={{ color: '#2d3436' }}>{value}</h5>
      </div>
      <span style={{ fontSize: '20px' }}>{icon}</span>
    </div>
  </div>
);

// ── Modal Chi tiết ──────────────────────────────────────────────────────────
function EventDetailModal({ event, onClose }) {
  if (!event) return null;
  return (
    <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)', zIndex: 1100 }}>
      <div className="modal-dialog modal-lg modal-dialog-centered">
        <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '20px', overflow: 'hidden' }}>
          <div className="row g-0">
            <div className="col-md-5 position-relative">
              {event.imageUrls?.[0] ? (
                <img src={event.imageUrls[0]} alt={event.name} className="h-100 w-100" style={{ objectFit: 'cover', minHeight: '300px' }} />
              ) : (
                <div className="bg-light h-100 w-100 d-flex align-items-center justify-content-center text-muted">No Image</div>
              )}
              <div className="position-absolute top-3 start-3">
                <StatusBadge status={event.status} />
              </div>
            </div>
            <div className="col-md-7 p-4 bg-white">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <h4 className="fw-bold text-dark">{event.name}</h4>
                <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
              </div>
              
              <div className="row g-3 mb-4">
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">📅 Thời gian</small>
                  <span className="small">{new Date(event.startTime).toLocaleString()}</span>
                </div>
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">📍 Địa điểm</small>
                  <span className="small text-truncate d-block">{event.location}</span>
                </div>
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">📁 Danh mục</small>
                  <span className="badge bg-light text-dark border">{event.categoryName}</span>
                </div>
                <div className="col-6">
                  <small className="text-muted d-block uppercase small fw-bold">🏢 Ban tổ chức</small>
                  <span className="small fw-medium">{event.organizerName}</span>
                </div>
              </div>

              <h6 className="fw-bold mb-3 small text-uppercase" style={{ letterSpacing: '1px' }}>🎫 Quản lý vé</h6>
              <div className="p-3 bg-light rounded-3">
                {event.ticketTypes?.length > 0 ? event.ticketTypes.map(tt => {
                  const sold = tt.totalQuantity - tt.remainingQuantity;
                  const percent = tt.totalQuantity > 0 ? Math.round((sold/tt.totalQuantity)*100) : 0;
                  return (
                    <div key={tt.id} className="mb-3 last-child-mb-0">
                      <div className="d-flex justify-content-between small mb-1">
                        <span className="fw-bold">{tt.name}</span>
                        <span className="text-muted">{sold}/{tt.totalQuantity} vé</span>
                      </div>
                      <div className="progress" style={{ height: '6px' }}>
                        <div className="progress-bar bg-primary rounded-pill" style={{ width: `${percent}%` }}></div>
                      </div>
                    </div>
                  )
                }) : <div className="text-muted small">Chưa thiết lập loại vé</div>}
              </div>

              <div className="mt-4 pt-3 border-top">
                <button className="btn btn-primary w-100 rounded-pill fw-bold" onClick={onClose}>Đóng cửa sổ</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// ── Card sự kiện ────────────────────────────────────────────────────────────
function EventCard({ event, onDetail, onChangeStatus, changingId }) {
  let totalSold = 0, totalQty = 0;
  event.ticketTypes?.forEach(tt => { totalSold += (tt.totalQuantity - tt.remainingQuantity); totalQty += tt.totalQuantity; });
  const percent = totalQty > 0 ? Math.round((totalSold/totalQty)*100) : 0;

  return (
    <div className="card h-100 border-0 shadow-sm transition-all hover-translate" style={{ borderRadius: '16px', overflow: 'hidden' }}>
      <div className="position-relative" style={{ height: '160px' }}>
        {event.imageUrls?.[0] ? (
          <img src={event.imageUrls[0]} alt={event.name} className="w-100 h-100" style={{ objectFit: 'cover' }} />
        ) : (
          <div className="w-100 h-100 bg-secondary-subtle d-flex align-items-center justify-content-center text-secondary">No Image</div>
        )}
        <div className="position-absolute top-0 end-0 p-2">
          <StatusBadge status={event.status} />
        </div>
        <div className="position-absolute bottom-0 start-0 w-100 p-2 bg-gradient-dark">
           <span className="badge bg-white text-dark small">{event.categoryName}</span>
        </div>
      </div>

      <div className="card-body p-3 d-flex flex-column">
        <h6 className="fw-bold text-dark text-truncate mb-1" title={event.name}>{event.name}</h6>
        <p className="text-muted small mb-3 text-truncate" style={{ fontSize: '11px' }}>{event.organizerName}</p>
        
        <div className="mt-auto">
          <div className="d-flex justify-content-between small mb-1">
            <span className="text-muted">Tỷ lệ bán</span>
            <span className="fw-bold">{percent}%</span>
          </div>
          <div className="progress mb-3" style={{ height: '4px' }}>
            <div className="progress-bar bg-success" style={{ width: `${percent}%` }}></div>
          </div>

          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-outline-primary flex-grow-1 border-2 fw-bold" style={{ fontSize: '11px', borderRadius: '8px' }} onClick={() => onDetail(event)}>
              CHI TIẾT
            </button>
            {(event.status === 'DRAFT' || event.status === 'PENDING') && (
              <button 
                className="btn btn-sm btn-success border-0 shadow-sm"
                style={{ fontSize: '11px', borderRadius: '8px', padding: '0 12px' }}
                disabled={changingId === event.id}
                onClick={() => onChangeStatus(event, 'PUBLISHED')}
              >
                {changingId === event.id ? '...' : 'DUYỆT'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

// ── Trang chính ─────────────────────────────────────────────────────────────
const STATUSES = [
  { value: "",          label: "Tất cả trạng thái" },
  { value: "PUBLISHED", label: "Đã đăng"           },
  { value: "DRAFT",     label: "Nháp"               },
  { value: "CANCELLED", label: "Đã hủy"             },
  { value: "COMPLETED", label: "Hoàn thành"         },
];

export default function EventsPage({ api }) {
  const [events, setEvents]           = useState([]);
  const [page, setPage]               = useState(1);
  const [totalPages, setTotalPages]   = useState(1);
  const [loading, setLoading]         = useState(true);
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch]           = useState("");
  const [status, setStatus]           = useState("");
  const [refetch, setRefetch]         = useState(0);
  const [detail, setDetail]           = useState(null);
  const [changingId, setChangingId]   = useState(null);

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams({ page, size: 12 });
    if (search) params.set("search", search);
    if (status) params.set("status", status);

    api.get(`/events/admin/all?${params}`).then((res) => {
      setEvents(res.result?.content ?? []);
      setTotalPages(res.result?.totalPages ?? 1);
      setLoading(false);
    });
  }, [page, search, status, refetch, api]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(1);
    setSearch(searchInput);
  };

  const handleChangeStatus = async (event, newStatus) => {
    if (!window.confirm(`Xác nhận thay đổi trạng thái sự kiện "${event.name}"?`)) return;
    setChangingId(event.id);
    await api.patch(`/events/${event.id}/status?status=${newStatus}`);
    setChangingId(null);
    setRefetch(n => n + 1);
  };

  return (
    <div className="animate-fade-in">
      {detail && <EventDetailModal event={detail} onClose={() => setDetail(null)} />}

      <div className="mb-4">
        <h4 className="fw-bold mb-1 font-inter">📅 Quản lý sự kiện</h4>
        <p className="text-secondary small">Theo dõi, duyệt và quản lý các sự kiện trên hệ thống.</p>
      </div>

      {/* Quick Stats Row */}
      <div className="row g-3 mb-4">
        <div className="col-md-3"><QuickStat label="Sự kiện chờ duyệt" value={events.filter(e => e.status === 'PENDING' || e.status === 'DRAFT').length} color="#fdcb6e" icon="⏳" /></div>
        <div className="col-md-3"><QuickStat label="Đang hoạt động" value={events.filter(e => e.status === 'PUBLISHED').length} color="#00b894" icon="✅" /></div>
        <div className="col-md-3"><QuickStat label="Hoàn thành" value={events.filter(e => e.status === 'COMPLETED').length} color="#0984e3" icon="🏁" /></div>
        <div className="col-md-3"><QuickStat label="Tổng sự kiện" value={events.length} color="#6c5ce7" icon="📶" /></div>
      </div>

      <div className="card border-0 shadow-sm" style={{ borderRadius: '16px' }}>
        <div className="card-header bg-white p-4 border-0">
          <div className="row g-3 align-items-center">
            <div className="col-md-4">
              <div className="input-group input-group-sm border rounded-pill px-2 py-1" style={{ backgroundColor: '#f8f9fa' }}>
                <span className="input-group-text bg-transparent border-0 text-muted">🔍</span>
                <input
                  type="text"
                  className="form-control bg-transparent border-0 shadow-none"
                  placeholder="Tìm tên sự kiện..."
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && handleSearch(e)}
                />
              </div>
            </div>
            <div className="col-md-4">
              <select
                className="form-select form-select-sm border-0 bg-light rounded-pill px-3 shadow-none"
                value={status}
                onChange={(e) => { setStatus(e.target.value); setPage(1); }}
              >
                {STATUSES.map((s) => (
                  <option key={s.value} value={s.value}>{s.label}</option>
                ))}
              </select>
            </div>
            <div className="col-md-4 text-md-end">
              <button className="btn btn-sm btn-primary rounded-pill px-4 fw-bold shadow-sm" onClick={() => setRefetch(n => n +1)}>LÀM MỚI ↻</button>
            </div>
          </div>
        </div>

        <div className="card-body p-4 pt-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status"></div>
              <p className="mt-3 text-muted small fw-medium">Đang đồng bộ dữ liệu...</p>
            </div>
          ) : events.length === 0 ? (
            <div className="text-center py-5">
               <span style={{ fontSize: '48px' }}>📂</span>
               <p className="mt-3 text-muted">Không tìm thấy sự kiện nào khớp với bộ lọc.</p>
            </div>
          ) : (
            <div className="row g-4 transition-all">
              {events.map((ev) => (
                <div key={ev.id} className="col-12 col-md-6 col-lg-4 col-xl-3">
                  <EventCard
                    event={ev}
                    onDetail={setDetail}
                    onChangeStatus={handleChangeStatus}
                    changingId={changingId}
                  />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="p-4 border-top">
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <style>{`
        .bg-gradient-dark {
          background: linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 100%);
        }
        .hover-translate:hover {
          transform: translateY(-5px);
        }
        .last-child-mb-0:last-child {
          margin-bottom: 0 !important;
        }
        .font-inter { font-family: 'Inter', sans-serif; }
      `}</style>
    </div>
  );
}