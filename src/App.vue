<template>
  <div>
    <div v-if="isAutenticado == true">
      <Application />
    </div>
    <div v-if="isAutenticado == null">
      Loading :D
    </div>
    <div v-if="isAutenticado == false">
      <router-view />
    </div>
  </div>
</template>

<script>
import Application from './views/Application'

export default {
  created() {
    this.checkCredentials();
  },
  updated() {
    this.checkCredentials();
  },
  methods: {
    checkCredentials() {
      if (this.isAutenticado == false && this.$route.path !== '/login') {
        this.$router.push({
          path: '/login',
          query: {
            next: this.$route.path
          }
        });
      }
    }
  },
  computed: {
    isAutenticado() {
      return this.$store.state.auth.isAutenticado
    }
  },
  components: {
    Application
  }

}
</script>

<style>
</style>
