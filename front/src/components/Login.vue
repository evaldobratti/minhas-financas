<template>
  <v-layout row>
    <v-flex xs6 offset-xs3>
      <v-card>
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
    </v-flex>
  </v-layout>
</template>

<script>
import axios from 'axios';
import AUTH from '../store/auth';

export default {
  methods: {
    tryLogin() {
      this.$store.dispatch(AUTH.d.LOGIN).then(() => {
        console.info('ok')
        if (this.$route.query.next){
            this.$router.push(this.$route.query.next);}
          else
            this.$router.push('dashboard');
      }).catch(() => {});
    }
  }
}
</script>

<style>
  
</style>
