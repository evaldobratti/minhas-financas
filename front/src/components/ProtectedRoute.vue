<template>
  <div>
    <slot v-if="isAuthenticated">
    </slot>
    <div v-else>
      vc não está logado :S
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
    }
  },
  watch: {
    isAuthenticated() {
      if (this.isAuthenticated != null && !this.isAuthenticated) {
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
      return this.$store.state.auth.isAuthenticated;
    }
  }
}
</script>

<style>
  
</style>
