<template>
  <v-card>
    <form @submit.prevent="submit">
      <v-card-text>
        <v-container fluid>
          <v-layout row>
            <v-flex xs12>
              <v-text-field v-model="nome" label="Nome"></v-text-field>  
            </v-flex>
          </v-layout>
          <v-layout row>
            <v-flex xs12>
              <v-text-field v-model="saldoInicial" label="Saldo inicial"></v-text-field>  
            </v-flex>
          </v-layout>
          <v-layout row justify-space-around>
              <v-btn type="submit" primary>salvar</v-btn>
          </v-layout>
        </v-container>
      </v-card-text>
    </form>
  </v-card>
</template>

<script>
import { CONTA_SET_NOME, CONTA_SET_SALDO_INICIAL } from '../../store/conta';

export default {
  methods: {
    submit() {
      this.$store.dispatch('contaSubmit');
    }
  },
  computed: {
    nome: {
      get() {
        return this.$store.state.conta.form.nome;
      },
      set(nome) {
        this.$store.commit(CONTA_SET_NOME, nome);
      }
    },
    saldoInicial: {
      get() {
        return this.$store.state.conta.form.saldoInicial;
      },
      set(saldoInicial) {
        this.$store.commit(CONTA_SET_SALDO_INICIAL, saldoInicial);
      }
    }
  }
}
</script>

<style>
  
</style>
