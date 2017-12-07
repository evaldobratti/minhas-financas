<template>
  <v-card>
      Finalizando autenticação
  </v-card>
</template>

<script>
import Cookies from 'js-cookie';

export default {
  created() {
    const token = Cookies.get('social-authentication');
    localStorage.setItem('token', token);
    this.$store.dispatch('getInitialData').then(() => {
      this.$router.push('/dashboard');
    });
  }
}
</script>

<style>

</style>
