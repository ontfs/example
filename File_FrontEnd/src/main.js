import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './assets/reset.css'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// http api
import * as http from '@/api/role'
import * as utils from '@/utils'
Vue.prototype.$http = http
Vue.prototype.$utils = utils
import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css'
Vue.use(Viewer)
Viewer.setDefaults({
  Options: {
    inline: true,
    button: true,
    navbar: true,
    title: true,
    toolbar: true,
    tooltip: true,
    movable: true,
    zoomable: true,
    rotatable: true,
    scalable: true,
    transition: true,
    fullscreen: true,
    keyboard: true,
    url: 'data-source'
  }
})
Vue.config.productionTip = false
Vue.use(ElementUI)
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
