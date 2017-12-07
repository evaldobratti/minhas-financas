<template>
  <v-layout row>
    <v-flex xs6 offset-xs3>
      <v-card>
        <form @submit.prevent="tryLogin">
          <v-card-text>
            <v-container fluid>
              <v-layout row>
                <v-flex xs12>
                  <v-text-field v-model="usuario" name="input-1" label="Usuário"></v-text-field>  
                </v-flex>
              </v-layout>
              <v-layout row>
                <v-flex xs12>
                  <v-text-field v-model="senha" name="input-1" label="Senha" type="password"></v-text-field>  
                </v-flex>
              </v-layout>
              <v-layout row justify-space-around>
                  <v-btn type="submit" color="primary">Entrar</v-btn>
              </v-layout>
            </v-container>
          </v-card-text>
        </form>
        <form action="/signin/google" method="post">
          <input type="hidden" name="scope" value="https://www.googleapis.com/auth/userinfo.email" />
          <v-btn type="submit" color="primary">google</v-btn>
        </form>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import axios from 'axios';

export default {
  methods: {
    tryLogin() {
      this.$store.dispatch('login')
    }
  },
  computed: {
    usuario: {
      get() {
        return this.$store.state.authentication.form.usuario;
      },
      set(value) {
        this.$store.commit('setFormUsuario', value);
      }
    },
    senha: {
      get() {
        return this.$store.state.authentication.form.senha;
      },
      set(val) {
        this.$store.commit('setFormSenha', val);
      }
    }
  }
}
</script>

<style>
  
</style>
