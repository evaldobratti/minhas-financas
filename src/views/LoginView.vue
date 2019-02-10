<template>
  <v-btn @click="login">Login</v-btn>
</template>

<script>
export default {
  methods: {
    login() {
      this.$store.dispatch('login').then(() => {
        this.$router.push({
          path: this.$route.query.next || '/',
        });
      })
    }
  }
}
</script>

<style>

</style>
