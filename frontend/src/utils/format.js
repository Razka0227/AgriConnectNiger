export function formatFCFA(n) {
  if (n === null || n === undefined) return '—';
  return new Intl.NumberFormat('fr-FR').format(Math.round(n)) + ' FCFA';
}

export function formatNumber(n) {
  if (n === null || n === undefined) return '—';
  return new Intl.NumberFormat('fr-FR').format(n);
}

export function formatDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

export function formatDateTime(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return (
    d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' }) +
    ' ' +
    d.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
  );
}

export function timeAgo(iso) {
  if (!iso) return '—';
  const diff = (Date.now() - new Date(iso)) / 1000;
  if (diff < 60) return "à l'instant";
  if (diff < 3600) return `il y a ${Math.floor(diff / 60)} min`;
  if (diff < 86400) return `il y a ${Math.floor(diff / 3600)} h`;
  return `il y a ${Math.floor(diff / 86400)} j`;
}
