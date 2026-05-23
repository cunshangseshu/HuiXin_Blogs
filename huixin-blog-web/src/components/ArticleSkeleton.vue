<template>
  <div class="card article-skeleton mb-3 p-3">
    <div class="d-flex gap-3">
      <div class="skeleton-dot"></div>
      <div class="flex-grow-1">
        <div class="skeleton-line w-25 mb-2"></div>
        <div class="skeleton-line w-75 mb-2 skeleton-line--strong"></div>
        <div class="skeleton-line w-100 mb-2"></div>
        <div class="skeleton-line w-50"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.article-skeleton {
  pointer-events: none;
}
.skeleton-dot,
.skeleton-line {
  background: linear-gradient(90deg, #eef2ef 25%, #f8faf9 37%, #eef2ef 63%);
  background-size: 400% 100%;
  animation: shimmer 1.25s ease-in-out infinite;
}
.skeleton-dot {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  flex: 0 0 auto;
}
.skeleton-line {
  height: 12px;
  border-radius: 999px;
}
.skeleton-line--strong {
  height: 18px;
}
@keyframes shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: -100% 0; }
}
</style>
