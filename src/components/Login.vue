<template>
  <v-layout row>
    <v-flex xs6 offset-xs3>
      <v-card v-if="!isAuthenticated">
        <form @submit.prevent="tryLogin">
          <v-card-text>
            <v-container fluid>
              <v-layout row justify-space-around>
                  <v-btn type="submit" color="primary">Entrar</v-btn>
              </v-layout>
            </v-container>
          </v-card-text>
        </form>
      </v-card>
      <span v-if="isAuthenticated">Você já realizou login, {{usuario.displayName}}</span>
    </v-flex>
  </v-layout>
</template>

<script>
import axios from 'axios';
import AUTH from '../store/auth';

export default {
  methods: {
    tryLogin() {
      this.$store.dispatch(AUTH.d.LOGIN);
    },
    redirect() {
      if (this.isAuthenticated) {
        if (this.$route.query.next){
          this.$router.push(this.$route.query.next);}
        else
          this.$router.push('dashboard');
      }
    }
  },
  created() {
    this.redirect();
  },
  watch: {
    isAuthenticated() {
      this.redirect();
    }
  },
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
    usuario() {
      return this.$store.getters.usuario;
    }
  }
}
</script>

<style>
  
</style>
