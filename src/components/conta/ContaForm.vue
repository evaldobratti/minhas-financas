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
              <v-text-field class="number-input" type="number" step="0.01" v-model="saldoInicial" label="Saldo inicial"></v-text-field>  
            </v-flex>
          </v-layout>
          <v-layout row justify-space-around>
              <v-btn type="submit" color="primary">salvar</v-btn>
          </v-layout>
        </v-container>
      </v-card-text>
    </form>
  </v-card>
</template>

<script>
import CONTA from '../../store/conta';

export default {
  data() {
    return {
      nome: '',
      saldoInicial: 0
    }
  },
  methods: {
    submit() {
      
      this.$store.dispatch(CONTA.d.CONTA_SUBMIT, {
        nome: this.nome,
        saldoInicial: this.saldoInicial
      }).then(() => {
        this.$emit('cadastrado');
      });
    }
  },
}
</script>

<style>
  
</style>
