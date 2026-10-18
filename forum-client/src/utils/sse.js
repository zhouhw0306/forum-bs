// Each SSE event can contain multiple data lines and span several network chunks.
export function createSseTextParser(onData) {
  let buffer = ''
  function emit(event) {
    const lines = event.split('\n').filter(line => line.startsWith('data:'))
    if (!lines.length) return
    const data = lines.map(line => line.slice(5).replace(/^ /, '')).join('\n')
    if (data === '[DONE]') return
    let text = data
    try {
      const payload = JSON.parse(data)
      if (payload && typeof payload.content === 'string') text = payload.content
    } catch (error) { /* Older endpoints can still send plain text events. */ }
    onData(text)
  }
  return {
    push(chunk) {
      buffer = (buffer + chunk).replace(/\r\n/g, '\n')
      let boundary
      while ((boundary = buffer.indexOf('\n\n')) !== -1) {
        emit(buffer.slice(0, boundary))
        buffer = buffer.slice(boundary + 2)
      }
    },
    finish() {
      if (buffer) emit(buffer)
      buffer = ''
    }
  }
}
