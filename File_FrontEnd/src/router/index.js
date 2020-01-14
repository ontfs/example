import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import { Message } from 'element-ui'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home
  },
  {
    path: '/login',
    name: 'LoginWrap',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'RegisterWrap',
    component: () => import('../views/Register.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})
// Route guard judges token
router.beforeEach(async (to, from, next) => {
  if (to.name === 'LoginWrap' || to.name === 'RegisterWrap') {
    return next()
  }
  let userName = sessionStorage.getItem('userName')
  let ontId = sessionStorage.getItem('ontId')

  if (!userName || !ontId) {
    let message = 'Please Sign in!'
    Message({ message, type: 'error' })
    return next({ name: 'LoginWrap' })
  } else {
    return next()
  }
})
export default router
