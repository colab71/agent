<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="main-layout">
    <nav class="app-nav">
      <div class="nav-inner">
        <RouterLink to="/" class="nav-brand"><img src="@/assets/logo.svg" alt="Logo" class="nav-logo" />
        </RouterLink>
        <div class="nav-links">
          <RouterLink to="/" class="nav-link">首页</RouterLink>
          <RouterLink to="/about" class="nav-link">关于</RouterLink>
          <RouterLink to="/contact" class="nav-link">联系</RouterLink>
        </div>
      </div>
    </nav>

    <main class="layout-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-nav {
  background-color: rgb(252, 250, 248);
  border-bottom: 1px solid #e5e5e5;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-brand {
  display: flex;
  align-items: center;
  height: 40px;
}

.nav-logo {
  height: 100%;
  width: auto;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: #555;
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #000;
}

.layout-content {
  padding: 2rem;
}
</style>
