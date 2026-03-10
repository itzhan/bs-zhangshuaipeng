/**
 * 宠物图片工具 — 统一映射，确保列表和详情页使用同一张图片
 */

const catImages = [
  'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1495360010541-f48722b34f7d?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1533738363-b7f9aef128ce?w=800&h=500&fit=crop'
]

const dogImages = [
  'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1477884213360-7e9d7dcc8f9b?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1558788353-f76d92427f16?w=800&h=500&fit=crop'
]

const rabbitImages = [
  'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=800&h=500&fit=crop',
  'https://images.unsplash.com/photo-1452857297128-d9c29adba80b?w=800&h=500&fit=crop'
]

/**
 * 根据宠物信息获取图片 URL
 * 同一个 pet.id + species 始终返回同一张图片
 */
export function getPetImage(pet: { id?: number; species?: string; photo?: string }): string {
  // 如果有真实上传的图片（非本地占位路径），直接使用
  if (pet.photo && !pet.photo.includes('placeholder') && !pet.photo.includes('/upload/')) {
    return pet.photo
  }

  const id = pet.id || 1
  const sp = (pet.species || '').toLowerCase()

  if (sp.includes('猫') || sp.includes('cat')) {
    return catImages[id % catImages.length]
  }
  if (sp.includes('狗') || sp.includes('dog')) {
    return dogImages[id % dogImages.length]
  }
  if (sp.includes('兔') || sp.includes('rabbit')) {
    return rabbitImages[id % rabbitImages.length]
  }

  // 默认用猫图
  return catImages[id % catImages.length]
}
