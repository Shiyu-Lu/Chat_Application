import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import SignUp from '@/components/SignUp'
import Home from '@/components/Home'
import ChatsIndex from '@/components/chats/ChatsIndex'
import ContactsIndex from '@/components/contacts/ContactsIndex'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/signup',
      name: 'SignUp',
      component: SignUp
    },
    {
      path:'/home',
      name: 'Home',
      component: Home,
      redirect: '/index',
      children: [
        {
          path: '/index',
          name: 'ChatsIndex',
          component: ChatsIndex,
          // meta: {
          //   requireAuth: true
          // }
        },
        {
          path: '/contacts',
          name: 'ContactsIndex',
          component: ContactsIndex,
          // meta: {
          //   requireAuth: true
          // }
        },
      ]
    }
  ]
})
