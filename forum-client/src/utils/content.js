// Excerpts are rendered as text so Markdown and HTML never become active markup.
export function toPlainText(value) {
  const html = (value || '').replace(/<\/(?:p|div|h[1-6]|li|pre)>|<br\s*\/?>/gi, ' ')
  const doc = new DOMParser().parseFromString(html, 'text/html')
  doc.querySelectorAll('script, style').forEach(node => node.remove())
  return (doc.body.textContent || '')
    .replace(/&nbsp;/g, ' ')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/\s+/g, ' ')
    .trim()
}

export function toExcerpt(value) {
  return toPlainText(value)
    .replace(/\x60{3}[\s\S]*?\x60{3}/g, ' ')
    .replace(/!\[.*?\]\(.*?\)/g, '')
    .replace(/\[([^\]]+)\]\([^)]*\)/g, '$1')
    .replace(/#{1,6}\s*/g, '')
    .replace(/[*_>\x60]/g, '')
    .replace(/\s+/g, ' ')
    .trim() || '打开讨论，查看完整内容。'
}
