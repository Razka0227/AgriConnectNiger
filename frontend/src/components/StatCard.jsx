export default function StatCard({ label, value, icon, sub }) {
  return (
    <div className="card stat-card">
      <div className="stat-icon">{icon}</div>
      <div>
        <div className="stat-value">{value}</div>
        <div className="stat-label">{label}</div>
        {sub ? <div className="stat-sub muted">{sub}</div> : null}
      </div>
    </div>
  );
}
