import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../views/Layout/MainLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/Home/HomeView.vue'),
        },
      ],
    },
  ],
})

export default router
