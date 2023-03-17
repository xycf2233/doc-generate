import { createApp } from 'vue'
import type { App as Application } from "vue";
import { createPinia } from "pinia";
import router from "./router";
import App from "./App.vue"
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'

const pinia = createPinia()
const app: Application = createApp(App)

app.use(ElementPlus)
app.use(pinia)
app.use(router)
app.mount('#app')