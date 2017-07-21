// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css';
import store from './store'
import { sync } from 'vuex-router-sync';
import currency from './filters/currency';
import date from './filters/date';

sync(store, router);

Vue.config.productionTip = false

Vue.use(Vuetify);
Vue.filter('currency', currency);
Vue.filter('date', date);
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
