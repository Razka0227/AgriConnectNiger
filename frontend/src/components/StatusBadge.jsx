const STYLES = {
  PENDING: 'st-pending',
  CONFIRMED: 'st-confirmed',
  PACKED: 'st-packed',
  IN_TRANSIT: 'st-transit',
  DELIVERED: 'st-delivered',
  CANCELLED: 'st-cancelled',
  ACTIVE: 'st-active',
  RESERVED: 'st-reserved',
  CLOSED: 'st-closed'
};

export default function StatusBadge({ status }) {
  return <span className={`status ${STYLES[status] || ''}`}>{status}</span>;
}
