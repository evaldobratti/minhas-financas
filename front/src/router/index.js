import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
import Login from '@/components/Login'
import Dashboard from '@/components/Dashboard'
import Conta from '@/components/conta/Conta'
import Categorias from '@/components/categoria/Categorias'
import UploadExtrato from '@/components/UploadExtrato';
import AnaliseConjunta from '@/components/conta/AnaliseConjunta';
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Hello',
      component: Hello
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Dashboard
    },
    {
      path: '/contas/:id',
      name: 'detalhe-conta',
      component: Conta
    },
    {
      path: '/categorias',
      name: 'categorias',
      component: Categorias
    },
    {
      path: '/upload',
      name: 'upload',
      component: UploadExtrato
    },
    {
      path: '/analise',
      name: 'analise',
      component: AnaliseConjunta
    },

  ]
})
