/**
 * 寻宠招领图片工具 — 统一映射，确保列表和详情页使用同一张图片
 */

const lostFoundImages = [
  'https://images.unsplash.com/photo-1415369629372-26f2fe60c467?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1606567595334-d39972c85dbe?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800&h=500&fit=crop'
]

/**
 * 根据寻宠招领信息获取图片 URL
 * 同一个 item.id 始终返回同一张图片
 */
export function getLostFoundImage(item: { id?: number; photo?: string }): string {
  if (item.photo && !item.photo.includes('/upload/')) return item.photo
  const id = item.id || 0
  return lostFoundImages[id % lostFoundImages.length]
}
