<template>
  <div>
    <slot v-if="isAuthenticated">
    </slot>
    <div v-if="isUndetermined">
      Aguarde 1 momento
    </div>
  </div>
</template>

<script>
export default {
  created() {
    this.checkCredentials();
  },
  updated() {
    this.checkCredentials();
  },
  methods: {
    checkCredentials() {
      if (!this.isUndetermined && this.isNotAuthenticated) {
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
    usuario() {
      return this.$store.state.auth.usuario;
    },
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    isUndetermined() {
      return this.$store.getters.isUndetermined;
    },
    isNotAuthenticated() {
      return this.$store.getters.isNotAuthenticated;
    }
  }
}
</script>

<style>
  
</style>
