import { createRouter, createWebHistory } from "vue-router";
import type { Router, RouteRecordRaw } from "vue-router";

const HomePage = () => import("../components/HomePage.vue")

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/homepage"
  },
  {
    path: "/homepage",
    component: HomePage
  }
]

const router: Router = createRouter({
  routes,
  history: createWebHistory(),
  sensitive: true,
  strict: true
})

export default router