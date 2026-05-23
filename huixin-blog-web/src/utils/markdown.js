function escapeHtml(value) {
  return String(value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

function renderInline(value) {
  return value
    .replace(/!\[([^\]]*)\]\((https?:\/\/[^)\s]+|data:image\/[^)]+)\)/g, '<img src="$2" alt="$1">')
    .replace(/\[([^\]]+)\]\((https?:\/\/[^)\s]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/\*([^*]+)\*/g, '<em>$1</em>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
}

export function markdownToHtml(markdown) {
  const source = escapeHtml(markdown).replace(/\r\n/g, '\n')
  const lines = source.split('\n')
  const html = []
  let inList = false
  let inCode = false
  let codeLines = []

  function closeList() {
    if (inList) {
      html.push('</ul>')
      inList = false
    }
  }

  function closeCode() {
    if (inCode) {
      html.push(`<pre><code>${codeLines.join('\n')}</code></pre>`)
      codeLines = []
      inCode = false
    }
  }

  for (const rawLine of lines) {
    const line = rawLine.trimEnd()

    if (line.startsWith('```')) {
      if (inCode) closeCode()
      else {
        closeList()
        inCode = true
      }
      continue
    }

    if (inCode) {
      codeLines.push(rawLine)
      continue
    }

    if (!line.trim()) {
      closeList()
      continue
    }

    if (line.startsWith('### ')) {
      closeList()
      html.push(`<h3>${renderInline(line.slice(4))}</h3>`)
    } else if (line.startsWith('## ')) {
      closeList()
      html.push(`<h2>${renderInline(line.slice(3))}</h2>`)
    } else if (line.startsWith('# ')) {
      closeList()
      html.push(`<h1>${renderInline(line.slice(2))}</h1>`)
    } else if (line.startsWith('> ')) {
      closeList()
      html.push(`<blockquote>${renderInline(line.slice(2))}</blockquote>`)
    } else if (line.startsWith('- ')) {
      if (!inList) {
        html.push('<ul>')
        inList = true
      }
      html.push(`<li>${renderInline(line.slice(2))}</li>`)
    } else {
      closeList()
      html.push(`<p>${renderInline(line)}</p>`)
    }
  }

  closeList()
  closeCode()
  return html.join('')
}
