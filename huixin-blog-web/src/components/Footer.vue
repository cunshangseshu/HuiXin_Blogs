<template>
  <footer class="huixin-footer mt-5">
    <div class="container-fluid">
      <div class="row align-items-center">
        <div class="col-md-6 text-center text-md-start mb-2 mb-md-0">
          <span class="footer-brand" style="font-family:var(--font-calligraphy); letter-spacing:2px;">&#x2728; 慧芯博客</span>
        </div>
        <div class="col-md-6 text-center text-md-end">
          <router-link to="/" class="footer-link mx-2">首页</router-link>
          <router-link to="/about" class="footer-link mx-2">关于</router-link>
          <router-link to="/search" class="footer-link mx-2">搜索</router-link>
        </div>
      </div>
      <hr style="border-color:rgba(255,255,255,.15); margin: .6rem 0;">
      <div class="text-center small" style="color:rgba(255,255,255,.5);">
        © 2026 慧芯博客 · 广州慧芯智能网络科技有限公司
      </div>
    </div>
  </footer>
</template>

<style scoped>
.huixin-footer {
  background: linear-gradient(135deg, var(--huixin-primary-dark), var(--huixin-primary));
  color: rgba(255,255,255,.85);
  padding: 1.2rem 0 .8rem;
  font-size: .85rem;
}
.footer-brand { color: #fff; font-size: 1.1rem; }
.footer-link { color: rgba(255,255,255,.7); text-decoration: none; transition: color .2s; }
.footer-link:hover { color: var(--huixin-gold); }
</style>
