import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Vuetify from 'vuetify'
import DatePicker from './components/DatePicker'
import MonthPicker from './components/MonthPicker'
import Currency from './components/Currency'

import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify)

Vue.component('date-picker', DatePicker)
Vue.component('month-picker', MonthPicker)
Vue.component('currency', Currency)

store.dispatch('inicializa')

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

