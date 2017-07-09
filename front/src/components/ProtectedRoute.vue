<template>
  <div>
    {{usuario.login}}
    <slot >
    </slot>
  </div>
</template>

<script>
export default {
  created() {
    if (!this.$store.state.authentication.isAuthenticated)
      this.$router.push({
        path: '/login',
        query: {
        next: this.$route.path
        }
      });
  },
  computed: {
    usuario() {
      return this.$store.state.authentication.usuario;
    }
  }
}
</script>

<style>
  
</style>
