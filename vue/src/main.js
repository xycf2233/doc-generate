import { createApp } from 'vue'
import App from './App.vue'
// import router from './router'
// import store from './store'
import elementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css';
const app=createApp(App);

app.use(elementPlus);
createApp(App).mount('#app');
