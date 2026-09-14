/**
 * Chuẩn hóa response từ backend về mảng.
 * Hỗ trợ các dạng:
 *   - [...]
 *   - { data: [...] }
 *   - { data: { content: [...] } }
 *   - { content: [...] }
 *   - { data: {...} }   → wrap thành [data]
 */
export function normalizeListResponse(response) {
  if (!response) return []

  // Array trực tiếp
  if (Array.isArray(response)) return response

  // { data: [...] }
  if (Array.isArray(response.data)) return response.data

  // { data: { content: [...] } }
  if (Array.isArray(response.data?.content)) return response.data.content

  // { content: [...] }
  if (Array.isArray(response.content)) return response.content

  // { data: {...} }  → single object, wrap thành array
  if (response.data && typeof response.data === "object") {
    return [response.data]
  }

  return []
}

/**
 * Chuẩn hóa 1 object từ backend (unwrap { success, data })
 */
export function normalizeItemResponse(response) {
  if (!response) return null
  if (response.data && typeof response.data === "object") return response.data
  return response
}